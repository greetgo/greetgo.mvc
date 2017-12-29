package kz.greetgo.mvc;

import kz.greetgo.mvc.core.EventTunnelCookies;
import kz.greetgo.mvc.core.HttpServletTunnelCookies;
import kz.greetgo.mvc.core.RequestMethod;
import kz.greetgo.mvc.core.UploadOnPartBridge;
import kz.greetgo.mvc.interfaces.RequestAttributes;
import kz.greetgo.mvc.interfaces.RequestContent;
import kz.greetgo.mvc.interfaces.RequestHeaders;
import kz.greetgo.mvc.interfaces.RequestMeta;
import kz.greetgo.mvc.interfaces.RequestParams;
import kz.greetgo.mvc.interfaces.RequestPaths;
import kz.greetgo.mvc.interfaces.RequestSession;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.mvc.interfaces.TunnelCookies;
import kz.greetgo.mvc.interfaces.Upload;
import kz.greetgo.mvc.model.UploadInfo;
import kz.greetgo.util.events.EventHandlerList;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.MultiException;
import org.eclipse.jetty.util.MultiPartInputStreamParser;

import javax.servlet.DispatcherType;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Locale;

public class JettyRequestTunnel implements RequestTunnel {

  final String target;
  @SuppressWarnings({"FieldCanBeLocal", "unused"})
  final Request baseRequest;
  final HttpServletRequest request;
  final HttpServletResponse response;

  private final EventTunnelCookies cookiesReturn;

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
    HttpServletTunnelCookies cookies = new HttpServletTunnelCookies(request, response);
    cookiesReturn = new EventTunnelCookies(cookies, beforeCompleteHeaders);
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
    return cookiesReturn;
  }

  @Override
  public RequestParams requestParams() {
    return requestParams;
  }

  private final RequestParams requestParams = new RequestParams() {
    @Override
    public String[] asArray(String name) {
      return request.getParameterValues(name);
    }

    @Override
    public Enumeration<String> nameAsEnumeration() {
      return request.getParameterNames();
    }
  };

  @Override
  public BufferedReader getRequestReader() {
    try {
      return request.getReader();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public RequestHeaders requestHeaders() {
    return requestHeaders;
  }

  private final RequestHeaders requestHeaders = new RequestHeaders() {
    @Override
    public String value(String headerName) {
      return request.getHeader(headerName);
    }

    @Override
    public long asDate(String headerName) {
      return request.getDateHeader(headerName);
    }

    @Override
    public long asInt(String headerName) {
      return request.getIntHeader(headerName);
    }

    @Override
    public Enumeration<String> allValuesForAsEnumeration(String headerName) {
      return request.getHeaders(headerName);
    }

    @Override
    public Enumeration<String> namesAsEnumeration() {
      return request.getHeaderNames();
    }

  };

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
  public RequestAttributes requestAttributes() {
    return requestAttributes;
  }

  private final RequestAttributes requestAttributes = new RequestAttributes() {
    @Override
    public <T> T get(String name) {
      //noinspection unchecked
      return (T) request.getAttribute(name);
    }

    @Override
    public void set(String name, Object value) {
      request.setAttribute(name, value);
    }

    @Override
    public void remove(String name) {
      request.removeAttribute(name);
    }
  };


  @Override
  public RequestMeta requestMeta() {
    return requestMeta;
  }

  private final RequestMeta requestMeta = new RequestMeta() {
    @Override
    public String getLocalAddr() {
      return request.getLocalAddr();
    }

    @Override
    public int getLocalPort() {
      return request.getLocalPort();
    }

    @Override
    public String getLocalName() {
      return request.getLocalName();
    }

    @Override
    public Locale getLocale() {
      return request.getLocale();
    }

    @Override
    public Enumeration<Locale> getLocales() {
      return request.getLocales();
    }

    @Override
    public String getProtocol() {
      return request.getProtocol();
    }

    @Override
    public String getScheme() {
      return request.getScheme();
    }

    @Override
    public String getServerName() {
      return request.getServerName();
    }

    @Override
    public int getServerPort() {
      return request.getServerPort();
    }

    @Override
    public String getRemoteAddr() {
      return request.getRemoteAddr();
    }

    @Override
    public String getRemoteHost() {
      return request.getRemoteHost();
    }

    @Override
    public int getRemotePort() {
      return request.getRemotePort();
    }
  };

  @Override
  public DispatcherType getDispatcherType() {
    return request.getDispatcherType();
  }

  @Override
  public RequestContent requestContent() {
    return requestContent;
  }

  private final RequestContent requestContent = new RequestContent() {
    @Override
    public int intLength() {
      return request.getContentLength();
    }

    @Override
    public long length() {
      return request.getContentLengthLong();
    }

    @Override
    public String type() {
      return request.getContentType();
    }
  };


  @Override
  public RequestSession requestSession() {
    return requestSession;
  }

  private final RequestSession requestSession = new RequestSession() {

    @Override
    public HttpSession getSession(boolean create) {
      return request.getSession(create);
    }

    @Override
    public HttpSession getSession() {
      return request.getSession();
    }

    @Override
    public String changeSessionId() {
      return request.changeSessionId();
    }

    @Override
    public boolean isRequestedSessionIdValid() {
      return request.isRequestedSessionIdValid();
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
      return request.isRequestedSessionIdFromCookie();
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
      return request.isRequestedSessionIdFromURL();
    }

    @Override
    public boolean authenticate() throws IOException, ServletException {
      return request.authenticate(response);
    }

    @Override
    public void login(String username, String password) throws ServletException {
      request.login(username, password);
    }

    @Override
    public void logout() throws ServletException {
      request.logout();
    }

    @Override
    public String getRemoteUser() {
      return request.getRemoteUser();
    }

    @Override
    public String getAuthType() {
      return request.getAuthType();
    }

    @Override
    public boolean isSecure() {
      return request.isSecure();
    }
  };

  @Override
  public RequestPaths requestPaths() {
    return requestPaths;
  }

  private final RequestPaths requestPaths = new RequestPaths() {

    @Override
    public String getRequestURI() {
      return request.getRequestURI();
    }

    @Override
    public StringBuffer getRequestURL() {
      return request.getRequestURL();
    }

    @Override
    public String getServletPath() {
      return request.getServletPath();
    }

    @Override
    public String getQueryString() {
      return request.getQueryString();
    }

    @Override
    public String getContextPath() {
      return request.getContextPath();
    }

    @Override
    public String getPathTranslated() {
      return request.getPathTranslated();
    }

    @Override
    public String getPathInfo() {
      return request.getPathInfo();
    }
  };
}
