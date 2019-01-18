package kz.greetgo.mvc.builder;

import kz.greetgo.mvc.core.TargetMapper;

import java.lang.reflect.Method;
import java.util.Objects;

public class ExecDefinition {
  public final Method method;
  public final TargetMapper targetMapper;

  public ExecDefinition(Method method, TargetMapper targetMapper) {
    Objects.requireNonNull(method, "method");
    Objects.requireNonNull(targetMapper, "targetMapper");
    this.method = method;
    this.targetMapper = targetMapper;
  }

  public String infoStr() {
    return method.getDeclaringClass().getSimpleName() + "." + method.getName() + " : " + targetMapper.infoStr();
  }

  @Override
  public String toString() {
    return "ExecDefinition{method=" + method + ",targetMapper=" + targetMapper + "}";
  }
}
