package kz.greetgo.mvc.errors;

public class IllegalCallSequence extends RuntimeException {
  public IllegalCallSequence(String message) {
    super(message);
  }
}
