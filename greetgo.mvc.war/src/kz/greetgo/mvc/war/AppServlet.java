package kz.greetgo.mvc.war;

import kz.greetgo.mvc.builder.ExecDefinition;
import kz.greetgo.mvc.builder.RequestProcessingBuilder;
import kz.greetgo.mvc.interfaces.RequestProcessing;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.mvc.interfaces.Views;
import kz.greetgo.mvc.model.UploadInfo;

import javax.servlet.GenericServlet;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.util.List;

public abstract class AppServlet extends GenericServlet {

  protected abstract List<Object> getControllerList();

  protected abstract Views getViews();

  protected abstract UploadInfo getUploadInfo();

  protected String getAddingServletName() {
    return "appServlet";
  }

  protected boolean checkControllerMappersConflicts() {
    return true;
  }

  private RequestProcessing requestProcessing;

  public List<ExecDefinition> execDefinitionList() {
    return requestProcessing.execDefinitionList();
  }

  protected ServletRegistration.Dynamic servletRegistration;

  @SuppressWarnings("SameParameterValue")
  public void register(ServletContext ctx, String mappingBase) {

    requestProcessing = RequestProcessingBuilder
      .newBuilder(getViews())
      .setCheckControllerMappersConflicts(checkControllerMappersConflicts())
      .with(builder -> getControllerList().forEach(builder::addController))
      .build();

    servletRegistration = ctx.addServlet(getAddingServletName(), this);
    {
      if (mappingBase == null) {
        mappingBase = getTargetSubContext() + "/*";
      }
      servletRegistration.addMapping(mappingBase);
    }

    {
      UploadInfo ui = getUploadInfo();
      if (ui != null) {
        servletRegistration.setMultipartConfig(
          new MultipartConfigElement(ui.location, ui.maxFileSize, ui.maxRequestSize, ui.fileSizeThreshold)
        );
      }
    }

    afterRegister();
  }

  protected void afterRegister() {}

  public void register(ServletContext ctx) {
    register(ctx, null);
  }

  @Override
  public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {

    try {

      requestProcessing.processRequest(getTunnel(req, res));

    } catch (ServletException | IOException | RuntimeException e) {
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

  protected String getTargetSubContext() {
    return "";
  }
}
