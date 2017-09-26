## Описание методов контроллеров
### Концепция

Идея о том, как реализовать обработку Rest-запросов взята из SpringMVC, только реализация значительно
упрощена, а также, по мнению автора, упрощён способ подключения. Также у greetgo.mvc нет интеграции
в дебри какого-то фрэймворка, как у SpringMVC, т.е. SpringMVC нельзя использовать без друких компонентов Spring,
а greetgo.mvc - это независимая полноценная библиотека.

Допустим у нас есть какой-нибудь такой контроллер:

```java
import kz.greetgo.mvc.annotations.*;
import kz.greetgo.mvc.core.*;

@Mapping("/client")
public class ClientController {
  
  @AsIs
  @MethodFilter(RequestMethod.GET)
  @Mapping("/surname")
  public String getSurname(@Par("id") String id) {
    return "surname of " + id;
  }
  
}
```
