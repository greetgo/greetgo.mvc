### References

 - [Concept]
 - [Example project mvc.war.example (Quick setup and launch)](mvc_war_example.md)
 - [Specification of controllers](controller_spec.md)

### Concept

The idea representing how to implement the processing of Rest-requests is taken from SpringMVC, but 
the implementation is greatly simplified, and, in the author's opinion, the way of requests description
is simplified as well. Also greetgo.mvc has no integration deeply into some framework, like in SpringMVC, 
i.e. SpringMVC can not be used without other Spring components, but greetgo.mvc is an independent 
full library that implements only MVC in the SpringMVC manner.

##### Views.performRequest Method

It is important to understand that **any request does not call the controller method, but calls `Views.performRequest`**,
and then from within this method it is needed to call the controller method. But do not worry, you do not have to figure
out which controller and its method you need to call - all these preparatory works have already been done. If you look 
at the specification of this method:

    void performRequest(MethodInvoker methodInvoker)

It can be seen that `MethodInvoker`is transferred to it, in which everything that is necessary for
a simple call of the controller method and processing of results of the controller method work are prepared. 
To call the controller method, write this code:

```
1.  MethodInvokedResult invokedResult = methodInvoker.invoke();
2.  boolean renderedOk = invokedResult.tryDefaultRender();
3.  if (renderedOk) return;
4.  // the result is processed by ourselves...
```

Here in line 1, `invoke()` method is called, where all the necessary works are done, i.e. finding
the right controller method, preparing arguments for it, calling the controller method and memorizing
results of operation of the controller method. The results of the controller method are wrapped in
 `MethodInvokedResult` class and returned. Then the user has the opportunity to work with these results.
The library can process many results on its own, to do this `tryDefaultRender()` method is provided , 
which is called after `invoke` method - it is shown in line 2. The method returns a mark of whether
it was able to process.

###### Controller Method Return Table

| `tryDefaultRender()` method returns `true` if:|What the method does in this case|
|---|---|
| The controller method is marked with an annotation @ToJson|Calls `Views.toJson` method transferring that was returned by the controller method as the first argument|
| The controller method is marked with an annotation @ToXml |Calls `Views.toXml`  method transferring that was returned by the controller method as the first argument|
| The controller method is marked with an annotation @AsIs  |The controller method must return a string. This line as it is goes into the request response in the UTF-8 encoding. If the controller method does not return a string, an error is generated when analyzing controllers (this is usually done at application launch)|
| The controller method returned `kz.greetgo.mvc.model.Redirect` object | In response, sends the specified redirect. The specified object can have cookies defined - they are used |
| In the controller method, an exception `kz.greetgo.mvc.model.Redirect` was generated | In response, sends the specified redirect. The specified object can have cookies defined - they are used |

If the processing was done according to this table, `true` is returned. In this case
the processing of the request is finished, and it is allowed to exit as shown in line 3.

In cases that are not described in this table `false` is returned, and the response should be done by you,
as it shown in line 4.

Self-processing consists of two main things: 1) handling errors (exceptions), 2) rendering,
for example in HTML using JSP. Although you can use any other option instead of JSP - it is up to you.

There are can be other specific options unusual for your system:

In order to understand whether an exception was thrown when calling the controller method, it is necessary to call 
`MethodInvokedResult.error()` method - it returns an exception object from which you can request a stack-trace and
put it in the log and / or in the request response (this is the best practice), or do something else to the user's choice.

If the method returns `null`, then controllers method works without exceptions. 

In this case, the standard approach means that it is assumed the controller method returns the name of jsp file,
which must form the body of the request response. This is implemented through a forward mechanism, for example:

    methodInvoker.tunnel().forward(that_was_returned by_controller_method);

The controller's method can transmit some parameters to the jsp-file. To do this, it will connect `MvcModel` as an argument
and transmit the necessary parameters to this model. Before the forwarding, these parameters must be copied to the tunnel, it
requires access to MvcModel. This is done by calling `methodInvoker.model()` method, where all transmitted data is.

Since the library does not call the controller method, but the user does, he can do various checks or
preparatory work before or after calling the controller method, for example, before calling `methodInvoker.invoke()` it is
possible to prepare the session and check access rights. it is possible to remember the execution time of the controller method and 
throw it into the log.

##### Connection example

Let's suppose we have any such controller:

```java
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.mvc.core.*;

@ControllerPrefix("/client")
public class ClientController {
  
  @AsIs
  @OnGet("/surname")
  public String getSurname(@Par("id") String id) {
    return "surname of " + id;
  }
  
}
```

First, it is needed to define a strategy for calling the controller method. For this, it is necessary to
implement `kz.greetgo.mvc.interfaces.Views` interface, for example, in this way:

```java

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.annotations.ParSession;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.mvc.annotations.ToXml;
import kz.greetgo.mvc.interfaces.MethodInvokedResult;
import kz.greetgo.mvc.interfaces.MethodInvoker;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.mvc.interfaces.SessionParameterGetter;
import kz.greetgo.mvc.interfaces.Views;
import kz.greetgo.sandbox.controller.errors.JsonRestError;
import kz.greetgo.sandbox.controller.errors.RestError;
import kz.greetgo.sandbox.controller.register.AuthRegister;
import kz.greetgo.sandbox.controller.register.model.SessionInfo;
import kz.greetgo.sandbox.controller.security.NoSecurity;
import kz.greetgo.sandbox.controller.security.SecurityError;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Controller methods processing is implemented in this class 
 */
public class ViewsImpl implements Views {

  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * This method is called after the call of the controller method marked with an annotation {@link ToJson}.
   *
   * @param object here is the object, that returned the controller method
   * @param tunnel this is a request tunnel (through which you can control the request process)
   * @param method the reference to the controller method that was just called
   * @return this string will be sent as the response body to the request, encoded in UTF-8.
   * @throws Exception is needed for not to use try/catch-blocks
   */
  @Override
  public String toJson(Object object, RequestTunnel tunnel, Method method) throws Exception {
    return convertToJson(object);
  }

  private String convertToJson(Object object) throws Exception {
    if (object == null) return null;
    return objectMapper.writer().writeValueAsString(object);
  }

  /**
   * This method is called after the call of the controller method marked with an annotation {@link ToXml}.
   *
   * @param object here is the object, that returned the controller method
   * @param tunnel this is a request tunnel (through which you can control the request process)
   * @param method the reference to the controller method that was just called
   * @return this string will be sent as the response body to the request, encoded in UTF-8.
   * @throws Exception is needed for not to use try/catch-blocks
   */
  @Override
  public String toXml(Object object, RequestTunnel tunnel, Method method) throws Exception {
    //Here it is needed to transform object into XML and return
    //here ToXml annotation does not work
    throw new UnsupportedOperationException();
  }

  /**
   * This field contains a reference to the object in which the security logic is focused
   */
  public BeanGetter<AuthRegister> authRegister;

  /**
   * This method is called each time when the request is processed. The controller method has not yet been called, and it must be called
   * from this method. But you can not to call, for example, because there are no rights or for any other reason.
   *
   * @param methodInvoker The executor of the controller method is an auxiliary object, in which everything
   *                      needed for calling the controller method and for studying the called controller method 
   *                      is prepared. For example, you can see what annotations the method
   *                      has and to carry oy the additional actions.
   * @throws Exception is needed for not to use try/catch-блоки
   */
  @Override
  public void performRequest(MethodInvoker methodInvoker) throws Exception {

    //call this method so that in the future we can get a moment just before the call of the controller method
    beforeRequest();

    //prepare the session. There may be an error, for example, a token is damaged. And then the method will not be
    prepareSession(methodInvoker);

    //call this method so that in the future we can get a moment just before the call of the controller method
    beforeRequestWithSession();

    //call the controller method and get the result of calling the method
    MethodInvokedResult invokedResult = methodInvoker.invoke();

    //trying to render the result by default behavior. Such behavior is the annotations: ToJson, ToXml, AsIs
    if (invokedResult.tryDefaultRender()) {
      //the default behavior turned out to apply. This means that the request is fully processed and
      //nothing more is not necessary - we exit
      return;
    }

    //here it is needed to process the specific result of the controller method work, for example, to render the JSP or to display
    //the erorr, or something else

    //see if there was an error in the method
    if (invokedResult.error() != null) {
      //processing an error
      performError(methodInvoker, invokedResult);
    } else {
      //processing normal behavior
      performRender(methodInvoker, invokedResult);
    }
  }

  /**
   * This method is always called before calling the controller method, but after the security check.
   * If the security check fails, then this method is not called
   *
   * @throws Exception is needed for not to use try/catch-blocks
   */
  private void beforeRequestWithSession() throws Exception {}

  /**
   * This method is always called before calling the controller method
   *
   * @throws Exception is needed for not to use try/catch-blocks
   */
  protected void beforeRequest() throws Exception {}

  /**
   * Prepares the session and stores it in the LocalThread variable.
   *
   * @param methodInvoker  controller method executor
   */
  private void prepareSession(MethodInvoker methodInvoker) {
      //see if the method has an annotation NoSecurity
      if (methodInvoker.getMethodAnnotation(NoSecurity.class) == null) {
        // If there is no annotation, then it is needed to check for the rights


        //Get the token from the request header. If the token is not present, then we get null
        String token = methodInvoker.tunnel().getRequestHeader("Token");

        //the token will be decrypted and placed in the ThreadLocal variable in this method
        //if something goes wrong, an error occurs and the controller method call will not be done
        //thereby we prevent possible hacking
        authRegister.get().checkTokenAndPutToThreadLocal(token);
      } else {

        // if there is a NoSecurity annotation, this means that the method does not need session parameters
        // and does not need protection - i.e. it can be called by any. For example, logging method.
        // In this case, we clear the ThreadLocal variable
        authRegister.get().cleanTokenThreadLocal();
      }
  }

  /**
   * This method is called when it is needed to fill in the controller method parameter
   * marked with annotation {@link ParSession}
   *
   * @param context parameter information: kind, type, etc.
   * @param tunnel  the request tunnel is given so that you can get any data for the parameter
   * @return The value of this parameter: it will be substituted into this parameter
   */
  @Override
  public Object getSessionParameter(SessionParameterGetter.ParameterContext context, RequestTunnel tunnel) {
    if ("personId".equals(context.parameterName())) {
      if (context.expectedReturnType() != String.class) throw new SecurityError("personId must be string");

      //sessionInfo is taken from ThreadLocal variable, which was defined in prepareSession method
      SessionInfo sessionInfo = authRegister.get().getSessionInfo();
      if (sessionInfo == null) throw new SecurityError("No session");
      return sessionInfo.personId;
    }

    throw new SecurityError("Unknown session parameter " + context.parameterName());
  }

  /**
   * The request view rendering is carrying out here
   *
   * @param methodInvoker controller method executor
   * @param invokedResult results of calling the controller method
   */
  private void performRender(MethodInvoker methodInvoker, MethodInvokedResult invokedResult) {
    assert invokedResult.error() == null;
    //the value returned by the controller method
    Object returnedValue = invokedResult.returnedValue();
    if (returnedValue == null) return;

    //we process only the strings. It is not clear how to process other types.
    if (!(returnedValue instanceof String)) {
      throw new IllegalArgumentException("Cannot view " + returnedValue.getClass()
        + " with value " + returnedValue);
    }

    //we assume that the return value is the local path to the jsp file, for example: jsp/hello.jsp
    String place = (String) returnedValue;

    RequestTunnel tunnel = methodInvoker.tunnel();

    //fill in the data for the view, which will be available through $, for example $hello - in the method, they were added to MvcModel
    for (Map.Entry<String, Object> e : methodInvoker.model().data.entrySet()) {
      tunnel.setRequestAttribute(e.getKey(), e.getValue());
    }

    //forward to the rendering of jsp-file
    tunnel.forward("/jsp/" + place, true);
  }

  /**
   * A request error is processed.
   *
   * @param methodInvoker controller method information for processing the request
   * @param invokedResult results of calling the controller method
   * @throws Exception is needed for not to use try/catch-blocks
   */
  private void performError(MethodInvoker methodInvoker, MethodInvokedResult invokedResult) throws Exception {
    Throwable error = invokedResult.error();
    assert error != null;

    error.printStackTrace();

    RequestTunnel tunnel = methodInvoker.tunnel();
    tunnel.setRequestAttribute("ERROR_TYPE", error.getClass().getSimpleName());

    if (error instanceof JsonRestError) {
      JsonRestError restError = (JsonRestError) error;
      tunnel.setResponseStatus(restError.statusCode);
      String json = convertToJson(restError.sendingAsJsonObject);
      if (json != null) try (final PrintWriter writer = tunnel.getResponseWriter()) {
        writer.print(json);
      }
      return;
    }

    if (error instanceof RestError) {
      RestError restError = (RestError) error;
      tunnel.setResponseStatus(restError.statusCode);

      if (restError.getMessage() != null) {
        try (final PrintWriter writer = tunnel.getResponseWriter()) {
          writer.print(restError.getMessage());
        }
      }

      return;
    }

    {
      tunnel.setResponseStatus(500);
      try (final PrintWriter writer = tunnel.getResponseWriter()) {
        writer.println("Internal server error: " + error.getMessage());
        writer.println();
        error.printStackTrace(writer);
      }

      error.printStackTrace();
    }

    return;
  }

}
```

The key element of this class is `performRequest(MethodInvoker methodInvoker)` method, where the processing
of the entire request takes place. Implementing this method, we define the logic of the security check and rendering.
It turns out that there is no need to invent something fabulous as Spring-Security, all Security is given to the user
for implementation, and a very convenient way to connect security of any complexity is available. 

If security is not needed yet, cut out authRegister field and prepareSession method

Having these classes, you can already prepare a servlet that will process the request. Here's something like this:
```java
import kz.greetgo.depinject.core.BeanGetter;
import kz.greetgo.mvc.interfaces.TunnelExecutorGetter;
import kz.greetgo.mvc.interfaces.Views;
import kz.greetgo.mvc.model.UploadInfo;
import kz.greetgo.mvc.war.AppServlet;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class ControllerServlet extends AppServlet {

  /**
    * There can be some controllers, so this method returns a list of controllers
    */
  @Override
  protected List<Object> getControllerList() {
    List<Object> ret = new ArrayList<>();
    ret.add(new ClientController());//here we specify our controller
    return unmodifiableList(ret);
  }

  @Override
  protected Views getViews() {
    return new ViewsImpl();//do not forget to cut out authRegister and prepareSession here
  }

  @Override
  protected UploadInfo getUploadInfo() {
    final UploadInfo ret = new UploadInfo();
    ret.maxFileSize = 50_000_000;
    ret.fileSizeThreshold = 1_000;
    return ret;
  }

  @Override
  protected void afterRegister() {

    System.err.println("[ControllerServlet] --------------------------------------");
    System.err.println("[ControllerServlet] -- USING CONTROLLERS:");
    for (TunnelExecutorGetter teg : tunnelExecutorGetters) {
      System.err.println("[ControllerServlet] --   " + teg.infoStr());
    }
    System.err.println("[ControllerServlet] --------------------------------------");

    super.afterRegister();
  }

  @Override
  protected String getTargetSubContext() {
    return "/api";//it is important to put the right way to servlet here
  }
}

```

Now this servlet can be registered in the application in the following way:

```java
import kz.greetgo.sandbox.server.beans.ControllerServlet;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

public class Application implements ServletContainerInitializer {

  @Override
  public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
    ControllerServlet controllerServlet = new ControllerServlet();

    // register method is implemented in AppServlet
    controllerServlet.register(ctx);

  }
}
```

If you now compile a war file, for example with the name: asd.war, and put it into the webapps folder on tomcat, you can
make a request:

    GET http://localhost:8080/asd/api/client/surname?id=222

And to receive the message in response body:

    surname of 222

The library has all the necessary capabilities to implement Rest requests in SpringMVC manner, but, at the same time,
it is very small and do not depend on any infrastructure.
