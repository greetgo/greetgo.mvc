package kz.greetgo.mvc.war.stand.jsp;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import java.util.Set;

public class AppJsp implements ServletContainerInitializer {
  @Override
  public void onStartup(Set<Class<?>> set, ServletContext servletContext) throws ServletException {

    final MyServlet myServlet = new MyServlet();
    final ServletRegistration.Dynamic myServletReg = servletContext.addServlet("myServlet", myServlet);
    myServletReg.addMapping("/my/*");

  }
}
