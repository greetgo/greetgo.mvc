package kz.greetgo.mvc.annotations;

import kz.greetgo.mvc.core.RequestMethod;

import java.lang.annotation.*;

/**
 * Controller method filter
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodFilter {
  /**
   * List of HTTP-methods to perform. Empty list is blocking access to this controller method.
   *
   * @return list of HTTP-methods
   */
  RequestMethod[] value();
}
