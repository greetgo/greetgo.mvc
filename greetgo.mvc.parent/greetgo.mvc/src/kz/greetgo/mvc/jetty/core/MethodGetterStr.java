package kz.greetgo.mvc.jetty.core;

import kz.greetgo.mvc.jetty.interfaces.GetterStr;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodGetterStr implements GetterStr {

  private final Object object;
  private final String strReturnMethodName;

  public MethodGetterStr(Object object, String strReturnMethodName) {
    this.object = object;
    this.strReturnMethodName = strReturnMethodName;
  }

  private Method cachedMethod = null;

  @Override
  public String get() {
    try {
      if (cachedMethod == null) cachedMethod = object.getClass().getMethod(strReturnMethodName);
      return (String) cachedMethod.invoke(object);
    } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }
}
