package kz.greetgo.mvc.util;

public class BytesUtil {
  public static void putLong(byte[] target, int offset, long longValue) {
    for (int i = 0; i < 8; i++) {
      target[offset + i] = (byte) ((longValue >> (8 * i)) & 0xFF);
    }
  }


  public static long extractLong(byte[] target, int offset) {
    //noinspection PointlessBitwiseExpression,PointlessArithmeticExpression
    return 0L
      | (((long) target[offset + 0]) << (8 * 0)) & 0xFFL
      | (((long) target[offset + 1]) << (8 * 1)) & 0xFF00L
      | (((long) target[offset + 2]) << (8 * 2)) & 0xFF0000L
      | (((long) target[offset + 3]) << (8 * 3)) & 0xFF000000L
      | (((long) target[offset + 4]) << (8 * 4)) & 0xFF00000000L
      | (((long) target[offset + 5]) << (8 * 5)) & 0xFF0000000000L
      | (((long) target[offset + 6]) << (8 * 6)) & 0xFF000000000000L
      | (((long) target[offset + 7]) << (8 * 7)) & 0xFF00000000000000L
      ;
  }

}
