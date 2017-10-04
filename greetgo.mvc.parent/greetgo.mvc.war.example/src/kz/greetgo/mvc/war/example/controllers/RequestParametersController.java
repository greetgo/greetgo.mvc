package kz.greetgo.mvc.war.example.controllers;

import kz.greetgo.mvc.annotations.AsIs;
import kz.greetgo.mvc.annotations.Json;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.annotations.ParPath;
import kz.greetgo.mvc.annotations.ParSession;
import kz.greetgo.mvc.annotations.ParamsTo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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

  /////////////////////////////// Base Example /////////////////////////////////////////////////////////////////////////

  @AsIs
  @Mapping("/base-example")
  public String baseExample(@Par("helloMessage") String helloMessage,
                            @Par("age") int age,
                            @Par("amount") BigDecimal amount,
                            @Par("weather") Weather weather,
                            @Par("happenedAt") Date happenedAt,
                            @Par("address") List<String> addresses
  ) {
    return "called RequestParametersController.baseExample with arguments:\n" +
      "    helloMessage = " + helloMessage + "\n" +
      "    age = " + age + "\n" +
      "    amount = " + amount + "\n" +
      "    weather = " + weather + "\n" +
      "    happenedAt = " + happenedAt + "\n" +
      "    addresses = " + addresses + "\n" +
      "    addresses.size() = " + addresses.size();
  }

  /////////////////////////////// @Par @Json Example ///////////////////////////////////////////////////////////////////

  public static class ClientToSave {
    public String id;
    public String surname;
    public String name;

    @Override
    public String toString() {
      return "ClientToSave{" +
        "id='" + id + '\'' +
        ", surname='" + surname + '\'' +
        ", name='" + name + '\'' +
        '}';
    }
  }

  public static class AccountToSave {
    public String number;
    public BigDecimal amount;
    public Long typeId;

    @Override
    public String toString() {
      return "AccountToSave{" +
        "number='" + number + '\'' +
        ", amount=" + amount +
        ", typeId=" + typeId +
        '}';
    }
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

  /////////////////////////////// ParamsTo Example /////////////////////////////////////////////////////////////////////

  public static class Client {
    public Long id;
    public String name;
    public BigDecimal amount;
    public List<String> addresses;

    @SuppressWarnings("unused")
    public void setName(String name) {
      this.name = name + " - from setter";
    }

    @Override
    public String toString() {
      return "Client {\n" +
        "        id               = " + id + "\n" +
        "        name             = '" + name + "'\n" +
        "        amount           = " + amount + "\n" +
        "        addresses.size() = " + (addresses == null ? "-" : "" + addresses.size()) + "\n" +
        "        addresses        = " + addresses + "\n" +
        "    }";
    }
  }

  @AsIs
  @Mapping("/params-to-example")
  public String paramsToExample(@ParamsTo Client client) {
    return "called RequestParametersController.paramsToExample with\n" +
      "    client = " + client;
  }

  /////////////////////////////// ParPath Example //////////////////////////////////////////////////////////////////////


  @AsIs
  @Mapping("/par-path-example/id:{id}/{name}")
  public String parPathExample(@ParPath("id") Long id, @ParPath("name") String name) {
    return "called RequestParametersController.parPathExample with\n" +
      "    id   = " + id + "\n" +
      "    name = " + name;
  }

  /////////////////////////////// ParSession Example ///////////////////////////////////////////////////////////////////

  @AsIs
  @Mapping("/par-session-example")
  public String parSessionExample(@ParSession("personId") Long personId, @ParSession("role") String role) {
    return "called RequestParametersController.parSessionExample with\n" +
      "    personId = " + personId + "\n" +
      "    role     = '" + role + "'";
  }
}
