package kz.greetgo.mvc.war;

import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.mvc.interfaces.TunnelHandler;

import javax.servlet.*;
import java.io.IOException;

public class TunnelHandlerFilter implements Filter {
  private final TunnelHandler tunnelHandler;

  public TunnelHandlerFilter(TunnelHandler tunnelHandler) {
    this.tunnelHandler = tunnelHandler;
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    RequestTunnel tunnel = new WarRequestTunnel("target", request, response);
    tunnelHandler.handleTunnel(tunnel);
  }

  @Override
  public void destroy() {

  }
}
