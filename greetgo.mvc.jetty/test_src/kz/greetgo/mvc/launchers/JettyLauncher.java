package kz.greetgo.mvc.launchers;

import kz.greetgo.mvc.utils.MultipartInjectionHandler;
import kz.greetgo.mvc.utils.MyHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;

import java.io.File;

public class JettyLauncher {

  public static void main(String[] args) throws Exception {

    String warDir = "test_war";
    {
      String prj = "greetgo.mvc.jetty/";
      if (new File(prj).isDirectory()) {
        warDir = prj + warDir;
      }
    }

    ResourceHandler resourceHandler = new ResourceHandler();
    resourceHandler.setDirectoriesListed(true);
    resourceHandler.setWelcomeFiles(new String[]{"index.html"});
    resourceHandler.setResourceBase(warDir);

    MultipartInjectionHandler mih = new MultipartInjectionHandler();
    mih.setHandler(new MyHandler());

    HandlerList handlerList = new HandlerList();
    handlerList.addHandler(mih);
    handlerList.addHandler(resourceHandler);

    {
      Server server = new Server(8080);

      server.setHandler(handlerList);

      server.start();
      server.join();
    }

    System.out.println("Hello world!!!");

  }

}
