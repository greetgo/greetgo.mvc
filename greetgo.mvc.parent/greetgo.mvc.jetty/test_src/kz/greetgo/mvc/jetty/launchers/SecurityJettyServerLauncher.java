package kz.greetgo.mvc.jetty.launchers;

import kz.greetgo.mvc.jetty.JettyWrapperOfTunnelHandler;
import kz.greetgo.mvc.jetty.TunnelHandlerWrapperOfJetty;
import kz.greetgo.mvc.jetty.core.TunnelHandlerList;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;

import java.io.File;

public class SecurityJettyServerLauncher {

  public static void main(String[] args) throws Exception {
    new SecurityJettyServerLauncher().run();
  }

  public SecurityJettyServerLauncher() {
  }


  private void run() throws Exception {

    TunnelHandlerList tunnelHandlerList = new TunnelHandlerList();

    {
      String warDir = "test_war_security";
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

      tunnelHandlerList.list.add(new TunnelHandlerWrapperOfJetty(resourceHandler));

    }

    {
      Server server = new Server(8080);

      server.setHandler(new JettyWrapperOfTunnelHandler(tunnelHandlerList));

      server.start();
      server.join();
    }
  }


}
