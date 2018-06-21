package kz.greetgo.mvc.core;

import kz.greetgo.mvc.annotations.*;
import kz.greetgo.mvc.annotations.on_methods.ControllerPrefix;
import kz.greetgo.mvc.annotations.on_methods.OnConnect;
import kz.greetgo.mvc.annotations.on_methods.OnDelete;
import kz.greetgo.mvc.annotations.on_methods.OnGet;
import kz.greetgo.mvc.annotations.on_methods.OnHead;
import kz.greetgo.mvc.annotations.on_methods.OnMove;
import kz.greetgo.mvc.annotations.on_methods.OnOptions;
import kz.greetgo.mvc.annotations.on_methods.OnPost;
import kz.greetgo.mvc.annotations.on_methods.OnPri;
import kz.greetgo.mvc.annotations.on_methods.OnProxy;
import kz.greetgo.mvc.annotations.on_methods.OnPut;
import kz.greetgo.mvc.annotations.on_methods.OnTrace;
import kz.greetgo.mvc.errors.AmbiguousMaxFileSize;
import kz.greetgo.mvc.errors.CompatibleTargetMapping;
import kz.greetgo.mvc.errors.InconsistentUploadAnnotationsUnderClass;
import kz.greetgo.mvc.errors.InconsistentUploadAnnotationsUnderMethod;
import kz.greetgo.mvc.interfaces.TunnelExecutor;
import kz.greetgo.mvc.interfaces.TunnelExecutorGetter;
import kz.greetgo.mvc.model.MvcModel;
import kz.greetgo.mvc.model.Redirect;
import kz.greetgo.mvc.model.UploadInfo;
import kz.greetgo.mvc.util.MvcUtil;
import kz.greetgo.mvc.utils.TestTunnel;
import kz.greetgo.mvc.utils.TestViews;
import kz.greetgo.util.RND;
import org.fest.assertions.api.Assertions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.servlet.http.Cookie;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static kz.greetgo.mvc.core.RequestMethod.GET;
import static kz.greetgo.mvc.core.RequestMethod.POST;
import static org.fest.assertions.api.Assertions.assertThat;

public class ControllerTunnelExecutorBuilderTest {

  private static final long RETURN_JSON = RND.plusLong(1000000000);
  private static final long RETURN_XML = RND.plusLong(1000000000);
  private static final String STR_PARAM_VALUE = RND.str(10);
  private static final String RETURN_DEFAULT_STR = RND.str(10);
  private static final String MODEL_PARAMETER_NAME = RND.str(10);
  private static final String MODEL_PARAMETER_VALUE = RND.str(10);

  private static final String COOKIE_KEY1 = "COOKIE_KEY1_" + RND.intStr(10);
  private static final String COOKIE_VALUE1 = "COOKIE_VALUE1_" + RND.str(10);
  private static final String COOKIE_KEY2 = "COOKIE_KEY2_" + RND.intStr(10);
  private static final String COOKIE_VALUE2 = "COOKIE_VALUE2_" + RND.str(10);

  @ControllerPrefix("/test")
  @SuppressWarnings("unused")
  public static class TestController {

    public String strParam;

    @ToJson
    @OnGet("/to_json")
    public Object performToJson(@Par("strParam") String strParam) {
      this.strParam = strParam;
      return RETURN_JSON;
    }

    @ToXml
    @OnGet("/to_xml")
    public Object performToXml(@Par("strParam") String strParam) {
      this.strParam = strParam;
      return RETURN_XML;
    }

    @OnGet("/default_str")
    public String performDefaultStr(@Par("strParam") String strParam, MvcModel model) {
      this.strParam = strParam;

      model.setParam(MODEL_PARAMETER_NAME, MODEL_PARAMETER_VALUE);
      model.setStatus(200);

      return RETURN_DEFAULT_STR;
    }

    public boolean calledReturnRedirect = false;

    @OnGet("/return_redirect")
    public Redirect returnRedirect() {
      calledReturnRedirect = true;
      //noinspection ThrowableResultOfMethodCallIgnored
      return Redirect.to(RETURN_REDIRECT_TO).addCookie(COOKIE_KEY1, COOKIE_VALUE1)
        .addCookie(COOKIE_KEY2, COOKIE_VALUE2);
    }

    public boolean calledThrowRedirect = false;

    @OnGet("/throw_redirect")
    public Redirect throwRedirect() {
      calledThrowRedirect = true;
      //noinspection ThrowableResultOfMethodCallIgnored
      throw Redirect.to(THROW_REDIRECT_TO).addCookie(COOKIE_KEY1, COOKIE_VALUE1)
        .addCookie(COOKIE_KEY2, COOKIE_VALUE2);
    }

  }

  private static final String RETURN_REDIRECT_TO = RND.str(10);
  private static final String THROW_REDIRECT_TO = RND.str(10);

  private static boolean handleFirst(List<TunnelExecutorGetter> handlerGetterList, TestTunnel tunnel) throws Exception {
    for (TunnelExecutorGetter tunnelExecutorGetter : handlerGetterList) {
      final TunnelExecutor tunnelExecutor = tunnelExecutorGetter.getTunnelExecutor(tunnel);
      if (tunnelExecutor != null) {
        tunnelExecutor.execute();
        return true;
      }
    }

    return false;
  }

  @Test
  public void create_handleTunnel_return_redirect() throws Exception {

    final TestViews views = new TestViews();

    final TestController controller = new TestController();

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(controller, views);
    //
    //

    final TestTunnel tunnel = new TestTunnel(GET);
    tunnel.target = "/test/return_redirect";

    //
    //
    final boolean handled = handleFirst(handlerGetterList, tunnel);
    //
    //

    assertThat(handled).isTrue();
    assertThat(controller.calledReturnRedirect).isTrue();
    assertThat(tunnel.redirectedTo).isEqualTo(RETURN_REDIRECT_TO);

    final Map<String, String> savedCookies = tunnel.testCookies.addedCookies
      .stream().collect(Collectors.toMap(Cookie::getName, Cookie::getValue));

    assertThat(savedCookies.get(COOKIE_KEY1)).isEqualTo(COOKIE_VALUE1);
    assertThat(savedCookies.get(COOKIE_KEY2)).isEqualTo(COOKIE_VALUE2);

  }

  @Test
  public void create_handleTunnel_throw_redirect() throws Exception {

    final TestViews views = new TestViews();

    final TestController controller = new TestController();

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(controller, views);
    //
    //

    final TestTunnel tunnel = new TestTunnel(GET);
    tunnel.target = "/test/throw_redirect";

    //
    //
    final boolean handled = handleFirst(handlerGetterList, tunnel);
    //
    //

    assertThat(handled).isTrue();
    assertThat(controller.calledThrowRedirect).isTrue();
    assertThat(tunnel.redirectedTo).isEqualTo(THROW_REDIRECT_TO);

    final Map<String, String> savedCookies = tunnel.testCookies.addedCookies
      .stream().collect(Collectors.toMap(Cookie::getName, Cookie::getValue));
    assertThat(savedCookies.get(COOKIE_KEY1)).isEqualTo(COOKIE_VALUE1);
    assertThat(savedCookies.get(COOKIE_KEY2)).isEqualTo(COOKIE_VALUE2);
  }

  @Test
  public void create_handleTunnel_notHandled() throws Exception {

    final TestViews views = new TestViews();

    final TestController controller = new TestController();

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(controller, views);
    //
    //

    final TestTunnel tunnel = new TestTunnel(GET);
    tunnel.target = "b34h25b34h542hb42k5bh425hkb";

    //
    //
    final boolean handled = handleFirst(handlerGetterList, tunnel);
    //
    //

    assertThat(handled).isFalse();

  }

  @Test
  public void create_handleTunnel_to_json() throws Exception {

    final TestViews views = new TestViews();

    final TestController controller = new TestController();

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(controller, views);
    //
    //

    final TestTunnel tunnel = new TestTunnel(GET);
    tunnel.target = "/test/to_json";
    tunnel.setParam("strParam", STR_PARAM_VALUE);

    //
    //
    final boolean handled = handleFirst(handlerGetterList, tunnel);
    //
    //

    assertThat(handled).isTrue();
    assertThat(tunnel.responseCharText()).isEqualTo("JSON " + RETURN_JSON);
    assertThat(controller.strParam).isEqualTo(STR_PARAM_VALUE);

  }

  @Test
  public void create_handleTunnel_to_xml() throws Exception {

    final TestViews views = new TestViews();

    final TestController controller = new TestController();

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(controller, views);
    //
    //

    final TestTunnel tunnel = new TestTunnel(GET);
    tunnel.target = "/test/to_xml";
    tunnel.setParam("strParam", STR_PARAM_VALUE);

    //
    //
    final boolean handled = handleFirst(handlerGetterList, tunnel);
    //
    //

    assertThat(handled).isTrue();
    assertThat(tunnel.responseCharText()).isEqualTo("XML " + RETURN_XML);
    assertThat(controller.strParam).isEqualTo(STR_PARAM_VALUE);

  }

  @Test
  public void create_handleTunnel_default_str() throws Exception {

    final TestViews views = new TestViews();

    final TestController controller = new TestController();

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(controller, views);
    //
    //

    final TestTunnel tunnel = new TestTunnel(GET);
    tunnel.target = "/test/default_str";
    tunnel.setParam("strParam", STR_PARAM_VALUE);

    //
    //
    final boolean handled = handleFirst(handlerGetterList, tunnel);
    //
    //

    assertThat(handled).isTrue();
    assertThat(controller.strParam).isEqualTo(STR_PARAM_VALUE);
    assertThat(tunnel.responseStatus).isEqualTo(200);
    assertThat(tunnel.responseBinText()).isEqualTo("view of " + RETURN_DEFAULT_STR);
    assertThat(views.returnValue).isEqualTo(RETURN_DEFAULT_STR);
    assertThat(views.model).isNotNull();
    assertThat(views.model.data.get(MODEL_PARAMETER_NAME)).isEqualTo(MODEL_PARAMETER_VALUE);
  }

  @SuppressWarnings("unused")
  class UploadInfoDefault {
    @SuppressWarnings("EmptyMethod")
    @OnPost("tmp")
    public void forTest() {}
  }

  @Test
  public void getUploadInfo_UploadInfoDefault() {
    UploadInfoDefault c = new UploadInfoDefault();

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(c, null);
    //
    //

    assertThat(handlerGetterList).hasSize(1);

    TestTunnel tunnel = new TestTunnel(POST);
    tunnel.target = "tmp";

    final TunnelExecutor tunnelExecutor = handlerGetterList.get(0).getTunnelExecutor(tunnel);

    //
    //
    final UploadInfo multipartConf = tunnelExecutor.getUploadInfo();
    //
    //

    assertThat(multipartConf).isNotNull();
    assertThat(multipartConf.location).isEqualTo(System.getProperty("java.io.tmpdir"));
    assertThat(multipartConf.maxFileSize).isEqualTo(-1L);
    assertThat(multipartConf.maxRequestSize).isEqualTo(-1L);
    assertThat(multipartConf.fileSizeThreshold).isZero();

  }

  @SuppressWarnings("unused")
  @UploadInfoFromMethod("getUploadInfoForTest")
  class UploadInfoFromMethod1 {
    @SuppressWarnings("EmptyMethod")
    @OnPost("tmp")
    public void forTest() {}

    final String location = RND.str(10);

    public UploadInfo getUploadInfoForTest() {
      UploadInfo ret = new UploadInfo();
      ret.location = location;
      return ret;
    }
  }

  @Test
  public void getUploadInfo_UploadInfoFromMethod1() {
    UploadInfoFromMethod1 c = new UploadInfoFromMethod1();

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(c, null);
    //
    //

    assertThat(handlerGetterList).hasSize(1);

    TestTunnel tunnel = new TestTunnel(POST);
    tunnel.target = "tmp";

    final TunnelExecutor tunnelExecutor = handlerGetterList.get(0).getTunnelExecutor(tunnel);

    assertThat(tunnelExecutor).isNotNull();

    //
    //
    final UploadInfo multipartConf = tunnelExecutor.getUploadInfo();
    //
    //

    assertThat(multipartConf).isNotNull();
    assertThat(multipartConf.location).isEqualTo(c.location);
  }

  @SuppressWarnings("unused")
  @UploadInfoFromMethod("getUploadInfoForTest")
  class UploadInfoFromMethod2 {
    @SuppressWarnings("EmptyMethod")
    @OnPost("tmp")
    @UploadInfoFromMethod("getUploadInfoForMethod")
    public void forTest() {
    }

    final String location = RND.str(10);

    public UploadInfo getUploadInfoForMethod() {
      UploadInfo ret = new UploadInfo();
      ret.location = location;
      return ret;
    }

    public UploadInfo getUploadInfoForTest() {
      UploadInfo ret = new UploadInfo();
      ret.location = "left";
      return ret;
    }
  }

  @Test
  public void getUploadInfo_UploadInfoFromMethod2() {
    UploadInfoFromMethod2 c = new UploadInfoFromMethod2();

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(c, null);
    //
    //

    assertThat(handlerGetterList).hasSize(1);

    TestTunnel tunnel = new TestTunnel(POST);
    tunnel.target = "tmp";

    final TunnelExecutor tunnelExecutor = handlerGetterList.get(0).getTunnelExecutor(tunnel);

    assertThat(tunnelExecutor).isNotNull();

    //
    //
    final UploadInfo multipartConf = tunnelExecutor.getUploadInfo();
    //
    //

    assertThat(multipartConf).isNotNull();
    assertThat(multipartConf.location).isEqualTo(c.location);
  }

  @SuppressWarnings("unused")
  class AmountFormats {
    @SuppressWarnings("EmptyMethod")
    @OnPost("forTest1")
    @UploadMaxFileSize("22 745")
    public void forTest1() {}

    @SuppressWarnings("EmptyMethod")
    @OnPost("forTest2")
    @UploadMaxFileSize("22_745")
    public void forTest2() {}

    @SuppressWarnings("EmptyMethod")
    @OnPost("forTest_K")
    @UploadMaxFileSize("22_745 K")
    public void forTest_K() {}

    @SuppressWarnings("EmptyMethod")
    @OnPost("forTest_k")
    @UploadMaxFileSize("22_745 k")
    public void forTest_k() {}

    @SuppressWarnings("EmptyMethod")
    @OnPost("forTest_Kb")
    @UploadMaxFileSize("22_745Kb")
    public void forTest_Kb() {}

    @SuppressWarnings("EmptyMethod")
    @OnPost("forTest_KB")
    @UploadMaxFileSize("22 745 KB")
    public void forTest_KB() {}

    @SuppressWarnings("EmptyMethod")
    @OnPost("forTest_kb")
    @UploadMaxFileSize("22_745kb")
    public void forTest_kb() {}

    @SuppressWarnings("EmptyMethod")
    @OnPost("forTest_kB")
    @UploadMaxFileSize("22 745 kB")
    public void forTest_kB() {}

    @SuppressWarnings("EmptyMethod")
    @OnPost("forTest_M")
    @UploadMaxFileSize("711 M")
    public void forTest_M() {}

    @SuppressWarnings("EmptyMethod")
    @OnPost("forTest_Mb")
    @UploadMaxFileSize("711 Mb")
    public void forTest_Mb() {}

    @SuppressWarnings("EmptyMethod")
    @OnPost("forTest_MB")
    @UploadMaxFileSize("711 MB")
    public void forTest_MB() {}

    @SuppressWarnings("EmptyMethod")
    @OnPost("forTest_G")
    @UploadMaxFileSize("317 G")
    public void forTest_G() {}

    @SuppressWarnings("EmptyMethod")
    @OnPost("forTest_Gb")
    @UploadMaxFileSize("317 Gb")
    public void forTest_Gb() {}

    @SuppressWarnings("EmptyMethod")
    @OnPost("forTest_GB")
    @UploadMaxFileSize("317 GB")
    public void forTest_GB() {}

    @SuppressWarnings("EmptyMethod")
    @OnPost("forTest_m1")
    @UploadMaxFileSize("-1")
    public void forTest_m1() {}

    @SuppressWarnings("EmptyMethod")
    @OnPost("forTest_zero")
    @UploadMaxFileSize("0")
    public void forTest_zero() {}
  }

  @DataProvider
  public Object[][] dataFor_getUploadInfo_amountFormats() {
    return new Object[][]{

      new Object[]{"forTest1", 22745L}, new Object[]{"forTest2", 22745L},
      new Object[]{"forTest_k", 22745L * 1024L}, new Object[]{"forTest_K", 22745L * 1024L},
      new Object[]{"forTest_Kb", 22745L * 1024L},
      new Object[]{"forTest_KB", 22745L * 1024L},
      new Object[]{"forTest_kb", 22745L * 1024L},
      new Object[]{"forTest_kB", 22745L * 1024L},
      new Object[]{"forTest_M", 711L * 1024L * 1024L},
      new Object[]{"forTest_Mb", 711L * 1024L * 1024L},
      new Object[]{"forTest_MB", 711L * 1024L * 1024L},
      new Object[]{"forTest_G", 317L * 1024L * 1024L * 1024L},
      new Object[]{"forTest_Gb", 317L * 1024L * 1024L * 1024L},
      new Object[]{"forTest_GB", 317L * 1024L * 1024L * 1024L},
      new Object[]{"forTest_m1", -1L}, new Object[]{"forTest_zero", 0L},

    };
  }

  @Test(dataProvider = "dataFor_getUploadInfo_amountFormats")
  public void getUploadInfo_amountFormats(String target, long expectedValue) {
    AmountFormats c = new AmountFormats();

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(c, null);
    //
    //

    TestTunnel tunnel = new TestTunnel(POST);
    tunnel.target = target;

    for (TunnelExecutorGetter tunnelExecutorGetter : handlerGetterList) {
      final TunnelExecutor tunnelExecutor = tunnelExecutorGetter.getTunnelExecutor(tunnel);
      if (tunnelExecutor != null) {
        final UploadInfo uploadInfo = tunnelExecutor.getUploadInfo();
        assertThat(uploadInfo).isNotNull();
        assertThat(uploadInfo.maxFileSize).describedAs("target = " + target).isEqualTo(
          expectedValue);
        return;
      }
    }

    Assertions.fail("ERROR IN DATA PROVIDER: No method for target " + target);
  }

  @SuppressWarnings("unused")
  @UploadMaxFileSize("22745")
  class UploadMaxFileSize1 {
    @SuppressWarnings("EmptyMethod")
    @OnPost("tmp")
    public void forTest() {}
  }

  @Test
  public void getUploadInfo_UploadMaxFileSize1() {
    UploadMaxFileSize1 c = new UploadMaxFileSize1();

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(c, null);
    //
    //

    assertThat(handlerGetterList).hasSize(1);

    TestTunnel tunnel = new TestTunnel(POST);
    tunnel.target = "tmp";

    final TunnelExecutor tunnelExecutor = handlerGetterList.get(0).getTunnelExecutor(tunnel);
    assertThat(tunnelExecutor).isNotNull();

    //
    //
    final UploadInfo multipartConf = tunnelExecutor.getUploadInfo();
    //
    //

    assertThat(multipartConf).isNotNull();
    assertThat(multipartConf.maxFileSize).isEqualTo(22745L);
  }

  @SuppressWarnings("unused")
  @UploadMaxFileSize("22745")
  class UploadMaxFileSize2 {
    @SuppressWarnings("EmptyMethod")
    @OnPost("tmp")
    @UploadMaxFileSize("7711")
    public void forTest() {}
  }

  @Test
  public void getUploadInfo_UploadMaxFileSize2() {
    UploadMaxFileSize2 c = new UploadMaxFileSize2();

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(c, null);
    //
    //

    assertThat(handlerGetterList).hasSize(1);

    TestTunnel tunnel = new TestTunnel(POST);
    tunnel.target = "tmp";

    final TunnelExecutor tunnelExecutor = handlerGetterList.get(0).getTunnelExecutor(tunnel);
    assertThat(tunnelExecutor).isNotNull();

    //
    //
    final UploadInfo multipartConf = tunnelExecutor.getUploadInfo();
    //
    //

    assertThat(multipartConf).isNotNull();
    assertThat(multipartConf.maxFileSize).isEqualTo(7711L);
  }

  @SuppressWarnings("unused")
  @UploadMaxRequestSize("22745")
  class UploadMaxRequestSize1 {
    @SuppressWarnings("EmptyMethod")
    @OnPost("tmp")
    public void forTest() {}
  }

  @Test
  public void getUploadInfo_UploadMaxRequestSize1() {
    UploadMaxRequestSize1 c = new UploadMaxRequestSize1();

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(c, null);
    //
    //

    assertThat(handlerGetterList).hasSize(1);

    TestTunnel tunnel = new TestTunnel(POST);
    tunnel.target = "tmp";

    final TunnelExecutor tunnelExecutor = handlerGetterList.get(0).getTunnelExecutor(tunnel);
    assertThat(tunnelExecutor).isNotNull();

    //
    //
    final UploadInfo multipartConf = tunnelExecutor.getUploadInfo();
    //
    //

    assertThat(multipartConf).isNotNull();
    assertThat(multipartConf.maxRequestSize).isEqualTo(22745L);
  }

  @SuppressWarnings("unused")
  @UploadMaxRequestSize("22745")
  class UploadMaxRequestSize2 {
    @SuppressWarnings("EmptyMethod")
    @OnPost("tmp")
    @UploadMaxRequestSize("7711")
    public void forTest() {}
  }

  @Test
  public void getUploadInfo_UploadMaxRequestSize2() {
    UploadMaxRequestSize2 c = new UploadMaxRequestSize2();

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(c, null);
    //
    //

    assertThat(handlerGetterList).hasSize(1);

    TestTunnel tunnel = new TestTunnel(POST);
    tunnel.target = "tmp";

    final TunnelExecutor tunnelExecutor = handlerGetterList.get(0).getTunnelExecutor(tunnel);
    assertThat(tunnelExecutor).isNotNull();

    //
    //
    final UploadInfo multipartConf = tunnelExecutor.getUploadInfo();
    //
    //

    assertThat(multipartConf).isNotNull();
    assertThat(multipartConf.maxRequestSize).isEqualTo(7711L);
  }

  @SuppressWarnings("unused")
  @UploadFileSizeThreshold("22745")
  class UploadFileSizeThreshold1 {
    @SuppressWarnings("EmptyMethod")
    @OnGet("tmp")
    public void forTest() {}
  }

  @Test
  public void getUploadInfo_UploadFileSizeThreshold1() {
    UploadFileSizeThreshold1 c = new UploadFileSizeThreshold1();

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(c, null);
    //
    //

    assertThat(handlerGetterList).hasSize(1);

    TestTunnel tunnel = new TestTunnel(GET);
    tunnel.target = "tmp";

    final TunnelExecutor tunnelExecutor = handlerGetterList.get(0).getTunnelExecutor(tunnel);

    assertThat(tunnelExecutor).isNotNull();

    //
    //
    final UploadInfo multipartConf = tunnelExecutor.getUploadInfo();
    //
    //

    assertThat(multipartConf).isNotNull();
    assertThat(multipartConf.fileSizeThreshold).isEqualTo(22745);
  }

  @SuppressWarnings("unused")
  @UploadFileSizeThreshold("22745")
  class UploadFileSizeThreshold2 {
    @SuppressWarnings("EmptyMethod")
    @OnPost("tmp")
    @UploadFileSizeThreshold("7711")
    public void forTest() {}
  }

  @Test
  public void getUploadInfo_UploadFileSizeThreshold2() {
    UploadFileSizeThreshold2 c = new UploadFileSizeThreshold2();

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(c, null);
    //
    //

    assertThat(handlerGetterList).hasSize(1);

    TestTunnel tunnel = new TestTunnel(POST);
    tunnel.target = "tmp";

    final TunnelExecutor tunnelExecutor = handlerGetterList.get(0).getTunnelExecutor(tunnel);

    assertThat(tunnelExecutor).isNotNull();

    //
    //
    final UploadInfo multipartConf = tunnelExecutor.getUploadInfo();
    //
    //

    assertThat(multipartConf).isNotNull();
    assertThat(multipartConf.fileSizeThreshold).isEqualTo(7711);
  }

  @SuppressWarnings("unused")
  @UploadLocationFromMethod("getTestLocation")
  class UploadLocationFromMethod1 {
    @SuppressWarnings("EmptyMethod")
    @OnPost("tmp")
    public void forTest() {}

    public String testLocation;

    public String getTestLocation() {
      return testLocation;
    }
  }

  @Test
  public void getUploadInfo_UploadLocationFromMethod1() {
    UploadLocationFromMethod1 c = new UploadLocationFromMethod1();
    c.testLocation = RND.str(10);

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(c, null);
    //
    //

    assertThat(handlerGetterList).hasSize(1);

    TestTunnel tunnel = new TestTunnel(POST);
    tunnel.target = "tmp";

    final TunnelExecutor tunnelExecutor = handlerGetterList.get(0).getTunnelExecutor(tunnel);

    assertThat(tunnelExecutor).isNotNull();

    String expectedLocation = c.testLocation = RND.str(10);

    //
    //
    final UploadInfo multipartConf = tunnelExecutor.getUploadInfo();
    //
    //

    assertThat(multipartConf).isNotNull();
    assertThat(multipartConf.location).isEqualTo(expectedLocation);
  }

  @SuppressWarnings("unused")
  @UploadLocationFromMethod("getTestLocationLeft")
  class UploadLocationFromMethod2 {
    @SuppressWarnings("EmptyMethod")
    @OnPost("tmp")
    @UploadLocationFromMethod("getTestLocation")
    public void forTest() {}

    public String testLocation;

    public String getTestLocation() {
      return testLocation;
    }
  }

  @Test
  public void getUploadInfo_UploadLocationFromMethod2() {
    UploadLocationFromMethod2 c = new UploadLocationFromMethod2();
    c.testLocation = RND.str(10);

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(c, null);
    //
    //

    assertThat(handlerGetterList).hasSize(1);

    TestTunnel tunnel = new TestTunnel(POST);
    tunnel.target = "tmp";

    final TunnelExecutor tunnelExecutor = handlerGetterList.get(0).getTunnelExecutor(tunnel);
    assertThat(tunnelExecutor).isNotNull();
    String expectedLocation = c.testLocation = RND.str(10);

    //
    //
    final UploadInfo multipartConf = tunnelExecutor.getUploadInfo();
    //
    //

    assertThat(multipartConf).isNotNull();
    assertThat(multipartConf.location).isEqualTo(expectedLocation);
  }

  @SuppressWarnings("unused")
  @UploadInfoFromMethod("tmp1")
  @UploadLocationFromMethod("tmp2")
  class TestInconsistentUploadAnnotationsUnderClass {
    @SuppressWarnings("EmptyMethod")
    @OnPost("tmp")
    public void forTest() {}
  }

  @Test(expectedExceptions = InconsistentUploadAnnotationsUnderClass.class)
  public void getUploadInfo_InconsistentUploadAnnotationsUnderClass() {
    TestInconsistentUploadAnnotationsUnderClass c = new TestInconsistentUploadAnnotationsUnderClass();
    //
    //
    ControllerTunnelExecutorBuilder.build(c, null);
    //
    //
  }

  @SuppressWarnings("unused")
  class TestInconsistentUploadAnnotationsUnderMethod {
    @SuppressWarnings("EmptyMethod")
    @OnPost("tmp")
    @UploadInfoFromMethod("tmp1")
    @UploadLocationFromMethod("tmp2")
    public void forTest() {}
  }

  @Test(expectedExceptions = InconsistentUploadAnnotationsUnderMethod.class)
  public void getUploadInfo_InconsistentUploadAnnotationsUnderMethod() {
    TestInconsistentUploadAnnotationsUnderMethod c = new TestInconsistentUploadAnnotationsUnderMethod();
    //
    //
    ControllerTunnelExecutorBuilder.build(c, null);
    //
    //
  }

  @SuppressWarnings("unused")
  @UploadMaxFileSizeFromMethod("left")
  class UploadMaxFileSizeFromMethod_long {
    @SuppressWarnings("EmptyMethod")
    @OnPost("tmp")
    @UploadMaxFileSizeFromMethod("getTestMaxFileSize")
    public void forTest() {}

    public long testMaxFileSize;

    public long getTestMaxFileSize() {
      return testMaxFileSize;
    }
  }

  @Test
  public void getUploadInfo_UploadMaxFileSizeFromMethod_long() {
    UploadMaxFileSizeFromMethod_long c = new UploadMaxFileSizeFromMethod_long();
    c.testMaxFileSize = RND.plusLong(1_000_000_000);

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(c, null);
    //
    //

    assertThat(handlerGetterList).hasSize(1);

    TestTunnel tunnel = new TestTunnel(POST);
    tunnel.target = "tmp";

    final TunnelExecutor tunnelExecutor = handlerGetterList.get(0).getTunnelExecutor(tunnel);
    assertThat(tunnelExecutor).isNotNull();

    long expectedMaxFileSize = c.testMaxFileSize = RND.plusLong(1_000_000_000);

    //
    //
    final UploadInfo multipartConf = tunnelExecutor.getUploadInfo();
    //
    //

    assertThat(multipartConf).isNotNull();
    assertThat(multipartConf.maxFileSize).isEqualTo(expectedMaxFileSize);
  }

  @SuppressWarnings("unused")
  @UploadMaxFileSizeFromMethod("left")
  class UploadMaxFileSizeFromMethod_int {
    @SuppressWarnings("EmptyMethod")
    @OnPost("tmp")
    @UploadMaxFileSizeFromMethod("getTestMaxFileSize")
    public void forTest() {}

    public int testMaxFileSize;

    public int getTestMaxFileSize() {
      return testMaxFileSize;
    }
  }

  @Test
  public void getUploadInfo_UploadMaxFileSizeFromMethod_int() {
    UploadMaxFileSizeFromMethod_int c = new UploadMaxFileSizeFromMethod_int();
    c.testMaxFileSize = RND.plusInt(1_000_000_000);

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(c, null);
    //
    //

    assertThat(handlerGetterList).hasSize(1);

    TestTunnel tunnel = new TestTunnel(POST);
    tunnel.target = "tmp";

    final TunnelExecutor tunnelExecutor = handlerGetterList.get(0).getTunnelExecutor(tunnel);
    assertThat(tunnelExecutor).isNotNull();

    int expectedMaxFileSize = c.testMaxFileSize = RND.plusInt(1_000_000_000);

    //
    //
    final UploadInfo multipartConf = tunnelExecutor.getUploadInfo();
    //
    //

    assertThat(multipartConf).isNotNull();
    assertThat(multipartConf.maxFileSize).isEqualTo(expectedMaxFileSize);
  }

  @SuppressWarnings("unused")
  @UploadMaxFileSize("1M")
  class UploadMaxFileSizeFromMethod_String {
    @SuppressWarnings("EmptyMethod")
    @OnPost("tmp")
    @UploadMaxFileSizeFromMethod("getTestMaxFileSize")
    public void forTest() {}

    public String testMaxFileSize;

    public String getTestMaxFileSize() {
      return testMaxFileSize;
    }
  }

  @Test
  public void getUploadInfo_UploadMaxFileSizeFromMethod_String() {
    UploadMaxFileSizeFromMethod_String c = new UploadMaxFileSizeFromMethod_String();
    c.testMaxFileSize = "left";

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(c, null);
    //
    //

    assertThat(handlerGetterList).hasSize(1);

    TestTunnel tunnel = new TestTunnel(POST);
    tunnel.target = "tmp";

    final TunnelExecutor tunnelExecutor = handlerGetterList.get(0).getTunnelExecutor(tunnel);
    assertThat(tunnelExecutor).isNotNull();

    c.testMaxFileSize = "123G";
    long expectedMaxFileSize = 123L * 1024L * 1024L * 1024L;

    //
    //
    final UploadInfo multipartConf = tunnelExecutor.getUploadInfo();
    //
    //

    assertThat(multipartConf).isNotNull();
    assertThat(multipartConf.maxFileSize).isEqualTo(expectedMaxFileSize);
  }

  @SuppressWarnings("unused")
  @UploadMaxFileSize("1M")
  class TestAmbiguousMaxFileSize {
    @SuppressWarnings("EmptyMethod")
    @OnPost("tmp")
    @UploadMaxFileSize("1M")
    @UploadMaxFileSizeFromMethod("asd")
    public void forTest() {}
  }

  @Test(expectedExceptions = AmbiguousMaxFileSize.class)
  public void testAmbiguousMaxFileSize() throws Exception {
    TestAmbiguousMaxFileSize c = new TestAmbiguousMaxFileSize();

    //
    //
    ControllerTunnelExecutorBuilder.build(c, null);
    //
    //
  }

  public static class TestController2 {

    public final Map<RequestMethod, AtomicInteger> counts = new HashMap<>();

    {
      counts.put(RequestMethod.GET, new AtomicInteger(0));
      counts.put(RequestMethod.POST, new AtomicInteger(0));
      counts.put(RequestMethod.HEAD, new AtomicInteger(0));
      counts.put(RequestMethod.PUT, new AtomicInteger(0));
      counts.put(RequestMethod.OPTIONS, new AtomicInteger(0));
      counts.put(RequestMethod.DELETE, new AtomicInteger(0));
      counts.put(RequestMethod.TRACE, new AtomicInteger(0));
      counts.put(RequestMethod.CONNECT, new AtomicInteger(0));
      counts.put(RequestMethod.MOVE, new AtomicInteger(0));
      counts.put(RequestMethod.PROXY, new AtomicInteger(0));
      counts.put(RequestMethod.PRI, new AtomicInteger(0));
    }

    @OnGet("/commonTarget")
    public void forGET() {counts.get(RequestMethod.GET).incrementAndGet();}

    @OnPost("/commonTarget")
    public void forPOST() {counts.get(RequestMethod.POST).incrementAndGet();}

    @OnHead("/commonTarget")
    public void forHEAD() {counts.get(RequestMethod.HEAD).incrementAndGet();}

    @OnPut("/commonTarget")
    public void forPUT() {counts.get(RequestMethod.PUT).incrementAndGet();}

    @OnOptions("/commonTarget")
    public void forOPTIONS() {counts.get(RequestMethod.OPTIONS).incrementAndGet();}

    @OnDelete("/commonTarget")
    public void forDELETE() {counts.get(RequestMethod.DELETE).incrementAndGet();}

    @OnTrace("/commonTarget")
    public void forTRACE() {counts.get(RequestMethod.TRACE).incrementAndGet();}

    @OnConnect("/commonTarget")
    public void forCONNECT() {counts.get(RequestMethod.CONNECT).incrementAndGet();}

    @OnMove("/commonTarget")
    public void forMOVE() {counts.get(RequestMethod.MOVE).incrementAndGet();}

    @OnProxy("/commonTarget")
    public void forPROXY() {counts.get(RequestMethod.PROXY).incrementAndGet();}

    @OnPri("/commonTarget")
    public void forPRI() {counts.get(RequestMethod.PRI).incrementAndGet();}

  }

  @Test
  public void methodFilter() throws Exception {

    TestController2 c = new TestController2();

    TestViews views = new TestViews();
    views.returnValue = "asd";

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(c, views);
    //
    //

    for (RequestMethod method : RequestMethod.values()) {
      TestTunnel tunnel = new TestTunnel(method);
      tunnel.target = "/commonTarget";

      String d = "requestMethod = " + method;

      assertThat(handleFirst(handlerGetterList, tunnel)).describedAs(d).isTrue();
      assertThat(c.counts.get(method)).describedAs(d).isNotNull();
      assertThat(c.counts.get(method).get()).describedAs(d).isEqualTo(1);
      assertThat(views.returnValue).isNull();
    }

    for (RequestMethod method : RequestMethod.values()) {
      TestTunnel tunnel = new TestTunnel(method);
      tunnel.target = "/leftTarget";

      String d = "requestMethod = " + method;

      assertThat(handleFirst(handlerGetterList, tunnel)).describedAs(d).isFalse();
    }
  }

  @ControllerPrefix("/asd")
  public static class LeftController {
    @OnPost("/asd")
    public void asd1() {}

    @OnPost("/asd")
    public void asd2() {}
  }

  @Test(expectedExceptions = CompatibleTargetMapping.class)
  public void checkTunnelExecutorGetters() {
    TestViews views = new TestViews();
    LeftController c = new LeftController();

    final List<TunnelExecutorGetter> list = ControllerTunnelExecutorBuilder.build(c, views);

    //
    //
    MvcUtil.checkTunnelExecutorGetters(list);
    //
    //
  }
}
