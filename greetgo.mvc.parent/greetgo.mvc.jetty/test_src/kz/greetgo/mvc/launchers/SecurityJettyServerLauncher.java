package kz.greetgo.mvc.launchers;

import kz.greetgo.mvc.JettyWrapperOfTunnelHandler;
import kz.greetgo.mvc.TunnelHandlerWrapperOfJetty;
import kz.greetgo.mvc.controllers.LoginController;
import kz.greetgo.mvc.core.ControllerTunnelExecutorBuilder;
import kz.greetgo.mvc.core.ExecutorListHandler;
import kz.greetgo.mvc.core.TunnelHandlerList;
import kz.greetgo.mvc.interfaces.TunnelExecutorGetter;
import kz.greetgo.mvc.utils.ProbeViews;
import kz.greetgo.mvc.utils.UserDetailsStorage;
import kz.greetgo.mvc.security.*;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;

import javax.xml.ws.Endpoint;
import java.io.File;
import java.util.List;

public class SecurityJettyServerLauncher {

  public static void main(String[] args) throws Exception {
    new SecurityJettyServerLauncher().run();
  }

  private SecurityJettyServerLauncher() {
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

    UserDetailsStorage userDetailsStorage = new UserDetailsStorage();

    {
      final ProbeViews views = new ProbeViews();
      LoginController loginController = new LoginController(userDetailsStorage);
      final List<TunnelExecutorGetter> executorList = ControllerTunnelExecutorBuilder.build(loginController, views);
      tunnelHandlerList.list.add(new ExecutorListHandler(executorList));
    }

    SecurityCrypto sessionCrypto, signatureCrypto;

    {
      String dir = "build/JettyServerLauncherKeys/";
      final File sessionPriKey = new File(dir + "session.pri.key");
      final File sessionPubKey = new File(dir + "session.pub.key");
      final File signaturePriKey = new File(dir + "signature.pri.key");
      final File signaturePubKey = new File(dir + "signature.pub.key");

      final SecuritySource sessionSS = new SecuritySource_RSA_SHA256(sessionPriKey, sessionPubKey);
      final SecuritySource signatureSS = new SecuritySource_RSA_SHA256(signaturePriKey, signaturePubKey);

      sessionCrypto = new SecurityCryptoBridge(sessionSS);
      signatureCrypto = new SecurityCryptoBridge(signatureSS);
    }

    {
      Server server = new Server(8080);

      server.setHandler(new JettyWrapperOfTunnelHandler(new SecurityTunnelWrapper(
        tunnelHandlerList, securityProvider, userDetailsStorage, sessionCrypto, signatureCrypto
      )));

      server.start();
      server.join();

      Endpoint e;

      JaxWsProxyFactoryBean d;
    }
  }

  private final SecurityProvider securityProvider = new SecurityProvider() {
    @Override
    public String cookieKeySession() {
      return "GGS";
    }

    @Override
    public String cookieKeySignature() {
      return "GGC";
    }

    @Override
    public boolean skipSession(String target) {
      //noinspection RedundantIfStatement
      if (target.startsWith("/img/")) return true;
      return false;
    }

    @Override
    public boolean isUnderSecurityUmbrella(String target) {
      if (target.startsWith("/login")) return false;
      //noinspection RedundantIfStatement
      return true;
    }

    @Override
    public String redirectOnSecurityError(String target) {
      return "/login.html";
    }

  };
}
