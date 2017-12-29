package kz.greetgo.mvc.interfaces;

import java.lang.reflect.Method;

public interface Views extends SessionParameterGetter {
  default String toJson(Object object, RequestTunnel tunnel, Method method) throws Exception {
    throw new UnsupportedOperationException("Conversion to JSON has been not implemented");
  }

  default String toXml(Object object, RequestTunnel tunnel, Method method) throws Exception {
    throw new UnsupportedOperationException("Conversion to XML has been not implemented");
  }

  void performRequest(MethodInvoker methodInvoker) throws Exception;

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
