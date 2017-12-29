package kz.greetgo.mvc.war;

public class ExceptionWrapper extends RuntimeException {

  public final Exception wrappedException;

  public ExceptionWrapper(Exception e) {
    super(e);
    wrappedException = e;
  }
}
