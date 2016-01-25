package kz.greetgo.mvc.jetty;

import java.io.OutputStream;

public interface Views {
  String toJson(Object object) throws Exception;

  String toXml(Object object) throws Exception;

  void defaultView(OutputStream outputStream, Object returnValue, MvcModel model, MappingResult mappingResult) throws Exception;

  void errorView(OutputStream outputStream, String target, Exception error) throws Exception;
}
