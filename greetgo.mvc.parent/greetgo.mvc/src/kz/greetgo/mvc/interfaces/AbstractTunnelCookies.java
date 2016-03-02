package kz.greetgo.mvc.interfaces;

import kz.greetgo.mvc.util.CookieUtil;

public abstract class AbstractTunnelCookies implements TunnelCookies {
  public abstract String getFromRequestStr(String name);

  public abstract void saveToResponseStr(String name, String value);

  public abstract void removeFromResponse(String name);

  @Override
  public <T> T getFromResponse(String name) {
    return CookieUtil.strToObject(getFromRequestStr(name));
  }

  @Override
  public void saveToResponse(String name, Object object) {
    saveToResponseStr(name, CookieUtil.objectToStr(object));
  }
}
