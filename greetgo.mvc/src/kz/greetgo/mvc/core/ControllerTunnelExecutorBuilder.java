package kz.greetgo.mvc.core;

import kz.greetgo.mvc.annotations.AsIs;
import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.MethodFilter;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.mvc.annotations.ToXml;
import kz.greetgo.mvc.interfaces.MappingResult;
import kz.greetgo.mvc.interfaces.MethodInvokedResult;
import kz.greetgo.mvc.interfaces.MethodInvoker;
import kz.greetgo.mvc.interfaces.MethodParamExtractor;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.mvc.interfaces.TunnelCookies;
import kz.greetgo.mvc.interfaces.TunnelExecutor;
import kz.greetgo.mvc.interfaces.TunnelExecutorGetter;
import kz.greetgo.mvc.interfaces.Views;
import kz.greetgo.mvc.model.MvcModelData;
import kz.greetgo.mvc.model.Redirect;
import kz.greetgo.mvc.model.UploadInfo;
import kz.greetgo.mvc.util.MvcUtil;

import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static kz.greetgo.util.ServerUtil.getAnnotation;

public class ControllerTunnelExecutorBuilder {
  public static List<TunnelExecutorGetter> build(Object controller, Views views) {
    final ControllerTunnelExecutorBuilder builder = new ControllerTunnelExecutorBuilder(controller, views);

    builder.build();

    return builder.result;
  }

  final Object controller;
  final Views views;

  final List<TunnelExecutorGetter> result = new ArrayList<>();

  final Class<?> controllerClass;

  private ControllerTunnelExecutorBuilder(Object controller, Views views) {
    this.controller = controller;
    this.views = views;

    controllerClass = controller.getClass();
  }

  String parentMapping[] = null;

  private final UploadInfoGetter uploadInfoGetter = new UploadInfoGetter();

  private void build() {
    prepareParentMapping();
    uploadInfoGetter.assembleAnnotationFromController(controller);
    for (Method method : controllerClass.getMethods()) {
      appendHandlerForMethod(method);
    }
  }

  private void prepareParentMapping() {
    final Mapping mapping = controllerClass.getAnnotation(Mapping.class);
    if (mapping != null) parentMapping = mapping.value();
  }

  private void appendHandlerForMethod(final Method method) {
    final Mapping mapping = method.getAnnotation(Mapping.class);
    if (mapping == null) return;

    final UploadInfoGetter localUploadInfoGetter = uploadInfoGetter.copy();

    localUploadInfoGetter.assembleAnnotationFromMethod(method);

    final List<MethodParamExtractor> extractorList = MethodParameterMeta.create(method, views);

    if (parentMapping == null || parentMapping.length == 0) {

      for (String mappingStr : mapping.value()) {
        final TargetMapper targetMapper = new TargetMapper(mappingStr, method.getAnnotation(MethodFilter.class));
        result.add(createTunnelExecutorGetter(method, localUploadInfoGetter, targetMapper, extractorList));
      }

    } else for (String parentMapperStr : parentMapping) {

      for (String mappingStr : mapping.value()) {
        final TargetMapper targetMapper = new TargetMapper
          (parentMapperStr + mappingStr, method.getAnnotation(MethodFilter.class));
        result.add(createTunnelExecutorGetter(method, localUploadInfoGetter, targetMapper, extractorList));
      }

    }


  }

  private TunnelExecutorGetter createTunnelExecutorGetter(Method method,
                                                          UploadInfoGetter localUploadInfoGetter,
                                                          TargetMapper targetMapper,
                                                          List<MethodParamExtractor> extractorList) {
    return new TunnelExecutorGetter() {
      @Override
      public String infoStr() {
        return method.getDeclaringClass().getSimpleName() + "." + method.getName() + " : " + targetMapper.infoStr();
      }

      @Override
      public MappingIdentity getMappingIdentity() {
        return targetMapper.getMappingIdentity();
      }

      @Override
      public TunnelExecutor getTunnelExecutor(final RequestTunnel tunnel) {
        final MappingResult mappingResult = targetMapper.mapTarget(tunnel);
        if (!mappingResult.ok()) return null;

        return new TunnelExecutor() {
          @Override
          public UploadInfo getUploadInfo() {
            return localUploadInfoGetter.get();
          }

          @Override
          public void execute() throws Exception {
            views.performRequest(new MethodInvoker() {
              @Override
              public RequestTunnel tunnel() {
                return tunnel;
              }

              @Override
              public Method method() {
                return method;
              }

              @Override
              public MappingResult mappingResult() {
                return mappingResult;
              }

              @Override
              public <T extends Annotation> T getMethodAnnotation(Class<T> annotation) {
                return getAnnotation(method, annotation);
              }

              final MvcModelData model = new MvcModelData();

              @Override
              public MvcModelData model() {
                return model;
              }

              @Override
              public Object controller() {
                return controller;
              }

              @Override
              public MethodInvokedResult invoke() {

                Throwable error = null;
                Object returnedValue = null;

                try {

                  Object[] paramValues = new Object[extractorList.size()];
                  for (int i = 0, n = extractorList.size(); i < n; i++) {
                    final MethodParamExtractor e = extractorList.get(i);
                    paramValues[i] = e.extract(mappingResult, tunnel, model);
                  }

                  returnedValue = method.invoke(controller, paramValues);

                } catch (Throwable e) {
                  error = e;
                  if (error instanceof InvocationTargetException) {
                    error = ((InvocationTargetException) error).getTargetException();
                  }
                }

                {
                  final Throwable finalError = error;
                  final Object finalReturnedValue = returnedValue;
                  return new MethodInvokedResult() {
                    @Override
                    public Object returnedValue() {
                      return finalReturnedValue;
                    }

                    @Override
                    public Throwable error() {
                      return finalError;
                    }

                    @Override
                    public boolean tryDefaultRender() {
                      if (model.statusCode != null) tunnel.setResponseStatus(model.statusCode);

                      if (finalError != null) {
                        final Redirect redirect = MvcUtil.extractRedirect(finalError, 4);
                        if (redirect != null) {
                          copyCookies(redirect, tunnel.cookies());
                          tunnel.sendRedirect(redirect.reference);
                          return true;
                        }

                        return false;
                      }

                      if (finalReturnedValue instanceof Redirect) {
                        final Redirect redirect = (Redirect) finalReturnedValue;
                        copyCookies(redirect, tunnel.cookies());
                        tunnel.sendRedirect(redirect.reference);
                        return true;
                      }

                      if (getAnnotation(method, ToJson.class) != null) try {
                        final String content = views.toJson(finalReturnedValue, tunnel, method);
                        try (final PrintWriter writer = tunnel.getResponseWriter()) {
                          writer.print(content);
                        }
                        return true;
                      } catch (Exception e) {
                        if (e instanceof RuntimeException) throw (RuntimeException) e;
                        throw new RuntimeException(e);
                      }

                      if (getAnnotation(method, ToXml.class) != null) try {
                        final String content = views.toXml(finalReturnedValue, tunnel, method);
                        try (final PrintWriter writer = tunnel.getResponseWriter()) {
                          writer.print(content);
                        }
                        return true;
                      } catch (Exception e) {
                        if (e instanceof RuntimeException) throw (RuntimeException) e;
                        throw new RuntimeException(e);
                      }

                      if (getAnnotation(method, AsIs.class) != null) {
                        String resultStr;
                        if (finalReturnedValue == null) {
                          resultStr = "";
                        } else if (finalReturnedValue instanceof String) {
                          resultStr = (String) finalReturnedValue;
                        } else {
                          resultStr = finalReturnedValue.toString();
                        }
                        try (final PrintWriter writer = tunnel.getResponseWriter()) {
                          writer.print(resultStr);
                        }
                        return true;
                      }

                      return false;
                    }
                  };
                }
              }
            });
          }
        };
      }

    };
  }

  private static void copyCookies(Redirect redirect, TunnelCookies cookies) {
    for (Map.Entry<String, String> e : redirect.savingCookiesToResponse.entrySet()) {
      cookies.forName(e.getKey()).saveValue(e.getValue());
    }
  }
}


