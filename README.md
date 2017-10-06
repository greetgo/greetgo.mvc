# greetgo.mvc

Облегчённая реализация паттерна MVC. Сделана по образу и подобию spring.mvc

 - [Концепция](greetgo.mvc.parent/doc/concept.md)
 - [Проект-пример mvc.war.example (быстрая установка и запуск)](greetgo.mvc.parent/doc/mvc_war_example.md)
 - [Спецификация контроллеров](greetgo.mvc.parent/doc/controller_spec.md)

# Возможности

### Функциональные возможности
  - Создана по образу и подобию SpringMVC (но без многих недостатков);
##### Параметры
  - Позволяет считывать параметры
    - с помощью `@Par`:
      - как первый элемент `...?asd=значение`  с помощью `@Par("asd") String asd`, получим `asd === "значение"`
      - так и все элементы `...?asd=значение1?asd=значение2`  с помощью `@Par("asd") List<String> asd`, получим `asd === ["значение1","значение2"]`
    - Если параметр содержит JSON `...?parameterAsd={"asd":"значение"}`, то его можно десериализовать с помощью `@Par @Json ClassWithAsd asd`;
    - Все параметры `...?asd=значение&dsa=значение` можно запихать напрямую в объект с помощью `@ParamsTo ClassWithAsdAndDsa argument`;
    - Параметр можно считать из URL-пути `.../{asd}/...` с помощью аннотации `@ParPath("asd") String asd` (можно добавить `@Json`);
    - Параметр можно считать из сессии с помощью `@ParSession`;
    - Параметр можно считать из кукисов с помощью `@ParCookie` (притом как на прямую, так и с помощью встроенного механизма cookie-сериализации);
    - Всё тело запроса можно запихнуть в парамтер с помощью `@RequestInput String requestContentUtf8`;
##### Рендеринг
  - то, что возвращает метод контроллера можно сериализовать
    - в JSON с помощью `@ToJson` у метода контроллера;
    - в XML с помощью `@ToXml` у метода контроллера;
  - позволяет рендерить в JSP, и передавать туда параметры через `MvcModel`;
  - вместо JSP можно использовать любой другой механизм рендеринга (библиотека ничего не знает про JSP);
##### Редиректы
  - редиректы можно делать с помощью исключений: `throws Redirect.to("some/uri")`
    - и добавлять к ним кукисы: `throws Redirect.to("some/uri").addCookie("KEY", "str value");`
      - их потом можно считывать в параметре метода тонтроллера так: `@ParCookie(value="KEY", asIs = **true**) String str`
    - а можно сериализованные кукисы: `throws Redirect.to("some/uri").addCookieObject("KEY", object);`
      - их потом можно считывать в параметре метода тонтроллера так: `@ParCookie(value="KEY", asIs = **false**) SomeClass object`
  - редиректы можно просто возвращать из метода контроллера: `return Redirect.to("some/uri")`;
##### MethodFilter
  - можно фильтровать по разным HTTP-методам, с помощью `@MethodFilter`;


# Типичный Rest-контроллер

```java

@Bean
@Mapping("/orgUnit")
public class OrgUnitController implements Controller {

  public BeanGetter<OrgUnitRegister> orgUnitRegister;

  @ToJson
  @Mapping("/rootList")
  public OrgUnitRootListResult rootList() {
    return orgUnitRegister.get().rootList();
  }

  @ToJson
  @Mapping("/children/{parentId}")
  public List<OrgUnitNode> children(@ParPath("parentId") String parentId) {
    return orgUnitRegister.get().children(parentId);
  }


  @ToJson
  @Mapping("/findRootAndSaveCurrentOrgUnitId")
  public void findRootAndSaveCurrentOrgUnitId(@Par("type") String type, @Par("id") String id) {
    orgUnitRegister.get().findRootAndSaveCurrentOrgUnitId(type, id);
  }

  @ToJson
  @Mapping("/details/{orgUnitId}")
  public OrgUnitDetails details(@ParPath("orgUnitId") String orgUnitId) {
    return orgUnitRegister.get().details(orgUnitId);
  }

  @ToJson
  @Mapping("/delete/{orgUnitId}")
  public void delete(@ParPath("orgUnitId") String orgUnitId) {
    orgUnitRegister.get().delete(orgUnitId);
  }

  @ToJson
  @Mapping("/save")
  public OrgUnitNode save(@Par("parentId") String parentId,
                          @Par("id") String id,
                          @Par("guid") String guid,
                          @Par("name") String name
  ) {
    return orgUnitRegister.get().save(parentId, id, guid, name);
  }

  @ToJson
  @Mapping("/pathToPerson/{rootOrgUnitId}/{personId}")
  public List<String> pathToPerson(@ParPath("rootOrgUnitId") String rootOrgUnitId,
                                   @ParPath("personId") String personId,
                                   MvcModel model
  ) {
    List<String> ret = orgUnitRegister.get().pathToPerson(rootOrgUnitId, personId);
    model.setStatus(ret != null ? 200 : 202);
    return ret;
  }

  @ToJson
  @Mapping("/pathToOrgUnit/{orgUnitId}")
  public List<String> pathToOrgUnit(@ParPath("orgUnitId") String orgUnitId, MvcModel model) {
    List<String> ret = orgUnitRegister.get().pathToOrgUnit(orgUnitId);
    model.setStatus(ret != null ? 200 : 202);
    return ret;
  }

  @ToJson
  @Mapping("/search")
  public List<OrgUnitSearchRecord> search(@ParamsTo OrgUnitSearch a, MvcModel model) {
    orgUnitRegister.get().search(a);
    model.setStatus(a.hasMoreElements ? 200 : 201);
    return a.result;
  }

  @ToJson
  @Mapping("/switchingRoleContent")
  public List<SwitchingRole> switchingRoleContent() {
    return orgUnitRegister.get().switchingRoleContent();
  }

  @ToJson
  @Mapping("/assignedRoles/{orgUnitId}")
  public List<AssignedRoleId> assignedRoles(@ParPath("orgUnitId") String urgUnitId) {
    return orgUnitRegister.get().assignedRoles(urgUnitId);
  }

  @ToJson
  @MethodFilter(POST)
  @Mapping("/addRoles")
  public Map<String, AssignTypeModification> addRoles(
    @Par("id") String personId, @Par("roleIds") @Json List<String> roleIds,
    @ParSession("personId") String modifierId
  ) {
    return orgUnitRegister.get().addRoles(personId, roleIds, modifierId);
  }

  @ToJson
  @MethodFilter(POST)
  @Mapping("/removeRoles")
  public Map<String, AssignTypeModification> removeRoles(
    @Par("id") String personId, @Par("roleIds") @Json List<String> roleIds,
    @ParSession("personId") String modifierId
  ) {
    return orgUnitRegister.get().removeRoles(personId, roleIds, modifierId);
  }


  @ToJson
  @Mapping("/allPersonsUnder")
  public List<PersonRecord> allPersonsUnder(@ParamsTo InOutAllPersonsUnder inOut, MvcModel model) {
    orgUnitRegister.get().allPersonsUnder(inOut);
    model.setStatus(inOut.hasMore ? 201 : 200);
    return inOut.list;
  }
}

```