package kz.greetgo.depinject.mvc;

import kz.greetgo.depinject.mvc.utils.RND;
import kz.greetgo.depinject.mvc.utils.TestMappingResult;
import kz.greetgo.depinject.mvc.utils.TestTunnel;
import kz.greetgo.depinject.mvc.utils.TestUpload;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.*;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static java.lang.System.identityHashCode;
import static kz.greetgo.depinject.mvc.utils.TestUtil.getMethod;
import static org.fest.assertions.api.Assertions.assertThat;

public class MethodParameterMetaTest {

  class ForStrRequestParam {
    @SuppressWarnings({"unused", "EmptyMethod"})
    public void forTest(@Par("strParam") String strParam) {
    }
  }

  @Test
  public void strRequestParam() throws Exception {
    final Method method = getMethod(ForStrRequestParam.class, "forTest");

    final MethodParamExtractor e = MethodParameterMeta.create(method).get(0);

    final TestMappingResult catchResult = new TestMappingResult();

    TestTunnel tunnel = new TestTunnel();

    String paramValue = RND.str(10);

    tunnel.setParam("strParam", paramValue, "left value");

    final Object actualParamValue = e.extract(catchResult, tunnel, null);

    assertThat(actualParamValue).isEqualTo(paramValue);
  }

  @SuppressWarnings("unused")
  class ForLongRequestParam {
    @SuppressWarnings({"unused", "EmptyMethod"})
    public void forTest(@Par("param1") long param1, @Par("param2") Long param2) {
    }
  }

  @Test
  public void longRequestParam() throws Exception {
    final Method method = getMethod(ForLongRequestParam.class, "forTest");

    final List<MethodParamExtractor> ee = MethodParameterMeta.create(method);
    MethodParamExtractor e1 = ee.get(0);
    MethodParamExtractor e2 = ee.get(1);

    TestTunnel tunnel = new TestTunnel();

    String param1 = "" + RND.plusLong(1000000000);
    String param2 = "" + RND.plusLong(1000000000);

    tunnel.setParam("param1", param1, "left value 1");
    tunnel.setParam("param2", param2, "left value 2");

    assertThat(e1.extract(null, tunnel, null)).isEqualTo(Long.valueOf(param1));
    assertThat(e2.extract(null, tunnel, null)).isEqualTo(Long.valueOf(param2));

    tunnel.clearParam("param1");
    tunnel.clearParam("param2");

    assertThat(e1.extract(null, tunnel, null)).isEqualTo(0L);
    assertThat(e2.extract(null, tunnel, null)).isNull();

  }

  @SuppressWarnings("unused")
  class ForIntRequestParam {
    @SuppressWarnings({"unused", "EmptyMethod"})
    public void forTest(@Par("param1") int param1, @Par("param2") Integer param2) {
    }
  }

  @Test
  public void intRequestParam() throws Exception {
    final Method method = getMethod(ForIntRequestParam.class, "forTest");

    final List<MethodParamExtractor> ee = MethodParameterMeta.create(method);
    MethodParamExtractor e1 = ee.get(0);
    MethodParamExtractor e2 = ee.get(1);

    TestTunnel tunnel = new TestTunnel();

    String param1 = "" + RND.plusInt(1000000000);
    String param2 = "" + RND.plusInt(1000000000);

    tunnel.setParam("param1", param1, "left value 1");
    tunnel.setParam("param2", param2, "left value 2");

    assertThat(e1.extract(null, tunnel, null)).isEqualTo(Integer.valueOf(param1));
    assertThat(e2.extract(null, tunnel, null)).isEqualTo(Integer.valueOf(param2));

    tunnel.clearParam("param1");
    tunnel.clearParam("param2");

    assertThat(e1.extract(null, tunnel, null)).isEqualTo(0);
    assertThat(e2.extract(null, tunnel, null)).isNull();

  }

  @SuppressWarnings("unused")
  @DataProvider
  public Object[][] simpleDateFormats() {
    return new Object[][]{

      new Object[]{"yyyy-MM-dd HH:mm:ss"},
      new Object[]{"dd.MM.yyyy HH:mm:ss"},
      new Object[]{"dd/MM/yyyy HH:mm:ss"},
      new Object[]{"yyyy-MM-dd HH:mm"},
      new Object[]{"dd.MM.yyyy HH:mm"},
      new Object[]{"dd/MM/yyyy HH:mm"},
      new Object[]{"yyyy-MM-dd"},
      new Object[]{"dd.MM.yyyy"},
      new Object[]{"dd/MM/yyyy"},

    };
  }

  @SuppressWarnings("unused")
  class ForDateRequestParam {
    @SuppressWarnings({"unused", "EmptyMethod"})
    public void forTest(@Par("param") Date param) {
    }
  }

  @Test(dataProvider = "simpleDateFormats")
  public void dateRequestParam(String sdfFormat) throws Exception {
    final Method method = getMethod(ForDateRequestParam.class, "forTest");

    final List<MethodParamExtractor> ee = MethodParameterMeta.create(method);
    MethodParamExtractor e1 = ee.get(0);

    TestTunnel tunnel = new TestTunnel();

    final Date date = RND.dateYears(-100, 0);
    final SimpleDateFormat sdf = new SimpleDateFormat(sdfFormat);
    final String dateStr = sdf.format(date);

    tunnel.setParam("param", dateStr, "left value");

    final Object actualDate = e1.extract(null, tunnel, null);
    assertThat(actualDate).isInstanceOf(Date.class);
    assertThat(sdf.format(actualDate)).isEqualTo(dateStr);

    tunnel.clearParam("param");

    assertThat(e1.extract(null, tunnel, null)).isNull();

  }

  @SuppressWarnings("unused")
  class ForStrListRequestParam {
    @SuppressWarnings({"unused", "EmptyMethod"})
    public void forTest(@Par("param") List<String> param) {
    }
  }

  @Test
  public void listRequestParam() throws Exception {
    final Method method = getMethod(ForStrListRequestParam.class, "forTest");

    final List<MethodParamExtractor> ee = MethodParameterMeta.create(method);
    MethodParamExtractor e = ee.get(0);

    TestTunnel tunnel = new TestTunnel();

    String param1 = "" + RND.str(10);
    String param2 = "" + RND.str(10);

    tunnel.setParam("param", param1, param2);

    {
      final Object actual = e.extract(null, tunnel, null);
      assertThat(actual).isInstanceOf(List.class);
      //noinspection unchecked
      assertThat((List<String>) actual).containsExactly(param1, param2);
    }

    tunnel.clearParam("param");

    {
      final Object actual = e.extract(null, tunnel, null);
      assertThat(actual).isInstanceOf(List.class);
      assertThat((List) actual).isEmpty();
    }
  }

  @SuppressWarnings("unused")
  class ForStrSetRequestParam {
    @SuppressWarnings({"unused", "EmptyMethod"})
    public void forTest(@Par("param") Set<String> param) {
    }
  }

  @Test
  public void setRequestParam() throws Exception {
    final Method method = getMethod(ForStrSetRequestParam.class, "forTest");

    final List<MethodParamExtractor> ee = MethodParameterMeta.create(method);
    MethodParamExtractor e = ee.get(0);

    TestTunnel tunnel = new TestTunnel();

    String param1 = "" + RND.str(10);
    String param2 = "" + RND.str(10);

    tunnel.setParam("param", param1, param2);

    {
      final Object actual = e.extract(null, tunnel, null);
      assertThat(actual).isInstanceOf(Set.class);
      //noinspection unchecked
      assertThat((Set<String>) actual).containsOnly(param1, param2);
    }

    tunnel.clearParam("param");

    {
      final Object actual = e.extract(null, tunnel, null);
      assertThat(actual).isInstanceOf(Set.class);
      assertThat((Set) actual).isEmpty();
    }
  }

  @SuppressWarnings("unused")
  class ForStrPathParam {
    @SuppressWarnings({"unused", "EmptyMethod"})
    public void forTest(@PathPar("param") String param) {
    }
  }

  @Test
  public void strPathParam() throws Exception {
    final Method method = getMethod(ForStrPathParam.class, "forTest");

    final MethodParamExtractor e = MethodParameterMeta.create(method).get(0);

    final TestMappingResult catchResult = new TestMappingResult();

    String paramValue = RND.str(10);

    catchResult.params.put("param", paramValue);

    final Object actualParamValue = e.extract(catchResult, null, null);

    assertThat(actualParamValue).isEqualTo(paramValue);
  }

  @SuppressWarnings("unused")
  class ForRequestInput_String {
    @SuppressWarnings({"unused", "EmptyMethod"})
    public void forTest(@RequestInput String content) {
    }
  }

  @Test
  public void requestInput_String() throws Exception {
    final Method method = getMethod(ForRequestInput_String.class, "forTest");

    final MethodParamExtractor e = MethodParameterMeta.create(method).get(0);

    TestTunnel tunnel = new TestTunnel();
    tunnel.forGetRequestReader = RND.str(10);

    final Object actualParamValue = e.extract(null, tunnel, null);

    assertThat(actualParamValue).isInstanceOf(String.class);
    assertThat(actualParamValue).isEqualTo(tunnel.forGetRequestReader);
  }

  @SuppressWarnings("unused")
  class ForRequestInput_StringList {
    @SuppressWarnings({"unused", "EmptyMethod"})
    public void forTest(@RequestInput List<String> contentLines) {
    }
  }

  @Test
  public void requestInput_StringList() throws Exception {
    final Method method = getMethod(ForRequestInput_StringList.class, "forTest");

    final MethodParamExtractor e = MethodParameterMeta.create(method).get(0);

    String line1 = RND.str(10);
    String line2 = RND.str(10);
    String line3 = RND.str(10);

    TestTunnel tunnel = new TestTunnel();
    tunnel.forGetRequestReader = line1 + '\n' + line2 + '\n' + line3;

    final Object actualParamValue = e.extract(null, tunnel, null);

    assertThat(actualParamValue).isInstanceOf(List.class);
    //noinspection unchecked
    List<String> actual = (List<String>) actualParamValue;
    assertThat(actual).containsExactly(line1, line2, line3);
  }

  @SuppressWarnings("unused")
  class ForRequestInput_byteArray {
    @SuppressWarnings({"unused", "EmptyMethod"})
    public void forTest(@RequestInput byte[] content) {
    }
  }

  @Test
  public void requestInput_byteArray() throws Exception {
    final Method method = getMethod(ForRequestInput_byteArray.class, "forTest");

    final MethodParamExtractor e = MethodParameterMeta.create(method).get(0);

    TestTunnel tunnel = new TestTunnel();
    tunnel.forGetRequestInputStream = RND.byteArray(100);

    final Object actualParamValue = e.extract(null, tunnel, null);

    assertThat(actualParamValue).isInstanceOf(byte[].class);
    assertThat(actualParamValue).isEqualTo(tunnel.forGetRequestInputStream);

  }

  @SuppressWarnings("unused")
  class ForRequestInput_InputStream {
    @SuppressWarnings({"unused", "EmptyMethod"})
    public void forTest(@RequestInput InputStream requestContentInputStream) {
    }
  }

  @Test
  public void requestInput_InputStream() throws Exception {
    final Method method = getMethod(ForRequestInput_InputStream.class, "forTest");

    final MethodParamExtractor e = MethodParameterMeta.create(method).get(0);

    TestTunnel tunnel = new TestTunnel();
    tunnel.forGetRequestInputStream = RND.byteArray(100);

    final Object actualParamValue = e.extract(null, tunnel, null);

    assertThat(actualParamValue).isInstanceOf(InputStream.class);

    ByteArrayOutputStream actual = new ByteArrayOutputStream();

    {
      byte[] buffer = new byte[1024 * 4];
      InputStream in = (InputStream) actualParamValue;
      while (true) {
        final int count = in.read(buffer);
        if (count < 0) break;
        actual.write(buffer, 0, count);
      }
    }

    assertThat(actual.toByteArray()).isEqualTo(tunnel.forGetRequestInputStream);

  }

  @SuppressWarnings("unused")
  class ForRequestInput_BufferedReader {
    @SuppressWarnings({"EmptyMethod", "unused"})
    public void forTest1(@RequestInput BufferedReader requestContentReader) {
    }

    @SuppressWarnings({"EmptyMethod", "unused"})
    public void forTest2(@RequestInput Reader requestContentReader) {
    }
  }

  @SuppressWarnings("unused")
  @DataProvider
  public Object[][] methodsIn_ForRequestInput_BufferedReader() {
    return new Object[][]{
      new Object[]{"forTest1"}, new Object[]{"forTest2"},
    };
  }

  @Test(dataProvider = "methodsIn_ForRequestInput_BufferedReader")
  public void requestInput_BufferedReader(String methodName) throws Exception {
    final Method method1 = getMethod(ForRequestInput_BufferedReader.class, methodName);
    final MethodParamExtractor e1 = MethodParameterMeta.create(method1).get(0);

    TestTunnel tunnel = new TestTunnel();
    tunnel.forGetRequestReader = RND.str(100);

    final Object actualParamValue = e1.extract(null, tunnel, null);

    assertThat(actualParamValue).isInstanceOf(Reader.class);

    CharArrayWriter actual = new CharArrayWriter();

    {
      char[] buffer = new char[1024];
      Reader in = (Reader) actualParamValue;
      while (true) {
        final int count = in.read(buffer);
        if (count < 0) break;
        actual.write(buffer, 0, count);
      }
    }

    assertThat(actual.toString()).isEqualTo(tunnel.forGetRequestReader);

  }

  @SuppressWarnings("unused")
  class ForRequestInput_RequestTunnel {
    @SuppressWarnings({"unused", "EmptyMethod"})
    public void forTest(@RequestInput RequestTunnel requestTunnel) {
    }
  }

  @Test
  public void requestInput_RequestTunnel() throws Exception {
    final Method method = getMethod(ForRequestInput_RequestTunnel.class, "forTest");

    final MethodParamExtractor e = MethodParameterMeta.create(method).get(0);

    TestTunnel tunnel = new TestTunnel();

    final Object actualParamValue = e.extract(null, tunnel, null);

    assertThat(actualParamValue).isInstanceOf(RequestTunnel.class);

    assertThat(identityHashCode(actualParamValue)).isEqualTo(identityHashCode(tunnel));
  }

  @SuppressWarnings("unused")
  class ForMvcModel {
    @SuppressWarnings({"unused", "EmptyMethod"})
    public void forTest(MvcModel model) {
    }
  }

  @Test
  public void mvcModel() throws Exception {
    final Method method = getMethod(ForMvcModel.class, "forTest");

    final List<MethodParamExtractor> ee = MethodParameterMeta.create(method);
    final MethodParamExtractor e = ee.get(0);

    MvcModel model = new MvcModel();

    final Object actualParamValue = e.extract(null, null, model);

    assertThat(identityHashCode(actualParamValue)).isEqualTo(identityHashCode(model));

  }

  class ForUpload {
    @SuppressWarnings({"unused", "EmptyMethod"})
    public void forTest(@Par("abra") Upload upload) {
    }
  }

  @Test
  public void upload() throws Exception {
    final Method method = getMethod(ForUpload.class, "forTest");

    final List<MethodParamExtractor> ee = MethodParameterMeta.create(method);
    final MethodParamExtractor e = ee.get(0);

    TestUpload abra = new TestUpload("abra");

    TestTunnel tunnel = new TestTunnel();
    tunnel.appendTestUpload(abra);
    tunnel.appendTestUpload(new TestUpload("left"));

    final Object actualParamValue = e.extract(null, tunnel, null);

    assertThat(identityHashCode(actualParamValue)).isEqualTo(identityHashCode(abra));

  }
}
