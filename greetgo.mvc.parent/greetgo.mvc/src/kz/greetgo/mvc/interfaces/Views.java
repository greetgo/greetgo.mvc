package kz.greetgo.mvc.interfaces;

import kz.greetgo.mvc.model.MvcModelData;

import java.lang.reflect.Method;

public interface Views extends SessionParameterGetter {
  String toJson(Object object, RequestTunnel tunnel, Method method) throws Exception;

  String toXml(Object object, RequestTunnel tunnel, Method method) throws Exception;

  void defaultView(RequestTunnel tunnel, Object returnValue,
                   MvcModelData modelData, MappingResult mappingResult) throws Exception;

  void errorView(RequestTunnel tunnel, String target, Method method, Throwable error) throws Exception;

  long controllerMethodSlowTime();

  class MissedView extends RuntimeException {
    public final RequestTunnel tunnel;

    public MissedView(RequestTunnel tunnel) {
      super("Target " + tunnel.getTarget());
      this.tunnel = tunnel;
    }
  }

  default void missedView(RequestTunnel tunnel) {
    throw new MissedView(tunnel);
  }

  default Object getSessionParameter(ParameterContext context, RequestTunnel tunnel) {
    throw new UnsupportedOperationException("Reading of a session parameters has been not implemented");
  }
}
