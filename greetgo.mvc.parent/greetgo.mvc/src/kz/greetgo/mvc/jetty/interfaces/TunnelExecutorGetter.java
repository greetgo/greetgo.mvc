package kz.greetgo.mvc.jetty.interfaces;

public interface TunnelExecutorGetter {
  TunnelExecutor getTunnelExecutor(RequestTunnel tunnel);
}
