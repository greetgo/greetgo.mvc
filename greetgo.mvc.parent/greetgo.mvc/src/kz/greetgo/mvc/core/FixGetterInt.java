package kz.greetgo.mvc.core;

import kz.greetgo.mvc.interfaces.GetterInt;

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
