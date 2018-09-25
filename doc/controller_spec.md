### Ссылки

 - [Концепция](concept.md)
 - [Проект-пример mvc.war.example (быстрая установка и запуск)](mvc_war_example.md)
 - [Спецификация контроллеров]
   - [Доступ к параметрам запроса](#access-to-request-parameters)
     - [Аннотация @Par (простые параметры)](#base-example)
     - [Аннотация @Par с @Json (структурные параметры в формате JSON)](#json-parameter-example)
     - [Аннотация @ParamsTo (все параметры в один класс)](#params-to-example)
     - [Аннотация @ParPath (параметры из URL‐пути)](#parpath-example)
     - [Аннотация @ParSession (параметры из сессии)](#parsession-example)
     - [Аннотация @RequestInput (тело запроса как параметр)](#requestinput-help)
   - [Возврат из метода контроллера](#controller-method-return)
     - [Аннотация @ToJson](#annotation-tojson)
     - [Аннотация @ToXml](#annotation-toxml)
     - [Аннотация @AsIs](#annotation-asis)
     - [Производство редиректа](#using-redirect)

### Спецификация контроллеров

Контроллеры - это инстанции классов, содержащие методы с аннотациями. В этих аннотациях прописана информация
о rest-запросах, которые эти методы обслуживают. Идея взята из SpringMVC, но упрощены аннотации. Чтобы контроллеры
стали обслуживать rest-запросы необходимо их подключить к специальной инфраструктуре, которая
описана в [концепции](concept.md)

Разработчиками составлен специальный [проект-пример](mvc_war_example.md), в котором продемонсрированы
все возможные способы, и на который ссылается спецификация далее.

##### Access to Request Parameters
### Доступ к параметрам запроса

###### Base Example
#### Аннотация @Par (простые параметры)
Получать значения параметров запроса можно через аргументы метода контроллера помеченные аннотацией `@Par`.
Например: к контроллере `RequestParametersController` есть метод:

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
> Пример его вызова находиться в файле: `greetgo.mvc.war.example/war/webapps/jsp/request_parameters/base_example.jsp`
>
> В [проекте-примере](mvc_war_example.md) заходим сюда: http://localhost:10000/mvc_example/api/request_parameters/form#base-example

Если сделать запрос:

    GET /request_parameters/base-example?helloMessage=HI&age=19

То значения `HI` и `19` присвоятся аргументам `helloMessage` и `age` соответственно. Притом `age` автоматически
преобразуется в тип `int`.

Имя параметра определяет аннотация @Par. Имя аргумента метода может быть любым. Рекомендуется чтобы они совпадали, дабы
избежать ненужную путанницу.

К сожалению механизм Java Reflection не позволяет получать доступ к имени параметра метода, поэтому приходиться
довольствоваться параметрами аннотации. Есть возможность вытащить эту информации из отладочной информации, но намеренно
это не сделанно, во избежание путанницы (например, если откомпилировать без отладочной информации, то всё сломается).

**@Par List<...>** Спецификация Rest позволяет один и тот же параметр указывать несколько раз - это параметр-массив.
В этом случае аннотация `@Par` предоставит только одно первое значение. Если же необходимо получить все значения, то 
в качестве типа аргумента метода контроллера
следует выбрать `java.util.List<здесь указать нужный тип>` и аргумент будет получать все передаваемые значения.
В проекте примере таким параметров является `address`.

Аннотация @Par может преобразовывать данные в следующие типы:

| Тип аргумента с аннотацией `@Par` | Особенности конвертации при передаче |
|---|---|
| `String` | Копируется как есть. Если параметр не указан вообще, то аргументу присваивается `null`. Если параметр передали, но пустым, то аргументу присваивается пустая строка |
| `int`, `long` | Значение параметра конвертится в число и присваивается аргументу. Если параметр не указан, то аргументу присваивается `0`. Если конвертация в число не удаётся, то запрос прерывается с ошибкой 500. |
| `Integer`, `Long` | Передаваемое значение параметра конвертиться в число и присваивается аргументу. Если параметр не указан, то аргументу присваивается `null`. Если конвертация в число не удаётся, то запрос прерывается с ошибкой 500. |
| Enums | Если передаваемый параметр состоит из одних цифр, то предполагается что это ordinal, по нему выбирается элемент enum-а и присваивается аргументу. Если параметр содержит хотябы одну не цифру, то значение enum-а вычисляется с помощью функции `Enum.valueOf` и присваивается аргументу. Если такого элемента нет в enum-е, то возникает ошибка 500. Если параметр пустой или не указан, то аргументу присваивается `null`. |
| `boolean`, `Boolean` | Если параметр отсутствует или передаваемое значение параметра пустое или равно одному из: "0", "false", "f", "off", "no", "n" (с любым регистром), то аргумету присваивается `false`, в остальных случаях - `true`. Типу Boolean ни при каких обстоятельствах не присваивается `null`. |
| `java.util.Date` | Передаваемый параметр тримится и преобразуется в дату с помощью `SimpleDateFormat`, используя следующие паттерны: `"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd", "dd.MM.yyyy HH:mm:ss", "dd.MM.yyyy HH:mm", "dd.MM.yyyy", "dd/MM/yyyy HH:mm:ss", "dd/MM/yyyy HH:mm", "dd/MM/yyyy"` в указанном порядке, и присваивается аргумету. Если ни один паттерн не подошёл, то генерируется 500 ошибка. Если параметра нет или он пустой, то аргументу присваивается `null`.  |
| `BigDecimal`, `BigInteger`, `Double`, `Float` | У передаваемого параметра удаляются все пробелы и знаки подчёркивания, запятые меняются на точки и преобразуются в соответствующие типы контрукторами или методом `valueOf`, полученное значение присваивается аргументу. Если параметр не передаётся, или он пустой, или содержит только пробелы, то аргументу присваивается `null` |
| `double`, `float` | У передаваемого параметра удаляются все пробелы и знаки подчёркивания, запятые меняются на точки и преобразуются в соответствующие типы методом `valueOf`, полученное значение присваивается аргументу. Если параметр не передаётся, или он пустой, или содержит только пробелы, то аргументу присваивается `0` |

###### Json Parameter Example
#### Аннотация @Par с @Json (структурные параметры в формате JSON)

Аннотация `@Par` может также работать с более сложными структурами, передаваемыми в формате JSON. Для этого к этой
аннотации можно добавить аннотацию `@Json` как в приведённом миже примере:

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

Например параметр `clientToSave` нужно приготовить так:
```javascript
  
  //Например есть объект, который надо отослать на сервер
  var clientToSave = {
     id: "sa676hyu",
     surname: "Smith",
     name: "John"
  };

  // Вот так можно составить запрос GET
  var uri = "../par-json-example?clientToSave=" + encodeURIComponent(JSON.stringify(clientToSave));
  
  console.log("uri = " + uri);

```

> Другой пример вызова находиться в файле: `greetgo.mvc.war.example/war/webapps/jsp/request_parameters/par_json_example.jsp`
>
>В [проекте-примере](mvc_war_example.md) смотрите здесь: http://localhost:10000/mvc_example/api/request_parameters/form#par-json-example

###### Params To Example
#### Аннотация @ParamsTo (все параметры в один класс)

Все переметры запроса можно передавть одному аргументу-классу. Для этого можно воспользоваться аннотацией `@ParamsTo`,
как приведено на примере ниже:

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

> Пример его вызова находиться в файле: `greetgo.mvc.war.example/war/webapps/jsp/request_parameters/params_to_example.jsp`
>
> В [проекте-примере](mvc_war_example.md) смотрите здесь: http://localhost:10000/mvc_example/api/request_parameters/form#params-to-example


###### ParPath Example
#### Аннотация @ParPath (параметры из URL‐пути)

Параметры можно передавать через URL-путь, используя фигурные скобки - `{имя_параметра}`. Например так:

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

Тогда, если сделать запрос:

    GET /request_parameters/par-path-example/id:4567/John

То аргументу `id` присвоиться значение `4567`, а аргументу `name` присвоиться значение `John`.

> Пример находиться в файле: `greetgo.mvc.war.example/war/webapps/jsp/request_parameters/par_path_example.jsp`
>
> В [проекте-примере](mvc_war_example.md) смотрите здесь: http://localhost:10000/mvc_example/api/request_parameters/form#par-path-example

Если в аннотации `@ParPath` указать параметр, которого нет в аннотации `@Mapping` в фигурных скобках, то, при вызове,
произойдёт ошибка 500. В лог вывалиться ошибка `kz.greetgo.mvc.errors.NoPathParam`.

###### ParSession Example
#### Аннотация @ParSession (параметры из сессии)

Параметры можно получать из сессии. Для этого используется аннотация `@ParSession`. Вот пример метода контроллера,
который использует эту аннотацию:

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

Для получения значения запрашиваемого параметра из сессии библиотека обращается к методу: 
`kz.greetgo.mvc.interfaces.Views#getSessionParameter(...)`, и то, что этот метод вернёт, передаётся на запрашиваемый
параметр. Вот пример реализации этого метода:

```java
public class ViewsImpl implements kz.greetgo.mvc.interfaces.Views {
  /**
   * Этот метод вызывается, когда необходимо заполнить параметр метода контроллера
   * помеченный аннотацией {@link ParSession}
   *
   * @param context информация о параметре: что за параметр, его тип и пр.
   * @param tunnel  тунель запроса - дан для того, чтобы можно было получить какие-нибудь данные для параметра
   * @return значение этого параметра: оно будет подставлено в этот параметр
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

> Пример его вызова находиться в файле: `greetgo.mvc.war.example/war/webapps/jsp/request_parameters/par_session_example.jsp`
>
> В [проекте-примере](mvc_war_example.md) смотрите здесь: http://localhost:10000/mvc_example/api/request_parameters/form#par-session-example

###### RequestInput Help
#### Аннотация @RequestInput (тело запроса как параметр)

Можно обращаться ко всему телу запроса как к одному параметру. Для этого используется аннотация `@RequestInput`.
Пример использования:

```java
@ControllerPrefix("/some_prefix")
public class SomeController {
  @OnPost("/some_suffix")
  public String someMethod(@RequestInput String content) {
    //...
  }
}
```

Возможны следующие варианты использования этой аннотации:

| Определение аргумента метода контроллера|Описание|
|---|---|
|`@RequestInput String content`|Всё тело запроса будет преобразовано в строку, используя кодировку UTF-8, и передано параметру `content`|
|`@RequestInput List<String> lines`|Всё тело запроса будет преобразовано в строку, используя кодировку UTF-8, разрезано на строки по \\n или по \\r\\n, и, в качесте списка, передано аргументу `lines` |
|`@RequestInput byte[] content`|Всё тело запроса будет передано параметру `content` как массив байтов|
|`@RequestInput @Json SomeClass object`|Всё тело запроса будет будет рассмотрено, как JSON, и десериализовано в указанный объект. Если тело запроса пустое, то будет передан `null`|
|`@RequestInput @Json List<SomeClass? object`|Всё тело запроса будет будет рассмотрено, как JSON, и десериализовано в список указанных объектов (корневым элементов JSON-а должен быть массив). Если тело запроса пустое, то будет передан пустой массив|
|`@RequestInput InputStream inputStream`|Тело запроса предоставляется как `InputStream`|
|`@RequestInput BufferedReader reader`|Тело запроса предоставляется как `BufferedReader` через кодировку в запросе|
|`@RequestInput Reader reader`|Тело запроса предоставляется как `BufferedReader` через кодировку в запросе|

###### Controller Method Return
### Возврат из метода контроллера

В таблице [Controller Method Return Table](concept.md#controller-method-return-table) описаны способы,
которыми обрабатываются результаты методов контроллеров, реализлованные методом
[MethodInvokedResult.tryDefaultRenderer()](concept.md#method-viewsperformrequest).

###### Annotation @AsIs

Если же метод контроллера помечен аннотацией `@AsIs`, то этот метод должен возвращать строку. Эта строка будет
преобразована в текст в кодировке UTF-8, и полученный текст будет отправлен в тело ответа на запрос.

> Пример его использования находиться в файле: `greetgo.mvc.war.example/war/webapps/jsp/method_returns/using_as_is.jsp`
>
> В [проекте-примере](mvc_war_example.md) смотрите здесь: http://localhost:10000/mvc_example/api/method_returns/form#using-as-is

###### Annotation @ToJson

Если метод контроллера пометить аннотацией `@ToJson`, то предполагается, что возвращённый объект метода контроллера
должен конвертироваться JSON, и полученный JSON уйти в теле ответа на запрос. Это реализовано тем, что вызывается
метод `Views.toJson` и передаётся ему объект, возвращённый методом контроллера,  в качестве первого аргумента. А строка,
которую вернёт метод `Views.toJson` будет преобразована в текст в кодировке UTF-8, и полученный текст будет отправлен
в тело ответа на запрос. 

> Пример его использования находиться в файле: `greetgo.mvc.war.example/war/webapps/jsp/method_returns/using_to_json.jsp`
>
> В [проекте-примере](mvc_war_example.md) смотрите здесь: http://localhost:10000/mvc_example/api/method_returns/form#using-to-json

###### Annotation @ToXml

Если метод контроллера пометить аннотацией `@ToXml`, то предполагается, что возвращённый объект метода контроллера
должен конвертироваться XML, и полученный XML уйти в теле ответа на запрос. Это реализовано тем, что вызывается
метод `Views.toXml` и передаётся ему объект, возвращённый методом контроллера,  в качестве первого аргумента. А строка,
которую вернёт метод `Views.toXml` будет преобразована в текст в кодировке UTF-8, и полученный текст будет отправлен
в тело ответа на запрос. 

> Пример его использования находиться в файле: `greetgo.mvc.war.example/war/webapps/jsp/method_returns/using_to_xml.jsp`
>
> В [проекте-примере](mvc_war_example.md) смотрите здесь: http://localhost:10000/mvc_example/api/method_returns/form#using-to-xml

###### Using Redirect

Если метод контроллера вернёт объект `kz.greetgo.mvc.model.Redirect`, то соответствующий редирект
отправится в ответ запроса. При этом указанный редирект может содержать кукие, которые будут корректно добавлены
в заголовки ответа на запрос.

> Пример его использования находиться в файле: `greetgo.mvc.war.example/war/webapps/jsp/method_returns/return_redirect.jsp`
>
> В [проекте-примере](mvc_war_example.md) смотрите здесь: http://localhost:10000/mvc_example/api/method_returns/form#return-redirect

Если метод контроллера сгенерирует исключение класса `kz.greetgo.mvc.model.Redirect`, то соответствующий редирект
отправится в ответ запроса. При этом указанный редирект может содержать кукие, которые будут корректно добавлены
в заголовки ответа на запрос.

> Пример его использования находиться в файле: `greetgo.mvc.war.example/war/webapps/jsp/method_returns/throw_redirect.jsp`
>
> В [проекте-примере](mvc_war_example.md) смотрите здесь: http://localhost:10000/mvc_example/api/method_returns/form#throw-redirect

###### User Renderer

Во всех остальных случаях пользователю необходимо самостоятельно обрабатывать результаты методов контроллеров. Более
детально это описано в [концепции](concept.md). В проекте-примеры все формы, оттображаемые в браузере, реализованы
посредством jsp-рендеринга. В методе `ViewsImpl.performRequest` происходит вызов метода `ViewsImpl.performRender`,
в котором происходит форвардинг на jsp-рендеринг. Имя jsp-файла берётся из возврата метода контроллера.

В [проекте-примере](mvc_war_example.md) такие методы, как: `RequestParametersController.form`,
`MethodReturnsController.form`, `MethodReturnsController.returnRedirectParam1` и другие, возвращают необходимый им
для рендеринга jsp-файл.
