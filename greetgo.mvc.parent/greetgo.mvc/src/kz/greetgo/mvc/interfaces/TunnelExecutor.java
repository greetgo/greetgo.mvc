package kz.greetgo.mvc.interfaces;

import kz.greetgo.mvc.model.UploadInfo;

public interface TunnelExecutor {
  void execute();

  UploadInfo getUploadInfo();
}
