package kz.greetgo.mvc.interfaces;

/**
 * Cookie getter from request tunnel
 */
public interface CookieRequestGetter {
  /**
   * Takes cookie and converts it to object
   *
   * @param <T> type of returning object
   * @return converted object
   */
  <T> T object();

  /**
   * Takes cookie value as string
   *
   * @return cookie value as string
   */
  String value();
}
