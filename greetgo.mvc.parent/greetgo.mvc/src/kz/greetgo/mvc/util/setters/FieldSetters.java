package kz.greetgo.mvc.util.setters;

import java.util.Set;

public interface FieldSetters extends Iterable<FieldSetter> {
  Set<String> names();

  FieldSetter get(String name);
}
