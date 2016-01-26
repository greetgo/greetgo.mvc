package kz.greetgo.mvc.jetty.core;

import kz.greetgo.mvc.jetty.interfaces.GetterInt;

public class FixGetterInt implements GetterInt {
  private final int value;

  public FixGetterInt(int value) {
    this.value = value;
  }

  @Override
  public int get() {
    return value;
  }
}
