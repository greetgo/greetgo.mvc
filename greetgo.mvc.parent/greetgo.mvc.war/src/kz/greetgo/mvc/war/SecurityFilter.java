package kz.greetgo.mvc.war;

import kz.greetgo.mvc.interfaces.MvcTrace;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.mvc.interfaces.TunnelHandler;
import kz.greetgo.mvc.security.SecurityCrypto;
import kz.greetgo.mvc.security.SecurityProvider;
import kz.greetgo.mvc.security.SecurityTunnelWrapper;
import kz.greetgo.mvc.security.SessionStorage;

import javax.servlet.DispatcherType;
import javax.servlet.FilterConfig;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.EnumSet;

public abstract class SecurityFilter extends TunnelFilter {
  @Override
  public void init(FilterConfig filterConfig) throws ServletException {}

  @Override
  public void destroy() {}

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
  protected void filter(RequestTunnel tunnel, TunnelFilterChain tunnelChain) throws Exception {
    trace("SF hew3jbd6s344h Started security doFilter");

    final TunnelHandler chainHandler = handlerTunnel -> {
      try {
        trace("SF kdm4sde1urt started chainHandler");
        tunnelChain.goChain();
        trace("SF nsg3r4gD finished chainHandler");
      } catch (RuntimeException e) {
        trace("SF gds5vgh4ewg", e);
        throw e;
      }
    };

    final SecurityTunnelWrapper stw = new SecurityTunnelWrapper(chainHandler, getProvider(),
      getSessionStorage(), getSessionCrypto(), getSignatureCrypto());

    try {
      stw.handleTunnel(tunnel);
      trace("SF gw263vrf2ex Finished security doFilter");
    } catch (ExceptionWrapper e) {
      trace("SF dbh1sbe2wr456e SecurityTunnelWrapper exception", e);
      throw e.wrappedException;
    }
  }

  private void trace(Object message, Exception e) {
    MvcTrace trace = SecurityFilter.trace;
    if (trace == null) return;
    trace.trace(message, e);
  }

  private static void trace(Object message) {
    MvcTrace trace = SecurityFilter.trace;
    if (trace == null) return;
    trace.trace(message);
  }
}
