package kz.greetgo.mvc.interfaces;

/**
 * Работает с атрибутами запроса
 */
public interface RequestAttributes {
  /**
   * Получить значение атрибута
   *
   * @param name имя атрибута
   * @param <T>  ожидаемый тип атрибута
   * @return значение атрибута
   */
  <T> T get(String name);

  /**
   * Установить новое значение атрибута
   *
   * @param name  имя атрибута
   * @param value новое значение атрибута
   */
  void set(String name, Object value);

  /**
   * Удалить атрибут из запроса
   *
   * @param name имя удаляемого атрибута
   */
  void remove(String name);
}
