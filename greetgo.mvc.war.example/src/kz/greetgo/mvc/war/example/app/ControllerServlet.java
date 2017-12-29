package kz.greetgo.mvc.war.example.app;

import kz.greetgo.mvc.interfaces.TunnelExecutorGetter;
import kz.greetgo.mvc.interfaces.Views;
import kz.greetgo.mvc.model.UploadInfo;
import kz.greetgo.mvc.war.AppServlet;
import kz.greetgo.mvc.war.example.controllers.MethodReturnsController;
import kz.greetgo.mvc.war.example.controllers.RequestParametersController;
import kz.greetgo.mvc.war.example.controllers.RootController;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class ControllerServlet extends AppServlet {

  private final Views views;

  public ControllerServlet(Views views) {
    this.views = views;
  }

  /**
   * Контроллеров может быть несколько, поэтому этот метод возвращает список контроллеров
   */
  @Override
  protected List<Object> getControllerList() {
    List<Object> ret = new ArrayList<>();
    ret.add(new RootController());//здесь указываем наш контроллер
    ret.add(new RequestParametersController());
    ret.add(new MethodReturnsController());
    return unmodifiableList(ret);
  }

  @Override
  protected Views getViews() {
    return views;
  }

  @Override
  protected UploadInfo getUploadInfo() {
    final UploadInfo ret = new UploadInfo();
    ret.maxFileSize = 50_000_000;
    ret.fileSizeThreshold = 1_000;
    return ret;
  }

  @Override
  protected void afterRegister() {

    System.out.println("-- [ControllerServlet] --------------------------------------");
    System.out.println("-- [ControllerServlet] -- USING CONTROLLERS:");
    for (TunnelExecutorGetter teg : tunnelExecutorGetters) {
      System.out.println("-- [ControllerServlet] --   " + teg.infoStr());
    }
    System.out.println("-- [ControllerServlet] --------------------------------------");

    super.afterRegister();
  }

  @Override
  protected String getTargetSubContext() {
    return "/api";//важно здесь поставить правильный путь к сервлету
  }
}