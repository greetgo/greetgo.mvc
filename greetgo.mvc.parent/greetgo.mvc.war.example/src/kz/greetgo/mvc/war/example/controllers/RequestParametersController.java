package kz.greetgo.mvc.war.example.controllers;

import kz.greetgo.mvc.annotations.AsIs;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.Par;

import java.math.BigDecimal;

@Mapping("/request_parameters")
public class RequestParametersController {

  @Mapping("/form")
  public String form() {
    return "request_parameters.jsp";
  }

  @AsIs
  @Mapping("/base-example")
  public String baseExample(@Par("helloMessage") String helloMessage,
                            @Par("age") int age,
                            @Par("amount") BigDecimal amount
  ) {
    return "called RequestParametersController.baseExample with arguments:\n" +
      "    helloMessage = " + helloMessage + "\n" +
      "    age = " + age + "\n" +
      "    amount = " + amount;
  }

  public static class Client {
    public String id;
    public String surname;
    public String name;
  }

  public static class Account {
    public String number;
    public String amount;
    public String name;
  }


}
