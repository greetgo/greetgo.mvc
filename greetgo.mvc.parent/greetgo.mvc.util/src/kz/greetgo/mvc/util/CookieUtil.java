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
//    cookie.setHttpOnly(true);
    cookie.setPath("/");
    cookie.setSecure(false);
    response.addCookie(cookie);
  }

  public static void addCookie(HttpServletResponse response, String cookieName, String cookieValue) {
    final Cookie cookie = new Cookie(cookieName, cookieValue);
    cookie.setMaxAge(60 * 60 * 24);
    cookie.setSecure(false);
    cookie.setPath("/");
    response.addCookie(cookie);
  }

  public static String objectToStr(Object object) {
    ByteArrayOutputStream bOut = new ByteArrayOutputStream();

    try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(bOut)) {
      objectOutputStream.writeObject(object);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    return Base64Util.bytesToBase64(bOut.toByteArray());
  }

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
