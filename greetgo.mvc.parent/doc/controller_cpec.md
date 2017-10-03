### Ссылки

 - [Концепция](concept.md)
 - [Проект-пример mvc.war.example](mvc_war_example.md)
 - [Спецификация контроллеров]
   - [Доступ к параметрам запроса](#access-to-request-parameters)
     - [Аннотация @Par (простые параметры)](#base-example)
     - [Аннотация @Par с @Json (структурные параметры в формате JSON)](#json-parameter-example)
     - [Аннотация @ParamsTo (все параметры в один класс)](#params-to-example)
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
#### Аннотация @Par (простые параметры)
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
@Mapping("/request_parameters")
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
  @Mapping("/par-json-example")
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

В [проекте-примере](mvc_war_example.md) смотрите здесь: http://localhost:10000/mvc_example/api/request_parameters/form#par-json-example

###### Params To Example
#### Аннотация @ParamsTo (все параметры в один класс)

Все переметры запроса можно передавть одному аргументу-классу. Для этого можно воспользоваться аннотацией `@ParamsTo`,
как приведено на примере ниже:

```java
@Mapping("/request_parameters")
public class RequestParametersController {
  
  public static class Client {
    public String id;
    public String name;
    public BigDecimal amount;
    public List<String> addresses;
  }
  
  @AsIs
  @Mapping("/params-to-example")
  public String paramsToExample(@ParamsTo Client client) {
    return "called RequestParametersController.paramsToExample with\n" +
      "    client = " + client;
  }
  
}
```

В [проекте-примере](mvc_war_example.md) смотрите здесь: http://localhost:10000/mvc_example/api/request_parameters/form#params-to-example

### MethodFilter

Аннотация MethodFilter позволяет отфильтровать только некоторые HTTP-методы запроса - те, которые перечисленны в
аннотации. Если эту аннотацию не указать, то фильтрации по HTTP-методам не будет - будут проходить все HTTP-методы.
