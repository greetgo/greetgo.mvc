package kz.greetgo.mvc.interfaces;

import kz.greetgo.mvc.builder.ExecDefinition;

public interface TunnelExecutorGetter {
  TunnelExecutor getTunnelExecutor(RequestTunnel tunnel);

  ExecDefinition definition();
}
