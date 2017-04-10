package kz.greetgo.mvc.util.setters;

public interface FieldSetter {
  String name();

  void setFromStrs(Object destination, String[] strValues);
}
