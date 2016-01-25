package kz.greetgo.mvc.jetty;

import java.lang.annotation.*;

@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PathPar {
  String value();
}
