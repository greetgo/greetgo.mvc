package kz.greetgo.mvc.model;

import java.util.HashMap;
import java.util.Map;

public class MvcModelData implements MvcModel {
  public final Map<String, Object> data = new HashMap<>();

  public void setParam(String name, Object value) {
    data.put(name, value);
  }

  public Integer statusCode = null;

  @Override
  public void setStatus(int statusCode) {
    this.statusCode = statusCode;
  }

  @Override
  public String toString() {
    return data.toString();
  }
}
