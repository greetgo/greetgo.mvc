package kz.greetgo.mvc.interfaces;

public interface MvcTrace {
  void trace(Object message);
  
  void trace(Object message, Throwable error);
  
  void traceInTunnel(Object message, RequestTunnel tunnel);
}
