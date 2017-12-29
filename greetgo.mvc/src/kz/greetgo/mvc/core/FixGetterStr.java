package kz.greetgo.mvc.core;

import kz.greetgo.mvc.interfaces.GetterStr;

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
