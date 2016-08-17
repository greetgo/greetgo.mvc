package kz.greetgo.mvc.model;

import kz.greetgo.mvc.util.CookieUtil;

import java.util.HashMap;
import java.util.Map;

public class Redirect extends RuntimeException {
  public final String reference;

  public final Map<String, String> savingCookiesToResponse = new HashMap<>();

  public static Redirect to(String reference) {
    return new Redirect(reference);
  }

  private Redirect(String reference) {
    super("Redirection to " + reference);
    this.reference = reference;
  }

  public Redirect addCookieObject(String name, Object value) {
    savingCookiesToResponse.put(name, CookieUtil.objectToStr(value));
    return this;
  }

  public Redirect addCookie(String name, String value) {
    savingCookiesToResponse.put(name, value);
    return this;
  }
}
