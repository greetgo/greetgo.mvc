package kz.greetgo.mvc.annotations.on_methods;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Determine prefix of request address for request performing of controller marked by this annotation
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ControllerPrefix {
  /**
   * @return request address prefix
   */
  String[] value();
}
