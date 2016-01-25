package kz.greetgo.mvc.jetty;

public interface MappingResult {
  boolean ok();

  String getParam(String name);
}
