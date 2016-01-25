package kz.greetgo.depinject.mvc.utils;

import kz.greetgo.depinject.mvc.Upload;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public class TestUpload implements Upload {
  private final String name;

  public TestUpload(String name) {
    this.name = name;
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return new ByteArrayInputStream(("Content of " + name).getBytes("UTF-8"));
  }

  @Override
  public String getContentType() {
    return "ContentType of " + name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getSubmittedFileName() {
    return "SubmittedFileName of " + name;
  }

  @Override
  public long getSize() {
    return name.length();
  }

  @Override
  public String getHeader(String name) {
    return null;
  }

  @Override
  public Collection<String> getHeaders(String name) {
    return null;
  }

  @Override
  public Collection<String> getHeaderNames() {
    return null;
  }
}
