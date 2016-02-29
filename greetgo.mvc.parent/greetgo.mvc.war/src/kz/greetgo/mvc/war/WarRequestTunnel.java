package kz.greetgo.mvc.war;

import kz.greetgo.mvc.core.EventTunnelCookies;
import kz.greetgo.mvc.core.HttpServletTunnelCookies;
import kz.greetgo.mvc.core.RequestMethod;
import kz.greetgo.mvc.core.UploadOnPartBridge;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.mvc.interfaces.TunnelCookies;
import kz.greetgo.mvc.interfaces.Upload;
import kz.greetgo.mvc.model.UploadInfo;
import kz.greetgo.util.events.EventHandlerList;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class WarRequestTunnel implements RequestTunnel {
  public final HttpServletRequest request;
  public final HttpServletResponse response;
  private final HttpServletTunnelCookies cookies;
  private final EventTunnelCookies cookiesReturn;
  private volatile boolean executed = false;

  public WarRequestTunnel(ServletRequest request, ServletResponse response) {
    this.request = (HttpServletRequest) request;
    this.response = (HttpServletResponse) response;
    cookies = new HttpServletTunnelCookies(this.request, this.response);
    cookiesReturn = new EventTunnelCookies(cookies, beforeCompleteHeaders);
  }

  @Override
  public String getTarget() {
    return request.getPathInfo();
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
  public String[] getParamValues(String paramName) {
    return request.getParameterValues(paramName);
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
      if (reference != null && reference.startsWith("/")) {
        reference = request.getContextPath() + reference;
      }
      response.sendRedirect(reference);
      executed = true;
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
  public void setResponseContentLength(int length) {
    beforeCompleteHeaders.fire();
    response.setContentLength(length);
  }

  @Override
  public void enableMultipartSupport(UploadInfo uploadInfo) {
    throw new RuntimeException("enableMultipartSupport cannot be called");
  }

  @Override
  public void removeMultipartData() {
    throw new RuntimeException("removeMultipartData cannot be called");
  }

  @Override
  public String getRequestContentType() {
    return request.getContentType();
  }

  @Override
  public boolean isExecuted() {
    return executed;
  }

  @Override
  public void setExecuted(boolean executed) {
    this.executed = executed;
  }

  @Override
  public RequestMethod getRequestMethod() {
    return RequestMethod.fromStr(request.getMethod());
  }

  @Override
  public TunnelCookies cookies() {
    return cookiesReturn;
  }

  private final EventHandlerList beforeCompleteHeaders = new EventHandlerList();

  @Override
  public EventHandlerList eventBeforeCompleteHeaders() {
    return beforeCompleteHeaders;
  }

  @Override
  public void flushBuffer() {
    beforeCompleteHeaders.fire();

    try {
      response.flushBuffer();
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
