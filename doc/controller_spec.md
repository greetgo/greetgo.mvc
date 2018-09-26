### References

 - [Concept](concept.md)
 - [Example project mvc.war.example (Quick setup and launch)](mvc_war_example.md)
 - [Specification of controllers]
   - [Access to request parameters](#access-to-request-parameters)
     - [Annotation @Par (simple parameters)](#base-example)
     - [Annotation @Par с @Json (structural parameters in JSON format)](#json-parameter-example)
     - [Annotation @ParamsTo (all parameters in one class)](#params-to-example)
     - [Annotation @ParPath (parameters from URL path)](#parpath-example)
     - [Annotation @ParSession (parameters from session)](#parsession-example)
     - [Annotation @RequestInput (request body as a parameter)](#requestinput-help)
   - [Controller method return](#controller-method-return)
     - [Annotation @ToJson](#annotation-tojson)
     - [Annotation @ToXml](#annotation-toxml)
     - [Annotation @AsIs](#annotation-asis)
     - [Using redirect](#using-redirect)

### Specification of controllers

Controllers are the instances of classes that contain methods with annotations. These annotations contain information
about the rest-requests that serve these methods. The idea is taken from SpringMVC, but the annotations are simplified. 
In order to make controllers begin to serve rest-requests, they must be connected to a special infrastructure that
described in [concept](concept.md)

Developers created the special [example project](mvc_war_example.md), in which the
all possible methods are demonstrated, and to which the specification further refers.

### Access to Request Parameters

###### Base Example
#### Annotation @Par (simple parameters)
It is possible to get the values of request parameters through arguments of the controller method marked with an annotation `@Par`.
For example: `RequestParametersController` has the method:

```java
@ControllerPrefix("/request_parameters")
public class RequestParametersController {
  @AsIs
  @OnGet("/base-example")
  public String baseExample(@Par("helloMessage") String helloMessage, @Par("age") int age) {
    return "called RequestParametersController.baseExample with arguments:\n" +
      "    helloMessage = " + helloMessage + "\n" +
      "    age = " + age;
  }
}
```
> An example of its call  is in a file: `greetgo.mvc.war.example/war/webapps/jsp/request_parameters/base_example.jsp`
>
> In [example project](mvc_war_example.md) we enter here: http://localhost:10000/mvc_example/api/request_parameters/form#base-example

If you make a request:

    GET /request_parameters/base-example?helloMessage=HI&age=19

Then `HI` and `19` values are assigned to `helloMessage` and `age` arguments respectively. Besides `age` is automatically
converted to `int`.

The parameter name defines @Par annotation. The name of the method argument can be whatever. It is recommended that they match 
each othet so that avoid unnecessary confusion.

Unfortunately, the Java Reflection mechanism does not allow to access the method parameter name, so you have to
be content with the parameters of the annotation. There is an opportunity to pull this information out of debug information, but intentionally
this is not done, to avoid confusion (for example, if you compile without debug information, everything will fail).

**@Par List<...>** Rest specification allows to specify the same parameter for multiple times - it is an array parameter.
In this case,  `@Par` annotation will provide only one first value. If it is necessary to get all the values, то 
then you should select `java.util.List<specify the type here>` as the argument type of the controller method, and the argument will receive all the values transmitted.
In the example project, this parameter is `address`.

@Par annotation can convert data to the following types:

| The type of the argument with `@Par` annotation| Features of conversion during transmission |
|---|---|
| `String` | Copied as it is. If the parameter is not specified at all, then the argument is set to `null`. If the parameter is transmitted, but it is empty, then the argument is assigned with an empty string |
| `int`, `long` | The value of the parameter is converted to a number and assigned to the argument. If the parameter is not specified, then the argument is set to `0`. If the conversion to a number fails, the request is terminated with an error 500. |
| `Integer`, `Long` | The transmitted parameter value is converted to a number and assigned to the argument. If the parameter is not specified, then the argument is set to `null`. If the conversion to a number fails, the request is terminated with an error 500. |
| Enums | If the passed parameter consists of only digits, then it is assumed that this is ordinal, according to this enum element is chosen and assigned to the argument. If the parameter contains at least one non-digit, then the enum value is evaluated using `Enum.valueOf` function and assigned to the argument. If there is no such element in enum, an error 500 occurs. If the parameter is empty or not specified, then the argument is set to `null`. |
| `boolean`, `Boolean` | If the parameter is missing or the transmitted parameter value is empty or equal to the following: "0", "false", "f", "off", "no", "n" (with any register), the the argument is set to `false`, in other cases - `true`. Boolean type is never set to `null`. |
| `java.util.Date` | The transmitted parameter is trimmed and converted to a date with a help of `SimpleDateFormat`, using the following patterns: `"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd", "dd.MM.yyyy HH:mm:ss", "dd.MM.yyyy HH:mm", "dd.MM.yyyy", "dd/MM/yyyy HH:mm:ss", "dd/MM/yyyy HH:mm", "dd/MM/yyyy"` in the specified order, and assigned to the argument. If no pattern is right, then an error 500 is generated. If there is no parameter or it is empty, then the argument is set to `null`.  |
| `BigDecimal`, `BigInteger`, `Double`, `Float` | The transmitted parameter should not have any spaces and underscores, the commas are changed to points and are converted to the corresponding types by the constructors or `valueOf` method, the obtained value is assigned to the argument. If the parameter is not transmitted, or it is empty, or contains only spaces, then the argument is set to `null` |
| `double`, `float` | The transmitted parameter should not have any spaces and underscores, the commas are changed to points and are converted to the corresponding types using `valueOf` method, the obtained value is assigned to the argument. If the parameter is not transmitted, or it is empty, or contains only spaces, then the argument is set to `0` |

###### Json Parameter Example
#### @Par annotation with @Json (structural parameters in JSON format)

`@Par` annotation can also work with more complex structures transmitten in JSON format. To do this, these
annotations can be added with `@Json` annotation, as the following example shows:

```java
@ControllerPrefix("/request_parameters")
public class RequestParametersController {
  public static class ClientToSave {
    public String id;
    public String surname;
    public String name;
  }

  public static class AccountToSave {
    public String number;
    public BigDecimal amount;
    public Long typeId;
  }

  @AsIs
  @OnPost("/par-json-example")
  public String parJsonExample(@Par("clientToSave") @Json ClientToSave clientToSave,
                               @Par("accountToSave") @Json AccountToSave accountToSave
  ) {
    return "called RequestParametersController.parJsonExample with arguments:\n" +
      "    clientToSave  = " + clientToSave + "\n" +
      "    accountToSave = " + accountToSave;
  }
}
```

For example, `clientToSave` parameter shpuld be prepared in the following way:
```javascript
  
  //For example, there is an object that needs to be sent to the server
  var clientToSave = {
     id: "sa676hyu",
     surname: "Smith",
     name: "John"
  };

  // That's how you can compose a GET request
  var uri = "../par-json-example?clientToSave=" + encodeURIComponent(JSON.stringify(clientToSave));
  
  console.log("uri = " + uri);

```

> Another example of a call is in the file: `greetgo.mvc.war.example/war/webapps/jsp/request_parameters/par_json_example.jsp`
>
> In [example project](mvc_war_example.md) see here: http://localhost:10000/mvc_example/api/request_parameters/form#par-json-example

###### Params To Example
#### @ParamsTo annotation (all parameters in one class)

All request parameters can be transmitted to one argument-class. To do this,  `@ParamsTo` annotation can be used,
as the following example shows:

```java
@ControllerPrefix("/request_parameters")
public class RequestParametersController {
  
  public static class Client {
    public String id;
    public String name;
    public BigDecimal amount;
    public List<String> addresses;
  }
  
  @AsIs
  @OnPost("/params-to-example")
  public String paramsToExample(@ParamsTo Client client) {
    return "called RequestParametersController.paramsToExample with\n" +
      "    client = " + client;
  }
  
}
```

> An example of a call is in the file: `greetgo.mvc.war.example/war/webapps/jsp/request_parameters/params_to_example.jsp`
>
> In [example project](mvc_war_example.md) see here: http://localhost:10000/mvc_example/api/request_parameters/form#params-to-example


###### ParPath Example
#### @ParPath annotation (parameters from URL‐path)

Parameters can be transmitted through URL path using curly braces - `{parameter_name}`. For example:

```java
@ControllerPrefix("/request_parameters")
public class RequestParametersController {
  @AsIs
  @OnPost("/par-path-example/id:{id}/{name}")
  public String parPathExample(@ParPath("id") Long id, @ParPath("name") String name) {
    return "called RequestParametersController.parPathExample with\n" +
      "    id   = " + id + "\n" +
      "    name = " + name;
  }
}
```

Then, if you make a request:

    GET /request_parameters/par-path-example/id:4567/John

`id` argument is set to `4567` value, `name` argument is set to `John` value.

> An example is in the file:`greetgo.mvc.war.example/war/webapps/jsp/request_parameters/par_path_example.jsp`
>
> In [example project](mvc_war_example.md) see here: http://localhost:10000/mvc_example/api/request_parameters/form#par-path-example

If in `@ParPath` annotation you specify the parameter that is not in `@Mapping` annotation using curly braces, then, when called,
an error 500 will occur. The log will have `kz.greetgo.mvc.errors.NoPathParam` error.

###### ParSession Example
#### @ParSession annotation (параметры из сессии)

The parameters can be received from the session. To do this, `@ParSession` annotation is used. Here is an example of the controller method,
which uses this annotation:

```java
@ControllerPrefix("/request_parameters")
public class RequestParametersController {
  @AsIs
  @OnPost("/par-session-example")
  public String parSessionExample(@ParSession("personId") Long personId, @ParSession("role") String role) {
    return "called RequestParametersController.parSessionExample with\n" +
      "    personId = " + personId + "\n" +
      "    role     = '" + role + "'";
  }
}
```

To get the value of the requested parameter from the session, the library refers to the method:
`kz.greetgo.mvc.interfaces.Views#getSessionParameter(...)`, and what this method returns is transmitted to the requested
parameter. Here is an example of implementing this method:

```java
public class ViewsImpl implements kz.greetgo.mvc.interfaces.Views {
  /**
   * This method is called when it is needed to fill in the controller method parameter
   * marked with the annotation {@link ParSession}
   *
   * @param context parameter information: kind of parameter, its type, etc.
   * @param tunnel  request tunnel is given so that you can get any data for the parameter
   * @return the value of this parameter: it will be substituted in this parameter
   */
  @Override
  public Object getSessionParameter(SessionParameterGetter.ParameterContext context, RequestTunnel tunnel) {
    if ("personId".equals(context.parameterName())) {
      if (context.expectedReturnType() != Long.class) {
        throw new RuntimeException("Session parameter `personId` must be a Long");
      }
      return 543265L;
    }

    if ("role".equals(context.parameterName())) {
      if (context.expectedReturnType() != String.class) {
        throw new RuntimeException("Session parameter `role` must be a string");
      }
      return "role value taken from session";
    }

    throw new RuntimeException("Unknown session parameter " + context.parameterName());
  }
}
```

> An example of its call is in the file: `greetgo.mvc.war.example/war/webapps/jsp/request_parameters/par_session_example.jsp`
>
> In [example project](mvc_war_example.md) see here: http://localhost:10000/mvc_example/api/request_parameters/form#par-session-example

###### RequestInput Help
#### @RequestInput annotation (request body as a parameter)

You can call the entire request body as one parameter. To do this, `@RequestInput` annotation is used.
Here is an example of use:

```java
@ControllerPrefix("/some_prefix")
public class SomeController {
  @OnPost("/some_suffix")
  public String someMethod(@RequestInput String content) {
    //...
  }
}
```

The following ways of using this annotation are possible:

| Defining the controller method argument|Description|
|---|---|
|`@RequestInput String content`|The entire body of the request will be converted to a string using UTF-8 encoding, and transmitted to `content` parameter|
|`@RequestInput List<String> lines`|The entire body of the request will be converted to a string using UTF-8 encoding, cut into lines by \\n or by \\r\\n, and, in the form of a list, transmitted to the  `lines` argument|
|`@RequestInput byte[] content`|The entire body of the request will be transmitted to `content` parameter as an array of bytes |
|`@RequestInput @Json SomeClass object`|The whole body of the request will be considered as JSON, and deserialized to the specified object. If the request body is empty, `null` will be transmitted|
|`@RequestInput @Json List<SomeClass? object`|The whole body of the request will be considered as JSON, and deserialized to the list of specified objects (JSON root element should be an array). If the request body is empty, an empty array will be transmitted|
|`@RequestInput InputStream inputStream`|The request body is `InputStream`|
|`@RequestInput BufferedReader reader`|The request body is `BufferedReader` through the encoding in the request|
|`@RequestInput Reader reader`|The request body is `BufferedReader` through the encoding in the request|

### Controller Method Return

Thetable [Controller Method Return Table](concept.md#controller-method-return-table) describes the ways,
which processes the results of controllers methods, which implemented with the following method
[MethodInvokedResult.tryDefaultRenderer()](concept.md#method-viewsperformrequest).

###### Annotation @AsIs

If the controller method is marked with `@AsIs` annotation, then this method should return a string. This string will be
converted into text in UTF-8 encoding, and the received text will be sent to request response body.

> The example of its use is in the file: `greetgo.mvc.war.example/war/webapps/jsp/method_returns/using_as_is.jsp`
>
> In [example project](mvc_war_example.md) see here: http://localhost:10000/mvc_example/api/method_returns/form#using-as-is

###### Annotation @ToJson

If the controller method is marked with `@ToJson` annotation, then it is assumed that the returned object of the controller method
should be converted to JSON, and the resulting JSON will go away from the request response body. This is implemented by that
`Views.toJson` method is called, and the object returned by controller method is transmitted to it as the first argument. And the string,
which will be returned by `Views.toJson` method will be converted into text in UTF-8 encoding, and the received text will be sent to 
request response body.

> The example of its use is in the file: `greetgo.mvc.war.example/war/webapps/jsp/method_returns/using_to_json.jsp`
>
> In [example project](mvc_war_example.md) see here: http://localhost:10000/mvc_example/api/method_returns/form#using-to-json

###### Annotation @ToXml

If the controller method is marked with `@ToXml`annotation, then it is assumed that the returned object of the controller method
should be converted to XML, and the resulting XML will go away from the request response body. This is implemented by that
`Views.toXml` method is called, and the object returned by controller method is transmitted to it as the first argument. And the string,
which will be returned by `Views.toXml` method will be converted into text in UTF-8 encoding, and the received text will be sent to 
request response body.

> The example of its use is in the file: `greetgo.mvc.war.example/war/webapps/jsp/method_returns/using_to_xml.jsp`
>
> In [example project](mvc_war_example.md) see here: http://localhost:10000/mvc_example/api/method_returns/form#using-to-xml

###### Using Redirect

If the controller method returns `kz.greetgo.mvc.model.Redirect` object, then the corresponding redirect
will be sent to request response. In this case, the specified redirect may contain cookies that will be correctly added
to the headers of the request response.

> The example of its use is in the file: `greetgo.mvc.war.example/war/webapps/jsp/method_returns/return_redirect.jsp`
>
> In [example project](mvc_war_example.md) see here: http://localhost:10000/mvc_example/api/method_returns/form#return-redirect

If the controller method generates an exception of `kz.greetgo.mvc.model.Redirect` class, then the corresponding redirect
will be sent to request response. In this case, the specified redirect can contain cookies that will be correctly added
to the headers of the request response.

> The example of its use is in the file: `greetgo.mvc.war.example/war/webapps/jsp/method_returns/throw_redirect.jsp`
>
> In [example project](mvc_war_example.md) see here: http://localhost:10000/mvc_example/api/method_returns/form#throw-redirect

###### User Renderer

In all other cases, the user must process the results of the controller methods by himself. 
This is described in details in [Concept](concept.md). In example project, all forms displayed in the browser are implemented
through jsp-rendering. In `ViewsImpl.performRequest` method, `ViewsImpl.performRender` method is called,
in which there is a forwarding to jsp-rendering. The name of the jsp file is taken from the controller method return.

In [example project](mvc_war_example.md) the methods like: `RequestParametersController.form`,
`MethodReturnsController.form`, `MethodReturnsController.returnRedirectParam1` and others return jsp-file necessary for rendering.
