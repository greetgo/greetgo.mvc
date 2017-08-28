package kz.greetgo.mvc.interfaces;

import kz.greetgo.mvc.util.CookieUtil;

public abstract class AbstractTunnelCookies implements TunnelCookies {

  @Override
  public void saveToResponse(String name, String value) {
    saveToResponse(name, -1, value);
  }

  @Override
  public <T> T getFromRequestObject(String name) {
    return CookieUtil.strToObject(getFromRequest(name));
  }

  @Override
  public void saveToResponseObject(String name, Object object) {
    saveToResponse(name, CookieUtil.objectToStr(object));
  }

  @Override
  public void saveToResponse(String name, String value, boolean httpOnly) {
    saveToResponse(name, -1, value, httpOnly);
  }
}
