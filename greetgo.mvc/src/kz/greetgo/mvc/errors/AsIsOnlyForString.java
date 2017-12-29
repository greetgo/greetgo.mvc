package kz.greetgo.mvc.errors;

import java.lang.reflect.Method;

public class AsIsOnlyForString extends RuntimeException {
  public final int parameterIndex;
  public final Method method;

  public AsIsOnlyForString(int parameterIndex, Method method) {
    super("parameterIndex = " + parameterIndex + ", method = " + method.toGenericString());
    this.parameterIndex = parameterIndex;
    this.method = method;
  }
}
