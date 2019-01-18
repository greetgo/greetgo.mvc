package kz.greetgo.mvc.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JsonUtil {

  public static Object convertStrToType(String str, Type parameterType) {
    return convertStrsToType(new String[]{str}, parameterType);
  }

  public static Object convertStrsToType(String[] strArray, Type parameterType) {
    try {

      if (parameterType instanceof Class) {
        return convertToClass(strArray, (Class<?>) parameterType);
      }

      if (parameterType instanceof ParameterizedType) {
        return convertToParameterizedType(strArray, (ParameterizedType) parameterType);
      }

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    throw new IllegalArgumentException("Cannot convert json to type: " + parameterType);
  }

  private static Object convertToClass(String[] strArray, Class<?> aClass) throws Exception {
    if (strArray == null) return null;
    if (strArray.length == 0) return null;
    if (strArray[0] == null) return null;

    String str = strArray[0].trim();

    if (str.length() == 0) return null;

    if (str.startsWith("[")) {
      List list = new ArrayList();
      appendToList(list, str, aClass);
      if (list.size() == 0) return null;
      return list.get(0);
    }

    return directConvertToClass(aClass, str);
  }

  private static Object directConvertToClass(Class<?> aClass, String str) throws Exception {
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.readValue(str, aClass);
  }

  @SuppressWarnings("unchecked")
  private static void appendToList(List list, String str, Class<?> aClass) throws Exception {
    CollectionType collectionType = TypeFactory.defaultInstance().constructCollectionType(List.class, aClass);

    ObjectMapper objectMapper = new ObjectMapper();

    addCollection(list, (Collection) objectMapper.readValue(str, collectionType));
  }

  private static Object convertToParameterizedType(String[] strArray, ParameterizedType parameterType) throws Exception {

    if (parameterType.getRawType() == List.class) {
      return convertToListOfClass(parameterType.getActualTypeArguments()[0], strArray);
    }

    throw new IllegalArgumentException("Cannot convert json to type: " + parameterType);
  }

  private static Object convertToListOfClass(Type type, String[] strArray) throws Exception {

    if (type instanceof Class) {
      List<Object> list = new ArrayList<>();
      appendToListOfClass(list, (Class) type, strArray);
      return list;
    }

    if (type instanceof WildcardType) {
      System.out.println("wow");

      WildcardType wildcardType = (WildcardType) type;

      if (wildcardType.getUpperBounds().length == 1) {
        Type upperBound = wildcardType.getUpperBounds()[0];
        if (upperBound instanceof Class) {
          Class upperBoundClass = (Class) upperBound;
          List<Object> list = new ArrayList<>();
          appendToListOfClass(list, upperBoundClass, strArray);
          return list;
        }
      }
    }

    throw new IllegalArgumentException("Cannot convert json to list of type: " + type);
  }

  private static void appendToListOfClass(List<Object> list, Class aClass, String[] strArray) throws Exception {
    CollectionType collectionType = TypeFactory.defaultInstance().constructCollectionType(List.class, aClass);

    ObjectMapper objectMapper = new ObjectMapper();

    for (String strOrig : strArray) {
      if (strOrig == null) continue;
      String str = strOrig.trim();
      if (str.length() == 0) continue;

      if (str.startsWith("[")) {
        addCollection(list, (Collection) objectMapper.readValue(str, collectionType));
      } else {
        list.add(objectMapper.readValue(str, aClass));
      }

    }
  }

  @SuppressWarnings("unchecked")
  private static void addCollection(List<Object> list, Collection collection) {
    list.addAll(collection);
  }
}
