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

  void enableMultipartSupport(UploadInfo uploadInfo);

  void removeMultipartData();

  String getRequestContentType();

  boolean isExecuted();

  void setExecuted(boolean executed);

  RequestMethod getRequestMethod();

  TunnelCookies cookies();

  EventHandlerList eventBeforeCompleteHeaders();
}
