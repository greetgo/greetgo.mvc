package kz.greetgo.mvc.interfaces;

import kz.greetgo.mvc.model.MvcModelData;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Исполнитель метода контроллера. Позволяет получить доступ к различным данным, окружающим метод контроллера.
 * Также позволяет выполнить метод контроллера, и обработать результаты выполнения метода контроллера.
 */
public interface MethodInvoker {
  /**
   * @return Возвращает тунель запроса
   */
  RequestTunnel tunnel();

  /**
   * @return Возвращает метод контроллера
   */
  Method method();

  /**
   * Вспомогательный метод, для удобного получения аннотаций метода
   *
   * @param annotation запрашиваемая аннотация
   * @param <T>        внутренний тип аннотации
   * @return значение аннотации
   */
  <T extends Annotation> T getMethodAnnotation(Class<T> annotation);

  /**
   * @return Возвращает модель данных для рендеринга страницы запроса
   */
  MvcModelData model();

  /**
   * @return Возвращает результат мапинга запроса
   */
  MappingResult mappingResult();

  /**
   * @return Возвращает ссылку на сам контроллер
   */
  Object controller();

  /**
   * Производить запуск метода контроллера и предоставляет доступ к результатам отработки метода
   *
   * @return доступ к результатам обработки метода
   */
  MethodInvokedResult invoke();
}
