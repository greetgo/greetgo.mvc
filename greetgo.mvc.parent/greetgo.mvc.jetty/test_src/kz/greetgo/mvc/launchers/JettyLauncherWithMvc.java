package kz.greetgo.mvc.launchers;

import kz.greetgo.mvc.controllers.ControllerForJettyLauncherWithMvc1;
import kz.greetgo.mvc.controllers.ControllerForJettyLauncherWithMvc2;
import kz.greetgo.mvc.core.ControllerTunnelExecutorBuilder;
import kz.greetgo.mvc.JettyControllerHandler;
import kz.greetgo.mvc.utils.ProbeViews;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

import java.io.File;

public class JettyLauncherWithMvc {

  public static void main(String[] args) throws Exception {

    String warDir = "test_war";
    {
      String prj = "greetgo.mvc.jetty/";
      if (new File(prj).isDirectory()) {
        warDir = prj + warDir;
      }
    }

    HandlerList handlerList = new HandlerList();

    {
      ResourceHandler resourceHandler = new ResourceHandler();
      resourceHandler.setDirectoriesListed(true);
      resourceHandler.setWelcomeFiles(new String[]{"index.html"});
      resourceHandler.setResourceBase(warDir);
      handlerList.addHandler(resourceHandler);
    }
    {
      final ProbeViews views = new ProbeViews();

      final ControllerForJettyLauncherWithMvc1 c1 = new ControllerForJettyLauncherWithMvc1();
      final ControllerForJettyLauncherWithMvc2 c2 = new ControllerForJettyLauncherWithMvc2();

      final JettyControllerHandler controllerHandler = new JettyControllerHandler();

      controllerHandler.tunnelExecutorGetters.addAll(ControllerTunnelExecutorBuilder.build(c1, views));
      controllerHandler.tunnelExecutorGetters.addAll(ControllerTunnelExecutorBuilder.build(c2, views));

      handlerList.addHandler(controllerHandler);
    }

    {
      Server server = new Server(8080);

      server.setHandler(handlerList);

      server.start();
      server.join();
    }

    System.out.println("Hello world!!!");

  }

}
