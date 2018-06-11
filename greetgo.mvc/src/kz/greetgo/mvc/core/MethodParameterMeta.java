package kz.greetgo.mvc.core;

import kz.greetgo.mvc.annotations.Json;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.annotations.ParCookie;
import kz.greetgo.mvc.annotations.ParPath;
import kz.greetgo.mvc.annotations.ParSession;
import kz.greetgo.mvc.annotations.ParamsTo;
import kz.greetgo.mvc.annotations.RequestInput;
import kz.greetgo.mvc.errors.AsIsOnlyForString;
import kz.greetgo.mvc.errors.CannotExtractParamValue;
import kz.greetgo.mvc.errors.DoNotSetContentTypeAfterOut;
import kz.greetgo.mvc.errors.DoNotSetFilenameAfterOut;
import kz.greetgo.mvc.errors.IDoNotKnowHowToConvertRequestContentToType;
import kz.greetgo.mvc.errors.IllegalCallSequence;
import kz.greetgo.mvc.errors.NoAnnotationParInUploadParam;
import kz.greetgo.mvc.interfaces.BinResponse;
import kz.greetgo.mvc.interfaces.MappingResult;
import kz.greetgo.mvc.interfaces.MethodParamExtractor;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.mvc.interfaces.SessionParameterGetter;
import kz.greetgo.mvc.interfaces.TunnelCookies;
import kz.greetgo.mvc.interfaces.Upload;
import kz.greetgo.mvc.model.MvcModel;
import kz.greetgo.mvc.util.CookieUtil;
import kz.greetgo.mvc.util.JsonUtil;
import kz.greetgo.mvc.util.MimeUtil;
import kz.greetgo.mvc.util.MvcUtil;
import kz.greetgo.mvc.util.UriEscape;
import kz.greetgo.mvc.util.setters.FieldSetters;
import kz.greetgo.mvc.util.setters.FieldSettersStorage;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class MethodParameterMeta {

  public static List<MethodParamExtractor> create(Method method, SessionParameterGetter sessionParameterGetter) {
    final List<MethodParamExtractor> extractorList = new ArrayList<>();

    final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
    final Type[] genericParameterTypes = method.getGenericParameterTypes();
    for (int i = 0, C = genericParameterTypes.length; i < C; i++) {
      final MethodParameterMeta methodParameterMeta = new MethodParameterMeta(
        i, method, genericParameterTypes[i], parameterAnnotations[i], sessionParameterGetter
      );
      extractorList.add(methodParameterMeta.createExtractor());
    }

    return extractorList;
  }

  private final int parameterIndex;
  private final Method method;
  private final Type genericParameterType;
  private final Annotation[] parameterAnnotation;
  private final SessionParameterGetter sessionParameterGetter;

  private MethodParameterMeta(int parameterIndex, Method method,
                              Type genericParameterType, Annotation[] parameterAnnotation,
                              SessionParameterGetter sessionParameterGetter) {
    this.parameterIndex = parameterIndex;
    this.method = method;
    this.genericParameterType = genericParameterType;
    this.parameterAnnotation = parameterAnnotation;
    this.sessionParameterGetter = sessionParameterGetter;
    prepareAnnotations();
  }

  private Par par;
  private ParPath parPath;
  private ParSession parSession;

  private boolean requestInput = false;

  private ParCookie parCookie = null;

  private Json annotationJson = null;

  private ParamsTo paramsTo = null;

  private void prepareAnnotations() {
    for (Annotation annotation : parameterAnnotation) {
      if (annotation instanceof Par) {
        par = (Par) annotation;
        continue;
      }
      if (annotation instanceof ParSession) {
        parSession = (ParSession) annotation;
        continue;
      }
      if (annotation instanceof ParPath) {
        parPath = (ParPath) annotation;
        continue;
      }
      if (annotation instanceof RequestInput) {
        requestInput = true;
        continue;
      }
      if (annotation instanceof ParCookie) {
        parCookie = (ParCookie) annotation;
        continue;
      }
      if (annotation instanceof Json) {
        annotationJson = (Json) annotation;
        continue;
      }
      if (annotation instanceof ParamsTo) {
        paramsTo = (ParamsTo) annotation;
        continue;
      }
    }
  }

  private MethodParamExtractor createExtractor() {
    if (Upload.class == genericParameterType) {
      if (par == null) throw new NoAnnotationParInUploadParam(parameterIndex, method);

      return (mappingResult, tunnel, model) -> tunnel.getUpload(par.value());
    }

    if (par != null) {
      if (annotationJson != null) return (mappingResult, tunnel, model) -> {
        final String[] paramValues = tunnel.requestParams().asArray(par.value());
        return JsonUtil.convertStrsToType(paramValues, genericParameterType);
      };
      else return (mappingResult, tunnel, model) -> {
        final String[] paramValues = tunnel.requestParams().asArray(par.value());
        return MvcUtil.convertStringsToType(paramValues, genericParameterType);
      };
    }

    if (parPath != null) return (mappingResult, tunnel, model) -> {
      String paramValue = mappingResult.getParam(parPath.value());

      paramValue = URLDecoder.decode(paramValue, "UTF-8");

      if (annotationJson != null) {
        return JsonUtil.convertStrToType(paramValue, genericParameterType);
      } else {
        return MvcUtil.convertStrToType(paramValue, genericParameterType);
      }
    };

    if (parCookie != null) {
      if (parCookie.asIs() && String.class != genericParameterType) {
        throw new AsIsOnlyForString(parameterIndex, method);
      }
      return (mappingResult, tunnel, model) -> {
        final String str = tunnel.cookies().name(parCookie.value()).value();
        if (parCookie.asIs()) return str;
        return CookieUtil.strToObject(str);
      };
    }

    if (parSession != null) {
      if (sessionParameterGetter == null) throw new NullPointerException("sessionParameterGetter == null");
      final SessionParameterGetter.ParameterContext context = new SessionParameterGetter.ParameterContext() {
        @Override
        public String parameterName() {
          return parSession.value();
        }

        @Override
        public Type expectedReturnType() {
          return genericParameterType;
        }

        @Override
        public Json json() {
          return annotationJson;
        }
      };
      return (mappingResult, tunnel, model) -> sessionParameterGetter.getSessionParameter(context, tunnel);
    }

    if (requestInput) {
      if (annotationJson != null) return (mappingResult, tunnel, model) -> {
        String content = MvcUtil.readAll(tunnel.getRequestReader());
        return JsonUtil.convertStrToType(content, genericParameterType);
      };

      return (mappingResult, tunnel, model) -> convertRequestContentToType(tunnel, genericParameterType);
    }

    if (paramsTo != null) {

      Class<Object> parameterClass = MvcUtil.typeToClass(genericParameterType);
      final FieldSetters fieldSetters = FieldSettersStorage.getFor(parameterClass);

      return (mappingResult, tunnel, model) -> {

        Object ret = parameterClass.newInstance();

        for (String name : fieldSetters.names()) {
          String[] values = tunnel.requestParams().asArray(name);
          if (values != null) fieldSetters.get(name).setFromStrings(ret, values);
        }

        return ret;
      };
    }

    if (MvcModel.class == genericParameterType) return (mappingResult, tunnel, model) -> model;

    if (RequestTunnel.class == genericParameterType) return (mappingResult, tunnel, model) -> tunnel;

    if (TunnelCookies.class == genericParameterType) return (mappingResult, tunnel, model) -> tunnel.cookies();

    if (genericParameterType == RequestMethod.class) {
      return (mappingResult, tunnel, model) -> tunnel.getRequestMethod();
    }

    if (genericParameterType == BinResponse.class) {
      return new MethodParamExtractor() {
        @Override
        public Object extract(MappingResult mappingResult, RequestTunnel tunnel, MvcModel model) {
          return new BinResponse() {
            String filename = null;

            @Override
            public void setFilename(String filename) {
              if (out != null) throw new DoNotSetFilenameAfterOut();
              this.filename = filename;
              tunnel.setResponseHeader(
                "Content-Disposition",
                "attachment; filename=\"" + UriEscape.escape(filename) + "\""
              );
            }

            @Override
            public void setContentType(String contentType) {
              if (out != null) throw new DoNotSetContentTypeAfterOut();
              tunnel.setResponseContentType(contentType);
            }

            @Override
            public void setContentTypeByFilenameExtension() {
              if (filename == null) throw new IllegalCallSequence(
                "setContentTypeByFilenameExtension must be called after call setFilename"
              );
              setContentType(MimeUtil.mimeTypeFromFilename(filename));
            }

            private OutputStream out = null;

            @Override
            public OutputStream out() {
              if (out == null) out = tunnel.getResponseOutputStream();
              return out;
            }

            @Override
            public void flushBuffers() {
              tunnel.flushBuffer();
            }
          };
        }
      };
    }

    throw new CannotExtractParamValue(parameterIndex, method);
  }

  private static Object convertRequestContentToType(RequestTunnel tunnel, Type type) throws Exception {
    if (type instanceof Class) return convertRequestContentToClass(tunnel, (Class<?>) type);
    if (type instanceof ParameterizedType) {
      return convertRequestContentToParameterizedType(tunnel, (ParameterizedType) type);
    }
    throw new IDoNotKnowHowToConvertRequestContentToType(type);
  }

  private static Object convertRequestContentToParameterizedType(
    RequestTunnel tunnel, ParameterizedType type
  ) throws Exception {
    final Type rawType = type.getRawType();

    if (rawType == List.class) {
      List ret = new ArrayList();
      try (BufferedReader reader = tunnel.getRequestReader()) {
        while (true) {
          final String line = reader.readLine();
          if (line == null) return ret;
          //noinspection unchecked
          ret.add(MvcUtil.convertStrToType(line, type.getActualTypeArguments()[0]));
        }
      }
    }

    throw new IDoNotKnowHowToConvertRequestContentToType(type);
  }

  private static Object convertRequestContentToClass(RequestTunnel tunnel, Class<?> aClass) throws Exception {
    if (String.class.equals(aClass)) {
      return MvcUtil.readAll(tunnel.getRequestReader());
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
