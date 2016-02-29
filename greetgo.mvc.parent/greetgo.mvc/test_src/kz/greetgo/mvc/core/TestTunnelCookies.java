package kz.greetgo.mvc.core;

import kz.greetgo.mvc.interfaces.TunnelCookies;

import java.util.ArrayList;
import java.util.List;

public class TestTunnelCookies implements TunnelCookies {

  public String getRequestCookieValue_return;
  public String getRequestCookieValue_name;

  @Override
  public String getRequestCookieValue(String name) {
    getRequestCookieValue_name = name;
    return getRequestCookieValue_return;
  }

  public final List<String> calls = new ArrayList<>();

  @Override
  public void saveCookieToResponse(String name, String value) {
    calls.add("saveCookieToResponse " + name + ' ' + value);
  }

  @Override
  public void removeCookieFromResponse(String name) {
    calls.add("removeCookieFromResponse " + name);
  }
}
