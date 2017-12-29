package kz.greetgo.mvc.errors;

import java.lang.annotation.Annotation;

public class InconsistentUploadAnnotationsUnderClass extends RuntimeException {
  public final Class<?> controllerClass;
  public final Annotation usedAnnotation;
  public final String extractMethodName;

  public InconsistentUploadAnnotationsUnderClass(Class<?> controllerClass,
                                                 Annotation usedAnnotation,
                                                 String extractMethodName) {

    super(usedAnnotation.getClass().getSimpleName() + " in " + controllerClass);

    this.controllerClass = controllerClass;
    this.usedAnnotation = usedAnnotation;
    this.extractMethodName = extractMethodName;
  }
}
