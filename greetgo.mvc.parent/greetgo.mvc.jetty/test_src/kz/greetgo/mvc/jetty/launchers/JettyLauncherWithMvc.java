package kz.greetgo.mvc.jetty.launchers;

import kz.greetgo.mvc.jetty.ControllerTunnelHandlerBuilder;
import kz.greetgo.mvc.jetty.JettyControllerHandler;
import kz.greetgo.mvc.jetty.MultipartConf;
import kz.greetgo.mvc.jetty.TunnelHandlerGetter;
import kz.greetgo.mvc.jetty.controllers.ControllerForJettyLauncherWithMvc1;
import kz.greetgo.mvc.jetty.controllers.ControllerForJettyLauncherWithMvc2;
import kz.greetgo.mvc.jetty.utils.ProbeViews;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
      final List<TunnelHandlerGetter> controllerHandlerList = new ArrayList<>();
      controllerHandlerList.addAll(ControllerTunnelHandlerBuilder.build(new ControllerForJettyLauncherWithMvc1(), views));
      controllerHandlerList.addAll(ControllerTunnelHandlerBuilder.build(new ControllerForJettyLauncherWithMvc2(), views));

      MultipartConf multipartConf = new MultipartConf();
      multipartConf.fileSizeThreshold = 1000;
      handlerList.addHandler(new JettyControllerHandler(controllerHandlerList, multipartConf));
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
