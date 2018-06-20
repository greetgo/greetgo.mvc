package kz.greetgo.mvc.annotations;

import java.lang.annotation.*;

/**
 * Shows that method returning object converts to JSON
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ToJson {}
