package kz.greetgo.mvc.jetty.interfaces;

import kz.greetgo.mvc.jetty.model.MvcModel;

public interface MethodParamExtractor {
  Object extract(MappingResult mappingResult, RequestTunnel tunnel, MvcModel model) throws Exception;
}
