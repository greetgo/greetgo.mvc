package kz.greetgo.mvc.jetty;

public class UploadInfo {
  public String location = System.getProperty("java.io.tmpdir");
  public long maxFileSize = -1L;
  public long maxRequestSize = -1L;
  public int fileSizeThreshold = 0;

  public UploadInfo copy() {
    UploadInfo ret = new UploadInfo();
    ret.location = location;
    ret.maxFileSize = maxFileSize;
    ret.maxRequestSize = maxRequestSize;
    ret.fileSizeThreshold = fileSizeThreshold;
    return ret;
  }
}
