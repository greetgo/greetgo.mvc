package kz.greetgo.mvc.jetty;

public class MultipartConf {
  public String location = System.getProperty("java.io.tmpdir");
  public long maxFileSize = -1L;
  public long maxRequestSize = -1L;
  public int fileSizeThreshold = 0;

  //return new MultipartConfigElement(location, maxFileSize, maxRequestSize, fileSizeThreshold);
}
