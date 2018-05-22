package kz.greetgo.mvc.interfaces;

import kz.greetgo.mvc.core.MappingIdentity;

public interface TunnelExecutorGetter {
  TunnelExecutor getTunnelExecutor(RequestTunnel tunnel);

  String infoStr();

  MappingIdentity getMappingIdentity();
}
