package kz.greetgo.mvc.war.example.controllers;

import kz.greetgo.mvc.annotations.AsIs;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.Par;

@Mapping("/request_parameters")
public class RequestParametersController {

  @Mapping("/form")
  public String form() {
    return "request_parameters.jsp";
  }

  @AsIs
  @Mapping("/base-example")
  public String baseExample(@Par("helloMessage") String helloMessage, @Par("age") int age) {
    return "called RequestParametersController.baseExample with arguments:\n" +
      "    helloMessage = " + helloMessage + "\n" +
      "    age = " + age;
  }

}
