package kz.greetgo.mvc.interfaces;

public interface MappingResult {
  boolean ok();

  String getParam(String name);
}
