package kz.greetgo.mvc.war;

import kz.greetgo.mvc.interfaces.MvcTrace;
import kz.greetgo.mvc.interfaces.TunnelHandler;
import kz.greetgo.mvc.security.SecurityCrypto;
import kz.greetgo.mvc.security.SecurityProvider;
import kz.greetgo.mvc.security.SecurityTunnelWrapper;
import kz.greetgo.mvc.security.SessionStorage;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.EnumSet;

public abstract class SecurityFilter implements Filter {
  public static final String ATTRIBUTE_TUNNEL = "kz.greetgo.mvc.security.TUNNEL";

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
  public void doFilter(final ServletRequest request, final ServletResponse response,
                       final FilterChain chain) throws IOException, ServletException {

    if (trace != null) trace.trace("SF hew3jbd6s344h Started security doFilter");

    final WarRequestTunnel tunnel = new WarRequestTunnel(request, response);

    final TunnelHandler chainHandler = handlerTunnel -> {
      try {
        if (trace != null) trace.trace("SF kdm4sde1urt started chainHandler");
        request.setAttribute(ATTRIBUTE_TUNNEL, handlerTunnel);
        chain.doFilter(request, response);
        if (trace != null) trace.trace("SF nsg3r4gD finished chainHandler");
      } catch (ServletException | IOException e) {
        if (trace != null) trace.trace("SF gds5vgh4ewg", e);
        throw new ExceptionWrapper(e);
      }
    };

    final SecurityTunnelWrapper stw = createSecurityTunnelWrapper(chainHandler);

    try {
      stw.handleTunnel(tunnel);
      if (trace != null) trace.trace("SF gw263vrf2ex Finished security doFilter");
    } catch (ExceptionWrapper e) {
      if (trace != null) trace.trace("SF dbh1sbe2wr456e SecurityTunnelWrapper exception", e);

      if (e.wrappedException instanceof IOException) throw (IOException) e.wrappedException;
      if (e.wrappedException instanceof ServletException) throw (ServletException) e.wrappedException;
      if (e.wrappedException instanceof RuntimeException) throw (RuntimeException) e.wrappedException;
      throw e;
    }
  }

  protected SecurityTunnelWrapper createSecurityTunnelWrapper(TunnelHandler chainHandler) {
    return new SecurityTunnelWrapper(chainHandler, getProvider(),
      getSessionStorage(), getSessionCrypto(), getSignatureCrypto());
  }
}
