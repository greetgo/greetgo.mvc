package kz.greetgo.mvc.annotations;

import java.lang.annotation.*;

/**
 * Определяет какой запрос соответствует этому методу по формату адресной строки
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapping {
  /**
   * Определяет формат адресной строки
   *
   * @return формат адресной строки
   */
  String value();
}
