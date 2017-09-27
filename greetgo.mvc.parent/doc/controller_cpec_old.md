### Ссылки

 - [Концепция](concept.md)
 - [Проект-пример mvc.war.example](mvc_war_example.md)
 - [Спецификация контроллеров]
   - [Пример контроллера](#controller-example)
   - [MethodFilter](#methodfilter)
   - [Доступ к параметрам запроса](#access-to-request-parameters)

### Спецификация контроллеров

Контроллеры - это инстанции классов, содержащие методы с аннотациями. В этих аннотациях прописана информация
о rest-запросах, которые эти методы обслуживают. Идея взята из SpringMVC, но упрощены аннотации. Чтобы контроллеры
стали обслуживать rest-запросы необходимо их подключить к специальной инфраструктуре, которая
описана в [концепции](concept.md)

Разработчиками составлен специальный [проект-пример](mvc_war_example.md), в котором продемонсрированы
все возможные способы 

##### Controller Example
### Пример контроллера

```java
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.MethodFilter;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.core.RequestMethod;
import kz.greetgo.mvc.model.MvcModel;

//
// Пример метода контроллера
//
@Mapping({"/last/prefix", "/v1/prefix"})//этот мапинг общий для всех методов данного контроллера
public class SomeController {

  // Этот метод можно вызвать следующими запросами:
  // GET  /last/prefix/index?requestParam=asd&requestParamToLong=54356
  // GET  /last/prefix/home?requestParam=asd
  // GET  /v1/prefix/index?requestParamToLong=54356
  // GET  /v1/prefix/home
  // POST /last/prefix/index?requestParamToLong=54356&requestParam=asd
  // POST /last/prefix/home?requestParam=asd
  // POST /v1/prefix/index?requestParamToLong=54356
  // POST /v1/prefix/home
  @Mapping({"/index", "/home"})
  @MethodFilter({RequestMethod.GET, RequestMethod.POST})
  public String index(
    @Par("requestParam") String requestParam,
    @Par("requestParamToLong") long requestParamToLong,
    MvcModel model
  ) {

    //эти параметры можно будет использовать в jsp-файле так: ${PARAM1}, ${COOL_NUMBER}
    //эти параметры передаются в методе SandboxViews.performRender(...) [тут](concept.md)  
    model.setParam("PARAM1", "value of param1");
    model.setParam("COOL_NUMBER", 678);

    //так можно указать HTTP код ответа
    model.setStatus(208);

    //здесь мы указываем файл, который будет рендериться
    return "index.jsp";
  }

}
```

 - Примечание: параметры и jsp-файл передаются в методе SandboxViews.performRender(...), который можно найти [тут](concept.md)

Если @Mapping у класса контроллера отсутствует, то значит у методов в мапинге не используется префикс.

### MethodFilter

Аннотация MethodFilter позволяет отфильтровать только некоторые HTTP-методы запроса - те, которые перечисленны в
аннотации. Если эту аннотацию не указать, то фильтрации по HTTP-методам не будет - будут проходить все HTTP-методы.

##### Access to Request Parameters
### Доступ к параметрам запроса

#### Простые параметры
Получать значения параметров запроса можно через аргументы метода контроллера помеченные аннотацией `@Par`.
Например: к контроллере `RequestParametersController` есть метод:

```java
@Mapping("/request_parameters")
public class RequestParametersController {
  @AsIs
  @Mapping("/base-example")
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
