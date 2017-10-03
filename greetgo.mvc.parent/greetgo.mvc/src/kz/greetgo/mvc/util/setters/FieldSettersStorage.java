package kz.greetgo.mvc.util.setters;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FieldSettersStorage {
  private static final Map<Class<?>, FieldSetters> cache = new ConcurrentHashMap<>();

  public static FieldSetters getFor(Class<?> aClass) {
    return cache.computeIfAbsent(aClass, FieldSettersCreator::create);
  }
}
