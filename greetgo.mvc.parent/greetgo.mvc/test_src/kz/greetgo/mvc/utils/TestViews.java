package kz.greetgo.mvc.utils;

import kz.greetgo.mvc.interfaces.MappingResult;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.mvc.interfaces.Views;
import kz.greetgo.mvc.model.MvcModelData;

import java.io.PrintStream;
import java.lang.reflect.Method;

public class TestViews implements Views {
  @Override
  public String toJson(Object object, RequestTunnel tunnel, Method method) {
    return "JSON " + object;
  }

  @Override
  public String toXml(Object object, RequestTunnel tunnel, Method method) {
    return "XML " + object;
  }

  public Object returnValue = null;
  public MvcModelData model = null;
  public MappingResult mappingResult = null;

  @Override
  public void defaultView(RequestTunnel tunnel, Object returnValue, MvcModelData model, MappingResult mappingResult) {

    this.returnValue = returnValue;
    this.model = model;
    this.mappingResult = mappingResult;

    try (PrintStream pr = new PrintStream(tunnel.getResponseOutputStream(), false, "UTF-8")) {
      pr.print("view of " + returnValue);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public String errorTarget = null;

  @Override
  public void errorView(RequestTunnel tunnel, String target, Method method, Exception error) {
    errorTarget = target;
    try (PrintStream pr = new PrintStream(tunnel.getResponseOutputStream(), false, "UTF-8")) {
      error.printStackTrace(pr);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public long controllerMethodSlowTime() {
    return 0;
  }
}
