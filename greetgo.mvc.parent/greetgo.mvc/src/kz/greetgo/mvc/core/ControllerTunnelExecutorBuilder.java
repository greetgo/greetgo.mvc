package kz.greetgo.mvc.core;

import kz.greetgo.mvc.annotations.*;
import kz.greetgo.mvc.interfaces.*;
import kz.greetgo.mvc.model.MvcModelData;
import kz.greetgo.mvc.model.Redirect;
import kz.greetgo.mvc.model.UploadInfo;
import kz.greetgo.mvc.util.MvcUtil;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static kz.greetgo.util.ServerUtil.getAnnotation;

public class ControllerTunnelExecutorBuilder {
  public static List<TunnelExecutorGetter> build(Object controller, Views views, SessionParameterGetter sessionParameterGetter) {
    final ControllerTunnelExecutorBuilder builder = new ControllerTunnelExecutorBuilder(controller, views, sessionParameterGetter);

    builder.build();

    return builder.result;
  }

  final Object controller;
  final Views views;

  final List<TunnelExecutorGetter> result = new ArrayList<>();

  final Class<?> controllerClass;
  private final SessionParameterGetter sessionParameterGetter;

  private ControllerTunnelExecutorBuilder(Object controller, Views views, SessionParameterGetter sessionParameterGetter) {
    this.controller = controller;
    this.views = views;
    this.sessionParameterGetter = sessionParameterGetter;

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

    final TargetMapper targetMapper = new TargetMapper
        (parentMapping + mapping.value(), method.getAnnotation(MethodFilter.class));

    final List<MethodParamExtractor> extractorList = MethodParameterMeta.create(method, sessionParameterGetter);

    result.add(new TunnelExecutorGetter() {
      @Override
      public String infoStr() {
        return method.getDeclaringClass().getSimpleName() + "." + method.getName() + " : " + targetMapper.infoStr();
      }

      @Override
      public TunnelExecutor getTunnelExecutor(final RequestTunnel tunnel) {
        final MappingResult mappingResult = targetMapper.mapTarget(tunnel);
        if (!mappingResult.ok()) return null;

        return new TunnelExecutor() {
          @Override
          public void execute() {
            try {

              MvcModelData model = new MvcModelData();

              Object[] paramValues = new Object[extractorList.size()];
              for (int i = 0, n = extractorList.size(); i < n; i++) {
                final MethodParamExtractor e = extractorList.get(i);
                paramValues[i] = e.extract(mappingResult, tunnel, model);
              }

              if (views != null) {
                long slowTime = views.controllerMethodSlowTime();
                if (slowTime > 0) Thread.sleep(slowTime);
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

              if (views != null) {
                try {
                  views.errorView(tunnel, tunnel.getTarget(), method, e);
                } catch (Exception e1) {
                  throw new RuntimeException(e1);
                }
              } else if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
              } else {
                throw new RuntimeException(e);
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
      cookies.saveToResponse(e.getKey(), e.getValue());
    }
  }


  private void executeView(Object controllerMethodResult, MvcModelData model,
                           RequestTunnel tunnel, MappingResult mappingResult,
                           Method method) throws Exception {

    if (model.statusCode != null) tunnel.setResponseStatus(model.statusCode);

    if (controllerMethodResult instanceof Redirect) {
      Redirect redirect = (Redirect) controllerMethodResult;
      copyCookies(redirect, tunnel.cookies());
      tunnel.sendRedirect(redirect.reference);
      return;
    }

    if (getAnnotation(method, ToJson.class) != null) {
      final String content = views.toJson(controllerMethodResult, tunnel, method);
      try (final PrintWriter writer = tunnel.getResponseWriter()) {
        writer.print(content);
      }
      return;
    }

    if (getAnnotation(method, ToXml.class) != null) {
      final String content = views.toXml(controllerMethodResult, tunnel, method);
      try (final PrintWriter writer = tunnel.getResponseWriter()) {
        writer.print(content);
      }
      return;
    }

    if (getAnnotation(method, AsIs.class) != null) {
      String resultStr;
      if (controllerMethodResult == null) {
        resultStr = "";
      } else if (controllerMethodResult instanceof String) {
        resultStr = (String) controllerMethodResult;
      } else {
        resultStr = controllerMethodResult.toString();
      }
      try (final PrintWriter writer = tunnel.getResponseWriter()) {
        writer.print(resultStr);
      }
      return;
    }

    views.defaultView(tunnel, controllerMethodResult, model, mappingResult);
  }
}


