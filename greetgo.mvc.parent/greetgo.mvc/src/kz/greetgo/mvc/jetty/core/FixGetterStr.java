package kz.greetgo.mvc.jetty.core;

import kz.greetgo.mvc.jetty.interfaces.GetterStr;

public class FixGetterStr implements GetterStr {
  private final String value;

  public FixGetterStr(String value) {
    this.value = value;
  }

  @Override
  public String get() {
    return value;
  }
}
