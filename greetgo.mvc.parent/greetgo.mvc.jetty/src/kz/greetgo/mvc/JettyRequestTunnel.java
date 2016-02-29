package kz.greetgo.mvc;

import kz.greetgo.mvc.core.HttpServletTunnelCookies;
import kz.greetgo.mvc.core.RequestMethod;
import kz.greetgo.mvc.core.UploadOnPartBridge;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.mvc.interfaces.TunnelCookies;
import kz.greetgo.mvc.interfaces.Upload;
import kz.greetgo.mvc.model.UploadInfo;
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

  @Override
  public void flushBuffer() {
    try {
      response.flushBuffer();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
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
  public void setResponseContentType(String contentType) {
    beforeCompleteHeaders.fire();
    response.setContentType(contentType);
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

  @Override
  public String getRequestHeader(String headerName) {
    beforeCompleteHeaders.fire();
    return request.getHeader(headerName);
  }

  @Override
  public void setResponseStatus(int statusCode) {
    beforeCompleteHeaders.fire();
    response.setStatus(statusCode);
  }

  @Override
  public void setResponseHeader(String headerName, String headerValue) {
    beforeCompleteHeaders.fire();
    response.setHeader(headerName, headerValue);
  }

  @Override
  public void setResponseDateHeader(String headerName, long headerValue) {
    beforeCompleteHeaders.fire();
    response.setDateHeader(headerName, headerValue);
  }

  @Override
  public long getRequestDateHeader(String headerName) {
    return request.getDateHeader(headerName);
  }

  @Override
  public void setResponseContentLength(int length) {
    beforeCompleteHeaders.fire();
    response.setContentLength(length);
  }

  @Override
  public void forward(String reference, boolean executeBeforeCompleteHeaders) {
    if (executeBeforeCompleteHeaders) {
      beforeCompleteHeaders.fire();
    }
    try {
      request.getRequestDispatcher(reference).forward(request, response);
    } catch (ServletException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void setRequestAttribute(String name, Object value) {
    request.setAttribute(name, value);
  }
}
