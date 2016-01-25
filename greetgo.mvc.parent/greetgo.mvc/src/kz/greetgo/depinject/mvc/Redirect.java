package kz.greetgo.depinject.mvc;

public class Redirect extends RuntimeException {
  public final String reference;

  public static Redirect to(String reference) {
    return new Redirect(reference);
  }

  private Redirect(String reference) {
    super("Redirection to " + reference);
    this.reference = reference;
  }
}
