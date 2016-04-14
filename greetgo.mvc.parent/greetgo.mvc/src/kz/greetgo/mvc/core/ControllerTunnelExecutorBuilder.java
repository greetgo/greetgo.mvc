package kz.greetgo.mvc.core;

import kz.greetgo.mvc.annotations.Mapping;
import kz.greetgo.mvc.annotations.ToJson;
import kz.greetgo.mvc.annotations.ToXml;
import kz.greetgo.mvc.interfaces.*;
import kz.greetgo.mvc.model.DefaultMvcModel;
import kz.greetgo.mvc.model.MvcModel;
import kz.greetgo.mvc.model.Redirect;
import kz.greetgo.mvc.model.UploadInfo;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

  String parentMapping = "";

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

    final TargetMapper targetMapper = new TargetMapper(parentMapping + mapping.value());

    final List<MethodParamExtractor> extractorList = MethodParameterMeta.create(method);

    result.add(new TunnelExecutorGetter() {
      @Override
      public TunnelExecutor getTunnelExecutor(final RequestTunnel tunnel) {
        final MappingResult mappingResult = targetMapper.mapTarget(tunnel.getTarget());
        if (!mappingResult.ok()) return null;

        return new TunnelExecutor() {
          @Override
          public void execute() {
            try {

              DefaultMvcModel model = new DefaultMvcModel();

              Object[] paramValues = new Object[extractorList.size()];
              for (int i = 0, n = extractorList.size(); i < n; i++) {
                final MethodParamExtractor e = extractorList.get(i);
                paramValues[i] = e.extract(mappingResult, tunnel, model);
              }

              final Object result = method.invoke(controller, paramValues);

              executeView(result, model, tunnel, mappingResult, method);

            } catch (Exception e) {
              {
                //noinspection ThrowableResultOfMethodCallIgnored
                final Redirect redirect = MvcUtil.extractRedirect(e, 4);
                if (redirect != null) {
                  copyCookies(redirect, tunnel.cookies());
                  tunnel.sendRedirect(redirect.reference);
                  return;
                }
              }

              e.printStackTrace();

              {

                try {
                  views.errorView(tunnel, tunnel.getTarget(), e);
                } catch (Exception e1) {
                  throw new RuntimeException(e1);
                }

              }

            }
          }

          @Override
          public UploadInfo getUploadInfo() {
            return localUploadInfoGetter.get();
          }
        };
      }
    });
  }

  private static void copyCookies(Redirect redirect, TunnelCookies cookies) {
    for (Map.Entry<String, String> e : redirect.savingCookiesToResponse.entrySet()) {
      cookies.saveToResponseStr(e.getKey(), e.getValue());
    }
  }


  private void executeView(Object controllerMethodResult, MvcModel model,
                           RequestTunnel tunnel, MappingResult mappingResult,
                           Method method) throws Exception {

    if (controllerMethodResult instanceof Redirect) {
      Redirect redirect = (Redirect) controllerMethodResult;
      copyCookies(redirect, tunnel.cookies());
      tunnel.sendRedirect(redirect.reference);
      return;
    }

    if (method.getAnnotation(ToJson.class) != null) {
      final String content = views.toJson(controllerMethodResult);
      try (final PrintWriter writer = tunnel.getResponseWriter()) {
        writer.print(content);
      }
    }

    if (method.getAnnotation(ToXml.class) != null) {
      final String content = views.toXml(controllerMethodResult);
      try (final PrintWriter writer = tunnel.getResponseWriter()) {
        writer.print(content);
      }
    }

    if (controllerMethodResult == null) return;

    views.defaultView(tunnel, controllerMethodResult, model, mappingResult);
  }
}


