package kz.greetgo.depinject.mvc.utils;

import kz.greetgo.depinject.mvc.MappingResult;
import kz.greetgo.depinject.mvc.error.NoPathParam;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TestMappingResult implements MappingResult {
  @Override
  public boolean ok() {
    return false;
  }

  public final Map<String, String> params = new HashMap<>();

  @Override
  public String getParam(String name) {
    if (params.containsKey(name)) return params.get(name);
    throw new NoPathParam(name, Collections.unmodifiableMap(params));
  }
}
