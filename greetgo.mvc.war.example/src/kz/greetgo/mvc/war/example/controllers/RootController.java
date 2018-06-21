package kz.greetgo.mvc.war.example.controllers;

import kz.greetgo.mvc.annotations.on_methods.HttpGET;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.model.MvcModel;
import kz.greetgo.mvc.model.Redirect;

public class RootController {

  @HttpGET({"/", ""})
  public Redirect root() {
    return Redirect.to("/");
  }

  @HttpGET("/index")
  public String index() {
    return "index.jsp";
  }

  @HttpGET("/asd")
  public String asd(
    @Par("param1") String requestParam1,
    @Par("param2") Long requestParam2,
    MvcModel model
  ) {
    model.setParam("param1", "param 1 value : " + requestParam1);
    model.setParam("param2", 234L + requestParam2);
    return "asd.jsp";
  }
}
