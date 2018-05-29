package kz.greetgo.mvc.util;

public class TargetMapperComparator {

  private static String trimAsterisk(String tm) {
    if (tm == null) return "";
    int i1 = tm.indexOf('*');
    int i2 = tm.lastIndexOf('*');
    if (i1 == i2) return tm;
    return tm.substring(0, i1) + tm.substring(i2);
  }

  /**
   * Проверка точек входа REST-контроллеров на возможность путанницы (TRUE - это OK)
   *
   * @param tm1 одна точка входа (ТОЧНО известно, что подряд двух * нет)
   * @param tm2 вторая точка входа (ТОЧНО известно, что подряд двух * нет)
   * @return нужно вернуть true, если tm1 и tm2 обеспечивают РАЗНЫЕ точки входа REST-контроллера
   */
  public static boolean isDifferent(String tm1, String tm2) {
    tm1 = trimAsterisk(tm1);
    tm2 = trimAsterisk(tm2);

    if (tm1.length() == 0) return onEmpty(tm2);
    if (tm2.length() == 0) return onEmpty(tm1);

    if ("*".equals(tm1) || "*".equals(tm2)) return false;

    int i1 = tm1.indexOf('*');
    int i2 = tm2.indexOf('*');

    if (i1 < 0 && i2 < 0) return !tm1.equals(tm2);
    if (i1 < 0) return onLeftWordAndRightAsterisk(tm1, tm2, i2);
    if (i2 < 0) return onLeftWordAndRightAsterisk(tm2, tm1, i1);

    if (tm1.charAt(0) == '*') return onLeftStartWithAsterisk(tm1.substring(1), tm2);
    if (tm2.charAt(0) == '*') return onLeftStartWithAsterisk(tm2.substring(1), tm1);

    String start1 = tm1.substring(0, i1);
    String start2 = tm2.substring(0, i2);

    if (start1.startsWith(start2) || start1.startsWith(start2)) {
      return onLeftStartWithAsterisk(tm1.substring(i1 + 1), tm2.substring(i2));
    }

    return true;
  }

  private static boolean onLeftStartWithAsterisk(String tm1, String withAsterisk) {
    if (withAsterisk.endsWith("*")) return false;
    String right2 = withAsterisk.substring(withAsterisk.indexOf('*') + 1);

    if (tm1.endsWith(right2)) return false;
    if (right2.endsWith(tm1)) return false;

    return true;
  }

  private static boolean onLeftWordAndRightAsterisk(String word, String tm2, int i2) {
    String left = tm2.substring(0, i2);
    if (left.length() > 0 && !word.startsWith(left)) return true;
    String right = tm2.substring(i2 + 1);
    if (right.length() > 0 && !word.endsWith(right)) return true;
    return false;
  }

  private static boolean onEmpty(String tm) {
    if (tm.length() == 0) return false;
    if ("*".equals(tm)) return false;
    return true;
  }
}
