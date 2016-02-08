package kz.greetgo.mvc.model;

public class UploadInfo {
  public String location = System.getProperty("java.io.tmpdir");
  public long maxFileSize = -1L;
  public long maxRequestSize = -1L;
  public int fileSizeThreshold = 0;
}
