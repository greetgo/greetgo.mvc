package kz.greetgo.depinject.mvc;

import java.util.ArrayList;
import java.util.List;

public class TunnelHandlerList implements TunnelHandler {

  private final List<TunnelHandler> list = new ArrayList<>();

  public void add(TunnelHandler tunnelHandler) {
    list.add(tunnelHandler);
  }

  @Override
  public boolean handleTunnel(RequestTunnel tunnel) {
    for (TunnelHandler tunnelHandler : list) {
      if (tunnelHandler.handleTunnel(tunnel)) return true;
    }
    return false;
  }
}
