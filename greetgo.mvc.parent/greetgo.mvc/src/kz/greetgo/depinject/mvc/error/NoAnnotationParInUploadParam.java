package kz.greetgo.depinject.mvc.error;

import java.lang.reflect.Method;

public class NoAnnotationParInUploadParam extends RuntimeException {
  private final int parameterIndex;
  private final Method method;

  public NoAnnotationParInUploadParam(int parameterIndex, Method method) {
    super("No annotation Par in controller method parameter with type Upload: parameterIndex: " + parameterIndex +
      "; method: " + method.toGenericString());
    this.parameterIndex = parameterIndex;
    this.method = method;
  }
}
