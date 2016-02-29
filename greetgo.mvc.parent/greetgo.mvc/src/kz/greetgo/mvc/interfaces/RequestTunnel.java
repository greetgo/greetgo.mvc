package kz.greetgo.mvc.interfaces;

import kz.greetgo.mvc.core.RequestMethod;
import kz.greetgo.mvc.model.UploadInfo;
import kz.greetgo.util.events.EventHandlerList;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

public interface RequestTunnel {

  String getTarget();

  PrintWriter getResponseWriter();

  OutputStream getResponseOutputStream();

  String[] getParamValues(String paramName);

  BufferedReader getRequestReader();

  InputStream getRequestInputStream();

  Upload getUpload(String paramName);

  void sendRedirect(String reference);

  void setResponseContentLength(int length);

  void enableMultipartSupport(UploadInfo uploadInfo);

  void removeMultipartData();

  String getRequestContentType();

  boolean isExecuted();

  void setExecuted(boolean executed);

  RequestMethod getRequestMethod();

  TunnelCookies cookies();

  EventHandlerList eventBeforeCompleteHeaders();

  void flushBuffer();

  void setResponseContentType(String contentType);

  String getRequestHeader(String headerName);

  void setResponseDateHeader(String headerName, long headerValue);

  void setResponseStatus(int statusCode);

  void setResponseHeader(String headerName, String headerValue);

  long getRequestDateHeader(String headerName);

  void forward(String reference, boolean executeBeforeCompleteHeaders);

  void setRequestAttribute(String name, Object value);
}
