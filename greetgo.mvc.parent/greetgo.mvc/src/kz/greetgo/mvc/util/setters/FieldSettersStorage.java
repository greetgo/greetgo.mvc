package kz.greetgo.mvc.util.setters;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FieldSettersStorage {

  private static final Map<Class<?>, FieldSetters> cache = new ConcurrentHashMap<>();

  public static FieldSetters getFor(Class<?> aClass) {

    {
      FieldSetters x = cache.get(aClass);
      if (x != null) return x;
    }

    synchronized (cache) {
      {
        FieldSetters x = cache.get(aClass);
        if (x != null) return x;
      }

      {
        FieldSetters x = FieldSettersCreator.extractFrom(aClass);
        cache.put(aClass, x);
        return x;
      }
    }


  }

}
