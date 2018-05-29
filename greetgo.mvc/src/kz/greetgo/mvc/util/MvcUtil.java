package kz.greetgo.mvc.util;

import kz.greetgo.mvc.annotations.MethodFilter;
import kz.greetgo.mvc.core.MappingIdentity;
import kz.greetgo.mvc.core.RequestMethod;
import kz.greetgo.mvc.errors.CannotConvertToDate;
import kz.greetgo.mvc.errors.CompatibleTargetMapping;
import kz.greetgo.mvc.errors.DoublePathPar;
import kz.greetgo.mvc.errors.IllegalChar;
import kz.greetgo.mvc.errors.NoConverterFor;
import kz.greetgo.mvc.interfaces.TunnelExecutor;
import kz.greetgo.mvc.interfaces.TunnelExecutorGetter;
import kz.greetgo.mvc.model.Redirect;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Collections.unmodifiableMap;

public class MvcUtil {

  public static Redirect extractRedirect(Throwable e, int deep) {
    for (int i = 0; e != null && i < deep; i++) {
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

  public static <T> Class<T> typeToClass(Type type) {
    if (type instanceof Class<?>) return castType(type);
    if (type instanceof ParameterizedType) return castType(((ParameterizedType) type).getRawType());
    throw new IllegalArgumentException("Cannot convert type " + type + " to class");
  }

  @SuppressWarnings("unchecked")
  private static <T> Class<T> castType(Type type) {
    return (Class<T>) type;
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

    x.put(String.class, str -> str);

    {
      Converter converter = str -> {
        if (str == null) return null;
        str = str.trim().toLowerCase();

        if (str.length() == 0) return false;
        if ("false".equals(str)) return false;
        if ("f".equals(str)) return false;
        if ("off".equals(str)) return false;
        if ("no".equals(str)) return false;
        if ("n".equals(str)) return false;
        if ("0".equals(str)) return false;

        return true;
      };
      x.put(Boolean.TYPE, converter);
      x.put(Boolean.class, converter);
    }
    x.put(Integer.TYPE, str -> {
      if (str == null) return 0;
      if (str.length() == 0) return 0;
      return Integer.valueOf(str);
    });

    x.put(Integer.class, str -> {
      if (str == null) return null;
      if (str.length() == 0) return null;
      return Integer.valueOf(str);
    });

    x.put(Long.TYPE, str -> {
      if (str == null) return 0L;
      if (str.length() == 0) return 0L;
      return Long.valueOf(str);
    });

    x.put(Long.class, str -> {
      if (str == null) return null;
      if (str.length() == 0) return null;
      return Long.valueOf(str);
    });

    x.put(Date.class, str -> {
      if (str == null) return null;
      str = str.trim();
      if (str.length() == 0) return null;

      for (String x1 : SIMPLE_DATE_FORMATS) {
        //noinspection EmptyCatchBlock
        try {
          return new SimpleDateFormat(x1).parse(str);
        } catch (ParseException e) {
        }
      }

      throw new CannotConvertToDate(str);
    });

    x.put(BigDecimal.class, str -> {
      str = floating(str);
      return str == null ? null : new BigDecimal(str);
    });

    x.put(Double.class, str -> {
      str = floating(str);
      return str == null ? null : Double.valueOf(str);
    });

    x.put(Double.TYPE, str -> {
      str = floating(str);
      return str == null ? 0d : Double.valueOf(str);
    });

    x.put(Float.class, str -> {
      str = floating(str);
      return str == null ? null : Float.valueOf(str);
    });

    x.put(Float.TYPE, str -> {
      str = floating(str);
      return str == null ? 0f : Float.valueOf(str);
    });

    x.put(BigInteger.class, str -> {
      str = floating(str);
      return str == null ? null : new BigInteger(str);
    });

    CONVERTERS = unmodifiableMap(x);
  }

  private static String floating(String str) {
    if (str == null) return null;
    char chars[] = new char[str.length()];
    str.getChars(0, str.length(), chars, 0);
    StringBuilder sb = new StringBuilder(str.length());

    for (char c : chars) {
      if (c == ' ') continue;
      if (c == '\t') continue;
      if (c == '_') continue;
      if (c == ',') {
        sb.append('.');
        continue;
      }
      sb.append(c);
    }

    return sb.length() == 0 ? null : sb.toString();
  }

  private static String first(String[] strings) {
    if (strings == null) return null;
    if (strings.length == 0) return null;
    return strings[0];
  }

  private static Object convertStrToClass(String str, Class<?> aClass) {
    final Converter converter = CONVERTERS.get(aClass);
    if (converter == null) throw new NoConverterFor(aClass);
    return converter.convert(str);
  }

  public static Object convertStringsToType(String[] strings, Type type) {
    if (type instanceof Class) return convertStrToType(first(strings), type);

    if (type instanceof ParameterizedType) return convertStringsToParameterizedType(strings, (ParameterizedType) type);

    throw new IllegalArgumentException("Cannot convert strings to " + type);
  }

  private static Object convertStringsToParameterizedType(String[] strings, ParameterizedType type) {

    final Class<?> rawType = (Class<?>) type.getRawType();

    if (Collection.class.isAssignableFrom(rawType)) {

      Collection collection = createEmptyInstanceFor(rawType);

      if (strings == null) return collection;

      for (String str : strings) {
        //noinspection unchecked
        collection.add(convertStrToType(str, type.getActualTypeArguments()[0]));
      }

      return collection;

    }

    throw new IllegalArgumentException("Cannot convert strings to " + type);
  }

  public static Object convertStrToType(String str, Type type) {
    if (type instanceof Class) {
      Class<?> aClass = (Class<?>) type;
      if (aClass.isEnum()) return convertStrToEnum(str, aClass);
      return convertStrToClass(str, aClass);
    }

    throw new IllegalArgumentException("Cannot convert str [[" + str + "]] to " + type);
  }

  private static final Pattern ONLY_DIGITS = Pattern.compile("\\d+");

  private static Object convertStrToEnum(String str, Class<?> enumClass) {
    if (str == null) return null;
    if (str.isEmpty()) return null;


    try {

      if (ONLY_DIGITS.matcher(str).matches()) {
        Method methodValues = enumClass.getMethod("values");
        Object values = methodValues.invoke(null);
        return Array.get(values, Integer.parseInt(str));
      }

      Method valueOfMethod = enumClass.getMethod("valueOf", String.class);
      return valueOfMethod.invoke(null, str);
    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  public static Collection createEmptyInstanceFor(Class<?> collectionType) {
    if (List.class.isAssignableFrom(collectionType)) return new ArrayList();
    if (Set.class.isAssignableFrom(collectionType)) return new HashSet();
    throw new IllegalArgumentException("Cannot create collection empty instance for " + collectionType);
  }

  public static String readAll(Reader reader) {
    try {

      try {

        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[1024];
        while (true) {
          final int count = reader.read(buffer);
          if (count < 0) break;
          sb.append(buffer, 0, count);
        }
        return sb.toString();

      } finally {
        reader.close();
      }

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void executeExecutor(TunnelExecutor tunnelExecutor) throws ServletException, IOException {
    try {
      tunnelExecutor.execute();
    } catch (Exception e) {
      if (e instanceof ServletException) throw (ServletException) e;
      if (e instanceof IOException) throw (IOException) e;
      if (e instanceof RuntimeException) throw (RuntimeException) e;
      throw new RuntimeException(e);
    }
  }

  public static void checkTunnelExecutorGetters(List<TunnelExecutorGetter> tunnelExecutorGetters) {
    final int size = tunnelExecutorGetters.size();
    for (int i = 0; i < size; i++) {

      checkIncompatibilityOne(tunnelExecutorGetters.get(i));

      for (int j = i + 1; j < size; j++) {

        checkIncompatibility(tunnelExecutorGetters.get(i), tunnelExecutorGetters.get(j));

      }
    }
  }

  private static void checkIncompatibility(TunnelExecutorGetter teg1, TunnelExecutorGetter teg2) {
    MappingIdentity mi1 = teg1.getMappingIdentity();
    MappingIdentity mi2 = teg2.getMappingIdentity();

    if (!isCompatibles(mi1.methodFilter(), mi2.methodFilter())) return;

    String tm1 = mi1.targetMapping();
    String tm2 = mi2.targetMapping();

    if (!isTargetMappingDifferent(tm1, tm2)) {
      throw new CompatibleTargetMapping(teg1.infoStr(), teg2.infoStr());
    }
  }

  private final static Pattern DOUBLE_ASTERISK = Pattern.compile("\\*{2}");

  private static void checkIncompatibilityOne(TunnelExecutorGetter teg) {
    String targetMapping = teg.getMappingIdentity().targetMapping();

    if (DOUBLE_ASTERISK.matcher(targetMapping).find()) {
      throw new DoublePathPar(teg.infoStr());
    }
  }

  private static boolean isCompatibles(MethodFilter filter1, MethodFilter filter2) {
    if (filter1 == null) return true;
    if (filter2 == null) return true;

    if (filter1.value().length == 0) return false;
    if (filter2.value().length == 0) return false;

    Set<RequestMethod> set1 = Arrays.stream(filter1.value()).collect(Collectors.toSet());
    Set<RequestMethod> set2 = Arrays.stream(filter2.value()).collect(Collectors.toSet());

    set1.retainAll(set2);

    return set1.size() > 0;
  }

  private static final Random random = new Random();

  /*
   * Нужно вернуть true, если tm1 и tm2 обеспечивают РАЗНЫЕ точки входа
   */
  private static boolean isTargetMappingDifferent(String tm1, String tm2) {
    if (tm1 == null) tm1 = "";
    if (tm2 == null) tm2 = "";

    return random.nextBoolean();//Блин, не получается так угадывать!
  }
}
