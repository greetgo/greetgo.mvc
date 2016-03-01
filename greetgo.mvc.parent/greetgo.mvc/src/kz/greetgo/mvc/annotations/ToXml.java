package kz.greetgo.mvc.annotations;

import java.lang.annotation.*;

/**
 * Указывает, что возвращённый этим методом объект должен быть преобразован в XML и положен в ответ запроса
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ToXml {
}
