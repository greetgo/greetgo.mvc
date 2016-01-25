package kz.greetgo.depinject.mvc;

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.List;

public class ControllerHandler extends TunnelHandlerList {
  private ControllerHandler() {
  }

  public static ControllerHandler create(Object controller, Views views) {
    final Builder builder = new Builder(controller, views);

    builder.build();

    return builder.result;
  }

  private static class Builder {
    final Object controller;
    final Views views;

    final ControllerHandler result = new ControllerHandler();

    final Class<?> controllerClass;

    public Builder(Object controller, Views views) {
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

      result.add(new TunnelHandler() {
        @Override
        public boolean handleTunnel(RequestTunnel tunnel) {
          MappingResult mappingResult = targetMapper.mapTarget(tunnel.getTarget());
          if (!mappingResult.ok()) return false;

          try {

            MvcModel model = new MvcModel();

            Object[] paramValues = new Object[extractorList.size()];
            for (int i = 0, n = extractorList.size(); i < n; i++) {
              final MethodParamExtractor e = extractorList.get(i);
              paramValues[i] = e.extract(mappingResult, tunnel, model);
            }

            final Object result = method.invoke(controller, paramValues);

            executeView(result, model, tunnel, mappingResult, method);

            return true;

          } catch (Exception e) {
            {
              //noinspection ThrowableResultOfMethodCallIgnored
              final Redirect redirect = MvcUtil.extractRedirect(e, 4);
              if (redirect != null) {
                tunnel.sendRedirect(redirect.reference);
                return true;
              }
            }

            {
              views.errorView(tunnel.getResponseOutputStream(), tunnel.getTarget(), e);
              e.printStackTrace();
              return true;
            }
          }
        }
      });
    }


    private void executeView(Object controllerMethodResult, MvcModel model,
                             RequestTunnel tunnel, MappingResult mappingResult,
                             Method method) {

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

}
