package kz.greetgo.mvc.interfaces;

import kz.greetgo.mvc.model.MvcModel;

public interface MethodParamExtractor {
  Object extract(MappingResult mappingResult, RequestTunnel tunnel, MvcModel model) throws Exception;
}
