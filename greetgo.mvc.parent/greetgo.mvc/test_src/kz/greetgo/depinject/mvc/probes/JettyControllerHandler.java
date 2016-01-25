package kz.greetgo.depinject.mvc.probes;

import kz.greetgo.depinject.mvc.ControllerHandler;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.MultiException;
import org.eclipse.jetty.util.MultiPartInputStreamParser;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class JettyControllerHandler extends AbstractHandler {
  private final MultipartConfigElement multiPartConfig;

  public static final String MULTIPART_FORM_DATA_TYPE = "multipart/form-data";

  public static boolean isMultipartRequest(ServletRequest request) {
    return request.getContentType() != null && request.getContentType().startsWith(MULTIPART_FORM_DATA_TYPE);
  }

  private void enableMultipartSupport(HttpServletRequest request) {
    request.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, multiPartConfig);
  }

  private final List<ControllerHandler> tunnelHandlers = new ArrayList<>();

  public JettyControllerHandler(Collection<ControllerHandler> handlers, String tmpDir) {
    tunnelHandlers.addAll(handlers);
    multiPartConfig = new MultipartConfigElement(tmpDir);
  }

  public JettyControllerHandler(Collection<ControllerHandler> handlers) {
    this(handlers, System.getProperty("java.io.tmpdir"));
  }

  @Override
  public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {

    boolean multipartRequest = HttpMethod.POST.is(request.getMethod()) && isMultipartRequest(request);
    if (multipartRequest) enableMultipartSupport(request);

    try {
      handleTunnels(target, baseRequest, request, response);
    } finally {

      if (multipartRequest) {
        MultiPartInputStreamParser multipartInputStream =
          (MultiPartInputStreamParser) request.getAttribute(Request.__MULTIPART_INPUT_STREAM);

        if (multipartInputStream != null) {
          try {
            // a multipart request to a servlet will have the parts cleaned up correctly, but
            // the repeated call to deleteParts() here will safely do nothing.
            multipartInputStream.deleteParts();
          } catch (MultiException e) {
            e.printStackTrace();
          }
        }
      }

    }
  }


  private void handleTunnels(String target, Request baseRequest,
                             HttpServletRequest request, HttpServletResponse response) {

    final JettyRequestTunnel tunnel = new JettyRequestTunnel(target, baseRequest, request, response);

    for (ControllerHandler tunnelHandler : tunnelHandlers) {
      if (tunnelHandler.handleTunnel(tunnel)) {
        baseRequest.setHandled(true);
        return;
      }
    }

  }
}
