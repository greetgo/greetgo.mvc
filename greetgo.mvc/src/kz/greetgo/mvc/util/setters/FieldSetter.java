package kz.greetgo.mvc.util.setters;

public interface FieldSetter {
  String name();

  void setFromStrings(Object destination, String[] strValues);
}
