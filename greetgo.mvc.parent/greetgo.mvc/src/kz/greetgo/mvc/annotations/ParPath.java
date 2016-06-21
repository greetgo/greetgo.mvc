package kz.greetgo.mvc.annotations;

import java.lang.annotation.*;

/**
 * Takes value from request address
 */
@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ParPath {
  /**
   * @return parameter name in address mapping
   */
  String value();
}
