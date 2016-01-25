package kz.greetgo.depinject.mvc.probes;

import kz.greetgo.depinject.mvc.MappingResult;
import kz.greetgo.depinject.mvc.MvcModel;
import kz.greetgo.depinject.mvc.Views;

import java.io.OutputStream;

public class ProbeViews implements Views {

  @Override
  public String toJson(Object object) {
    return null;
  }

  @Override
  public String toXml(Object object) {
    return null;
  }

  @Override
  public void defaultView(OutputStream outputStream, Object returnValue, MvcModel model, MappingResult mappingResult) {

  }

  @Override
  public void errorView(OutputStream outputStream, String target, Exception error) {

  }
}
