package kz.greetgo.mvc.util.setters;

import kz.greetgo.mvc.annotations.SkipParameter;
import kz.greetgo.mvc.util.MvcUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import static kz.greetgo.util.ServerUtil.notNull;

public class FieldSettersCreator {

  public static FieldSetters extractFrom(Class<?> aClass) {
    final Map<String, FieldSetter> setterMap = new HashMap<>();
    Set<String> skips = new HashSet<>();

    appendFieldSetters(setterMap, skips, aClass);

    appendSettersOnSetMethods(setterMap, skips, aClass);

    appendSettersOnGetMethods(setterMap, skips, aClass);

    for (String key : skips) {
      setterMap.remove(key);
    }

    return new FieldSetters() {
      @Override
      public Iterator<FieldSetter> iterator() {
        return setterMap.values().iterator();
      }

      @Override
      public Set<String> names() {
        return setterMap.keySet();
      }

      @Override
      public FieldSetter get(String name) {
        return notNull(setterMap.get(name));
      }
    };
  }

  private static void appendFieldSetters(Map<String, FieldSetter> setterMap, Set<String> skips, Class<?> aClass) {
    for (Field field : aClass.getFields()) {
      String name = field.getName();

      if (field.getAnnotation(SkipParameter.class) != null) {
        skips.add(name);
        continue;
      }

      {
        FieldSetter setter = getterToSetter(name, field.getGenericType(), object -> {
          try {
            return field.get(object);
          } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
          }
        });
        if (setter != null) {
          setterMap.put(name, setter);
          continue;
        }
      }

      if (Modifier.isFinal(field.getModifiers())) continue;

      {
        FieldSetter setter = fieldToSetter(field);
        setterMap.put(setter.name(), setter);
      }
    }
  }

  private static FieldSetter fieldToSetter(Field field) {
    return new FieldSetter() {
      @Override
      public String name() {
        return field.getName();
      }

      @Override
      public void setFromStrings(Object destination, String[] strValues) {
        Type genericType = field.getGenericType();
        Object value = MvcUtil.convertStringsToType(strValues, genericType);
        try {
          field.set(destination, value);
        } catch (IllegalAccessException e) {
          throw new RuntimeException(e);
        }
      }
    };
  }

  private static String restToName(String restName) {
    if (restName.length() == 1) {
      return restName.toLowerCase();
    }
    {
      return restName.substring(0, 1).toLowerCase() + restName.substring(1);
    }
  }

  private interface Getter {
    Object get(Object object);
  }

  private static void appendSettersOnGetMethods(Map<String, FieldSetter> setterMap, Set<String> skips, Class<?> aClass) {
    for (Method method : aClass.getMethods()) {
      if (method.getParameterTypes().length > 0) continue;
      String methodName = method.getName();
      if (!methodName.startsWith("get")) continue;
      String restName = methodName.substring(3);
      if (restName.length() == 0) continue;
      final String name = restToName(restName);
      if (method.getAnnotation(SkipParameter.class) != null) {
        skips.add(name);
        continue;
      }

      FieldSetter setter = getterToSetter(name, method.getGenericReturnType(), object -> {
        try {
          return method.invoke(object);
        } catch (IllegalAccessException | InvocationTargetException e) {
          throw new RuntimeException(e);
        }
      });
      if (setter != null) setterMap.put(name, setter);
    }
  }

  private static FieldSetter getterToSetter(String name, Type type, Getter getter) {

    if (!(type instanceof ParameterizedType)) return null;
    ParameterizedType parameterizedType = (ParameterizedType) type;
    final Class<?> rawType = (Class<?>) parameterizedType.getRawType();
    if (!Collection.class.isAssignableFrom(rawType)) return null;

    final Type argumentType = parameterizedType.getActualTypeArguments()[0];

    return new FieldSetter() {
      @Override
      public String name() {
        return name;
      }

      @Override
      public void setFromStrings(Object destination, String[] strValues) {
        final Collection collection = (Collection) getter.get(destination);
        collection.clear();
        if (strValues != null) for (String strValue : strValues) {
          //noinspection unchecked
          collection.add(MvcUtil.convertStrToType(strValue, argumentType));
        }
      }
    };
  }

  private static void appendSettersOnSetMethods(Map<String, FieldSetter> setterMap, Set<String> skips, Class<?> aClass) {

    for (Method method : aClass.getMethods()) {
      if (method.getParameterTypes().length != 1) continue;
      String methodName = method.getName();
      if (!methodName.startsWith("set")) continue;
      String restName = methodName.substring(3);
      final String name = restToName(restName);
      if (method.getAnnotation(SkipParameter.class) != null) {
        skips.add(name);
        continue;
      }

      final Type type = method.getGenericParameterTypes()[0];

      setterMap.put(name, new FieldSetter() {
        @Override
        public String name() {
          return name;
        }

        @Override
        public void setFromStrings(Object destination, String[] strValues) {
          try {
            method.invoke(destination, MvcUtil.convertStringsToType(strValues, type));
          } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
          }
        }
      });
    }

  }
}
