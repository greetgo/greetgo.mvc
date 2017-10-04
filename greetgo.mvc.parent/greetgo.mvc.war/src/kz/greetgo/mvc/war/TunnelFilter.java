package kz.greetgo.mvc.war;

import kz.greetgo.mvc.interfaces.RequestTunnel;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

public abstract class TunnelFilter implements Filter {

  public static final String ATTRIBUTE_TUNNEL = "kz.greetgo.mvc.TUNNEL";

  public static RequestTunnel getTunnel(ServletRequest request, ServletResponse response, String targetSubContext) {
    {
      final Object tunnelFromAttribute = request.getAttribute(ATTRIBUTE_TUNNEL);
      if (tunnelFromAttribute != null) {
        WarRequestTunnel tunnel = (WarRequestTunnel) tunnelFromAttribute;
        tunnel.targetSubContext = targetSubContext;
        return tunnel;
      }
    }

    return new WarRequestTunnel(request, response, targetSubContext);
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {}

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    RequestTunnel tunnel = getTunnel(request, response, getTargetSubContext());
    TunnelFilterChain tunnelChain = () -> {
      try {
        chain.doFilter(request, response);
      } catch (IOException | ServletException e) {
        throw new ExceptionWrapper(e);
      }
    };

    try {
      filter(tunnel, tunnelChain);
    } catch (Exception e) {
      if (e instanceof IOException) throw (IOException) e;
      if (e instanceof ServletException) throw (ServletException) e;
      if (e instanceof RuntimeException) throw (RuntimeException) e;
      throw new RuntimeException(e);
    }
  }

  protected abstract void filter(RequestTunnel tunnel, TunnelFilterChain tunnelChain) throws Exception;

  @Override
  public void destroy() {}

  protected String getTargetSubContext() {
    return null;
  }
}
