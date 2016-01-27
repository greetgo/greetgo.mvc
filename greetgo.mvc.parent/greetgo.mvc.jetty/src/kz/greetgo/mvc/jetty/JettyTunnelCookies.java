package kz.greetgo.mvc.jetty;

import kz.greetgo.mvc.jetty.interfaces.TunnelCookies;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JettyTunnelCookies implements TunnelCookies {
  private final HttpServletRequest request;
  private final HttpServletResponse response;

  public JettyTunnelCookies(HttpServletRequest request, HttpServletResponse response) {
    this.request = request;
    this.response = response;
  }

  @Override
  public String getRequestCookieValue(String name) {
    return CookieUtil.getCookieValue(request, name);
  }

  @Override
  public void saveCookieToResponse(String name, String value) {
    CookieUtil.addCookie(response, name, value);
  }

  @Override
  public void removeCookieFromResponse(String name) {
    CookieUtil.removeCookie(response, name);
  }
}
