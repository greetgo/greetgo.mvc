package kz.greetgo.mvc.annotations;

import java.lang.annotation.*;

/**
 * Indicates that this parameter takes request input stream
 */
@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestInput {}
