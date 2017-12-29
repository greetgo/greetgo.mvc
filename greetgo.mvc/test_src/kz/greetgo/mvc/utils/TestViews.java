package kz.greetgo.mvc.utils;

import kz.greetgo.mvc.interfaces.MappingResult;
import kz.greetgo.mvc.interfaces.MethodInvoker;
import kz.greetgo.mvc.interfaces.MethodInvokedResult;
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

  public String errorTarget = null;

  @Override
  public void performRequest(MethodInvoker methodInvoker) {
    MethodInvokedResult invokingResult = methodInvoker.invoke();
    if (invokingResult.tryDefaultRender()) return;

    if (invokingResult.error() == null) {

      this.returnValue = invokingResult.returnedValue();
      this.model = methodInvoker.model();
      this.mappingResult = methodInvoker.mappingResult();

      try (PrintStream pr = new PrintStream(methodInvoker.tunnel().getResponseOutputStream(), false, "UTF-8")) {
        pr.print("view of " + returnValue);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }

    } else {
      errorTarget = methodInvoker.tunnel().getTarget();

      try (PrintStream pr = new PrintStream(methodInvoker.tunnel().getResponseOutputStream(), false, "UTF-8")) {
        invokingResult.error().printStackTrace(pr);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }

    }
  }
}
