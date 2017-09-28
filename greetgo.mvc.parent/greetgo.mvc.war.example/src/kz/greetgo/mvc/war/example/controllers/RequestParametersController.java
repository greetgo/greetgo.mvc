package kz.greetgo.mvc.war.example.controllers;

import kz.greetgo.mvc.annotations.AsIs;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.Par;

import java.math.BigDecimal;
import java.util.Date;

@Mapping("/request_parameters")
public class RequestParametersController {

  @Mapping("/form")
  public String form() {
    return "request_parameters.jsp";
  }

  @SuppressWarnings("unused")
  public enum Weather {
    SUNNY, CLOUDY, RAINY, HOT;
  }

  @AsIs
  @Mapping("/base-example")
  public String baseExample(@Par("helloMessage") String helloMessage,
                            @Par("age") int age,
                            @Par("amount") BigDecimal amount,
                            @Par("weather") Weather weather,
                            @Par("happenedAt") Date happenedAt
  ) {
    return "called RequestParametersController.baseExample with arguments:\n" +
      "    helloMessage = " + helloMessage + "\n" +
      "    age = " + age + "\n" +
      "    amount = " + amount + "\n" +
      "    weather = " + weather + "\n" +
      "    happenedAt = " + happenedAt;
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
