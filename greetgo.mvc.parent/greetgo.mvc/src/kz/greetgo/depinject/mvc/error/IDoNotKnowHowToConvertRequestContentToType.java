package kz.greetgo.depinject.mvc.error;

import java.lang.reflect.Type;

public class IDoNotKnowHowToConvertRequestContentToType extends RuntimeException {
  public final Type type;

  public IDoNotKnowHowToConvertRequestContentToType(Type type) {
    super("I don't know how to convert request content to " + type);
    this.type = type;
  }
}
