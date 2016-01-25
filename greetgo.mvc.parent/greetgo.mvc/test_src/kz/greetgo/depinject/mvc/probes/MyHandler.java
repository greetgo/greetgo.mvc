package kz.greetgo.depinject.mvc.probes;

import kz.greetgo.depinject.mvc.ControllerHandler;
import kz.greetgo.depinject.mvc.RequestTunnel;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

public class MyHandler extends AbstractHandler {

  private final ControllerHandler controllerHandler = ControllerHandler.create(new ProbeController(), null);

  @Override
  public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {


    final RequestTunnel tunnel = new JettyRequestTunnel(target, baseRequest, request, response);

    if (controllerHandler.handleTunnel(tunnel)) return;


    if ("/goto1.html".equals(target)) {

      final PrintWriter writer = response.getWriter();
      writer.println("Hello from java code");
      response.flushBuffer();

      baseRequest.setHandled(true);

      return;
    }

    if ("/lot/main.html".equals(target)) {

      final PrintWriter writer = response.getWriter();
      writer.println("Main lot code hello");

      response.flushBuffer();

      baseRequest.setHandled(true);

      return;
    }

    if ("/lot/save".equals(target)) {

      final Enumeration<String> attributeNames = request.getAttributeNames();
      while (attributeNames.hasMoreElements()) {
        final String attributeName = attributeNames.nextElement();
        final Object attributeValue = request.getAttribute(attributeName);
        System.out.println("attribute " + attributeName + " = " + attributeValue);
      }

      final Enumeration<String> parameterNames = request.getParameterNames();
      while (parameterNames.hasMoreElements()) {
        final String parameterName = parameterNames.nextElement();
        final String[] parameterValues = request.getParameterValues(parameterName);
        for (String parameterValue : parameterValues) {
          System.out.println("parameter " + parameterName + " = " + parameterValue);
        }
      }

      for (Part part : request.getParts()) {
        System.out.println("part.name = " + part.getName() + ", part.contentType = "
          + part.getContentType() + " : " + part);
      }

      final Part fileA = request.getPart("fileA");
      System.out.println("fileA.SubmittedFileName = " + fileA.getSubmittedFileName()
        + ", ContentType = " + fileA.getContentType() + ", Size = " + fileA.getSize());
      for (String headerName : fileA.getHeaderNames()) {
        System.out.println("fileA:  " + headerName + " = " + fileA.getHeaders(headerName));
      }

      final String surname = request.getParameter("surname");
      System.out.println("surname = " + surname);

      final String[] options = request.getParameterValues("options");
      if (options != null) for (String option : options) {
        System.out.println("option = " + option);
      }

      response.sendRedirect("after_post.html");

      baseRequest.setHandled(true);

      return;
    }

    System.out.println("skip");

  }
}
