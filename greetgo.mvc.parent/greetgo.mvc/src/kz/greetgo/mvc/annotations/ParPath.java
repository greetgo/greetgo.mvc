package kz.greetgo.mvc.annotations;

import java.lang.annotation.*;

/**
 * Заполняет параметр значением из адресной строки
 */
@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ParPath {
  /**
   * Указывает какой параметр из мапинга использовать
   *
   * @return имя параметра при мапинге адресной строки
   */
  String value();
}
