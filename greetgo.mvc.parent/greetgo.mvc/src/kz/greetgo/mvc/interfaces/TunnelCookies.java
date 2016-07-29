package kz.greetgo.mvc.interfaces;

public interface TunnelCookies {
  /**
   * Reads cookie value from request
   *
   * @param name cookie name
   * @return cookie value
   */
  String getFromRequest(String name);

  /**
   * Saves to response cookie with max age = 24 hours
   *
   * @param name  cookie name
   * @param value cookie value
   */
  void saveToResponse(String name, String value);

  /**
   * Saves cookie to response
   *
   * @param name   cookie name
   * @param maxAge cookie living age in seconds
   * @param value  cookie value
   */
  void saveToResponse(String name, int maxAge, String value);

  /**
   * Removes cookie from response
   *
   * @param name cookie name
   */
  void removeFromResponse(String name);

  /**
   * Reads cookie and converts it to object
   *
   * @param name cookie name
   * @param <T>  object type
   * @return object from cookie
   */
  <T> T getFromRequestObject(String name);

  /**
   * Saves object to cookie
   *
   * @param name   cookie name
   * @param object object to save in cookie
   */
  void saveToResponseObject(String name, Object object);
}
