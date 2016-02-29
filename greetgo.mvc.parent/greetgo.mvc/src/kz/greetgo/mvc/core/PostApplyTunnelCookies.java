package kz.greetgo.mvc.core;

import kz.greetgo.mvc.interfaces.TunnelCookies;

import java.util.ArrayList;
import java.util.List;

public class PostApplyTunnelCookies implements TunnelCookies {

  private final TunnelCookies target;

  public PostApplyTunnelCookies(TunnelCookies target) {
    this.target = target;
  }

  @Override
  public String getRequestCookieValue(String name) {
    return target.getRequestCookieValue(name);
  }

  private interface Command {
    void apply();
  }

  private List<Command> commandList = new ArrayList<>();

  private class SaveCommand implements Command {

    final String name;
    final String value;

    public SaveCommand(String name, String value) {
      this.name = name;
      this.value = value;
    }

    @Override
    public void apply() {
      target.saveCookieToResponse(name, value);
    }
  }

  @Override
  public void saveCookieToResponse(String name, String value) {
    if (commandList == null) {
      target.saveCookieToResponse(name, value);
    } else {
      commandList.add(new SaveCommand(name, value));
    }
  }

  private class RemoveCommand implements Command {

    final String name;

    RemoveCommand(String name) {
      this.name = name;
    }

    @Override
    public void apply() {
      target.removeCookieFromResponse(name);
    }
  }

  @Override
  public void removeCookieFromResponse(String name) {
    if (commandList == null) {
      target.removeCookieFromResponse(name);
    } else {
      commandList.add(new RemoveCommand(name));
    }
  }

  public void apply() {
    if (commandList == null) return;
    for (Command command : commandList) {
      command.apply();
    }
    commandList = null;
  }
}
