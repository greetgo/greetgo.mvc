package kz.greetgo.mvc.jetty;

import kz.greetgo.mvc.jetty.interfaces.TunnelHandler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JettyWrapperOfTunnelHandler extends AbstractHandler {
  private final TunnelHandler whatWrapping;

  public JettyWrapperOfTunnelHandler(TunnelHandler whatWrapping) {
    this.whatWrapping = whatWrapping;
  }

  @Override
  public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
    whatWrapping.handleTunnel(new JettyRequestTunnel(target, baseRequest, request, response));
  }
}
