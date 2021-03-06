package kz.greetgo.mvc.war.stand;

import kz.greetgo.mvc.interfaces.Views;
import kz.greetgo.mvc.model.UploadInfo;
import kz.greetgo.mvc.war.AppServlet;

import java.util.ArrayList;
import java.util.List;

public class StandAppServlet extends AppServlet {

  private final UserDetailsStorage userDetailsStorage;

  public StandAppServlet(UserDetailsStorage userDetailsStorage) {
    this.userDetailsStorage = userDetailsStorage;
  }

  @Override
  protected List<Object> getControllerList() {
    List<Object> ret = new ArrayList<>();
    ret.add(new LoginController(userDetailsStorage));
    return ret;
  }

  private final ProbeViews views = new ProbeViews();

  @Override
  protected Views getViews() {
    return views;
  }

  private final UploadInfo uploadInfo = new UploadInfo();

  @Override
  protected UploadInfo getUploadInfo() {
    return uploadInfo;
  }
}
