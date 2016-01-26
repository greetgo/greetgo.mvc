package kz.greetgo.mvc.jetty.errors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class InconsistentUploadAnnotationsUnderMethod extends RuntimeException {
  public final Method assemblingMethod;
  public final Annotation usedAnnotation;
  public final String extractMethodName;

  public InconsistentUploadAnnotationsUnderMethod(Method assemblingMethod,
                                                  Annotation usedAnnotation,
                                                  String extractMethodName) {
    super(usedAnnotation.getClass().getSimpleName() + " in " + assemblingMethod.toGenericString());
    this.assemblingMethod = assemblingMethod;
    this.usedAnnotation = usedAnnotation;
    this.extractMethodName = extractMethodName;
  }
}
