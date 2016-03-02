package kz.greetgo.mvc.core;

import kz.greetgo.mvc.interfaces.AbstractTunnelCookies;
import kz.greetgo.mvc.interfaces.TunnelCookies;
import kz.greetgo.mvc.util.CookieUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestTunnelCookies extends AbstractTunnelCookies {

  public String getRequestCookieValue_return;
  public String getRequestCookieValue_name;

  @Override
  public String getFromRequestStr(String name) {
    getRequestCookieValue_name = name;
    return getRequestCookieValue_return;
  }

  public final List<String> calls = new ArrayList<>();
  public final Map<String, String> savedCookies = new HashMap<>();

  @Override
  public void saveToResponseStr(String name, String value) {
    savedCookies.put(name, value);
    calls.add("saveToResponseStr " + name + ' ' + value);
  }

  @Override
  public void removeFromResponse(String name) {
    calls.add("removeFromResponse " + name);
  }
}
