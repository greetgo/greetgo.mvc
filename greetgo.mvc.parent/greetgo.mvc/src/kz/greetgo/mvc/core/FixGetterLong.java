package kz.greetgo.mvc.core;

import kz.greetgo.mvc.interfaces.GetterLong;

public class FixGetterLong implements GetterLong {
  private final long value;

  public FixGetterLong(long value) {
    this.value = value;
  }

  @Override
  public long get() {
    return value;
  }
}
