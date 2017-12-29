package kz.greetgo.mvc.interfaces;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Предоставление унифицированного доступа к параметрам запроса
 */
public interface RequestParams {
  /**
   * Предоставляет все значения указанного параметра
   *
   * @param name имя параметра
   * @return все значения указанного параметра или пустой список (null-а здесь не возвращается)
   */
  default List<String> values(String name) {
    String[] array = asArray(name);
    List<String> ret = new ArrayList<>();
    if (array != null) Collections.addAll(ret, array);
    return ret;
  }

  /**
   * Предоставляет все значения указанного параметра
   *
   * @param name имя параметра
   * @return массив значений
   */
  String[] asArray(String name);

  /**
   * Предоставляет одно значение указанного параметра
   *
   * @param name имя параметра
   * @return значение параметра
   */
  default String value(String name) {
    List<String> list = values(name);
    return list.size() == 0 ? null : list.get(0);
  }

  /**
   * Предоставляет список имён параметров
   *
   * @return список имён параметров
   */
  default List<String> names() {
    List<String> ret = new ArrayList<>();
    Enumeration<String> e = nameAsEnumeration();
    while (e.hasMoreElements()) ret.add(e.nextElement());
    return ret;
  }

  /**
   * Предоставляет список имён параметров
   *
   * @return список имён параметров
   */
  Enumeration<String> nameAsEnumeration();

  /**
   * Предоставляет все параметры
   *
   * @return все параметры
   */
  default Map<String, List<String>> all() {
    return names().stream()
      .map(name -> new AbstractMap.SimpleEntry<>(name, values(name)))
      .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));
  }
}
