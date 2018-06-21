package kz.greetgo.mvc.war;

import kz.greetgo.mvc.core.ControllerTunnelExecutorBuilder;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.mvc.interfaces.TunnelExecutor;
import kz.greetgo.mvc.interfaces.TunnelExecutorGetter;
import kz.greetgo.mvc.interfaces.Views;
import kz.greetgo.mvc.model.UploadInfo;
import kz.greetgo.mvc.util.MvcUtil;

import javax.servlet.GenericServlet;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AppServlet extends GenericServlet {

  protected abstract List<Object> getControllerList();

  protected abstract Views getViews();

  protected abstract UploadInfo getUploadInfo();

  protected final List<TunnelExecutorGetter> tunnelExecutorGetters = new ArrayList<>();

  protected String getAddingServletName() {
    return "appServlet";
  }

  protected boolean checkControllerMappersConflicts() {
    return true;
  }

  @SuppressWarnings("SameParameterValue")
  public void register(ServletContext ctx, String mappingBase) {
    final Views views = getViews();
    for (Object controller : getControllerList()) {
      tunnelExecutorGetters.addAll(ControllerTunnelExecutorBuilder.build(controller, views));
    }

    if (checkControllerMappersConflicts()) {
      MvcUtil.checkTunnelExecutorGetters(tunnelExecutorGetters);
    }

    final ServletRegistration.Dynamic registration = ctx.addServlet(getAddingServletName(), this);
    {
      if (mappingBase == null) mappingBase = getTargetSubContext() + "/*";
      registration.addMapping(mappingBase);
    }

    {
      UploadInfo ui = getUploadInfo();
      if (ui != null) {
        registration.setMultipartConfig(new MultipartConfigElement(ui.location, ui.maxFileSize,
          ui.maxRequestSize, ui.fileSizeThreshold));
      }
    }

    afterRegister();
  }

  protected void afterRegister() {}

  public void register(ServletContext ctx) {
    register(ctx, null);
  }

  private TunnelExecutor getTunnelExecutor(RequestTunnel tunnel) {
    for (TunnelExecutorGetter tunnelExecutorGetter : tunnelExecutorGetters) {
      final TunnelExecutor te = tunnelExecutorGetter.getTunnelExecutor(tunnel);
      if (te != null) return te;
    }

    return null;
  }

  @Override
  public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {

    final RequestTunnel tunnel = getTunnel(req, res);

    final TunnelExecutor te = getTunnelExecutor(tunnel);

    try {

      if (te == null) {
        getViews().missedView(tunnel);
      } else {
        te.execute();
      }

    } catch (Exception e) {
      if (e instanceof ServletException) throw (ServletException) e;
      if (e instanceof IOException) throw (IOException) e;
      if (e instanceof RuntimeException) throw (RuntimeException) e;
      throw new RuntimeException(e);
    }
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

  protected String getTargetSubContext() {
    return "";
  }
}
