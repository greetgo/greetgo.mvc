package kz.greetgo.mvc.core;

import kz.greetgo.mvc.annotations.*;
import kz.greetgo.mvc.errors.AmbiguousMaxFileSize;
import kz.greetgo.mvc.errors.InconsistentUploadAnnotationsUnderClass;
import kz.greetgo.mvc.errors.InconsistentUploadAnnotationsUnderMethod;
import kz.greetgo.mvc.interfaces.GetterInt;
import kz.greetgo.mvc.interfaces.GetterLong;
import kz.greetgo.mvc.interfaces.GetterStr;
import kz.greetgo.mvc.model.UploadInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class UploadInfoGetter {

  private GetterStr location = new FixGetterStr(System.getProperty("java.io.tmpdir"));
  private GetterLong maxFileSize = new FixGetterLong(-1L);
  private GetterLong maxRequestSize = new FixGetterLong(-1L);
  private GetterInt fileSizeThreshold = new FixGetterInt(0);

  private Object controller;
  private String extractMethodName = null;

  private Annotation usedAnnotation = null;

  public UploadInfoGetter copy() {
    UploadInfoGetter ret = new UploadInfoGetter();
    ret.location = location;
    ret.maxFileSize = maxFileSize;
    ret.maxRequestSize = maxRequestSize;
    ret.fileSizeThreshold = fileSizeThreshold;

    ret.controller = controller;
    ret.extractMethodName = extractMethodName;

    ret.usedAnnotation = usedAnnotation;

    return ret;
  }

  private Method cachedExtractMethod = null;

  public UploadInfo get() {
    if (extractMethodName != null) {
      if (controller == null) throw new NullPointerException("controller == null");
      try {

        if (cachedExtractMethod == null) {
          cachedExtractMethod = controller.getClass().getMethod(extractMethodName);
        }

        return (UploadInfo) cachedExtractMethod.invoke(controller);
      } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
        throw new RuntimeException(e);
      }
    }

    {
      UploadInfo ret = new UploadInfo();
      ret.location = location.get();
      ret.maxFileSize = maxFileSize.get();
      ret.maxRequestSize = maxRequestSize.get();
      ret.fileSizeThreshold = fileSizeThreshold.get();
      return ret;
    }
  }

  public void assembleAnnotationFromController(Object controller) {
    this.controller = controller;
    for (Annotation annotation : controller.getClass().getAnnotations()) {
      applyAnnotation(annotation);
    }
  }

  private Method assemblingMethod = null;

  public void assembleAnnotationFromMethod(Method method) {
    assemblingMethod = method;
    try {
      for (Annotation annotation : method.getAnnotations()) {
        applyAnnotation(annotation);
      }
    } finally {
      assemblingMethod = null;
    }
  }

  private UploadMaxFileSize uploadMaxFileSize = null;
  private UploadMaxFileSizeFromMethod uploadMaxFileSizeFromMethod = null;

  private void applyAnnotation(Annotation annotation) {
    if (annotation instanceof UploadMaxFileSize) {
      UploadMaxFileSize a = (UploadMaxFileSize) annotation;
      maxFileSize = new FixGetterLong(MvcUtil.amountBytesToLong(a.value()));
      usedAnnotation = annotation;
      uploadMaxFileSize = a;
      checkErrors();
      return;
    }
    if (annotation instanceof UploadMaxFileSizeFromMethod) {
      UploadMaxFileSizeFromMethod a = (UploadMaxFileSizeFromMethod) annotation;
      maxFileSize = new AmountMethodGetter(controller, a.value());
      usedAnnotation = annotation;
      uploadMaxFileSizeFromMethod = a;
      checkErrors();
      return;
    }
    if (annotation instanceof UploadMaxRequestSize) {
      UploadMaxRequestSize a = (UploadMaxRequestSize) annotation;
      maxRequestSize = new FixGetterLong(MvcUtil.amountBytesToLong(a.value()));
      usedAnnotation = annotation;
      checkErrors();
      return;
    }
    if (annotation instanceof UploadFileSizeThreshold) {
      UploadFileSizeThreshold a = (UploadFileSizeThreshold) annotation;
      fileSizeThreshold = new FixGetterInt(MvcUtil.amountBytesToInt(a.value()));
      usedAnnotation = annotation;
      checkErrors();
      return;
    }
    if (annotation instanceof UploadLocationFromMethod) {
      UploadLocationFromMethod a = (UploadLocationFromMethod) annotation;
      location = new MethodGetterStr(controller, a.value());
      usedAnnotation = annotation;
      checkErrors();
      return;
    }
    if (annotation instanceof UploadInfoFromMethod) {
      UploadInfoFromMethod a = (UploadInfoFromMethod) annotation;
      extractMethodName = a.value();
      cachedExtractMethod = null;
      checkErrors();
      //noinspection UnnecessaryReturnStatement
      return;
    }
  }

  private void checkErrors() {
    if (extractMethodName == null || usedAnnotation == null) {

      if (uploadMaxFileSize == null || uploadMaxFileSizeFromMethod == null) return;

      if (assemblingMethod != null) {
        throw new AmbiguousMaxFileSize(assemblingMethod.toGenericString());
      }

      throw new AmbiguousMaxFileSize(controller.getClass().toString());
    }

    if (assemblingMethod != null) {
      throw new InconsistentUploadAnnotationsUnderMethod(assemblingMethod, usedAnnotation, extractMethodName);
    }

    throw new InconsistentUploadAnnotationsUnderClass(controller.getClass(), usedAnnotation, extractMethodName);
  }

}
