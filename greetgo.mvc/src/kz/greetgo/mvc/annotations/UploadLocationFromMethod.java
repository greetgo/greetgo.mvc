package kz.greetgo.mvc.annotations;

import java.lang.annotation.*;

/**
 * This annotation can
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UploadLocationFromMethod {
  String value();
}
