package kz.greetgo.mvc.interfaces;

import kz.greetgo.mvc.annotations.Json;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Controller method parameter context
 */
public interface ParameterContext {
  /**
   * Returns parameter name
   *
   * @return parameter name
   */
  String parameterName();

  /**
   * Returns type of parameter
   *
   * @return type of parameter
   */
  Type expectedReturnType();

  /**
   * Returns json annotation of parameter or <code>null</code>
   *
   * @return json annotation of parameter or <code>null</code>
   */
  Json json();

  /**
   * Returns method of controller called in current request
   *
   * @return method of controller called in current request
   */
  Method controllerMethod();
}
