package kz.greetgo.mvc.core;

import kz.greetgo.mvc.interfaces.TunnelHandler;
import kz.greetgo.mvc.interfaces.RequestTunnel;

import java.util.ArrayList;
import java.util.List;

public class TunnelHandlerList implements TunnelHandler {

  public List<TunnelHandler> list = new ArrayList<>();

  @Override
  public void handleTunnel(RequestTunnel tunnel) {
    if (tunnel.isExecuted()) return;
    for (TunnelHandler tunnelHandler : list) {
      tunnelHandler.handleTunnel(tunnel);
      if (tunnel.isExecuted()) return;
    }
  }
}
