package kz.greetgo.mvc.jetty;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UploadInfoFromMethod {
  String value();
}
