package kz.greetgo.mvc.core;

import kz.greetgo.mvc.interfaces.AbstractTunnelCookies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestTunnelCookies extends AbstractTunnelCookies {

  public String getRequestCookieValue_return;
  public String getRequestCookieValue_name;

  @Override
  public String getFromRequest(String name) {
    getRequestCookieValue_name = name;
    return getRequestCookieValue_return;
  }

  public final List<String> calls = new ArrayList<>();
  public final Map<String, String> savedCookies = new HashMap<>();
  public final Map<String, Integer> savedMaxAges = new HashMap<>();

  @Override
  public void saveToResponse(String name, int maxAge, String value) {
    savedCookies.put(name, value);
    savedMaxAges.put(name, maxAge);
    calls.add("saveToResponseStr (maxAge " + maxAge + ") " + name + ' ' + value);
  }

  @Override
  public void removeFromResponse(String name) {
    calls.add("removeFromResponse " + name);
  }

  @Override
  public void saveToResponse(String name, int maxAge, String value, boolean httpOnly) {
    savedCookies.put(name, value);
    savedMaxAges.put(name, maxAge);
    calls.add("saveToResponseStr (maxAge " + maxAge + ") " + name + ' ' + value + " httpOnly " + httpOnly);
  }
}
