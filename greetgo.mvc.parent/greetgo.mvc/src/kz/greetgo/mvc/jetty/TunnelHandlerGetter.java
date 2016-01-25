package kz.greetgo.mvc.jetty;

public interface TunnelHandlerGetter {
  TunnelHandler getTunnelHandler(RequestTunnel tunnel);
}
