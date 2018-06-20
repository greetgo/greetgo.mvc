package kz.greetgo.mvc.annotations;

import java.lang.annotation.*;

/**
 * Send result of method <code>returnedFromControllerMethod.toString()</code> to response
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AsIs {}
