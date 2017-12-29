package kz.greetgo.mvc.errors;

public class AmbiguousMaxFileSize extends RuntimeException {
  public AmbiguousMaxFileSize(String message) {
    super(message);
  }
}
