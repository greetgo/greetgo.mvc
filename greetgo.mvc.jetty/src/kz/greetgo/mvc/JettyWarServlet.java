package kz.greetgo.mvc;

import kz.greetgo.mvc.core.ControllerTunnelExecutorBuilder;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.mvc.interfaces.TunnelExecutor;
import kz.greetgo.mvc.interfaces.TunnelExecutorGetter;
import kz.greetgo.mvc.interfaces.Views;
import kz.greetgo.mvc.util.MvcUtil;
import kz.greetgo.mvc.war.SecurityFilter;
import kz.greetgo.mvc.war.WarRequestTunnel;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static kz.greetgo.mvc.util.MvcUtil.executeExecutor;

public abstract class JettyWarServlet extends DefaultServlet {

  protected abstract List<Object> getControllerList();

  protected abstract Views getViews();

  protected final List<TunnelExecutorGetter> tunnelExecutorGetters = new ArrayList<>();

  private TunnelExecutor getTunnelExecutor(RequestTunnel tunnel) {
    for (TunnelExecutorGetter tunnelExecutorGetter : tunnelExecutorGetters) {
      final TunnelExecutor te = tunnelExecutorGetter.getTunnelExecutor(tunnel);
      if (te != null) return te;
    }
    return null;
  }

  @SuppressWarnings("unused")
  public void registerTo(WebAppContext webAppContext) {

    final Views views = getViews();

    for (Object controller : getControllerList()) {
      tunnelExecutorGetters.addAll(ControllerTunnelExecutorBuilder.build(controller, views));
    }

    MvcUtil.checkTunnelExecutorGetters(tunnelExecutorGetters);

    webAppContext.addServlet(new ServletHolder(this), mappingBase());

    afterRegistered();
  }

  protected void afterRegistered() {
  }

  protected String mappingBase() {
    return getTargetSubContext() + "/*";
  }

  protected abstract String getTargetSubContext();

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    if (req.getDispatcherType() != DispatcherType.REQUEST) {
      super.service(req, resp);
      return;
    }

    final RequestTunnel tunnel = getTunnel(req, resp);

    final TunnelExecutor te = getTunnelExecutor(tunnel);

    if (te == null) {
      targetMissed(tunnel, req, resp);
    } else {
      executeExecutor(te);
    }
  }

  protected void targetMissed(RequestTunnel tunnel,
                              @SuppressWarnings("unused") HttpServletRequest req,
                              @SuppressWarnings("unused") HttpServletResponse resp)
    throws ServletException, IOException {
    getViews().missedView(tunnel);
  }


  private RequestTunnel getTunnel(ServletRequest req, ServletResponse res) {

    {
      final Object tunnelFromAttribute = req.getAttribute(SecurityFilter.ATTRIBUTE_TUNNEL);
      if (tunnelFromAttribute != null) {
        WarRequestTunnel tunnel = (WarRequestTunnel) tunnelFromAttribute;
        tunnel.targetSubContext = getTargetSubContext();
        return tunnel;
      }
    }

    return new WarRequestTunnel(req, res, getTargetSubContext());
  }
}
