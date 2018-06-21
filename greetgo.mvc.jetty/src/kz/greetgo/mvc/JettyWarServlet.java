package kz.greetgo.mvc;

import kz.greetgo.mvc.builder.ExecDefinition;
import kz.greetgo.mvc.builder.RequestProcessingBuilder;
import kz.greetgo.mvc.interfaces.RequestProcessing;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.mvc.interfaces.Views;
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
import java.util.List;

public abstract class JettyWarServlet extends DefaultServlet {

  protected abstract List<Object> getControllerList();

  protected abstract Views getViews();

  protected boolean checkControllerMappersConflicts() {
    return true;
  }

  private RequestProcessing requestProcessing;

  public List<ExecDefinition> execDefinitionList() {
    return requestProcessing.execDefinitionList();
  }

  @SuppressWarnings("unused")
  public void registerTo(WebAppContext webAppContext) {

    requestProcessing = RequestProcessingBuilder
      .newBuilder(getViews())
      .setCheckControllerMappersConflicts(checkControllerMappersConflicts())
      .with(builder -> getControllerList().forEach(builder::addController))
      .build();

    webAppContext.addServlet(new ServletHolder(this), mappingBase());

    afterRegistered();
  }

  protected void afterRegistered() {}

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

    try {

      requestProcessing.processRequest(getTunnel(req, resp));

    } catch (IOException | ServletException | RuntimeException e) {
      throw e;
    } catch (Exception e) {
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
}
