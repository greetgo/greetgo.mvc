package kz.greetgo.mvc.jetty.launchers;

import kz.greetgo.mvc.jetty.ControllerHandler;
import kz.greetgo.mvc.jetty.JettyControllerHandler;
import kz.greetgo.mvc.jetty.controllers.ControllerForJettyLauncherWithMvc1;
import kz.greetgo.mvc.jetty.controllers.ControllerForJettyLauncherWithMvc2;
import kz.greetgo.mvc.jetty.utils.MultipartInjectionHandler;
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
      String prj = "greetgo.depinject.mvc/";
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
      final List<ControllerHandler> controllerHandlerList = new ArrayList<>();
      controllerHandlerList.add(ControllerHandler.create(new ControllerForJettyLauncherWithMvc1(), views));
      controllerHandlerList.add(ControllerHandler.create(new ControllerForJettyLauncherWithMvc2(), views));

      MultipartInjectionHandler mih = new MultipartInjectionHandler();
      mih.setHandler(new JettyControllerHandler(controllerHandlerList));
      handlerList.addHandler(mih);
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
