package kz.greetgo.mvc.annotations;

import java.lang.annotation.*;

/**
 * Показывает что передаётся JSON-формат
 */
@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Json {
}
