package kz.greetgo.mvc.interfaces;

import kz.greetgo.mvc.model.UploadInfo;

public interface TunnelExecutorGetter {
  TunnelExecutor getTunnelExecutor(RequestTunnel tunnel);
}
