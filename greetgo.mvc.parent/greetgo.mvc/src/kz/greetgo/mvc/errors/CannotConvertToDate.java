package kz.greetgo.mvc.errors;

public class CannotConvertToDate extends RuntimeException {
  public final String dateStr;

  public CannotConvertToDate(String dateStr) {
    super("Cannot convert " + dateStr + " to date");
    this.dateStr = dateStr;
  }
}
