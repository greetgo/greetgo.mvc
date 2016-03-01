package kz.greetgo.mvc.annotations;

import java.lang.annotation.*;

/**
 * Заполняет параметр значением из кукисов. Предварительно преобразует значение в соответствующий тип.
 */
@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ParCookie {
  /**
   * Указывает имя кукиса для параметра
   *
   * @return значение параметра из кукиса
   */
  String value();

  /**
   * <p>
   * Если равен <code>false</code>, то параметр вначале переводиться в соответствующий тип посредством
   * метода {@link kz.greetgo.mvc.util.CookieUtil#strToObject(String)}
   * </p>
   * <p>
   * Если равен <code>true</code>, то указывает, что параметр должан браться без преобразований (как есть).
   * Это поле может быть <code>true</code> только для аргументов типа <code>String</code>
   * </p>
   *
   * @return использовать как есть - <code>true</code>; или нет - <code>false</code>
   */
  boolean asIs() default false;
}
