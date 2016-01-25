package kz.greetgo.mvc.jetty;

public interface MethodParamExtractor {
  Object extract(MappingResult mappingResult, RequestTunnel tunnel, MvcModel model) throws Exception;
}
