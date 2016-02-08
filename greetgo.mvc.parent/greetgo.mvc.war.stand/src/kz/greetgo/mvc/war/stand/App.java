package kz.greetgo.mvc.war.stand;

import kz.greetgo.mvc.core.TunnelHandlerList;
import kz.greetgo.mvc.war.TunnelHandlerFilter;

import javax.servlet.*;
import java.util.EnumSet;
import java.util.Set;

public class App implements ServletContainerInitializer {

  @Override
  public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {

    TunnelHandlerList tunnelHandlerList = new TunnelHandlerList();

    TunnelHandlerFilter filter = new TunnelHandlerFilter(tunnelHandlerList);

    final FilterRegistration.Dynamic someFilter = ctx.addFilter("TunnelHandlerFilter", filter);
    someFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");

    System.out.println("-------------------- INIT ---");

  }
}
