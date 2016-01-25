package kz.greetgo.depinject.mvc.utils;

import java.lang.reflect.Method;

public class TestUtil {
  public static Method getMethod(Class<?> aClass, String methodName) {
    for (Method method : aClass.getMethods()) {
      if (method.getName().equals(methodName)) return method;
    }
    throw new IllegalArgumentException("No method " + methodName + " in " + aClass);
  }

}
