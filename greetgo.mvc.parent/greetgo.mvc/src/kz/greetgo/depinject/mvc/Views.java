package kz.greetgo.depinject.mvc;

import java.io.OutputStream;

public interface Views {
  String toJson(Object object);

  String toXml(Object object);

  void defaultView(OutputStream outputStream, Object returnValue, MvcModel model, MappingResult mappingResult);

  void errorView(OutputStream outputStream, String target, Exception error);
}
