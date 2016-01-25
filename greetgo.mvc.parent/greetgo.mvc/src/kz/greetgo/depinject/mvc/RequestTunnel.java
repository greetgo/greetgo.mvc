package kz.greetgo.depinject.mvc;

import java.io.*;

public interface RequestTunnel {

  String getTarget();

  PrintWriter getResponseWriter();

  OutputStream getResponseOutputStream();

  String[] getParamValues(String paramName);

  BufferedReader getRequestReader();

  InputStream getRequestInputStream();

  Upload getUpload(String paramName);

  void sendRedirect(String reference);
}
