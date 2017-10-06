package kz.greetgo.mvc.war.example.controllers;

import kz.greetgo.mvc.annotations.AsIs;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.annotations.ParCookie;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.mvc.annotations.ToXml;
import kz.greetgo.mvc.model.MvcModel;
import kz.greetgo.mvc.model.Redirect;

import java.io.Serializable;

@Mapping("/method_returns")
public class MethodReturnsController {

  @Mapping("/form")
  public String form() {
    return "method_returns.jsp";
  }

  @AsIs
  @Mapping("/using-as-is")
  public String usingAsIs() {
    return "Method content put into response body as is\n\tHello World!!\n\t\tWell done!!!";
  }

  public static class GoingToClientClass {
    public String someStr;
    public int someInt;
    public Integer someInteger;
  }

  @ToJson
  @Mapping("/using-to-json")
  public GoingToClientClass usingToJson() {
    GoingToClientClass ret = new GoingToClientClass();
    fillWithSomeValue(ret);
    return ret;
  }

  @ToXml
  @Mapping("/using-to-xml")
  public GoingToClientClass usingToXml() {
    GoingToClientClass ret = new GoingToClientClass();
    fillWithSomeValue(ret);
    return ret;
  }

  public static class HasName implements Serializable {
    public String name;

    public HasName(String name) {
      this.name = name;
    }
  }

  @Mapping("/return-redirect")
  public Redirect returnRedirect(@Par("param") String param) {
    switch (param) {

      case "param1":
        return Redirect.to("redirect-param1")
          .addCookieObject("NAME", new HasName("John"))
          .addCookie("ACT", "Return")
          ;

      case "param2":
        return Redirect.to("redirect-param2")
          .addCookie("NAME", "Maria")
          .addCookie("ACT", "Return");

      case "RuntimeException":
        throw new RuntimeException("ERROR");

      default:
        throw new RuntimeException("Unknown param = " + param);
    }
  }

  @Mapping("/redirect-param1")
  public String returnRedirectParam1(MvcModel model,
                                     @ParCookie("NAME") HasName nameFromCookie,
                                     @ParCookie(value = "ACT", asIs = true) String act) {
    model.setParam("SOME_ARGUMENT", "Good morning");
    model.setParam("NAME_FROM_COOKIE", nameFromCookie.name);
    model.setParam("ACT", act);
    return "method_returns/redirect_param1.jsp";
  }

  @Mapping("/redirect-param2")
  public String returnRedirectParam2(MvcModel model,
                                     @ParCookie(value = "NAME", asIs = true) String nameFromCookie,
                                     @ParCookie(value = "ACT", asIs = true) String act) {
    model.setParam("SOME_ARGUMENT", "Good evening");
    model.setParam("NAME_FROM_COOKIE", nameFromCookie + ", hi!");
    model.setParam("ACT", act);
    return "method_returns/redirect_param2.jsp";
  }

  @Mapping("/throw-redirect")
  public String throwRedirect(@Par("param") String param) {
    switch (param) {

      case "param1":
        throw Redirect.to("redirect-param1")
          .addCookieObject("NAME", new HasName("Simon"))
          .addCookie("ACT", "Throw")
          ;

      case "param2":
        throw Redirect.to("redirect-param2")
          .addCookie("NAME", "Lee loo")
          .addCookie("ACT", "Throw")
          ;

      case "ok":
        return "throw_redirect_ok.jsp";

      case "RuntimeException":
        throw new RuntimeException("ERROR");

    }

    throw new RuntimeException("Unknown param = " + param);
  }

  private void fillWithSomeValue(GoingToClientClass object) {
    object.someStr = "wow";
    object.someInt = 345;
    object.someInteger = 6543;
  }
}
