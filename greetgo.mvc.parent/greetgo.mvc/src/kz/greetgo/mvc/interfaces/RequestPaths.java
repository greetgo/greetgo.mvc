package kz.greetgo.mvc.interfaces;

/**
 * Положение запроса
 */
public interface RequestPaths {
  String getRequestURI();

  StringBuffer getRequestURL();

  String getServletPath();

  String getQueryString();

  String getContextPath();

  String getPathTranslated();

  String getPathInfo();
}
