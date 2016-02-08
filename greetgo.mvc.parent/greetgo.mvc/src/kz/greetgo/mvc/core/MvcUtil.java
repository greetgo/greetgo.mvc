package kz.greetgo.mvc.core;

import kz.greetgo.mvc.errors.CannotConvertToDate;
import kz.greetgo.mvc.errors.IllegalChar;
import kz.greetgo.mvc.errors.NoConverterFor;
import kz.greetgo.mvc.model.Redirect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.Collections.unmodifiableMap;

public class MvcUtil {

  public static Redirect extractRedirect(Throwable e, int deep) {
    for (int i = 0; i < deep; i++) {
      if (e instanceof Redirect) return (Redirect) e;
      e = e.getCause();
    }
    return null;
  }

  public static long amountBytesToLong(String amountBytes) {
    String tmp = amountBytes;

    if (tmp == null) return 0L;
    tmp = tmp.trim();
    if (tmp.length() == 0) return 0L;

    long sign = +1;
    if (tmp.startsWith("-")) {
      sign = -1;
      tmp = tmp.substring(1).trim();
    }

    char multiplicand = ' ';

    StringBuilder sb = new StringBuilder(tmp.length());
    for (int i = 0, n = tmp.length(); i < n; i++) {
      char c = tmp.charAt(i);
      if ('0' <= c && c <= '9') {
        sb.append(c);
        continue;
      }
      if (c == 'B' || c == 'b' || c == ' ' || c == '_') continue;
      if (multiplicand == ' ' && (c == 'K' || c == 'k' || c == 'M' || c == 'G')) {
        multiplicand = c;
        continue;
      }

      throw new IllegalChar(c, "amountBytesToLong(" + amountBytes + ")");
    }

    long value = Long.parseLong(sb.toString());
    if (multiplicand == 'K' || multiplicand == 'k') value *= 1024L;
    else if (multiplicand == 'M') value *= 1024L * 1024L;
    else if (multiplicand == 'G') value *= 1024L * 1024L * 1024L;

    return sign * value;
  }

  public static int amountBytesToInt(String amountBytes) {
    return (int) amountBytesToLong(amountBytes);
  }

  private interface Converter {
    Object convert(String str);
  }

  private static final String[] SIMPLE_DATE_FORMATS = {
    "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd",
    "dd.MM.yyyy HH:mm:ss", "dd.MM.yyyy HH:mm", "dd.MM.yyyy",
    "dd/MM/yyyy HH:mm:ss", "dd/MM/yyyy HH:mm", "dd/MM/yyyy",
  };

  private static final Map<Class<?>, Converter> CONVERTERS;

  static {
    Map<Class<?>, Converter> x = new HashMap<>();

    x.put(String.class, new Converter() {
      @Override
      public Object convert(String str) {
        if (str == null) return null;
        return str;
      }
    });

    x.put(Integer.TYPE, new Converter() {
      @Override
      public Object convert(String str) {
        if (str == null) return 0;
        if (str.length() == 0) return 0;
        return Integer.valueOf(str);
      }
    });

    x.put(Integer.class, new Converter() {
      @Override
      public Object convert(String str) {
        if (str == null) return null;
        if (str.length() == 0) return null;
        return Integer.valueOf(str);
      }
    });

    x.put(Long.TYPE, new Converter() {
      @Override
      public Object convert(String str) {
        if (str == null) return 0L;
        if (str.length() == 0) return 0L;
        return Long.valueOf(str);
      }
    });

    x.put(Long.class, new Converter() {
      @Override
      public Object convert(String str) {
        if (str == null) return null;
        if (str.length() == 0) return null;
        return Long.valueOf(str);
      }
    });

    x.put(Date.class, new Converter() {
      @Override
      public Object convert(String str) {
        if (str == null) return null;
        if (str.length() == 0) return null;

        for (String x : SIMPLE_DATE_FORMATS) {
          //noinspection EmptyCatchBlock
          try {
            return new SimpleDateFormat(x).parse(str);
          } catch (ParseException e) {
          }
        }

        throw new CannotConvertToDate(str);
      }
    });

    CONVERTERS = unmodifiableMap(x);
  }

  private static String first(String[] strs) {
    if (strs == null) return null;
    if (strs.length == 0) return null;
    return strs[0];
  }

  private static Object convertStrToClass(String str, Class<?> aClass) {
    final Converter converter = CONVERTERS.get(aClass);
    if (converter == null) throw new NoConverterFor(aClass);
    return converter.convert(str);
  }

  public static Object convertStrsToType(String[] strs, Type type) {
    if (type instanceof Class) return convertStrToClass(first(strs), (Class<?>) type);

    if (type instanceof ParameterizedType) return convertStrsToParameterizedType(strs, (ParameterizedType) type);

    throw new IllegalArgumentException("Cannot convert strings to " + type);
  }

  private static Object convertStrsToParameterizedType(String[] strs, ParameterizedType type) {

    final Class<?> rawType = (Class<?>) type.getRawType();

    if (Collection.class.isAssignableFrom(rawType)) {

      Collection collection = createEmptyInstanceFor(rawType);

      if (strs == null) return collection;

      for (String str : strs) {
        //noinspection unchecked
        collection.add(convertStrToType(str, type.getActualTypeArguments()[0]));
      }

      return collection;

    }

    throw new IllegalArgumentException("Cannot convert strings to " + type);
  }

  public static Object convertStrToType(String str, Type type) {
    if (type instanceof Class) return convertStrToClass(str, (Class<?>) type);

    throw new IllegalArgumentException("Cannot convert str [[" + str + "]] to " + type);
  }

  private static Collection createEmptyInstanceFor(Class<?> collectionType) {
    if (List.class.isAssignableFrom(collectionType)) return new ArrayList();
    if (Set.class.isAssignableFrom(collectionType)) return new HashSet();
    throw new IllegalArgumentException("Cannot create collection empty instance for " + collectionType);
  }
}
