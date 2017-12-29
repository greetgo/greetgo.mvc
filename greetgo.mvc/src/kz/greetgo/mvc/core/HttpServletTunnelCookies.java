package kz.greetgo.mvc.core;

import kz.greetgo.mvc.interfaces.AbstractTunnelCookies;

import javax.servlet.http.Cookie;
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
  public Cookie[] getRequestCookies() {
    return request.getCookies();
  }

  @Override
  public void addCookieToResponse(Cookie cookie) {
    response.addCookie(cookie);
  }
}
