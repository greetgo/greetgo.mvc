package kz.greetgo.mvc.jetty.core;

import kz.greetgo.mvc.jetty.interfaces.GetterLong;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AmountMethodGetter implements GetterLong {

  private final Object controller;
  private final String methodName;

  private GetterLong delegate = null;

  public AmountMethodGetter(Object controller, String methodName) {
    this.controller = controller;
    this.methodName = methodName;
  }

  @Override
  public long get() {
    if (delegate == null) try {
      createDelegate();
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
    return delegate.get();
  }

  private void createDelegate() throws NoSuchMethodException {
    final Method method = controller.getClass().getMethod(methodName);
    final Class<?> returnType = method.getReturnType();

    if (returnType == Integer.TYPE || returnType == Integer.class) {
      delegate = createIntDelegate(method);
      return;
    }

    if (returnType == Long.TYPE || returnType == Long.class) {
      delegate = createLongDelegate(method);
      return;
    }

    if (returnType == String.class) {
      delegate = createStringDelegate(method);
      return;
    }

    throw new IllegalArgumentException("Cannot read amount of bytes from " + returnType);
  }

  private GetterLong createIntDelegate(final Method method) {
    return new GetterLong() {
      @Override
      public long get() {
        try {
          final Object ret = method.invoke(controller);
          if (ret == null) return 0L;
          return ((Integer) ret).longValue();
        } catch (IllegalAccessException | InvocationTargetException e) {
          throw new RuntimeException(e);
        }
      }
    };
  }

  private GetterLong createLongDelegate(final Method method) {
    return new GetterLong() {
      @Override
      public long get() {
        try {
          final Object ret = method.invoke(controller);
          if (ret == null) return 0L;
          return (Long) ret;
        } catch (IllegalAccessException | InvocationTargetException e) {
          throw new RuntimeException(e);
        }
      }
    };
  }

  private GetterLong createStringDelegate(final Method method) {
    return new GetterLong() {
      @Override
      public long get() {
        try {
          final Object ret = method.invoke(controller);
          if (ret == null) return 0L;
          return MvcUtil.amountBytesToLong((String) ret);
        } catch (IllegalAccessException | InvocationTargetException e) {
          throw new RuntimeException(e);
        }
      }
    };
  }

}
