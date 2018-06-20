package kz.greetgo.mvc.annotations;

import java.lang.annotation.*;

/**
 * Shows that parameter value converts from JSON
 */
@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Json {}
