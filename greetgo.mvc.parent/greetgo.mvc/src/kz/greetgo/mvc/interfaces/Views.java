package kz.greetgo.mvc.interfaces;

import kz.greetgo.mvc.model.MvcModel;

import java.io.OutputStream;

public interface Views {
  String toJson(Object object) throws Exception;

  String toXml(Object object) throws Exception;

  void defaultView(RequestTunnel tunnel, Object returnValue, MvcModel model, MappingResult mappingResult) throws Exception;

  void errorView(RequestTunnel tunnel, String target, Exception error) throws Exception;
}
