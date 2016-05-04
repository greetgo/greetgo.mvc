package kz.greetgo.mvc.war;

import kz.greetgo.mvc.core.ControllerTunnelExecutorBuilder;
import kz.greetgo.mvc.core.FileResourceTunnelExecutorGetter;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.mvc.interfaces.TunnelExecutor;
import kz.greetgo.mvc.interfaces.TunnelExecutorGetter;
import kz.greetgo.mvc.interfaces.Views;
import kz.greetgo.mvc.model.UploadInfo;

import javax.servlet.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AppServlet extends GenericServlet {

  protected abstract FileResourceTunnelExecutorGetter getFileResourceTunnelExecutorGetter();

  protected abstract List<Object> getControllerList();

  protected abstract Views getViews();

  protected abstract UploadInfo getUploadInfo();

  private final List<TunnelExecutorGetter> tunnelExecutorGetters = new ArrayList<>();

  protected String getAddingServletName() {
    return "appServlet";
  }

  public void register(ServletContext ctx, String mappingBase) {
    final Views views = getViews();
    for (Object controller : getControllerList()) {
      tunnelExecutorGetters.addAll(ControllerTunnelExecutorBuilder.build(controller, views));
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
  }

  public void register(ServletContext ctx) {
    register(ctx, null);
  }

  private TunnelExecutor getTunnelExecutor(RequestTunnel tunnel) {
    for (TunnelExecutorGetter tunnelExecutorGetter : tunnelExecutorGetters) {
      final TunnelExecutor te = tunnelExecutorGetter.getTunnelExecutor(tunnel);
      if (te != null) return te;
    }

    final FileResourceTunnelExecutorGetter fileResourceTunnelExecutorGetter = getFileResourceTunnelExecutorGetter();
    if (fileResourceTunnelExecutorGetter == null) return null;

    return fileResourceTunnelExecutorGetter.getTunnelExecutor(tunnel);
  }

  @Override
  public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {

    final RequestTunnel tunnel = getTunnel(req, res);

    final TunnelExecutor te = getTunnelExecutor(tunnel);

    if (te == null) throw new RuntimeException("Unknown target = " + tunnel.getTarget());

    te.execute();

  }

  private RequestTunnel getTunnel(ServletRequest req, ServletResponse res) {

    {
      final Object tunnelFromAttribute = req.getAttribute(SecurityFilter.ATTRIBUTE_TUNNEL);
      if (tunnelFromAttribute != null) return (RequestTunnel) tunnelFromAttribute;
    }

    return new WarRequestTunnel(req, res, getTargetSubContext());
  }

  protected String getTargetSubContext() {
    return "";
  }
}
