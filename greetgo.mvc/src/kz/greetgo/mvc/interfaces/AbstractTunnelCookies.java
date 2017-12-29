package kz.greetgo.mvc.interfaces;

import kz.greetgo.mvc.util.CookieUtil;

import javax.servlet.http.Cookie;

public abstract class AbstractTunnelCookies implements TunnelCookies {

  private String getCookieValue(String cookieName) {
    Cookie[] cookies = getRequestCookies();
    if (cookies == null) return null;
    for (Cookie cookie : cookies) {
      if (cookie.getName().equals(cookieName)) {
        return cookie.getValue();
      }
    }
    return null;
  }

  @Override
  public CookieRequestGetter name(String name) {

    final String value = getCookieValue(name);

    return new CookieRequestGetter() {
      @Override
      public <T> T object() {
        return CookieUtil.strToObject(value);
      }

      @Override
      public String value() {
        return value;
      }
    };
  }

  @Override
  public CookieResponseSaver forName(String name) {

    final Cookie cookie = new Cookie(name, null);

    return new CookieResponseSaver() {
      @Override
      public void saveValue(String value) {
        cookie.setValue(value);
        addCookieToResponse(cookie);
      }

      @Override
      public void saveObject(Object object) {
        cookie.setValue(CookieUtil.objectToStr(object));
        addCookieToResponse(cookie);
      }

      @Override
      public CookieResponseSaver maxAge(int maxAge) {
        cookie.setMaxAge(maxAge);
        return this;
      }

      @Override
      public CookieResponseSaver secure(boolean secure) {
        cookie.setSecure(secure);
        return this;
      }

      @Override
      public CookieResponseSaver path(String path) {
        cookie.setPath(path);
        return this;
      }

      @Override
      public CookieResponseSaver version(int version) {
        cookie.setVersion(version);
        return this;
      }

      @Override
      public CookieResponseSaver httpOnly(boolean httpOnly) {
        cookie.setHttpOnly(httpOnly);
        return this;
      }

      @Override
      public CookieResponseSaver domain(String domain) {
        cookie.setDomain(domain);
        return this;
      }

      @Override
      public CookieResponseSaver comment(String comment) {
        cookie.setComment(comment);
        return this;
      }
    };
  }
}
