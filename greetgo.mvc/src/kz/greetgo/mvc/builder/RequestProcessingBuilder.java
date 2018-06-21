package kz.greetgo.mvc.builder;

import kz.greetgo.mvc.core.ControllerTunnelExecutorBuilder;
import kz.greetgo.mvc.interfaces.RequestProcessing;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.mvc.interfaces.TunnelExecutor;
import kz.greetgo.mvc.interfaces.TunnelExecutorGetter;
import kz.greetgo.mvc.interfaces.Views;
import kz.greetgo.mvc.util.MvcUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class RequestProcessingBuilder {

  private static class ControllerTaker {
    final Class<?> controllerClass;
    final Supplier<Object> instanceGetter;

    private ControllerTaker(Class<?> controllerClass, Supplier<Object> instanceGetter) {
      this.controllerClass = controllerClass;
      this.instanceGetter = instanceGetter;
    }
  }

  public static RequestProcessingBuilder newBuilder(Views views) {return new RequestProcessingBuilder(requireNonNull(views));}

  private RequestProcessingBuilder(Views views) {this.views = views;}

  public RequestProcessingBuilder with(Consumer<RequestProcessingBuilder> consumer) {
    consumer.accept(this);
    return this;
  }

  public RequestProcessingBuilder setCheckControllerMappersConflicts(boolean checkControllerMappersConflicts) {
    this.checkControllerMappersConflicts = checkControllerMappersConflicts;
    return this;
  }

  private final Views views;
  private final List<ControllerTaker> controllerTakerList = new ArrayList<>();
  private boolean built = false;
  private boolean checkControllerMappersConflicts = true;

  public RequestProcessing build() {
    checkBuilt();
    built = true;

    final List<TunnelExecutorGetter> getterList = controllerTakerList.stream()
      .map(t -> t.instanceGetter.get())
      .flatMap(controller -> ControllerTunnelExecutorBuilder.build(controller, views).stream())
      .collect(Collectors.toList());

    if (checkControllerMappersConflicts) MvcUtil.checkTunnelExecutorGetters(getterList);

    final List<ExecDefinition> execDefinitionList = getterList.stream()
      .map(TunnelExecutorGetter::definition)
      .collect(Collectors.toList());

    return new RequestProcessing() {
      @Override
      public List<ExecDefinition> execDefinitionList() {
        return execDefinitionList;
      }

      @Override
      public void processRequest(RequestTunnel tunnel) throws Exception {
        for (TunnelExecutorGetter teg : getterList) {
          TunnelExecutor executor = teg.getTunnelExecutor(tunnel);
          if (executor != null) {
            executor.execute();
            return;
          }
        }

        views.missedView(tunnel);
      }
    };
  }

  public RequestProcessingBuilder addController(Object controller) {
    checkBuilt();
    controllerTakerList.add(new ControllerTaker(controller.getClass(), () -> controller));
    return this;
  }

  private void checkBuilt() {
    if (built) throw new RuntimeException("Already built");
  }

  public RequestProcessingBuilder addControllerTaker(Class<?> controllerClass, Supplier<Object> controllerSupplier) {
    checkBuilt();
    controllerTakerList.add(new ControllerTaker(controllerClass, controllerSupplier));
    return this;
  }
}
