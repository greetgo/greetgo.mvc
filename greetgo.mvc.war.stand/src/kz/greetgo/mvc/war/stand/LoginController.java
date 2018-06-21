package kz.greetgo.mvc.war.stand;

import kz.greetgo.mvc.annotations.on_methods.HttpGET;
import kz.greetgo.mvc.annotations.on_methods.HttpPOST;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.interfaces.Upload;
import kz.greetgo.mvc.model.Redirect;

public class LoginController {

  private final UserDetailsStorage userDetailsStorage;

  public LoginController(UserDetailsStorage userDetailsStorage) {
    this.userDetailsStorage = userDetailsStorage;
  }

  @HttpPOST("/client/save")
  @SuppressWarnings("unused")
  public Redirect clientSave(@Par("surname") String surname, @Par("name") String name) {

    System.out.println("Prev surname = " + userDetailsStorage.getUserDetails().surname);
    System.out.println("Surname = " + surname + ", Name = " + name);
    userDetailsStorage.getUserDetails().surname = surname;

    return Redirect.to("/content.html");
  }

  @HttpPOST("/login")
  @SuppressWarnings("unused")
  public Redirect login(@Par("username") String username, @Par("password") String password) {

    if (!"111".equals(password)) {
      return Redirect.to("/login.html");
    }

    if (!("CR".equals(username) || "CR2".equals(username))) {
      return Redirect.to("/login.html");
    }

    userDetailsStorage.setUsername(username);

    return Redirect.to("/index.html");
  }

  @HttpGET("/logout")
  @SuppressWarnings("unused")
  public Redirect logout() {

    userDetailsStorage.setUsername(null);

    return Redirect.to("/login.html");
  }

  @HttpGET("/uploadFile")
  @SuppressWarnings("unused")
  public Redirect uploadFile(
    @Par("description") String description,
    @Par("file1") Upload file1, @Par("file2") Upload file2
  ) {

    System.out.println("----- upload file -----");
    System.out.println("description = " + description);
    System.out.println("file1 = " + file1.getSubmittedFileName());
    System.out.println("file2 = " + file2.getSubmittedFileName());
    System.out.println("file2.size = " + file2.getSize());

    return Redirect.to("/content.html");
  }
}
