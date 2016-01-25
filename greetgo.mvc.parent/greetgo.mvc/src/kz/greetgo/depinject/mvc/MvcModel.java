package kz.greetgo.depinject.mvc;

import java.util.HashMap;
import java.util.Map;

public class MvcModel {
  private final Map<String, Object> data = new HashMap<>();

  public void setParam(String name, Object value) {
    data.put(name, value);
  }

  public Object getParam(String name) {
    return data.get(name);
  }

  @Override
  public String toString() {
    return data.toString();
  }
}
