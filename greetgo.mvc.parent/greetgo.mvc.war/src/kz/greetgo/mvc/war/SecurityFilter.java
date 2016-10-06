package kz.greetgo.mvc.war;

import kz.greetgo.mvc.interfaces.MvcTrace;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.mvc.interfaces.TunnelHandler;
import kz.greetgo.mvc.security.SecurityCrypto;
import kz.greetgo.mvc.security.SecurityProvider;
import kz.greetgo.mvc.security.SecurityTunnelWrapper;
import kz.greetgo.mvc.security.SessionStorage;

import javax.servlet.*;
import java.io.IOException;
import java.util.EnumSet;

public abstract class SecurityFilter implements Filter {
  public static final String ATTRIBUTE_TUNNEL = "kz.greetgo.mvc.security.TUNNEL";

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void destroy() {
  }

  protected String getAddingFilterName() {
    return "SecurityFilter";
  }

  public void register(ServletContext ctx) {
    FilterRegistration.Dynamic reg = ctx.addFilter(getAddingFilterName(), this);
    reg.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");
  }

  protected abstract SecurityCrypto getSignatureCrypto();

  protected abstract SecurityCrypto getSessionCrypto();

  protected abstract SessionStorage getSessionStorage();

  protected abstract SecurityProvider getProvider();

  public static MvcTrace trace;

  @Override
  public void doFilter(final ServletRequest request, final ServletResponse response,
                       final FilterChain chain) throws IOException, ServletException {

    if (trace != null) trace.trace("SF hewjbds344h");

    final WarRequestTunnel tunnel = new WarRequestTunnel(request, response);

    final TunnelHandler chainHandler = new TunnelHandler() {
      @Override
      public void handleTunnel(RequestTunnel tunnel) {
        try {
          if (trace != null) trace.trace("SF kdmsdeurt");
          request.setAttribute(ATTRIBUTE_TUNNEL, tunnel);
          chain.doFilter(request, response);
        } catch (ServletException | IOException e) {
          if (trace != null) trace.trace("SF gdsvghewg", e);
          throw new ExceptionWrapper(e);
        }
      }
    };

    final SecurityTunnelWrapper stw = new SecurityTunnelWrapper(chainHandler, getProvider(),
        getSessionStorage(), getSessionCrypto(), getSignatureCrypto());

    try {
      stw.handleTunnel(tunnel);
    } catch (ExceptionWrapper e) {
      if (trace != null) trace.trace("SF dbhsbewr456e", e);

      if (e.wrappedException instanceof IOException) throw (IOException) e.wrappedException;
      if (e.wrappedException instanceof ServletException) throw (ServletException) e.wrappedException;
      if (e.wrappedException instanceof RuntimeException) throw (RuntimeException) e.wrappedException;
      throw e;
    }
  }
}
