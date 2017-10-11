package kz.greetgo.mvc.core;

import kz.greetgo.mvc.interfaces.AbstractTunnelCookies;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestTunnelCookies extends AbstractTunnelCookies {

  public Cookie[] requestCookies;

  @Override
  public Cookie[] getRequestCookies() {
    return requestCookies;
  }

  public final List<Cookie> addedCookies = new ArrayList<>();
  public final Map<String, Cookie> addedCookiesMap = new HashMap<>();

  @Override
  public void addCookieToResponse(Cookie cookie) {
    addedCookies.add(cookie);
    addedCookiesMap.put(cookie.getName(), cookie);
  }
}
