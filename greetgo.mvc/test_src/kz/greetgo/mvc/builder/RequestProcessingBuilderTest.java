package kz.greetgo.mvc.builder;

import kz.greetgo.mvc.annotations.AsIs;
import kz.greetgo.mvc.annotations.on_methods.ControllerPrefix;
import kz.greetgo.mvc.annotations.on_methods.OnPost;
import kz.greetgo.mvc.core.RequestMethod;
import kz.greetgo.mvc.interfaces.RequestProcessing;
import kz.greetgo.mvc.utils.TestTunnel;
import kz.greetgo.mvc.utils.TestViews;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class RequestProcessingBuilderTest {

  @ControllerPrefix("/c1")
  public static class Controller1 {
    public boolean calledM1 = false;
    public String returnM1 = null;

    @AsIs
    @OnPost("/m1")
    public String m1() {
      calledM1 = true;
      return returnM1;
    }
  }

  @ControllerPrefix("/c2")
  public static class Controller2 {
    public boolean calledM1 = false;
    public String returnM1 = null;

    @AsIs
    @OnPost("/m1")
    public String m1() {
      calledM1 = true;
      return returnM1;
    }
  }

  @Test
  public void testName() throws Exception {

    TestViews views = new TestViews();

    Controller1 c1 = new Controller1();
    c1.returnM1 = RND.str(10);

    Controller2 c2 = new Controller2();

    RequestProcessing processing = RequestProcessingBuilder
      .newBuilder(views)
      .addController(c1)
      .addControllerTaker(Controller2.class, () -> c2)
      .build();

    {
      TestTunnel tunnel = new TestTunnel(RequestMethod.POST);
      tunnel.target = "/c1/m1";

      processing.processRequest(tunnel);

      assertThat(c1.calledM1).isTrue();
      assertThat(tunnel.responseCharText()).isEqualTo(c1.returnM1);
    }
  }
}