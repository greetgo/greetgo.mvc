package kz.greetgo.mvc.core;

import kz.greetgo.mvc.annotations.*;
import kz.greetgo.mvc.errors.AmbiguousMaxFileSize;
import kz.greetgo.mvc.errors.InconsistentUploadAnnotationsUnderClass;
import kz.greetgo.mvc.errors.InconsistentUploadAnnotationsUnderMethod;
import kz.greetgo.mvc.interfaces.TunnelExecutor;
import kz.greetgo.mvc.interfaces.TunnelExecutorGetter;
import kz.greetgo.mvc.model.DefaultMvcModel;
import kz.greetgo.mvc.model.MvcModel;
import kz.greetgo.mvc.model.Redirect;
import kz.greetgo.mvc.model.UploadInfo;
import kz.greetgo.mvc.utils.TestTunnel;
import kz.greetgo.mvc.utils.TestViews;
import kz.greetgo.util.RND;
import org.fest.assertions.api.Assertions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class ControllerTunnelExecutorBuilderTest {

  private static final long RETURN_JSON = RND.plusLong(1000000000);
  private static final long RETURN_XML = RND.plusLong(1000000000);
  private static final String STR_PARAM_VALUE = RND.str(10);
  private static final String RETURN_DEFAULT_STR = RND.str(10);
  private static final String MODEL_PARAMETER_NAME = RND.str(10);
  private static final String MODEL_PARAMETER_VALUE = RND.str(10);

  @Mapping("/test")
  @SuppressWarnings("unused")
  public static class TestController {

    public String strParam;

    @ToJson
    @Mapping("/to_json")
    public Object performToJson(@Par("strParam") String strParam) {
      this.strParam = strParam;
      return RETURN_JSON;
    }

    @ToXml
    @Mapping("/to_xml")
    public Object performToXml(@Par("strParam") String strParam) {
      this.strParam = strParam;
      return RETURN_XML;
    }

    @Mapping("/default_str")
    public String performDefaultStr(@Par("strParam") String strParam, MvcModel model) {
      this.strParam = strParam;

      model.setParam(MODEL_PARAMETER_NAME, MODEL_PARAMETER_VALUE);

      return RETURN_DEFAULT_STR;
    }

    public boolean calledReturnRedirect = false;

    @Mapping("/return_redirect")
    public Redirect returnRedirect() {
      calledReturnRedirect = true;
      return Redirect.to(RETURN_REDIRECT_TO);
    }

    public boolean calledThrowRedirect = false;

    @Mapping("/throw_redirect")
    public Redirect throwRedirect() {
      calledThrowRedirect = true;
      throw Redirect.to(THROW_REDIRECT_TO);
    }

  }

  private static final String RETURN_REDIRECT_TO = RND.str(10);
  private static final String THROW_REDIRECT_TO = RND.str(10);

  private static boolean handleFirst(List<TunnelExecutorGetter> handlerGetterList, TestTunnel tunnel) {
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

    final TestTunnel tunnel = new TestTunnel();
    tunnel.target = "/test/return_redirect";

    //
    //
    final boolean handled = handleFirst(handlerGetterList, tunnel);
    //
    //

    assertThat(handled).isTrue();
    assertThat(controller.calledReturnRedirect).isTrue();
    assertThat(tunnel.redirectedTo).isEqualTo(RETURN_REDIRECT_TO);

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

    final TestTunnel tunnel = new TestTunnel();
    tunnel.target = "/test/throw_redirect";

    //
    //
    final boolean handled = handleFirst(handlerGetterList, tunnel);
    //
    //

    assertThat(handled).isTrue();
    assertThat(controller.calledThrowRedirect).isTrue();
    assertThat(tunnel.redirectedTo).isEqualTo(THROW_REDIRECT_TO);

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

    final TestTunnel tunnel = new TestTunnel();
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

    final TestTunnel tunnel = new TestTunnel();
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

    final TestTunnel tunnel = new TestTunnel();
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

    final TestTunnel tunnel = new TestTunnel();
    tunnel.target = "/test/default_str";
    tunnel.setParam("strParam", STR_PARAM_VALUE);

    //
    //
    final boolean handled = handleFirst(handlerGetterList, tunnel);
    //
    //

    assertThat(handled).isTrue();
    assertThat(controller.strParam).isEqualTo(STR_PARAM_VALUE);
    assertThat(tunnel.responseBinText()).isEqualTo("view of " + RETURN_DEFAULT_STR);
    assertThat(views.returnValue).isEqualTo(RETURN_DEFAULT_STR);
    assertThat(views.model).isNotNull();
    final DefaultMvcModel model = (DefaultMvcModel) views.model;
    assertThat(model.data.get(MODEL_PARAMETER_NAME)).isEqualTo(MODEL_PARAMETER_VALUE);
  }

  @SuppressWarnings("unused")
  class UploadInfoDefault {
    @Mapping("tmp")
    public void forTest() {
    }
  }

  @Test
  public void getUploadInfo_UploadInfoDefault() throws Exception {
    UploadInfoDefault c = new UploadInfoDefault();

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(c, null);
    //
    //

    assertThat(handlerGetterList).hasSize(1);

    TestTunnel tunnel = new TestTunnel();
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
    @Mapping("tmp")
    public void forTest() {
    }

    final String location = RND.str(10);

    public UploadInfo getUploadInfoForTest() {
      UploadInfo ret = new UploadInfo();
      ret.location = location;
      return ret;
    }
  }

  @Test
  public void getUploadInfo_UploadInfoFromMethod1() throws Exception {
    UploadInfoFromMethod1 c = new UploadInfoFromMethod1();

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(c, null);
    //
    //

    assertThat(handlerGetterList).hasSize(1);

    TestTunnel tunnel = new TestTunnel();
    tunnel.target = "tmp";

    final TunnelExecutor tunnelExecutor = handlerGetterList.get(0).getTunnelExecutor(tunnel);

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
    @Mapping("tmp")
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
  public void getUploadInfo_UploadInfoFromMethod2() throws Exception {
    UploadInfoFromMethod2 c = new UploadInfoFromMethod2();

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(c, null);
    //
    //

    assertThat(handlerGetterList).hasSize(1);

    TestTunnel tunnel = new TestTunnel();
    tunnel.target = "tmp";

    final TunnelExecutor tunnelExecutor = handlerGetterList.get(0).getTunnelExecutor(tunnel);

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
    @Mapping("forTest1")
    @UploadMaxFileSize("22 745")
    public void forTest1() {
    }

    @Mapping("forTest2")
    @UploadMaxFileSize("22_745")
    public void forTest2() {
    }

    @Mapping("forTest_K")
    @UploadMaxFileSize("22_745 K")
    public void forTest_K() {
    }

    @Mapping("forTest_k")
    @UploadMaxFileSize("22_745 k")
    public void forTest_k() {
    }

    @Mapping("forTest_Kb")
    @UploadMaxFileSize("22_745Kb")
    public void forTest_Kb() {
    }

    @Mapping("forTest_KB")
    @UploadMaxFileSize("22 745 KB")
    public void forTest_KB() {
    }

    @Mapping("forTest_kb")
    @UploadMaxFileSize("22_745kb")
    public void forTest_kb() {
    }

    @Mapping("forTest_kB")
    @UploadMaxFileSize("22 745 kB")
    public void forTest_kB() {
    }

    @Mapping("forTest_M")
    @UploadMaxFileSize("711 M")
    public void forTest_M() {
    }

    @Mapping("forTest_Mb")
    @UploadMaxFileSize("711 Mb")
    public void forTest_Mb() {
    }

    @Mapping("forTest_MB")
    @UploadMaxFileSize("711 MB")
    public void forTest_MB() {
    }

    @Mapping("forTest_G")
    @UploadMaxFileSize("317 G")
    public void forTest_G() {
    }

    @Mapping("forTest_Gb")
    @UploadMaxFileSize("317 Gb")
    public void forTest_Gb() {
    }

    @Mapping("forTest_GB")
    @UploadMaxFileSize("317 GB")
    public void forTest_GB() {
    }

    @Mapping("forTest_m1")
    @UploadMaxFileSize("-1")
    public void forTest_m1() {
    }

    @Mapping("forTest_zero")
    @UploadMaxFileSize("0")
    public void forTest_zero() {
    }
  }

  @DataProvider
  public Object[][] dataFor_getUploadInfo_amountFormats() {
    return new Object[][]{

      new Object[]{"forTest1", 22745L},
      new Object[]{"forTest2", 22745L},
      new Object[]{"forTest_k", 22745L * 1024L},
      new Object[]{"forTest_K", 22745L * 1024L},
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
      new Object[]{"forTest_m1", -1L},
      new Object[]{"forTest_zero", 0L},

    };
  }

  @Test(dataProvider = "dataFor_getUploadInfo_amountFormats")
  public void getUploadInfo_amountFormats(String target, long expectedValue) throws Exception {
    AmountFormats c = new AmountFormats();

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(c, null);
    //
    //

    TestTunnel tunnel = new TestTunnel();
    tunnel.target = target;

    for (TunnelExecutorGetter tunnelExecutorGetter : handlerGetterList) {
      final TunnelExecutor tunnelExecutor = tunnelExecutorGetter.getTunnelExecutor(tunnel);
      if (tunnelExecutor != null) {
        final UploadInfo uploadInfo = tunnelExecutor.getUploadInfo();
        assertThat(uploadInfo).isNotNull();
        assertThat(uploadInfo.maxFileSize).describedAs("target = " + target).isEqualTo(expectedValue);
        return;
      }
    }

    Assertions.fail("ERROR IN DATA PROVIDER: No method for target " + target);
  }

  @SuppressWarnings("unused")
  @UploadMaxFileSize("22745")
  class UploadMaxFileSize1 {
    @Mapping("tmp")
    public void forTest() {
    }
  }

  @Test
  public void getUploadInfo_UploadMaxFileSize1() throws Exception {
    UploadMaxFileSize1 c = new UploadMaxFileSize1();

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(c, null);
    //
    //

    assertThat(handlerGetterList).hasSize(1);

    TestTunnel tunnel = new TestTunnel();
    tunnel.target = "tmp";

    final TunnelExecutor tunnelExecutor = handlerGetterList.get(0).getTunnelExecutor(tunnel);

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
    @Mapping("tmp")
    @UploadMaxFileSize("7711")
    public void forTest() {
    }
  }

  @Test
  public void getUploadInfo_UploadMaxFileSize2() throws Exception {
    UploadMaxFileSize2 c = new UploadMaxFileSize2();

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(c, null);
    //
    //

    assertThat(handlerGetterList).hasSize(1);

    TestTunnel tunnel = new TestTunnel();
    tunnel.target = "tmp";

    final TunnelExecutor tunnelExecutor = handlerGetterList.get(0).getTunnelExecutor(tunnel);

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
    @Mapping("tmp")
    public void forTest() {
    }
  }

  @Test
  public void getUploadInfo_UploadMaxRequestSize1() throws Exception {
    UploadMaxRequestSize1 c = new UploadMaxRequestSize1();

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(c, null);
    //
    //

    assertThat(handlerGetterList).hasSize(1);

    TestTunnel tunnel = new TestTunnel();
    tunnel.target = "tmp";

    final TunnelExecutor tunnelExecutor = handlerGetterList.get(0).getTunnelExecutor(tunnel);

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
    @Mapping("tmp")
    @UploadMaxRequestSize("7711")
    public void forTest() {
    }
  }

  @Test
  public void getUploadInfo_UploadMaxRequestSize2() throws Exception {
    UploadMaxRequestSize2 c = new UploadMaxRequestSize2();

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(c, null);
    //
    //

    assertThat(handlerGetterList).hasSize(1);

    TestTunnel tunnel = new TestTunnel();
    tunnel.target = "tmp";

    final TunnelExecutor tunnelExecutor = handlerGetterList.get(0).getTunnelExecutor(tunnel);

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
    @Mapping("tmp")
    public void forTest() {
    }
  }

  @Test
  public void getUploadInfo_UploadFileSizeThreshold1() throws Exception {
    UploadFileSizeThreshold1 c = new UploadFileSizeThreshold1();

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(c, null);
    //
    //

    assertThat(handlerGetterList).hasSize(1);

    TestTunnel tunnel = new TestTunnel();
    tunnel.target = "tmp";

    final TunnelExecutor tunnelExecutor = handlerGetterList.get(0).getTunnelExecutor(tunnel);

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
    @Mapping("tmp")
    @UploadFileSizeThreshold("7711")
    public void forTest() {
    }
  }

  @Test
  public void getUploadInfo_UploadFileSizeThreshold2() throws Exception {
    UploadFileSizeThreshold2 c = new UploadFileSizeThreshold2();

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(c, null);
    //
    //

    assertThat(handlerGetterList).hasSize(1);

    TestTunnel tunnel = new TestTunnel();
    tunnel.target = "tmp";

    final TunnelExecutor tunnelExecutor = handlerGetterList.get(0).getTunnelExecutor(tunnel);

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
    @Mapping("tmp")
    public void forTest() {
    }

    public String testLocation;

    public String getTestLocation() {
      return testLocation;
    }
  }

  @Test
  public void getUploadInfo_UploadLocationFromMethod1() throws Exception {
    UploadLocationFromMethod1 c = new UploadLocationFromMethod1();
    c.testLocation = RND.str(10);

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(c, null);
    //
    //

    assertThat(handlerGetterList).hasSize(1);

    TestTunnel tunnel = new TestTunnel();
    tunnel.target = "tmp";

    final TunnelExecutor tunnelExecutor = handlerGetterList.get(0).getTunnelExecutor(tunnel);

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
    @Mapping("tmp")
    @UploadLocationFromMethod("getTestLocation")
    public void forTest() {
    }

    public String testLocation;

    public String getTestLocation() {
      return testLocation;
    }
  }

  @Test
  public void getUploadInfo_UploadLocationFromMethod2() throws Exception {
    UploadLocationFromMethod2 c = new UploadLocationFromMethod2();
    c.testLocation = RND.str(10);

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(c, null);
    //
    //

    assertThat(handlerGetterList).hasSize(1);

    TestTunnel tunnel = new TestTunnel();
    tunnel.target = "tmp";

    final TunnelExecutor tunnelExecutor = handlerGetterList.get(0).getTunnelExecutor(tunnel);

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
    @Mapping("tmp")
    public void forTest() {
    }
  }

  @Test(expectedExceptions = InconsistentUploadAnnotationsUnderClass.class)
  public void getUploadInfo_InconsistentUploadAnnotationsUnderClass() throws Exception {
    TestInconsistentUploadAnnotationsUnderClass c = new TestInconsistentUploadAnnotationsUnderClass();
    //
    //
    ControllerTunnelExecutorBuilder.build(c, null);
    //
    //
  }

  @SuppressWarnings("unused")
  class TestInconsistentUploadAnnotationsUnderMethod {
    @Mapping("tmp")
    @UploadInfoFromMethod("tmp1")
    @UploadLocationFromMethod("tmp2")
    public void forTest() {
    }
  }

  @Test(expectedExceptions = InconsistentUploadAnnotationsUnderMethod.class)
  public void getUploadInfo_InconsistentUploadAnnotationsUnderMethod() throws Exception {
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
    @Mapping("tmp")
    @UploadMaxFileSizeFromMethod("getTestMaxFileSize")
    public void forTest() {
    }

    public long testMaxFileSize;

    public long getTestMaxFileSize() {
      return testMaxFileSize;
    }
  }

  @Test
  public void getUploadInfo_UploadMaxFileSizeFromMethod_long() throws Exception {
    UploadMaxFileSizeFromMethod_long c = new UploadMaxFileSizeFromMethod_long();
    c.testMaxFileSize = RND.plusLong(1_000_000_000);

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(c, null);
    //
    //

    assertThat(handlerGetterList).hasSize(1);

    TestTunnel tunnel = new TestTunnel();
    tunnel.target = "tmp";

    final TunnelExecutor tunnelExecutor = handlerGetterList.get(0).getTunnelExecutor(tunnel);

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
    @Mapping("tmp")
    @UploadMaxFileSizeFromMethod("getTestMaxFileSize")
    public void forTest() {
    }

    public int testMaxFileSize;

    public int getTestMaxFileSize() {
      return testMaxFileSize;
    }
  }

  @Test
  public void getUploadInfo_UploadMaxFileSizeFromMethod_int() throws Exception {
    UploadMaxFileSizeFromMethod_int c = new UploadMaxFileSizeFromMethod_int();
    c.testMaxFileSize = RND.plusInt(1_000_000_000);

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(c, null);
    //
    //

    assertThat(handlerGetterList).hasSize(1);

    TestTunnel tunnel = new TestTunnel();
    tunnel.target = "tmp";

    final TunnelExecutor tunnelExecutor = handlerGetterList.get(0).getTunnelExecutor(tunnel);

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
    @Mapping("tmp")
    @UploadMaxFileSizeFromMethod("getTestMaxFileSize")
    public void forTest() {
    }

    public String testMaxFileSize;

    public String getTestMaxFileSize() {
      return testMaxFileSize;
    }
  }

  @Test
  public void getUploadInfo_UploadMaxFileSizeFromMethod_String() throws Exception {
    UploadMaxFileSizeFromMethod_String c = new UploadMaxFileSizeFromMethod_String();
    c.testMaxFileSize = "left";

    //
    //
    final List<TunnelExecutorGetter> handlerGetterList = ControllerTunnelExecutorBuilder.build(c, null);
    //
    //

    assertThat(handlerGetterList).hasSize(1);

    TestTunnel tunnel = new TestTunnel();
    tunnel.target = "tmp";

    final TunnelExecutor tunnelExecutor = handlerGetterList.get(0).getTunnelExecutor(tunnel);

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
    @Mapping("tmp")
    @UploadMaxFileSize("1M")
    @UploadMaxFileSizeFromMethod("asd")
    public void forTest() {
    }
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

}
