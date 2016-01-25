package kz.greetgo.depinject.mvc;

public interface MethodParamExtractor {
  Object extract(MappingResult mappingResult, RequestTunnel tunnel, MvcModel model) throws Exception;
}
