package kz.greetgo.mvc.jetty;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtil {

  public static String getCookieValue(HttpServletRequest request, String cookieName) {
    for (Cookie cookie : request.getCookies()) {
      if (cookie.getName().equals(cookieName)) {
        return cookie.getValue();
      }
    }
    return null;
  }

  public static void removeCookie(HttpServletResponse response, String name) {
    final Cookie cookie = new Cookie(name, null);
    cookie.setMaxAge(0);
//    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setSecure(false);
    response.addCookie(cookie);
  }

  public static void addCookie(HttpServletResponse response, String cookieName, String cookieValue) {
    final Cookie cookie = new Cookie(cookieName, cookieValue);
    cookie.setMaxAge(60 * 60 * 24);
    cookie.setSecure(false);
    cookie.setPath("/");
    response.addCookie(cookie);
  }
}
