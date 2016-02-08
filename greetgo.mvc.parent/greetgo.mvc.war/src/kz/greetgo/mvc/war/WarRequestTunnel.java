package kz.greetgo.mvc.war;

import kz.greetgo.mvc.core.RequestMethod;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.mvc.interfaces.TunnelCookies;
import kz.greetgo.mvc.interfaces.Upload;
import kz.greetgo.mvc.model.UploadInfo;
import kz.greetgo.mvc.util.HttpServletTunnelCookies;
import kz.greetgo.mvc.util.UploadOnPartBridge;
import kz.greetgo.util.events.EventHandlerList;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class WarRequestTunnel implements RequestTunnel {
  private final String target;
  private final HttpServletRequest request;
  private final HttpServletResponse response;
  private final HttpServletTunnelCookies cookies;
  private volatile boolean executed = false;

  public WarRequestTunnel(String target, ServletRequest request, ServletResponse response) {
    this.target = target;
    this.request = (HttpServletRequest)request;
    this.response = (HttpServletResponse)response;
    cookies = new HttpServletTunnelCookies(this.request, this.response);
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
      response.sendRedirect(reference);
      executed = true;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void enableMultipartSupport(UploadInfo uploadInfo) {
    //TODO realize it
    throw new RuntimeException("Just not realized");
  }

  @Override
  public void removeMultipartData() {
    //TODO realize it
    throw new RuntimeException("Just not realized");
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
    return cookies;
  }

  private final EventHandlerList beforeCompleteHeaders = new EventHandlerList();

  @Override
  public EventHandlerList eventBeforeCompleteHeaders() {
    return beforeCompleteHeaders;
  }
}
