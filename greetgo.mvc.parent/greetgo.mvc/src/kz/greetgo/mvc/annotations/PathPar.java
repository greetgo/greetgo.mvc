package kz.greetgo.mvc.annotations;

import java.lang.annotation.*;

@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PathPar {
  String value();
}
