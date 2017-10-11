package kz.greetgo.mvc.interfaces;

import javax.servlet.http.Cookie;

/**
 * Tunnel cookie worker
 */
public interface TunnelCookies {
  /**
   * Takes cookie getter for specified coolie name
   *
   * @param name cookie name
   * @return cookie getter
   */
  CookieRequestGetter name(String name);

  /**
   * Takes cookie getter for specified coolie name
   *
   * @param name cookie name
   * @return object for saving cookie
   */
  CookieResponseSaver forName(String name);

  /**
   * Gets request cookies direct
   *
   * @return all request cookies
   */
  Cookie[] getRequestCookies();

  /**
   * Adds cookie to response
   *
   * @param cookie adding cookie
   */
  void addCookieToResponse(Cookie cookie);
}
