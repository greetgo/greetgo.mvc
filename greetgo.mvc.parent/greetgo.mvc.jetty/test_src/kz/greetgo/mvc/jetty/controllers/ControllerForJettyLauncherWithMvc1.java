package kz.greetgo.mvc.jetty.controllers;

import kz.greetgo.mvc.jetty.Mapping;
import kz.greetgo.mvc.jetty.Par;
import kz.greetgo.mvc.jetty.Redirect;
import kz.greetgo.mvc.jetty.Upload;

@Mapping("/lot")
@SuppressWarnings("unused")
public class ControllerForJettyLauncherWithMvc1 {

  @Mapping("/save")
  public Redirect save(@Par("fileA") Upload fileA) throws Exception {
    System.out.println("fileA.SubmittedFileName = " + fileA.getSubmittedFileName());
    System.out.println("fileA.ContentType = " + fileA.getContentType());
    System.out.println("fileA.InputStream = " + fileA.getInputStream());
    return Redirect.to("after_post.html");
  }

}
