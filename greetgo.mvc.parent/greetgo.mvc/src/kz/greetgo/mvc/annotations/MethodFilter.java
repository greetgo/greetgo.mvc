package kz.greetgo.mvc.annotations;

import kz.greetgo.mvc.core.RequestMethod;

import java.lang.annotation.*;

/**
 * Устанавливает фильтр по методам
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodFilter {
  /**
   * Список методов, которым соответствует фильтр. Пустой список приведёт к блокировки метода (метод вызываться не будет)
   *
   * @return список методов
   */
  RequestMethod[] value();
}
