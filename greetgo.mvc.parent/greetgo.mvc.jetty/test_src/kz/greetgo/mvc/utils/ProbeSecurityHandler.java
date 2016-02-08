package kz.greetgo.mvc.utils;

import kz.greetgo.mvc.util.CookieUtil;
import kz.greetgo.mvc.security.SessionStorage;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ProbeSecurityHandler extends AbstractHandler {
  private final Handler inner;
  private final SessionStorage sessionStorage;

  public ProbeSecurityHandler(Handler inner, SessionStorage sessionStorage) {
    this.inner = inner;
    this.sessionStorage = sessionStorage;
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
