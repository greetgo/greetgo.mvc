package kz.greetgo.mvc.war.stand.jsp;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MyServlet extends HttpServlet {

  public static class AttrObject {
    public AttrObject(String hi) {
      this.hi = hi;
    }

    private final String hi;

    public String getHi() {
      return hi;
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    doGet(req, resp);
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    final String pathInfo = req.getPathInfo();
    System.out.println("pathInfo = " + pathInfo);

    if (pathInfo.startsWith("/jsp/")) {

      final AttrObject attrObject = new AttrObject("Hello from hi");

      req.setAttribute("attrObject", attrObject);

      List<AttrObject> hiList = new ArrayList<>();
      hiList.add(new AttrObject("Hi 1"));
      hiList.add(new AttrObject("Hi 2"));
      hiList.add(new AttrObject("Hi 3"));
      hiList.add(new AttrObject("Hi 4"));
      req.setAttribute("hiList", hiList);

      req.getRequestDispatcher(pathInfo + ".jsp").forward(req, resp);
      return;
    }


    final PrintWriter writer = resp.getWriter();
    writer.println("<html>");
    writer.println("<body>");
    writer.println(" Hello form my servlet");
    writer.println("</body>");
    writer.println("</html>");

    resp.flushBuffer();
  }

}
