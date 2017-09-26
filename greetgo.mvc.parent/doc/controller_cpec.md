### Ссылки

 - [Концепция](concept.md)
 - [Спецификация контроллеров]

### Спецификация контроллеров

Контроллеры - это инстанции классов, содержащие методы с аннотациями. В этих аннотациях прописана информация
о rest-запросах, которые эти методы обслуживают. Идея взята из SpringMVC, но упрощены аннотации.

#### Приблизительный вид стандартного метода контроллера

```java
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.MethodFilter;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.core.RequestMethod;
import kz.greetgo.mvc.model.MvcModel;

//
// Стандартный пример метода контроллера
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

    model.setParam("PARAM1", "value of param1");
    model.setParam("PARAM2", 678);

    model.setStatus(208);

    return "jsp/index.jsp";
  }

}
```

Если @Mapping у класса контроллера отсутствует, то значит у методов в мапинге не используется префикс.

#### MethodFilter
Аннотация MethodFilter позволяет отфильтровать только некоторые HTTP-методы запроса - те, которые перечисленны в
аннотации. Если эту аннотацию не указать, то фильтрации по HTTP-методам не будет - будут проходить все HTTP-методы.

#### Параметры запросов справа от знака ?

Параметры запросов справа от знака можно передать в аргумент метода с помощью аннотации @Par. При этом параметр
автоматически будет конвертирован в тип соответствующего параметра. 