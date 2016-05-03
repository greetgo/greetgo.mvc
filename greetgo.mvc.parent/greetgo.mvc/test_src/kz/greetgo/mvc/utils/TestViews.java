package kz.greetgo.mvc.utils;

import kz.greetgo.mvc.interfaces.MappingResult;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.mvc.model.MvcModel;
import kz.greetgo.mvc.interfaces.Views;

import java.io.OutputStream;
import java.io.PrintStream;

public class TestViews implements Views {
  @Override
  public String toJson(Object object) {
    return "JSON " + object;
  }

  @Override
  public String toXml(Object object) {
    return "XML " + object;
  }

  public Object returnValue = null;
  public MvcModel model = null;
  public MappingResult mappingResult = null;

  @Override
  public void defaultView(RequestTunnel tunnel, Object returnValue, MvcModel model, MappingResult mappingResult) {

    this.returnValue = returnValue;
    this.model = model;
    this.mappingResult = mappingResult;

    try (PrintStream pr = new PrintStream(tunnel.getResponseOutputStream(), false, "UTF-8")) {
      pr.print("view of " + returnValue);
      pr.flush();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public String errorTarget = null;

  @Override
  public void errorView(RequestTunnel tunnel, String target, Exception error) {
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
