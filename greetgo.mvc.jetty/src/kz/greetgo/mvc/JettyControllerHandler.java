package kz.greetgo.mvc;

import kz.greetgo.mvc.interfaces.TunnelExecutor;
import kz.greetgo.mvc.interfaces.TunnelExecutorGetter;
import kz.greetgo.mvc.model.UploadInfo;
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
import java.util.List;

import static kz.greetgo.mvc.util.MvcUtil.executeExecutor;

public final class JettyControllerHandler extends AbstractHandler {
  public static final String MULTIPART_FORM_DATA_TYPE = "multipart/form-data";

  public static boolean isMultipartRequest(ServletRequest request) {
    return request.getContentType() != null && request.getContentType().startsWith(MULTIPART_FORM_DATA_TYPE);
  }

  private static void enableMultipartSupport(HttpServletRequest request, MultipartConfigElement multiPartConfig) {
    request.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, multiPartConfig);
  }

  public final List<TunnelExecutorGetter> tunnelExecutorGetters = new ArrayList<>();

  @Override
  public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {

    final JettyRequestTunnel tunnel = new JettyRequestTunnel(target, baseRequest, request, response);

    final TunnelExecutor tunnelExecutor = getTunnelHandler(tunnel);
    if (tunnelExecutor == null) return;

    boolean multipartRequest = HttpMethod.POST.is(request.getMethod()) && isMultipartRequest(request);

    if (multipartRequest) {
      UploadInfo uploadInfo = tunnelExecutor.getUploadInfo();
      if (uploadInfo == null) uploadInfo = new UploadInfo();
      MultipartConfigElement multiPartConfig = new MultipartConfigElement(uploadInfo.location,
        uploadInfo.maxFileSize, uploadInfo.maxRequestSize, uploadInfo.fileSizeThreshold);
      enableMultipartSupport(request, multiPartConfig);
    }

    try {
      executeExecutor(tunnelExecutor);
      baseRequest.setHandled(true);
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

  private TunnelExecutor getTunnelHandler(JettyRequestTunnel tunnel) {
    for (TunnelExecutorGetter tunnelExecutorGetter : tunnelExecutorGetters) {
      final TunnelExecutor tunnelExecutor = tunnelExecutorGetter.getTunnelExecutor(tunnel);
      if (tunnelExecutor != null) return tunnelExecutor;
    }
    return null;
  }
}
