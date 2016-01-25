package kz.greetgo.mvc;

public interface MappingResult {
  boolean ok();

  String getParam(String name);
}
