package kz.greetgo.mvc.core;

import kz.greetgo.mvc.interfaces.AbstractTunnelCookies;
import kz.greetgo.mvc.interfaces.TunnelCookies;
import kz.greetgo.util.events.EventHandler;
import kz.greetgo.util.events.EventHandlerList;

import java.util.ArrayList;
import java.util.List;

public class EventTunnelCookies extends AbstractTunnelCookies implements EventHandler {
  private final TunnelCookies cookies;

  public EventTunnelCookies(TunnelCookies cookies, EventHandlerList beforeCompleteHeaders) {
    this.cookies = cookies;
    beforeCompleteHeaders.addEventHandler(this);
  }

  @Override
  public String getFromRequest(String name) {
    return cookies.getFromRequest(name);
  }

  interface Command {
    void apply();
  }

  private List<Command> commandList = new ArrayList<>();

  class Save implements Command {
    private final String name;
    private final int maxAge;
    private final String value;

    public Save(String name, int maxAge, String value) {
      this.name = name;
      this.maxAge = maxAge;
      this.value = value;
    }

    @Override
    public void apply() {
      cookies.saveToResponse(name, maxAge, value);
    }
  }

  @Override
  public void saveToResponse(String name, int maxAge, String value) {
    if (commandList == null) {
      cookies.saveToResponse(name, maxAge, value);
    } else {
      commandList.add(new Save(name, maxAge, value));
    }
  }

  class Remove implements Command {
    final String name;

    public Remove(String name) {
      this.name = name;
    }

    @Override
    public void apply() {
      cookies.removeFromResponse(name);
    }
  }

  @Override
  public void removeFromResponse(String name) {
    if (commandList == null) {
      cookies.removeFromResponse(name);
    } else {
      commandList.add(new Remove(name));
    }
  }

  @Override
  public void handle() {
    final List<Command> list;
    synchronized (this) {
      list = commandList;
      commandList = null;
    }

    if (list == null) return;

    for (Command command : list) {
      command.apply();
    }
  }

}
