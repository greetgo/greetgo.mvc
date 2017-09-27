### Ссылки

 - [Концепция](concept.md)
 - [Проект-пример mvc.war.example](mvc_war_example.md)
 - [Спецификация контроллеров]
   - [Доступ к параметрам запроса](#access-to-request-parameters)
     - [Простые параметры (аннотация @Par)](#base-example)
   - [MethodFilter](#methodfilter)

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
#### Простые параметры (аннотация @Par)
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

### MethodFilter

Аннотация MethodFilter позволяет отфильтровать только некоторые HTTP-методы запроса - те, которые перечисленны в
аннотации. Если эту аннотацию не указать, то фильтрации по HTTP-методам не будет - будут проходить все HTTP-методы.
