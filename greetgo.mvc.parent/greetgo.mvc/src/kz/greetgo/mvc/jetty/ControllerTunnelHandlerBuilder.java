package kz.greetgo.mvc.jetty;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ControllerTunnelHandlerBuilder {
  public static List<TunnelHandlerGetter> build(Object controller, Views views) {
    final ControllerTunnelHandlerBuilder builder = new ControllerTunnelHandlerBuilder(controller, views);

    builder.build();

    return builder.result;
  }

  final Object controller;
  final Views views;

  final List<TunnelHandlerGetter> result = new ArrayList<>();

  final Class<?> controllerClass;

  private ControllerTunnelHandlerBuilder(Object controller, Views views) {
    this.controller = controller;
    this.views = views;

    controllerClass = controller.getClass();
  }

  String parentMapping = "";

  void build() {
    prepareParentMapping();
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

    final TargetMapper targetMapper = new TargetMapper(parentMapping + mapping.value());

    final List<MethodParamExtractor> extractorList = MethodParameterMeta.create(method);

    result.add(new TunnelHandlerGetter() {
      @Override
      public TunnelHandler getTunnelHandler(final RequestTunnel tunnel) {
        final MappingResult mappingResult = targetMapper.mapTarget(tunnel.getTarget());
        if (!mappingResult.ok()) return null;

        return new TunnelHandler() {
          @Override
          public void handle() {
            try {

              MvcModel model = new MvcModel();

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
                  tunnel.sendRedirect(redirect.reference);
                  return;
                }
              }

              e.printStackTrace();

              {

                try {
                  views.errorView(tunnel.getResponseOutputStream(), tunnel.getTarget(), e);
                } catch (Exception e1) {
                  throw new RuntimeException(e1);
                }

              }
            }
          }
        };


      }
    });
  }


  private void executeView(Object controllerMethodResult, MvcModel model,
                           RequestTunnel tunnel, MappingResult mappingResult,
                           Method method) throws Exception {

    if (controllerMethodResult instanceof Redirect) {
      Redirect r = (Redirect) controllerMethodResult;
      tunnel.sendRedirect(r.reference);
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

    views.defaultView(tunnel.getResponseOutputStream(), controllerMethodResult, model, mappingResult);
  }
}


