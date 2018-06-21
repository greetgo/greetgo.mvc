package kz.greetgo.mvc.builder;

import kz.greetgo.mvc.core.TargetMapper;

import java.lang.reflect.Method;

public class ExecDefinition {
  public final Method method;
  public final TargetMapper targetMapper;

  public ExecDefinition(Method method, TargetMapper targetMapper) {
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
