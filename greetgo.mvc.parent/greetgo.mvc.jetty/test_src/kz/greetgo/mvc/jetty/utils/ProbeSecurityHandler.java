package kz.greetgo.mvc.jetty.utils;

import kz.greetgo.mvc.security.BytesSessionStorage;
import kz.greetgo.mvc.security.BytesStorage;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ProbeSecurityHandler extends AbstractHandler {
  private final Handler inner;
  private final BytesStorage bytesStorage;

  public ProbeSecurityHandler(Handler inner, BytesStorage bytesStorage) {
    this.inner = inner;
    this.bytesStorage = bytesStorage;
  }

  @Override
  public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
    final String ggs = CookieUtil.getCookieValue(request, "GGS");


    if (target.equals("/login") || target.equals("/login.html") || target.startsWith("/img/")) {
      inner.handle(target, baseRequest, request, response);
      return;
    }


    if ("OK".equals(ggs)) {

      if ("/logout".equals(target)) {
        CookieUtil.removeCookie(response, "GGS");
        response.sendRedirect("/login");
        return;
      }

      inner.handle(target, baseRequest, request, response);
      return;
    }
  }
}
