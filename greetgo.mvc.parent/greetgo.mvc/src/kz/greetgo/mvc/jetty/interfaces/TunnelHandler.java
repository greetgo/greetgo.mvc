package kz.greetgo.mvc.jetty.interfaces;

import kz.greetgo.mvc.jetty.model.UploadInfo;

public interface TunnelHandler {
  void handle();

  UploadInfo getUploadInfo();
}
