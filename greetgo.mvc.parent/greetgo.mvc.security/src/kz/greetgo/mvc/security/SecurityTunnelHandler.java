package kz.greetgo.mvc.security;

import kz.greetgo.mvc.jetty.interfaces.RequestTunnel;
import kz.greetgo.mvc.jetty.interfaces.TunnelHandler;

public class SecurityTunnelHandler implements TunnelHandler {

  private final TunnelHandler whatWrapping;

  public SecurityTunnelHandler(TunnelHandler whatWrapping) {
    this.whatWrapping = whatWrapping;
  }

  @Override
  public void handleTunnel(RequestTunnel tunnel) {



  }
}
