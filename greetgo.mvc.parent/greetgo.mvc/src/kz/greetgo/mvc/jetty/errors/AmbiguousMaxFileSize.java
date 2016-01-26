package kz.greetgo.mvc.jetty.errors;

public class AmbiguousMaxFileSize extends RuntimeException {
  public AmbiguousMaxFileSize(String message) {
    super(message);
  }
}
