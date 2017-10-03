package kz.greetgo.mvc.util.setters;

import kz.greetgo.mvc.annotations.SkipParameter;
import kz.greetgo.mvc.errors.CannotInstantiateCollection;
import kz.greetgo.mvc.util.MvcUtil;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FieldSettersCreator {
  interface CollectionManager {
    Collection createInstance();

    Class<?> elementType();
  }

  private static final class TypeManager {
    public final Type type;

    public TypeManager(Type type) {
      this.type = type;
    }

    public Class<?> takeClass() {
      if (type instanceof Class) return (Class<?>) type;
      if (type instanceof ParameterizedType) {
        ParameterizedType parameterizedType = (ParameterizedType) type;
        return (Class<?>) parameterizedType.getRawType();
      }
      throw new RuntimeException("Cannot take class from " + type);
    }

    public boolean isCollection() {
      return Collection.class.isAssignableFrom(takeClass());
    }

    public Class<?> getElementType() {
      if (type instanceof ParameterizedType) {
        ParameterizedType parameterizedType = (ParameterizedType) type;
        Type[] args = parameterizedType.getActualTypeArguments();
        if (args.length != 1) throw new RuntimeException(type + " is not collection");
        return new TypeManager(args[0]).takeClass();
      }
      throw new RuntimeException("No element type in " + type);
    }

    public Class<?> parameterType() {
      return isCollection() ? getElementType() : takeClass();
    }
  }

  static CollectionManager createCollectionManager(AccessibleObject member) {
    return toCollectionManager(extractTypeManager(member), member);
  }

  private static CollectionManager toCollectionManager(TypeManager typeManager, AccessibleObject member) {
    if (!typeManager.isCollection()) return null;

    Class<?> memberClass = typeManager.takeClass();

    if (memberClass.isAssignableFrom(ArrayList.class)) {
      return new CollectionManager() {
        @Override
        public Collection createInstance() {
          return new ArrayList();
        }

        @Override
        public Class<?> elementType() {
          return typeManager.getElementType();
        }
      };
    }

    throw new CannotInstantiateCollection(memberClass, member);
  }

  private static TypeManager extractTypeManager(AccessibleObject member) {
    if (member instanceof Field) {
      Field field = (Field) member;
      return new TypeManager(field.getGenericType());
    }
    if (member instanceof Method) {
      Method method = (Method) member;
      Type[] types = method.getGenericParameterTypes();
      if (types.length == 0) {
        return new TypeManager(method.getGenericReturnType());
      }
      if (types.length == 1 && method.getName().startsWith("set")) {
        return new TypeManager(types[0]);
      }

    }
    throw new IllegalArgumentException("Cannot extract type manager from " + member);
  }

  public static FieldSetters create(Class<?> extractingClass) {
    return new Extractor(extractingClass).extract();
  }

  private interface Getter {
    Collection get(Object object);
  }

  private interface Setter {
    void set(Object destination, Object value);
  }

  private static class Extractor {

    private final Class<?> extractingClass;

    public Extractor(Class<?> extractingClass) {
      this.extractingClass = extractingClass;
    }

    final Map<String, Getter> getters = new HashMap<>();
    final Map<String, Setter> setters = new HashMap<>();
    final Map<String, CollectionManager> collectionManagers = new HashMap<>();
    final Map<String, Class<?>> types = new HashMap<>();
    final Set<String> skipped = new HashSet<>();
    final List<String> properties = new ArrayList<>();

    final Map<String, FieldSetter> realSetters = new HashMap<>();


    public FieldSetters extract() {
      fillGettersFromFields();
      fillGettersFromGetMethods();

      fillSettersFromFields();
      fillSettersFromSetMethods();

      fillRealSetters();

      //noinspection NullableProblems
      return new FieldSetters() {
        @Override
        public Set<String> names() {
          return realSetters.keySet();
        }

        @Override
        public FieldSetter get(String name) {
          FieldSetter ret = realSetters.get(name);
          if (ret == null) throw new RuntimeException("Cannot set " + name + " to " + extractingClass);
          return ret;
        }

        @Override
        public Iterator<FieldSetter> iterator() {
          return realSetters.values().iterator();
        }
      };
    }

    private void fillRealSetters() {
      for (String name : properties) {
        if (skipped.contains(name)) continue;

        Getter getter = getters.get(name);
        Setter setter = setters.get(name);
        CollectionManager collectionManager = collectionManagers.get(name);
        Class<?> type = types.get(name);
        if (type == null) throw new RuntimeException("No type for " + name + " in " + extractingClass);

        realSetters.put(name, new FieldSetter() {
          @Override
          public String name() {
            return name;
          }

          @Override
          public void setFromStrings(Object destination, String[] strValues) {

            if (collectionManager == null) {

              setter.set(destination, MvcUtil.convertStringsToType(strValues, type));

            } else {

              Collection collection = getter == null ? null : getter.get(destination);
              boolean needSet = collection == null;
              if (needSet) collection = collectionManager.createInstance();
              else collection.clear();

              for (String strValue : strValues) {
                //noinspection unchecked
                collection.add(MvcUtil.convertStrToType(strValue, type));
              }

              if (needSet) setter.set(destination, collection);
            }

          }
        });
      }
    }

    private void fillGettersFromGetMethods() {
      for (Method method : extractingClass.getMethods()) {
        if (!method.getName().startsWith("get")) continue;
        if (method.getParameterTypes().length > 0) continue;
        String name = restToName(method.getName().substring(3));
        if (method.getAnnotation(SkipParameter.class) != null) {
          skipped.add(name);
          continue;
        }
        TypeManager typeManager = new TypeManager(method.getGenericReturnType());
        if (!typeManager.isCollection()) continue;
        newProperty(name);
        collectionManagers.put(name, toCollectionManager(typeManager, method));
        types.put(name, typeManager.getElementType());
        getters.put(name, object -> {
          try {
            //noinspection unchecked
            return (Collection) method.invoke(object);
          } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
          }
        });
      }
    }

    private void newProperty(String name) {
      for (int i = 0; i < properties.size(); ) {
        if (name.equals(properties.get(i))) {
          properties.remove(i);
        } else {
          i++;
        }
      }
      properties.add(name);
    }

    private void fillGettersFromFields() {
      for (Field field : extractingClass.getFields()) {
        String name = field.getName();
        if (field.getAnnotation(SkipParameter.class) != null) {
          skipped.add(name);
          continue;
        }
        TypeManager typeManager = new TypeManager(field.getGenericType());
        if (!typeManager.isCollection()) continue;
        newProperty(name);
        collectionManagers.put(name, toCollectionManager(typeManager, field));
        types.put(name, typeManager.getElementType());
        getters.put(name, object -> {
          try {
            //noinspection unchecked
            return (Collection<Object>) field.get(object);
          } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
          }
        });
      }
    }

    private void fillSettersFromFields() {
      for (Field field : extractingClass.getFields()) {
        if (Modifier.isFinal(field.getModifiers())) continue;
        String name = field.getName();
        if (field.getAnnotation(SkipParameter.class) != null) {
          skipped.add(name);
          continue;
        }

        newProperty(name);
        TypeManager typeManager = new TypeManager(field.getGenericType());
        collectionManagers.put(name, toCollectionManager(typeManager, field));
        types.put(name, typeManager.parameterType());

        setters.put(name, (destination, value) -> {
          try {
            field.set(destination, value);
          } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
          }
        });
      }
    }

    private void fillSettersFromSetMethods() {
      for (Method method : extractingClass.getMethods()) {
        if (!method.getName().startsWith("set")) continue;
        Type[] methodTypes = method.getGenericParameterTypes();
        if (methodTypes.length != 1) continue;
        String name = restToName(method.getName().substring(3));
        if (method.getAnnotation(SkipParameter.class) != null) {
          skipped.add(name);
          continue;
        }

        newProperty(name);
        TypeManager typeManager = new TypeManager(methodTypes[0]);
        collectionManagers.put(name, toCollectionManager(typeManager, method));
        types.put(name, typeManager.parameterType());

        setters.put(name, (destination, value) -> {
          try {
            method.invoke(destination, value);
          } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
          }
        });
      }
    }
  }

  private static String restToName(String restName) {
    if (restName.length() == 1) {
      return restName.toLowerCase();
    }
    {
      return restName.substring(0, 1).toLowerCase() + restName.substring(1);
    }
  }
}