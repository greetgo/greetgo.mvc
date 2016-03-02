package kz.greetgo.mvc.interfaces;

public interface TunnelCookies {
  String getFromRequestStr(String name);

  void saveToResponseStr(String name, String value);

  void removeFromResponse(String name);

  <T> T getFromResponse(String name);

  void saveToResponse(String name, Object object);
}
