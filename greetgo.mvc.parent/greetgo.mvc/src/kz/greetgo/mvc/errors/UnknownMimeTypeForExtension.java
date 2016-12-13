package kz.greetgo.mvc.errors;

public class UnknownMimeTypeForExtension extends RuntimeException {
  public final String extension;

  public UnknownMimeTypeForExtension(String extension) {
    super(extension);
    this.extension = extension;
  }

  @Override
  public synchronized Throwable fillInStackTrace() {
    return null;
  }
}
