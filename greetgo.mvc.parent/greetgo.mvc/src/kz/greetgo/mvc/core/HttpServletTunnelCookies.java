package kz.greetgo.mvc.core;

import kz.greetgo.mvc.interfaces.AbstractTunnelCookies;
import kz.greetgo.mvc.util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HttpServletTunnelCookies extends AbstractTunnelCookies {
  private final HttpServletRequest request;
  private final HttpServletResponse response;

  public HttpServletTunnelCookies(HttpServletRequest request, HttpServletResponse response) {
    this.request = request;
    this.response = response;
  }

  @Override
  public String getFromRequest(String name) {
    return CookieUtil.getCookieValue(request, name);
  }

  @Override
  public void saveToResponse(String name, int maxAge, String value) {
    CookieUtil.addCookie(response, maxAge, request.getContextPath(), name, value);
  }

  @Override
  public void removeFromResponse(String name) {
    CookieUtil.removeCookie(response, name);
  }
}
