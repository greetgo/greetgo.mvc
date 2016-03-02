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
  public String getFromRequestStr(String name) {
    return cookies.getFromRequestStr(name);
  }

  interface Command {
    void apply();
  }

  private List<Command> commandList = new ArrayList<>();

  class Save implements Command {
    private final String name;
    private final String value;

    public Save(String name, String value) {
      this.name = name;
      this.value = value;
    }

    @Override
    public void apply() {
      cookies.saveToResponseStr(name, value);
    }
  }

  @Override
  public void saveToResponseStr(String name, String value) {
    if (commandList == null) {
      cookies.saveToResponseStr(name, value);
    } else {
      commandList.add(new Save(name, value));
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
