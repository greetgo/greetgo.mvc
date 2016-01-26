package kz.greetgo.mvc.jetty.interfaces;

public interface MappingResult {
  boolean ok();

  String getParam(String name);
}
