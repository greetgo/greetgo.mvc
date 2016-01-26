package kz.greetgo.mvc.jetty.utils;

import kz.greetgo.mvc.jetty.interfaces.MappingResult;
import kz.greetgo.mvc.jetty.model.MvcModel;
import kz.greetgo.mvc.jetty.interfaces.Views;

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
  public void defaultView(OutputStream outputStream, Object returnValue, MvcModel model, MappingResult mappingResult) {

    this.returnValue = returnValue;
    this.model = model;
    this.mappingResult = mappingResult;

    try (PrintStream pr = new PrintStream(outputStream, false, "UTF-8")) {
      pr.print("view of " + returnValue);
      pr.flush();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public String errorTarget = null;

  @Override
  public void errorView(OutputStream outputStream, String target, Exception error) {
    errorTarget = target;
    try (PrintStream pr = new PrintStream(outputStream, false, "UTF-8")) {
      error.printStackTrace(pr);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
