package kz.greetgo.mvc.errors;

public class IllegalChar extends RuntimeException {
  public final char c;
  public final String place;

  public IllegalChar(char c, String place) {
    super("char = '" + c + "' (deg value " + (int) c + ") in " + place);
    this.c = c;
    this.place = place;
  }
}
