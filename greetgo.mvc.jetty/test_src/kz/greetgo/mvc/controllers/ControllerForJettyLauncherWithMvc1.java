package kz.greetgo.mvc.controllers;

import kz.greetgo.mvc.annotations.on_methods.ControllerPrefix;
import kz.greetgo.mvc.annotations.on_methods.OnGet;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.interfaces.Upload;
import kz.greetgo.mvc.model.Redirect;

@ControllerPrefix("/lot")
@SuppressWarnings("unused")
public class ControllerForJettyLauncherWithMvc1 {

  @OnGet("/save")
  public Redirect save(@Par("fileA") Upload fileA) throws Exception {
    System.out.println("fileA.SubmittedFileName = " + fileA.getSubmittedFileName());
    System.out.println("fileA.ContentType = " + fileA.getContentType());
    System.out.println("fileA.InputStream = " + fileA.getInputStream());
    System.out.println("fileA.Size = " + fileA.getSize());
    return Redirect.to("after_post.html");
  }

}
