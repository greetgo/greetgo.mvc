package kz.greetgo.mvc.war;

import javax.servlet.*;
import java.io.IOException;

public class SomeFilter implements Filter{
  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
    throws IOException, ServletException {



    System.out.println("request.class = " + request.getClass());
    System.out.println("response.class = " + response.getClass());

    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {
  }
}
