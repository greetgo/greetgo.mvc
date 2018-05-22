package kz.greetgo.mvc.errors;

public class DoublePathPar extends RuntimeException {
  public DoublePathPar(String infoStr) {
    super("В пути нельзя указывать несколько переменных подряд: " + infoStr);
  }
}
