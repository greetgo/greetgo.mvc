package kz.greetgo.depinject.mvc;

import kz.greetgo.depinject.mvc.error.CannotExtractParamValue;
import kz.greetgo.depinject.mvc.error.IDoNotKnowHowToConvertRequestContentToType;
import kz.greetgo.depinject.mvc.error.NoAnnotationParInUploadParam;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static kz.greetgo.depinject.mvc.MvcUtil.convertStrToType;
import static kz.greetgo.depinject.mvc.MvcUtil.convertStrsToType;

public class MethodParameterMeta {

  public static List<MethodParamExtractor> create(Method method) {
    final List<MethodParamExtractor> extractorList = new ArrayList<>();

    final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
    final Type[] genericParameterTypes = method.getGenericParameterTypes();
    for (int i = 0, C = genericParameterTypes.length; i < C; i++) {
      final MethodParameterMeta methodParameterMeta = new MethodParameterMeta(
        i, method, genericParameterTypes[i], parameterAnnotations[i]
      );
      extractorList.add(methodParameterMeta.createExtractor());
    }

    return extractorList;
  }

  private final int parameterIndex;
  private final Method method;
  private final Type genericParameterType;
  private final Annotation[] parameterAnnotation;

  private MethodParameterMeta(int parameterIndex, Method method,
                              Type genericParameterType, Annotation[] parameterAnnotation) {
    this.parameterIndex = parameterIndex;
    this.method = method;
    this.genericParameterType = genericParameterType;
    this.parameterAnnotation = parameterAnnotation;
    prepareAnnotations();
  }

  private String parValue = null, pathParValue = null;

  private boolean requestInput = false;

  private void prepareAnnotations() {
    for (Annotation annotation : parameterAnnotation) {
      if (annotation instanceof Par) {
        parValue = ((Par) annotation).value();
        continue;
      }
      if (annotation instanceof PathPar) {
        pathParValue = ((PathPar) annotation).value();
        continue;
      }
      if (annotation instanceof RequestInput) {
        requestInput = true;
        //noinspection UnnecessaryContinue
        continue;
      }
    }
  }

  private MethodParamExtractor createExtractor() {
    if (Upload.class == genericParameterType) {
      if (parValue == null) throw new NoAnnotationParInUploadParam(parameterIndex, method);

      return new MethodParamExtractor() {
        @Override
        public Object extract(MappingResult mappingResult, RequestTunnel tunnel, MvcModel model) throws Exception {
          return tunnel.getUpload(parValue);
        }
      };
    }

    if (parValue != null) return new MethodParamExtractor() {
      @Override
      public Object extract(MappingResult mappingResult, RequestTunnel tunnel, MvcModel model) {
        final String[] paramValues = tunnel.getParamValues(parValue);
        return convertStrsToType(paramValues, genericParameterType);
      }
    };

    if (pathParValue != null) return new MethodParamExtractor() {
      @Override
      public Object extract(MappingResult mappingResult, RequestTunnel tunnel, MvcModel model) {
        final String paramValue = mappingResult.getParam(pathParValue);
        return convertStrToType(paramValue, genericParameterType);
      }
    };

    if (requestInput) return new MethodParamExtractor() {
      @Override
      public Object extract(MappingResult mappingResult, RequestTunnel tunnel, MvcModel model) throws Exception {
        return convertRequestContentToType(tunnel, genericParameterType);
      }
    };

    if (MvcModel.class == genericParameterType) return new MethodParamExtractor() {
      @Override
      public Object extract(MappingResult mappingResult, RequestTunnel tunnel, MvcModel model) throws Exception {
        return model;
      }
    };

    throw new CannotExtractParamValue(parameterIndex, method);
  }

  private static Object convertRequestContentToType(RequestTunnel tunnel, Type type) throws Exception {
    if (type instanceof Class) return convertRequestContentToClass(tunnel, (Class<?>) type);
    if (type instanceof ParameterizedType) {
      return convertRequestContentToParameterizedType(tunnel, (ParameterizedType) type);
    }
    throw new IDoNotKnowHowToConvertRequestContentToType(type);
  }

  private static Object convertRequestContentToParameterizedType(RequestTunnel tunnel, ParameterizedType type) throws Exception {
    final Type rawType = type.getRawType();

    if (rawType == List.class) {
      List ret = new ArrayList();
      try (BufferedReader reader = tunnel.getRequestReader()) {
        while (true) {
          final String line = reader.readLine();
          if (line == null) return ret;
          //noinspection unchecked
          ret.add(convertStrToType(line, type.getActualTypeArguments()[0]));
        }
      }
    }

    throw new IDoNotKnowHowToConvertRequestContentToType(type);
  }

  private static Object convertRequestContentToClass(RequestTunnel tunnel, Class<?> aClass) throws Exception {
    if (String.class.equals(aClass)) {
      StringBuilder sb = new StringBuilder();
      char[] buffer = new char[1024];
      try (BufferedReader reader = tunnel.getRequestReader()) {
        while (true) {
          final int count = reader.read(buffer);
          if (count < 0) break;
          sb.append(buffer, 0, count);
        }
      }
      return sb.toString();
    }

    if (byte[].class.equals(aClass)) {
      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      byte[] buffer = new byte[1024 * 4];
      final InputStream in = tunnel.getRequestInputStream();
      while (true) {
        final int count = in.read(buffer);
        if (count < 0) return bout.toByteArray();
        bout.write(buffer, 0, count);
      }
    }

    if (InputStream.class.equals(aClass)) return tunnel.getRequestInputStream();

    if (Reader.class.equals(aClass)) return tunnel.getRequestReader();
    if (BufferedReader.class.equals(aClass)) return tunnel.getRequestReader();
    if (RequestTunnel.class.equals(aClass)) return tunnel;

    throw new IDoNotKnowHowToConvertRequestContentToType(aClass);
  }
}
