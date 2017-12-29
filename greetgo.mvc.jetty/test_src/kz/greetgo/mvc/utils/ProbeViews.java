package kz.greetgo.mvc.utils;

import kz.greetgo.mvc.interfaces.MethodInvokedResult;
import kz.greetgo.mvc.interfaces.MethodInvoker;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.mvc.interfaces.Views;

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
  public void performRequest(MethodInvoker methodInvoker) throws Exception {
    MethodInvokedResult invokedResult = methodInvoker.invoke();
    if (invokedResult.tryDefaultRender()) return;

    if (invokedResult.error() != null) {
      try (final PrintStream pr = new PrintStream(methodInvoker.tunnel().getResponseOutputStream(), false, "UTF-8")) {
        invokedResult.error().printStackTrace(pr);
      }
    }
  }
}
