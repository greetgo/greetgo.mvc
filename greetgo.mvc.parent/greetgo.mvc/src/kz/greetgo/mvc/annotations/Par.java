package kz.greetgo.mvc.annotations;

import java.lang.annotation.*;

/**
 * Заполняет параметр значением из параметров запроса
 */
@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Par {
  /**
   * Указывает имя параметра запроса
   *
   * @return имя параметра запроса
   */
  String value();
}
