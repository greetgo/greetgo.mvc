package kz.greetgo.mvc.jetty.utils;

import java.io.Serializable;

public class UserDetails implements Serializable {
  public String username, surname;

  @Override
  public String toString() {
    return "UserDetails{username=" + username + ", surname=" + surname + '}';
  }
}
