package kz.greetgo.mvc.annotations;

import java.lang.annotation.*;

/**
 * Indicate request parameter following to parameter of controller method
 */
@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Par {
  /**
   * @return request parameter name
   */
  String value();
}
