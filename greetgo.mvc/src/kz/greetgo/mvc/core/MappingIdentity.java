package kz.greetgo.mvc.core;

import kz.greetgo.mvc.annotations.MethodFilter;

public interface MappingIdentity {
  String targetMapping();

  MethodFilter methodFilter();
}
