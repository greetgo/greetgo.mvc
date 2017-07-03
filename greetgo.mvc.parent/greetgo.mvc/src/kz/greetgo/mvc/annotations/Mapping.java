package kz.greetgo.mvc.annotations;

import java.lang.annotation.*;

/**
 * Determine format of request address for request performing
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapping {
  /**
   * @return format of request address
   */
  String[] value();
}
