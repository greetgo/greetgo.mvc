package kz.greetgo.mvc.interfaces;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Доступ к заголовкам запроса
 */
public interface RequestHeaders {
  /**
   * Получение строкового значения по имени
   *
   * @param headerName имя заголовка
   * @return строковое значение
   */
  String value(String headerName);

  /**
   * Получение значения по имени в виде даты
   *
   * @param headerName имя заголовка
   * @return значение в виде даты
   */
  long asDate(String headerName);

  /**
   * Получение значения по имени в виде числа
   *
   * @param headerName имя заголовка
   * @return значение в виде числа
   */
  long asInt(String headerName);

  /**
   * <p>
   * Все значения указанного заголовка
   * </p>
   * <p>
   * Клиент может передавать несколько заголовков с одинаковым именем.
   * Данный метод предоставляет все значения этих заголовков
   * </p>
   *
   * @param headerName имя заголовка
   * @return все значения
   */
  default List<String> allValuesFor(String headerName) {
    List<String> ret = new ArrayList<>();
    Enumeration<String> e = allValuesForAsEnumeration(headerName);
    while (e.hasMoreElements()) ret.add(e.nextElement());
    return ret;
  }

  /**
   * <p>
   * Все значения указанного заголовка
   * </p>
   * <p>
   * Клиент может передавать несколько заголовков с одинаковым именем.
   * Данный метод предоставляет все значения этих заголовков
   * </p>
   *
   * @param headerName имя заголовка
   * @return все значения
   */
  Enumeration<String> allValuesForAsEnumeration(String headerName);

  /**
   * Предоставляет все имена заголовков
   *
   * @return все имена заголовков
   */
  default List<String> names() {
    List<String> ret = new ArrayList<>();
    Enumeration<String> e = namesAsEnumeration();
    while (e.hasMoreElements()) ret.add(e.nextElement());
    return ret;
  }

  /**
   * Предоставляет все имена заголовков
   *
   * @return все имена заголовков
   */
  Enumeration<String> namesAsEnumeration();

}
