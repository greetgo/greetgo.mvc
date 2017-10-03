package kz.greetgo.mvc.errors;

import java.lang.reflect.AccessibleObject;

public class CannotInstantiateCollection extends RuntimeException {
  public final Class<?> memberCollection;
  public final AccessibleObject member;

  public CannotInstantiateCollection(Class<?> memberCollection, AccessibleObject member) {
    super(memberCollection + " of " + member);
    this.memberCollection = memberCollection;
    this.member = member;
  }
}
