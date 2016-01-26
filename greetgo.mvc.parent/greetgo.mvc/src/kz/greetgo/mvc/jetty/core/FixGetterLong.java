package kz.greetgo.mvc.jetty.core;

import kz.greetgo.mvc.jetty.interfaces.GetterLong;

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
