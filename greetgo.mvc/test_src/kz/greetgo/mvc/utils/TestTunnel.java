package kz.greetgo.mvc.utils;

import kz.greetgo.mvc.core.EventTunnelCookies;
import kz.greetgo.mvc.core.RequestMethod;
import kz.greetgo.mvc.core.TestTunnelCookies;
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

import javax.servlet.DispatcherType;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestTunnel implements RequestTunnel {

  public String target;
  public String redirectedTo;


  public TestTunnel(RequestMethod requestMethod) {
    this.requestMethod = requestMethod;
  }

  public TestTunnel() {
    this(RequestMethod.GET);
  }

  @Override
  public String toString() {
    return "TestTunnel{http=" + requestMethod + ";target=" + target + "}";
  }

  @Override
  public String getTarget() {
    return target;
  }


  private final CharArrayWriter charArrayWriter = new CharArrayWriter();

  @Override
  public PrintWriter getResponseWriter() {
    return new PrintWriter(charArrayWriter);
  }

  private final ByteArrayOutputStream responseOutputStream = new ByteArrayOutputStream();

  @Override
  public OutputStream getResponseOutputStream() {
    return responseOutputStream;
  }

  public final Map<String, String[]> paramValues = new HashMap<>();

  private final RequestParams requestParams = new RequestParams() {
    @Override
    public String[] asArray(String name) {
      return paramValues.get(name);
    }

    @Override
    public Enumeration<String> nameAsEnumeration() {
      return new Enumeration<String>() {
        List<String> names = new ArrayList<>(paramValues.keySet());
        int i = 0;

        @Override
        public boolean hasMoreElements() {
          return i < names.size();
        }

        @Override
        public String nextElement() {
          return names.get(i++);
        }
      };
    }
  };

  @Override
  public RequestParams requestParams() {
    return requestParams;
  }

  public String forGetRequestReader;

  @Override
  public BufferedReader getRequestReader() {
    return new BufferedReader(new StringReader(forGetRequestReader));
  }

  public byte[] forGetRequestInputStream;

  @Override
  public InputStream getRequestInputStream() {
    return new ByteArrayInputStream(forGetRequestInputStream);
  }

  public String responseCharText() {
    return charArrayWriter.toString();
  }

  public String responseBinText() {
    try {
      return responseOutputStream.toString("UTF-8");
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  public void setParam(String paramName, String... paramValue) {
    paramValues.put(paramName, paramValue);
  }

  public void clearParam(String paramName) {
    paramValues.remove(paramName);
  }

  private final Map<String, TestUpload> uploadMap = new HashMap<>();

  public void appendTestUpload(TestUpload testUpload) {
    if (uploadMap.containsKey(testUpload.getName())) throw new IllegalArgumentException("Test upload with name "
      + testUpload.getName() + " already exists");

    uploadMap.put(testUpload.getName(), testUpload);
  }

  @Override
  public Upload getUpload(String paramName) {
    return uploadMap.get(paramName);
  }

  @Override
  public void sendRedirect(String reference) {
    beforeCompleteHeaders.fire();
    redirectedTo = reference;
  }

  public UploadInfo enableMultipartSupportWith;

  @Override
  public void enableMultipartSupport(UploadInfo uploadInfo) {
    enableMultipartSupportWith = uploadInfo;
  }

  public boolean removedMultipartData = false;

  @Override
  public void removeMultipartData() {
    removedMultipartData = true;
  }

  public String requestContentType;

  @Override
  public String getRequestContentType() {
    return requestContentType;
  }

  private boolean executed = false;

  @Override
  public boolean isExecuted() {
    return executed;
  }

  @Override
  public void setExecuted(boolean executed) {
    this.executed = executed;
  }

  private final RequestMethod requestMethod;

  @Override
  public RequestMethod getRequestMethod() {
    return requestMethod;
  }

  public final TestTunnelCookies testCookies = new TestTunnelCookies();


  @Override
  public TunnelCookies cookies() {
    return cookiesReturn;
  }

  private final EventHandlerList beforeCompleteHeaders = new EventHandlerList();
  private final EventTunnelCookies cookiesReturn = new EventTunnelCookies(testCookies, beforeCompleteHeaders);

  @Override
  public EventHandlerList eventBeforeCompleteHeaders() {
    return beforeCompleteHeaders;
  }

  public boolean flushBuffersCalled = false;

  @Override
  public void flushBuffer() {
    flushBuffersCalled = true;
  }

  public String responseContentType = null;

  @Override
  public void setResponseContentType(String contentType) {
    responseContentType = contentType;
  }

  @Override
  public RequestHeaders requestHeaders() {
    throw new RuntimeException();
  }

  public Integer responseStatus = null;

  @Override
  public void setResponseStatus(int statusCode) {
    responseStatus = statusCode;
  }

  public final Map<String, String> responseHeaders = new HashMap<>();

  @Override
  public void setResponseHeader(String headerName, String headerValue) {
    responseHeaders.put(headerName, headerValue);
  }

  @Override
  public void forward(String reference, boolean executeBeforeCompleteHeaders) {
    throw new RuntimeException();
  }

  @Override
  public RequestPaths requestPaths() {
    throw new RuntimeException();
  }

  @Override
  public RequestSession requestSession() {
    throw new RuntimeException();
  }

  @Override
  public RequestContent requestContent() {
    throw new RuntimeException();
  }

  @Override
  public RequestMeta requestMeta() {
    throw new RuntimeException();
  }

  @Override
  public DispatcherType getDispatcherType() {
    throw new RuntimeException();
  }

  @Override
  public void setResponseDateHeader(String headerName, long headerValue) {
    throw new RuntimeException();
  }

  @Override
  public void setResponseContentLength(int length) {
    throw new RuntimeException();
  }

  @Override
  public RequestAttributes requestAttributes() {
    throw new RuntimeException();
  }
}
