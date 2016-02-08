package kz.greetgo.mvc;

import kz.greetgo.mvc.core.RequestMethod;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.mvc.interfaces.TunnelCookies;
import kz.greetgo.mvc.interfaces.Upload;
import kz.greetgo.mvc.model.UploadInfo;
import kz.greetgo.mvc.util.HttpServletTunnelCookies;
import kz.greetgo.mvc.util.UploadOnPartBridge;
import kz.greetgo.util.events.EventHandlerList;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.MultiException;
import org.eclipse.jetty.util.MultiPartInputStreamParser;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class JettyRequestTunnel implements RequestTunnel {

  final String target;
  @SuppressWarnings({"FieldCanBeLocal", "unused"})
  final Request baseRequest;
  final HttpServletRequest request;
  final HttpServletResponse response;
  private final HttpServletTunnelCookies cookies;

  private final EventHandlerList beforeCompleteHeaders = new EventHandlerList();

  @Override
  public EventHandlerList eventBeforeCompleteHeaders() {
    return beforeCompleteHeaders;
  }

  public JettyRequestTunnel(String target, Request baseRequest,
                            HttpServletRequest request, HttpServletResponse response) {
    this.target = target;
    this.baseRequest = baseRequest;
    this.request = request;
    this.response = response;
    cookies = new HttpServletTunnelCookies(request, response);
  }

  @Override
  public String getTarget() {
    return target;
  }

  @Override
  public PrintWriter getResponseWriter() {
    beforeCompleteHeaders.fire();
    try {
      return response.getWriter();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public OutputStream getResponseOutputStream() {
    beforeCompleteHeaders.fire();
    try {
      return response.getOutputStream();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public InputStream getRequestInputStream() {
    try {
      return request.getInputStream();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Upload getUpload(String paramName) {
    try {
      return new UploadOnPartBridge(request.getPart(paramName));
    } catch (ServletException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void sendRedirect(String reference) {
    beforeCompleteHeaders.fire();
    try {
      response.sendRedirect(reference);
      baseRequest.setHandled(true);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void enableMultipartSupport(UploadInfo uploadInfo) {
    request.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, new MultipartConfigElement(uploadInfo.location,
      uploadInfo.maxFileSize, uploadInfo.maxRequestSize, uploadInfo.fileSizeThreshold));
  }

  @Override
  public void removeMultipartData() {
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

  @Override
  public String getRequestContentType() {
    return request.getContentType();
  }

  @Override
  public boolean isExecuted() {
    return baseRequest.isHandled();
  }

  @Override
  public void setExecuted(boolean executed) {
    baseRequest.setHandled(executed);
  }

  @Override
  public RequestMethod getRequestMethod() {
    return RequestMethod.fromStr(request.getMethod());
  }

  @Override
  public TunnelCookies cookies() {
    return cookies;
  }

  @Override
  public String[] getParamValues(String name) {
    return request.getParameterValues(name);
  }

  @Override
  public BufferedReader getRequestReader() {
    try {
      return request.getReader();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }


}
