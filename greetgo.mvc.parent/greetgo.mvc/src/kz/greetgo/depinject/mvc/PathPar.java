package kz.greetgo.depinject.mvc;

import java.lang.annotation.*;

@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PathPar {
  String value();
}
