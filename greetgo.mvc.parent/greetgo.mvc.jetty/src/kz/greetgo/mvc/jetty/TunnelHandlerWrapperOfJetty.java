package kz.greetgo.mvc.jetty;

import kz.greetgo.mvc.jetty.interfaces.RequestTunnel;
import kz.greetgo.mvc.jetty.interfaces.TunnelHandler;
import org.eclipse.jetty.server.Handler;

import javax.servlet.ServletException;
import java.io.IOException;

public class TunnelHandlerWrapperOfJetty implements TunnelHandler {

  private final Handler whatWrapping;

  public TunnelHandlerWrapperOfJetty(Handler whatWrapping) {
    this.whatWrapping = whatWrapping;
  }

  @Override
  public void handleTunnel(RequestTunnel tunnel) {
    if (!(tunnel instanceof JettyRequestTunnel)) throw new IllegalArgumentException(getClass().getSimpleName()
      + " can work only with " + JettyRequestTunnel.class.getSimpleName());

    JettyRequestTunnel j = (JettyRequestTunnel) tunnel;

    try {
      whatWrapping.handle(j.target, j.baseRequest, j.request, j.response);
    } catch (IOException | ServletException e) {
      throw new RuntimeException(e);
    }
  }
}
