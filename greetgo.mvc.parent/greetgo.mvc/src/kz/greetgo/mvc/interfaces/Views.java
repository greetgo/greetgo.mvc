package kz.greetgo.mvc.interfaces;

import kz.greetgo.mvc.model.MvcModelData;

public interface Views {
  String toJson(Object object) throws Exception;

  String toXml(Object object) throws Exception;

  void defaultView(RequestTunnel tunnel, Object returnValue,
                   MvcModelData modelData, MappingResult mappingResult) throws Exception;

  void errorView(RequestTunnel tunnel, String target, Exception error) throws Exception;

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

}
