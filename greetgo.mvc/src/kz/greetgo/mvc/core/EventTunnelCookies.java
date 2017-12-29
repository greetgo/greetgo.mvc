package kz.greetgo.mvc.core;

import kz.greetgo.mvc.interfaces.AbstractTunnelCookies;
import kz.greetgo.mvc.interfaces.TunnelCookies;
import kz.greetgo.util.events.EventHandler;
import kz.greetgo.util.events.EventHandlerList;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class EventTunnelCookies extends AbstractTunnelCookies implements EventHandler {
  private final TunnelCookies cookies;

  public EventTunnelCookies(TunnelCookies cookies, EventHandlerList beforeCompleteHeaders) {
    this.cookies = cookies;
    beforeCompleteHeaders.addEventHandler(this);
  }


  private final AtomicReference<List<Cookie>> cookieList =
    new AtomicReference<>(Collections.synchronizedList(new ArrayList<>()));

  @Override
  public void handle() {

    final List<Cookie> list = cookieList.getAndSet(null);

    if (list == null) return;

    for (Cookie cookie : list) {
      addCookieToResponse(cookie);
    }
  }

  @Override
  public Cookie[] getRequestCookies() {
    return cookies.getRequestCookies();
  }

  @Override
  public void addCookieToResponse(Cookie cookie) {
    List<Cookie> list = cookieList.get();
    if (list == null) {
      cookies.addCookieToResponse(cookie);
    } else {
      list.add(cookie);
    }
  }
}
