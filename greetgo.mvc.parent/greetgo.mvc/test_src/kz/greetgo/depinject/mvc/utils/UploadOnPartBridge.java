package kz.greetgo.depinject.mvc.utils;

import kz.greetgo.depinject.mvc.Upload;

import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

public class UploadOnPartBridge implements Upload {
  private final Part part;

  public UploadOnPartBridge(Part part) {
    this.part = part;
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return part.getInputStream();
  }

  @Override
  public String getContentType() {
    return part.getContentType();
  }

  @Override
  public String getName() {
    return part.getName();
  }

  @Override
  public String getSubmittedFileName() {
    return part.getSubmittedFileName();
  }

  @Override
  public long getSize() {
    return part.getSize();
  }

  @Override
  public String getHeader(String name) {
    return part.getHeader(name);
  }

  @Override
  public Collection<String> getHeaders(String name) {
    return part.getHeaders(name);
  }

  @Override
  public Collection<String> getHeaderNames() {
    return part.getHeaderNames();
  }
}
