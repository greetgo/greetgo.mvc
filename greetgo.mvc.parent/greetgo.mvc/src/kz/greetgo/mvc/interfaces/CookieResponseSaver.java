package kz.greetgo.mvc.interfaces;

/**
 * Cookie response saver. It can change cookie params
 */
public interface CookieResponseSaver {
  /**
   * Adds cookie to response. Termination operation.
   *
   * @param value string value for cookie
   */
  void saveValue(String value);

  /**
   * Adds cookie to response. Termination operation.
   *
   * @param object object, that converts to string for cookie value
   */
  void saveObject(Object object);

  /**
   * Adds cookie removing to response. Termination operation.
   */
  default void remove() {
    maxAge(0).saveValue(null);
  }

  /**
   * Set cookie parameter: maxAge, to use in future
   *
   * @param maxAge cookie max age
   * @return saver with this set value
   */
  CookieResponseSaver maxAge(int maxAge);

  /**
   * Set cookie parameter: secure, to use in future
   * @param secure cookie secure
   * @return saver with this set value
   */
  CookieResponseSaver secure(boolean secure);

  /**
   * Set cookie parameter: path, to use in future
   * @param path cookie path
   * @return saver with this set value
   */
  CookieResponseSaver path(String path);

  /**
   * Set cookie parameter: version, to use in future
   * @param version cookie version
   * @return saver with this set value
   */
  CookieResponseSaver version(int version);

  /**
   * Set cookie parameter: httpOnly, to use in future
   * @param httpOnly cookie httpOnly
   * @return saver with this set value
   */
  CookieResponseSaver httpOnly(boolean httpOnly);

  /**
   * Set cookie parameter: domain, to use in future
   * @param domain cookie domain
   * @return saver with this set value
   */
  CookieResponseSaver domain(String domain);

  /**
   * Set cookie parameter: comment, to use in future
   * @param comment cookie comment
   * @return saver with this set value
   */
  CookieResponseSaver comment(String comment);
}
