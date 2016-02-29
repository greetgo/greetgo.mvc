package kz.greetgo.mvc.utils;

import kz.greetgo.mvc.interfaces.MappingResult;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.mvc.model.MvcModel;
import kz.greetgo.mvc.interfaces.Views;

import java.io.OutputStream;
import java.io.PrintStream;

public class ProbeViews implements Views {
  @Override
  public String toJson(Object object) {
    return "JSON" + object;
  }

  @Override
  public String toXml(Object object) {
    return "XML" + object;
  }

  @Override
  public void defaultView(RequestTunnel tunnel, Object returnValue, MvcModel model, MappingResult mappingResult) {

  }

  @Override
  public void errorView(RequestTunnel tunnel, String target, Exception error) throws Exception {
    try (final PrintStream pr = new PrintStream(tunnel.getResponseOutputStream(), false, "UTF-8")) {
      error.printStackTrace(pr);
    }
  }
}
