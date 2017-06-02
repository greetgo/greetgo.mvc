package kz.greetgo.mvc.interfaces;

import kz.greetgo.mvc.annotations.AsIs;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.mvc.annotations.ToXml;

/**
 * Результаты выполнения метода контроллера
 */
public interface MethodInvokedResult {
  /**
   * Предоставляет доступ к объекту, возвращённому из метода контроллера
   *
   * @return объект, возвращённый из метода контроллера
   */
  Object returnedValue();

  /**
   * Предоставляет доступ к ошибке исполнения метода контроллера
   *
   * @return ошибка исполнения метода контроллера, или <code>null</code> если небыло ни какой ошибки
   */
  Throwable error();

  /**
   * <p>
   * Попытка выполнить рендеринг по умолчанию результатов метода контроллера
   * </p>
   * <p>
   * Рендеринг по умолчанию - это либо редирект, либо обработка аннотаций: {@link ToJson}, {@link ToXml}, {@link AsIs}
   * </p>
   *
   * @return признак успешности рендеринга по умолчанию: <code>true</code> - значит рендеринг по умолчанию
   * удался; <code>false</code> - рендеринг не удался - необходим
   * собственный рендеринг, например рендеринг JSP-страницы
   */
  boolean tryDefaultRender();
}
