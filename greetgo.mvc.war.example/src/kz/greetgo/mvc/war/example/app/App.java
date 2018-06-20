package kz.greetgo.mvc.war.example.app;

import kz.greetgo.mvc.interfaces.Views;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

public class App implements ServletContainerInitializer {
  @Override
  public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
    Views views = new ViewsExample();

    ControllerServlet controllerServlet = new ControllerServlet(views);
    controllerServlet.register(ctx);

    Utf8Filter utf8Filter = new Utf8Filter();
    utf8Filter.register(ctx);
  }
}
