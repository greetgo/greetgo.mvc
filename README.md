# greetgo.mvc

Light implementation of MVC pattern. It is made in the image and likeness of SpringMVC

 - [Concept](doc/concept.md)
 - [Example project mvc.war.example (Quick setup and launch)](doc/mvc_war_example.md)
 - [Specification of controllers](doc/controller_spec.md)

# Capabilities

### Organizational capabilities
  - Simple, light and small;
  - Does not contain unnecessary dependencies;
  - Not connected to any Dependency Injection library (so you can use any);

### Functionalities

It is made in the image and likeness of SpringMVC (but does not have any drawbacks);

##### Parameters
Allows to read parameters:
  - for the request: `...?asd=value&...&asd=value2&...`
    - using `@Par("asd") String asd`, get the first value: `asd === "value1"`
    - using `@Par("asd") List<String> asd`, get all values: `asd === ["value1","value2"]`
  - If the parameter contains JSON `...?wow={"asd":"value"}`, it can be deserialized using `@Par("wow") @Json ClassWithAsd wow`;
  - All parameters `...?asd=value&dsa=value` can be put directly into the object using `@ParamsTo ClassWithAsdAndDsa argument`;
  - The parameter can be read from the URL path `.../{asd}/...` using annotation `@ParPath("asd") String asd` (it is possible to add `@Json`);
  - The parameter can be read from the session using `@ParSession`;
  - The parameter can be read from cookies using `@ParCookie` (both directly and with the help of cookie-serialization built-in mechanism);
  - The whole request body can be put into the parameter using `@RequestInput String requestContentUtf8`;

##### Rendering
  - what the controller method returns can be serialized
    - in JSON using `@ToJson` from controller method;
    - in XML using `@ToXml` from controller method;
  - allows you to render in JSP, and transmit parameters there through `MvcModel`;
  - instead of JSP, you can use any other rendering mechanism (the library does not know anything about JSP);

##### Redirects
  - Redirects can be done using exceptions: `throws Redirect.to("some/uri")`
    - and add cookies to them: `throws Redirect.to("some/uri").addCookie("KEY", "str value");`
      - then they can be read in the parameter of the controller method in this way: `@ParCookie(value="KEY", asIs = true) String str`
    - or serialized cookies: `throws Redirect.to("some/uri").addCookieObject("KEY", object);`
      - then they can be read in the parameter of the controller method in this way: `@ParCookie(value="KEY", asIs = false) SomeClass object`
  - redirects can be returned from the controller method:`return Redirect.to("some/uri")`;

##### MethodFilter
  - can be filtered by different HTTP methods, using `@MethodFilter`;

### and
  - Request and Response classes are combined into one: RequestTunnel, which simplifies the structure of the library and its use;
  - Cool possibility of organizing erroneous requests:
    - somewhere inside the code `throw new RestError(450, "Плохо!");` and the request ends with error code 450 and the response body "Bad";
    - or - `throw new RestJsonError(450, someObject);` and the request ends with error code 450 and and the response body someObject deserialized in JSON;
  - It is possible to implement any complex security;

### Disadvantages

  - Previously, it is needed to implement two classes (but there is an example of implementation and you can just copypast!);

# Typical Rest-controller

```java

@Bean
@ControllerPrefix("/orgUnit")
public class OrgUnitController implements Controller {

  public BeanGetter<OrgUnitRegister> orgUnitRegister;

  @ToJson
  @OnGet("/rootList")
  public OrgUnitRootListResult rootList() {
    return orgUnitRegister.get().rootList();
  }

  @ToJson
  @OnGet("/children/{parentId}")
  public List<OrgUnitNode> children(@ParPath("parentId") String parentId) {
    return orgUnitRegister.get().children(parentId);
  }


  @ToJson
  @OnGet("/findRootAndSaveCurrentOrgUnitId")
  public void findRootAndSaveCurrentOrgUnitId(@Par("type") String type, @Par("id") String id) {
    orgUnitRegister.get().findRootAndSaveCurrentOrgUnitId(type, id);
  }

  @ToJson
  @OnGet("/details/{orgUnitId}")
  public OrgUnitDetails details(@ParPath("orgUnitId") String orgUnitId) {
    return orgUnitRegister.get().details(orgUnitId);
  }

  @ToJson
  @OnDelete("/delete/{orgUnitId}")
  public void delete(@ParPath("orgUnitId") String orgUnitId) {
    orgUnitRegister.get().delete(orgUnitId);
  }

  @ToJson
  @OnPost("/save")
  public OrgUnitNode save(@Par("parentId") String parentId,
                          @Par("id") String id,
                          @Par("guid") String guid,
                          @Par("name") String name
  ) {
    return orgUnitRegister.get().save(parentId, id, guid, name);
  }

  @ToJson
  @OnGet("/pathToPerson/{rootOrgUnitId}/{personId}")
  public List<String> pathToPerson(@ParPath("rootOrgUnitId") String rootOrgUnitId,
                                   @ParPath("personId") String personId,
                                   MvcModel model
  ) {
    List<String> ret = orgUnitRegister.get().pathToPerson(rootOrgUnitId, personId);
    model.setStatus(ret != null ? 200 : 202);
    return ret;
  }

  @ToJson
  @OnGet("/pathToOrgUnit/{orgUnitId}")
  public List<String> pathToOrgUnit(@ParPath("orgUnitId") String orgUnitId, MvcModel model) {
    List<String> ret = orgUnitRegister.get().pathToOrgUnit(orgUnitId);
    model.setStatus(ret != null ? 200 : 202);
    return ret;
  }

  @ToJson
  @OnGet("/search")
  public List<OrgUnitSearchRecord> search(@ParamsTo OrgUnitSearch a, MvcModel model) {
    orgUnitRegister.get().search(a);
    model.setStatus(a.hasMoreElements ? 200 : 201);
    return a.result;
  }

  @ToJson
  @OnPost("/switchingRoleContent")
  public List<SwitchingRole> switchingRoleContent() {
    return orgUnitRegister.get().switchingRoleContent();
  }

  @ToJson
  @OnPost("/assignedRoles/{orgUnitId}")
  public List<AssignedRoleId> assignedRoles(@ParPath("orgUnitId") String urgUnitId) {
    return orgUnitRegister.get().assignedRoles(urgUnitId);
  }

  @ToJson
  @OnPost("/addRoles")
  public Map<String, AssignTypeModification> addRoles(
    @Par("id") String personId, @Par("roleIds") @Json List<String> roleIds,
    @ParSession("personId") String modifierId
  ) {
    return orgUnitRegister.get().addRoles(personId, roleIds, modifierId);
  }

  @ToJson
  @OnPost("/removeRoles")
  public Map<String, AssignTypeModification> removeRoles(
    @Par("id") String personId, @Par("roleIds") @Json List<String> roleIds,
    @ParSession("personId") String modifierId
  ) {
    return orgUnitRegister.get().removeRoles(personId, roleIds, modifierId);
  }


  @ToJson
  @OnGet("/allPersonsUnder")
  public List<PersonRecord> allPersonsUnder(@ParamsTo InOutAllPersonsUnder inOut, MvcModel model) {
    orgUnitRegister.get().allPersonsUnder(inOut);
    model.setStatus(inOut.hasMore ? 201 : 200);
    return inOut.list;
  }
}

```
