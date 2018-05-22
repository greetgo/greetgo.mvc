package kz.greetgo.mvc.errors;

public class AsteriskInTargetMapper extends RuntimeException {
  public final String targetMapper;

  public AsteriskInTargetMapper(String targetMapper) {
    super("Target mapper cannot contain asterisk (*). targetMapper = " + targetMapper);
    this.targetMapper = targetMapper;
  }
}
