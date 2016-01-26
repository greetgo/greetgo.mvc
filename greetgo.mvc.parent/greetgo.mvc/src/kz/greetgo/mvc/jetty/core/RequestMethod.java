package kz.greetgo.mvc.jetty.core;

public enum RequestMethod {
  GET,
  POST,
  HEAD,
  PUT,
  OPTIONS,
  DELETE,
  TRACE,
  CONNECT,
  MOVE,
  PROXY,
  PRI;


  boolean is(String methodName) {
    return name().equalsIgnoreCase(methodName);
  }

  public static RequestMethod fromStr(String str) {
    if (str == null) return null;
    str = str.trim().toUpperCase();
    if (str.length() == 0) return null;
    for (RequestMethod e : values()) {
      if (e.name().equals(str)) return e;
    }
    return null;
  }
}
