package kz.greetgo.mvc.interfaces;

import java.util.Enumeration;
import java.util.Locale;

public interface RequestMeta {
  String getLocalAddr();

  int getLocalPort();

  String getLocalName();

  Locale getLocale();

  Enumeration<Locale> getLocales();

  String getProtocol();

  String getScheme();

  String getServerName();

  int getServerPort();

  String getRemoteAddr();

  String getRemoteHost();

  int getRemotePort();
}
