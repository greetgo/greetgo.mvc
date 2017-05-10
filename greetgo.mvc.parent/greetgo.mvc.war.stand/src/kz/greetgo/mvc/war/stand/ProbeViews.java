package kz.greetgo.mvc.war.stand;

import kz.greetgo.mvc.interfaces.MappingResult;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.mvc.interfaces.Views;
import kz.greetgo.mvc.model.MvcModelData;

import java.io.PrintStream;
import java.lang.reflect.Method;

public class ProbeViews implements Views {
  @Override
  public String toJson(Object object, RequestTunnel tunnel, Method method) {
    return "JSON" + object;
  }

  @Override
  public String toXml(Object object, RequestTunnel tunnel, Method method) {
    return "XML" + object;
  }

  @Override
  public void defaultView(RequestTunnel tunnel, Object returnValue, MvcModelData model, MappingResult mappingResult) {

  }

  @Override
  public void errorView(RequestTunnel tunnel, String target, Method method, Exception error) throws Exception {
    try (final PrintStream pr = new PrintStream(tunnel.getResponseOutputStream(), false, "UTF-8")) {
      error.printStackTrace(pr);
    }
  }

  @Override
  public long controllerMethodSlowTime() {
    return 0;
  }
}
