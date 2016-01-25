package kz.greetgo.depinject.mvc.probes.controllers;

import kz.greetgo.depinject.mvc.Mapping;
import kz.greetgo.depinject.mvc.Par;
import kz.greetgo.depinject.mvc.Redirect;
import kz.greetgo.depinject.mvc.Upload;

import java.io.IOException;

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
