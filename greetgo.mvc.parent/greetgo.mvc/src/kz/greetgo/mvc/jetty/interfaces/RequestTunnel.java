package kz.greetgo.mvc.jetty.interfaces;

import kz.greetgo.mvc.jetty.core.RequestMethod;
import kz.greetgo.mvc.jetty.model.UploadInfo;

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
}
