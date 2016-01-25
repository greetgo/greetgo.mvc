package kz.greetgo.depinject.mvc.error;

import java.lang.reflect.Method;

public class CannotExtractParamValue extends RuntimeException {
  public final int parameterIndex;
  public final Method method;

  public CannotExtractParamValue(int parameterIndex, Method method) {
    super("Cannot extract parameter value: parameterIndex: " + parameterIndex + " (zero based); method: "
      + method.toGenericString());
    this.parameterIndex = parameterIndex;
    this.method = method;
  }
}
