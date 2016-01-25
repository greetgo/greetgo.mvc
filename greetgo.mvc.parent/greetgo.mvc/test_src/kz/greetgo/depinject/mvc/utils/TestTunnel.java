package kz.greetgo.depinject.mvc.utils;

import kz.greetgo.depinject.mvc.RequestTunnel;
import kz.greetgo.depinject.mvc.Upload;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class TestTunnel implements RequestTunnel {

  public String target;
  public String redirectedTo;


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

  @Override
  public String[] getParamValues(String name) {
    return paramValues.get(name);
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
    redirectedTo = reference;
  }
}
