### Ссылки

 - [Концепция]
 - [Проект-пример mvc.war.example (быстрая установка и запуск)](mvc_war_example.md)
 - [Спецификация контроллеров](controller_spec.md)

### Концепция

Идея о том, как реализовать обработку Rest-запросов взята из SpringMVC, только реализация значительно
упрощена, а также, по мнению автора, упрощён способ описания запросов. Также у greetgo.mvc нет интеграции
в дебри какого-то фрэймворка, как у SpringMVC, т.е. SpringMVC нельзя использовать без других компонентов Spring,
а greetgo.mvc - это независимая полноценная библиотека, реализующая только MVC в стиле SpringMVC.

####### Method Views performRequest
##### Метод `Views.performRequest`

Важно понять, что **любой запрос приводит не к вызову метода контроллера, а к вызову метода `Views.performRequest`**,
и уже из нутри этого метода нужно вызвать метод контроллера. Но не беспокойтесь, Вам не придётся разбираться какой
контроллер и метод в нём надо вызвать - вся эта подготовительная работа уже сделана. Если посмотреть на спецификацию
этого метода:

    void performRequest(MethodInvoker methodInvoker)

То можно видеть, что ему передаётся `MethodInvoker`, в котором подготовлено всё, что необходимо для простого вызова
метода контроллера, и обработки результатов работы метода контроллера. Чтобы вызвать метод контроллера необходимо
написать такой код:

```
1.  MethodInvokedResult invokedResult = methodInvoker.invoke();
2.  boolean renderedOk = invokedResult.tryDefaultRender();
3.  if (renderedOk) return;
4.  // обрабатываем результат самостоятельно ...
```

Здесь в строке 1 происходит вызов метода `invoke()`, где происходит вся необходимая работа по поиску нужного метода
контроллера, подготовки аргументов для него, непосредственно вызов найденного метода контроллера и запоминание
результатов работы метода контроллера. Результаты работы метода контроллера обворачиваются в класс `MethodInvokedResult`
и возвращаются. И далее предоставляется возможность работать с этими результатами пользователю. Многие результаты
библиотека может обработать самостоятельно, для этого предоставлен метод `tryDefaultRender()`, который и стоит вызвать
сразу после метода `invoke` - это показано в строке 2. Метод возвращает признак того, смог ли он обработать.

###### Controller Method Return Table

| Метод `tryDefaultRender()` возвращает `true` в случаях:|Что делает метод в этом случае|
|---|---|
| Метод контроллера помечен аннотацией @ToJson|Вызывает метод `Views.toJson` передавая в качестве первого аргумента то, что вернул метод контроллера|
| Метод контроллера помечен аннотацией @ToXml |Вызывает метод `Views.toXml`  передавая в качестве первого аргумента то, что вернул метод контроллера|
| Метод контроллера помечен аннотацией @AsIs  |Метод котроллера должен возвращать строку. Эта строка как есть уходит в ответ запроса в кодировке UTF-8. Если метод контроллера не возвращает строку, то генерируется ошибка при анализе контроллеров (обычно это делается при старте приложения)|
| Метод контроллера вернул объект `kz.greetgo.mvc.model.Redirect` | В ответ запроса отправляет указанный редирект. У указанного объекта могут быть определены кукисы - они применяются |
| В методе контроллера сгенерировалось исключение `kz.greetgo.mvc.model.Redirect` | В ответ запроса отправляет указанный редирект. У указанного объекта могут быть определены кукисы - они применяются |

Если обработать смог по этой таблице, то возвращается `true`. В этом случае
вся работа по обработке запроса уже сделана и можно выходить, как показано в строке 3.

Ну а в случаях, не описанных в этой таблице, возвращается `false` и придётся готовить ответ запроса самостоятельно,
как указано в строке 4.

Самостоятельная обработка заключается в двух основных вещах: 1) это обработка ошибки (исключения), 2) рендеринг,
например в HTML при помощи JSP. Хотя можно использовать не JSP, а любой другой вариант - всё в Ваших руках.

Могут быть и другие специфичные для Вашей системы варианты. 

Чтобы понять было ли выброшено исключение при вызове метода контроллера, необходимо вызвать метод
`MethodInvokedResult.error()` - он возвращает объект-исключение (Exception) у которого можно запросить стэк-трэйс и 
сбросить его в лог и/или в ответ запроса (это лучшая практика), или сделать что-нибудь ещё на выбор пользователю.

Если этот метод вернёт `null`, то метод контроллера отработал без исключений. 

В этом случае, стандартный подход - это когда предполагается, что метод контроллера возвращает имя jsp-файла,
который должен сформировать тело ответа запроса. Это реализуется с помощью механизма
форвардинга (forward), напрмер так:

    methodInvoker.tunnel().forward(то_что_вернул_метод_контроллера);

Метод контроллера может передать jsp-файлу какие-то параметры. Для этого он подключит `MvcModel` в качестве аргумента
и передаст этой модели необходимые параметры. И перед форвардингом эти параметры необходимо скопировать в tunnel, для
этого нужно получить доступ к MvcModel. Это делается посредством вызова метода `methodInvoker.model()`,
в которой находятся все переданные данные

Так как не библиотека производит вызов метода контроллера, а пользователь, то он может сделать различные проверки или
подготовительные работы до или после вызова метода контроллера, например перед вызовом `methodInvoker.invoke()` можно
подготовить сессию и проверить права доступа. Можно запомнить время выполнения метода контроллера и скинуть его в лог.

##### Пример подключения

Допустим у нас есть какой-нибудь такой контроллер:

```java
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.mvc.core.*;

@Mapping("/client")
public class ClientController {
  
  @AsIs
  @Mapping("/surname")
  @MethodFilter(RequestMethod.GET)
  public String getSurname(@Par("id") String id) {
    return "surname of " + id;
  }
  
}
```

Вначале нужно определить стратегию вызова метода контроллера. Для этого необходимо
реализовать интерфейс `kz.greetgo.mvc.interfaces.Views`, например так:

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
 * В этом классе реализована обработка методов контроллеров
 */
public class ViewsImpl implements Views {

  private final ObjectMapper objectMapper = new ObjectMapper();

  /**
   * Этот метод вызывается после вызова метода контроллера помеченного аннотацией {@link ToJson}.
   *
   * @param object сюда подаётся объект, который вернул метод контроллера
   * @param tunnel это тунель запроса (через него можно управлять процессом запроса)
   * @param method ссылка на метод контроллера, который был только-что вызван
   * @return эта строка будет отправлена в качестве тела ответа на запрос, зашифрованной в кодировке UTF-8.
   * @throws Exception нужно чтобы не ставить надоедливые try/catch-блоки
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
   * Этот метод вызывается после вызова метода контроллера помеченного аннотацией {@link ToXml}.
   *
   * @param object сюда подаётся объект, который вернул метод контроллера
   * @param tunnel это тунель запроса (через него можно управлять процессом запроса)
   * @param method ссылка на метод контроллера, который был только-что вызван
   * @return эта строка будет отправлена в качестве тела ответа на запрос, зашифрованной в кодировке UTF-8.
   * @throws Exception нужно чтобы не ставить надоедливые try/catch-блоки
   */
  @Override
  public String toXml(Object object, RequestTunnel tunnel, Method method) throws Exception {
    //Здесь нужно object преобразовать в XML и вернуть
    //Здесь аннотация ToXml не работает
    throw new UnsupportedOperationException();
  }

  /**
   * Данное поле содержит ссылку на объект, в котором сосредоточена логика работы с security
   */
  public BeanGetter<AuthRegister> authRegister;

  /**
   * Этот метод вызывается, каждый раз при обработке запроса. Метод контроллера ещё не вызван, и его нужно вызвать
   * из этого метода. А можно и не вызвать, например потому-что нет прав или ещё по какой причине.
   *
   * @param methodInvoker исполнитель метода контроллера - вспомогательный объект, в котором подготовлено всё
   *                      необходимое для вызова метода контроллера,
   *                      и для изучения вызываемого метода контроллера. Например можно посмотреть какие аннотации есть
   *                      у метода и провести дополнительные манипуляции.
   * @throws Exception нужно чтобы не ставить надоедливые try/catch-блоки
   */
  @Override
  public void performRequest(MethodInvoker methodInvoker) throws Exception {

    //вызываем этот метод, чтобы в дальнейшем можно было получить момент непосредственно перед вызовом метода контроллера
    beforeRequest();

    //подготавливаем сессию. Здесь может произойти ошибка, например повреждён токен. И тогда метод вызва не будет
    prepareSession(methodInvoker);

    //вызываем этот метод, чтобы в дальнейшем можно было получить момент непосредственно перед вызовом метода контроллера
    beforeRequestWithSession();

    //вызываем метод контроллера и получаем результат вызова метода
    MethodInvokedResult invokedResult = methodInvoker.invoke();

    //пытаемся зарендерить результат поведением по-умолчанию. Таким поведением являются аннотации: ToJson, ToXml, AsIs
    if (invokedResult.tryDefaultRender()) {
      //поведение по-умолчанию получилось применить. Это значит что запрос полностью обработан и
      //больше ничего делать не нужно - выходим
      return;
    }

    //здесь нужно обработать специфичный результат работы метода контроллера, например прорендерить JSP или оттобразить
    //ошибку, или ещё что-то

    //смотрим была ли ошибка в метода
    if (invokedResult.error() != null) {
      //обрабатываем ошибку
      performError(methodInvoker, invokedResult);
    } else {
      //обрабатываем нормальное поведение
      performRender(methodInvoker, invokedResult);
    }
  }

  /**
   * Этот метод вызывается всегда перед вызовом метода контроллера, но уже после проверки безопасности.
   * Если проверка безопасности не прошла, то этот метод не вызывается
   *
   * @throws Exception нужно чтобы не ставить надоедливые try/catch-блоки
   */
  private void beforeRequestWithSession() throws Exception {}

  /**
   * Этот метод вызывается всегда перед вызовом метода контроллера
   *
   * @throws Exception нужно чтобы не ставить надоедливые try/catch-блоки
   */
  protected void beforeRequest() throws Exception {}

  /**
   * Осуществляет подготовку сессии и сохранения её в LocalThread-переменной.
   *
   * @param methodInvoker исполнитель метода контроллера
   */
  private void prepareSession(MethodInvoker methodInvoker) {
    try {
      //смотрим, есть ли у вызываемого метода аннотация NoSecurity
      if (methodInvoker.getMethodAnnotation(NoSecurity.class) == null) {
        // если аннотации нет, то нужно проверить на наличие прав


        //Достаём токен из заголовка запроса. Если токена нет, то получим null
        String token = methodInvoker.tunnel().getRequestHeader("Token");

        //в этом методе токен будет расшифрован и помещён в ThreadLocal-переменную
        //если произойдёт какой-нибудь сбой, то произойдёт ошибка и вызов метода контроллера не произойдёт
        //тем самым мы предотвратим вероятный взлом
        authRegister.get().checkTokenAndPutToThreadLocal(token);
      } else {

        // если есть аннотация NoSecurity то это значит, что метод не нуждается в параметрах сессии и не
        // нуждается в защите - т.е. его может вызвать любой. Таким методом например является логинг.
        // В этом случае мы очищаем ThreadLocal-переменную
        authRegister.get().cleanTokenThreadLocal();
      }
    } catch (RestError restError) {
      restError.printStackTrace();
    }
  }

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
      if (context.expectedReturnType() != String.class) throw new SecurityError("personId must be string");

      //sessionInfo берётся из ThreadLocal переменной, которая был определена в методе prepareSession
      SessionInfo sessionInfo = authRegister.get().getSessionInfo();
      if (sessionInfo == null) throw new SecurityError("No session");
      return sessionInfo.personId;
    }

    throw new SecurityError("Unknown session parameter " + context.parameterName());
  }

  /**
   * Здесь происходиь рендеринг вьюшки запроса
   *
   * @param methodInvoker исполнитель метода контроллера
   * @param invokedResult результаты вызова метода контроллера
   */
  private void performRender(MethodInvoker methodInvoker, MethodInvokedResult invokedResult) {
    assert invokedResult.error() == null;
    //возвращённое методом контроллера значение
    Object returnedValue = invokedResult.returnedValue();
    if (returnedValue == null) return;

    //обрабатываем только строки. Не понятно как обрабатывать другие типы.
    if (!(returnedValue instanceof String)) {
      throw new IllegalArgumentException("Cannot view " + returnedValue.getClass()
        + " with value " + returnedValue);
    }

    //предполагаем, что возвращённое значение - это локальный путь к jsp-файлу, например: jsp/hello.jsp
    String place = (String) returnedValue;

    RequestTunnel tunnel = methodInvoker.tunnel();

    //заполняем данные для вьюшки, которые будут доступны через $ например $hello - в методе их добавляли в MvcModel
    for (Map.Entry<String, Object> e : methodInvoker.model().data.entrySet()) {
      tunnel.setRequestAttribute(e.getKey(), e.getValue());
    }

    //форвардим на рендеринг jsp-файла
    tunnel.forward("/jsp/" + place, true);
  }

  /**
   * Обрабатывается ошибка запроса
   *
   * @param methodInvoker информация о методе контроллера для обработки запроса
   * @param invokedResult результаты вызова метода контроллера
   * @throws Exception нужно чтобы не ставить надоедливые try/catch-блоки
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

Ключевым элементом этого класса является метод `performRequest(MethodInvoker methodInvoker)`, где и происходит обработка
всего запроса. Реализуя этот метод, мы определяем логику проверки безопасности и рендеринга. Получается, что
нет необходимости придумывать что-то грандиозное типа Spring-Security, вся Security отдаётся на реализацию пользователя
самому, и даётся очень удобный способ подключения security любой сложности.

Если security пока не нужно вырежите поле authRegister и метод prepareSession.

Имея эти классы можно уже подготовить сервлет, который будет обрабатывать запрос. Вот примерно так:
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
    * Контроллеров может быть несколько, поэтому этот метод возвращает список контроллеров
    */
  @Override
  protected List<Object> getControllerList() {
    List<Object> ret = new ArrayList<>();
    ret.add(new ClientController());//здесь указываем наш контроллер
    return unmodifiableList(ret);
  }

  @Override
  protected Views getViews() {
    return new ViewsImpl();//незабудьте здесь вырезать authRegister и prepareSession
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
    return "/api";//важно здесь поставить правильный путь к сервлету
  }
}

```

Теперь этот сервлет можно зарегистрировать в приложении примерно таким образом:

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

    // метода register реализован в AppServlet
    controllerServlet.register(ctx);

  }
}
```

Если теперь собрать war-файл например с таким именем: asd.war, и положить его в папку webapps на tomcat-е, то можно
будет сделать запрос:

    GET http://localhost:8080/asd/api/client/surname?id=222

И получить в теле ответа сообщение:

    surname of 222

Библиотека обладает всеми необходимыми возможностями для реализация Rest запросов в стиле SpringMVC, но, при этом, очень
маленькая и не зависить от той или иной инфраструктуры.
