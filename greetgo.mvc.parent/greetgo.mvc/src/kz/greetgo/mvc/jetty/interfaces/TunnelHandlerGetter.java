package kz.greetgo.mvc.jetty.interfaces;

import kz.greetgo.mvc.jetty.interfaces.RequestTunnel;
import kz.greetgo.mvc.jetty.interfaces.TunnelHandler;

public interface TunnelHandlerGetter {
  TunnelHandler getTunnelHandler(RequestTunnel tunnel);
}
