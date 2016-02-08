package kz.greetgo.mvc.controllers;

import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.model.Redirect;
import kz.greetgo.mvc.utils.UserDetailsStorage;

public class LoginController {

  private final UserDetailsStorage userDetailsStorage;

  public LoginController(UserDetailsStorage userDetailsStorage) {
    this.userDetailsStorage = userDetailsStorage;
  }

  @Mapping("/client/save")
  @SuppressWarnings("unused")
  public Redirect clientSave(@Par("surname") String surname, @Par("name") String name) {

    System.out.println("Prev surname = " + userDetailsStorage.getUserDetails().surname);
    System.out.println("Surname = " + surname + ", Name = " + name);
    userDetailsStorage.getUserDetails().surname = surname;

    return Redirect.to("/content.html");
  }

  @Mapping("/login")
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

  @Mapping("/logout")
  @SuppressWarnings("unused")
  public Redirect logout() {

    userDetailsStorage.setUsername(null);

    return Redirect.to("/login.html");
  }
}
