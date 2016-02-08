package kz.greetgo.mvc.interfaces;

public interface TunnelCookies {
  String getRequestCookieValue(String name);

  void saveCookieToResponse(String name, String value);

  void removeCookieFromResponse(String name);
}
