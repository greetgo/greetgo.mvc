package kz.greetgo.mvc.annotations;

import java.lang.annotation.*;

/**
 * Указывает, что данный параметр соответствует входящему потоку запроса
 */
@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestInput {
}
