package kz.greetgo.mvc.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class CookieUtil {

  public static String getCookieValue(HttpServletRequest request, String cookieName) {
    if (request == null) return null;
    if (request.getCookies() == null) return null;
    for (Cookie cookie : request.getCookies()) {
      if (cookie.getName().equals(cookieName)) {
        return cookie.getValue();
      }
    }
    return null;
  }

  public static void removeCookie(HttpServletResponse response, String name) {
    final Cookie cookie = new Cookie(name, null);
    cookie.setMaxAge(0);
    cookie.setPath("/");
    cookie.setSecure(false);
    response.addCookie(cookie);
  }

  public static void addCookie(HttpServletResponse response, int maxAge, String cookieName, String cookieValue) {
    final Cookie cookie = new Cookie(cookieName, cookieValue);
    cookie.setMaxAge(maxAge);
    cookie.setSecure(false);
    cookie.setPath("/");
    response.addCookie(cookie);
  }

  /**
   * <p>
   * Converts object to string with java-serialization with following converting into base64
   * </p>
   * <p>
   * Complemented method is: {@link #strToObject(String)}
   * </p>
   *
   * @param object serializing object
   * @return string with serialized object
   */
  public static String objectToStr(Object object) {
    ByteArrayOutputStream bOut = new ByteArrayOutputStream();

    try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(bOut)) {
      objectOutputStream.writeObject(object);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return Base64Util.bytesToBase64(bOut.toByteArray());
  }

  /**
   * <p>
   * Преобразует строку в объект. Объект храниться в виде base64 кода, который получился java-десериализацией
   * Convert string into object. Object is storing in base64 form after java-serialization
   * </p>
   * <p>
   * Complemented method is: {@link #objectToStr(Object)}
   * </p>
   *
   * @param str base64-string stores serialized object
   * @return deserialized object
   */
  public static <T> T strToObject(String str) {

    if (str == null) return null;

    final byte[] bytes = Base64Util.base64ToBytes(str);

    if (bytes == null) return null;

    ByteArrayInputStream bIn = new ByteArrayInputStream(bytes);

    try {
      ObjectInputStream oIn = new ObjectInputStream(bIn);
      //noinspection unchecked
      return (T) oIn.readObject();
    } catch (IOException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
