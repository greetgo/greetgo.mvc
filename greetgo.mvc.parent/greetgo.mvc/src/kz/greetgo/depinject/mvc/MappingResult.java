package kz.greetgo.depinject.mvc;

public interface MappingResult {
  boolean ok();

  String getParam(String name);
}
