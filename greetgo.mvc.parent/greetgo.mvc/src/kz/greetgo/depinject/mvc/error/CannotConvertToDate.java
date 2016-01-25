package kz.greetgo.depinject.mvc.error;

public class CannotConvertToDate extends RuntimeException {
  public final String dateStr;

  public CannotConvertToDate(String dateStr) {
    super("Cannot convert " + dateStr + " to date");
    this.dateStr = dateStr;
  }
}
