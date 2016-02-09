package kz.greetgo.mvc.util;

import javax.xml.bind.DatatypeConverter;

public class Base64Util {
  public static byte[] base64ToBytes(String base64) {
    if (base64 == null) return null;
    try {
      final byte[] ret = DatatypeConverter.parseBase64Binary(base64);
      if (ret == null) return null;
      if (ret.length == 0) return null;
      return ret;
    } catch (ArrayIndexOutOfBoundsException e) {
      return null;
    }
  }

  public static String bytesToBase64(byte[] bytes) {
    if (bytes == null) return null;
    return DatatypeConverter.printBase64Binary(bytes);
  }
}
