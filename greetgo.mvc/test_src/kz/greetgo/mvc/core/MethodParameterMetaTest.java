package kz.greetgo.mvc.core;

import kz.greetgo.mvc.annotations.Json;
import kz.greetgo.mvc.annotations.Par;
import kz.greetgo.mvc.annotations.ParCookie;
import kz.greetgo.mvc.annotations.ParPath;
import kz.greetgo.mvc.annotations.ParSession;
import kz.greetgo.mvc.annotations.ParamsTo;
import kz.greetgo.mvc.annotations.RequestInput;
import kz.greetgo.mvc.annotations.SkipParameter;
import kz.greetgo.mvc.errors.AsIsOnlyForString;
import kz.greetgo.mvc.errors.DoNotSetContentTypeAfterOut;
import kz.greetgo.mvc.errors.DoNotSetFilenameAfterOut;
import kz.greetgo.mvc.interfaces.BinResponse;
import kz.greetgo.mvc.interfaces.MethodParamExtractor;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.mvc.interfaces.TunnelCookies;
import kz.greetgo.mvc.interfaces.Upload;
import kz.greetgo.mvc.model.MvcModel;
import kz.greetgo.mvc.model.MvcModelData;
import kz.greetgo.mvc.util.CookieUtil;
import kz.greetgo.mvc.utils.TestMappingResult;
import kz.greetgo.mvc.utils.TestTunnel;
import kz.greetgo.mvc.utils.TestUpload;
import kz.greetgo.mvc.utils.TestUtil;
import kz.greetgo.util.RND;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.MapEntry;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.servlet.http.Cookie;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static java.lang.System.identityHashCode;
import static org.assertj.core.api.Assertions.assertThat;

public class MethodParameterMetaTest {

  class ForStrRequestParam {
    @SuppressWarnings({"unused", "EmptyMethod"})
    public void forTest(@Par("strParam") String strParam) {}
  }

  @Test
  public void strRequestParam() throws Exception {
    final Method method = TestUtil.getMethod(ForStrRequestParam.class, "forTest");

    final MethodParamExtractor e = MethodParameterMeta.create(method, null).get(0);

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
    public void forTest(@Par("param1") long param1, @Par("param2") Long param2) {}
  }

  @Test
  public void longRequestParam() throws Exception {
    final Method method = TestUtil.getMethod(ForLongRequestParam.class, "forTest");

    final List<MethodParamExtractor> ee = MethodParameterMeta.create(method, null);
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
    public void forTest(@Par("param1") int param1, @Par("param2") Integer param2) {}
  }

  @Test
  public void intRequestParam() throws Exception {
    final Method method = TestUtil.getMethod(ForIntRequestParam.class, "forTest");

    final List<MethodParamExtractor> ee = MethodParameterMeta.create(method, null);
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

      new Object[]{"yyyy-MM-dd HH:mm:ss"}, new Object[]{"dd.MM.yyyy HH:mm:ss"},
      new Object[]{"dd/MM/yyyy HH:mm:ss"}, new Object[]{"yyyy-MM-dd HH:mm"},
      new Object[]{"dd.MM.yyyy HH:mm"}, new Object[]{"dd/MM/yyyy HH:mm"},
      new Object[]{"yyyy-MM-dd"}, new Object[]{"dd.MM.yyyy"},
      new Object[]{"dd/MM/yyyy"},

    };
  }

  @SuppressWarnings("unused")
  class ForDateRequestParam {
    @SuppressWarnings({"unused", "EmptyMethod"})
    public void forTest(@Par("param") Date param) {}
  }

  @Test(dataProvider = "simpleDateFormats")
  public void dateRequestParam(String sdfFormat) throws Exception {
    final Method method = TestUtil.getMethod(ForDateRequestParam.class, "forTest");

    final List<MethodParamExtractor> ee = MethodParameterMeta.create(method, null);
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
    public void forTest(@Par("param") List<String> param) {}
  }

  @Test
  public void listRequestParam() throws Exception {
    final Method method = TestUtil.getMethod(ForStrListRequestParam.class, "forTest");

    final List<MethodParamExtractor> ee = MethodParameterMeta.create(method, null);
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
    public void forTest(@Par("param") Set<String> param) {}
  }

  @Test
  public void setRequestParam() throws Exception {
    final Method method = TestUtil.getMethod(ForStrSetRequestParam.class, "forTest");

    final List<MethodParamExtractor> ee = MethodParameterMeta.create(method, null);
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

  public static class Client {
    public String id, name;
  }

  class ForJsonStrRequestParam_ClientList {
    @SuppressWarnings({"unused", "EmptyMethod"})
    public void forTest(@Par("param") @Json List<Client> param) {}
  }

  @Test
  public void forJsonStrRequestParam_ClientList_goList() throws Exception {
    final Method method = TestUtil.getMethod(ForJsonStrRequestParam_ClientList.class, "forTest");

    final MethodParamExtractor e = MethodParameterMeta.create(method, null).get(0);

    final TestMappingResult catchResult = new TestMappingResult();

    TestTunnel tunnel = new TestTunnel();

    tunnel.setParam("param",
      "[{\"id\":\"id1\",\"name\":\"name1\"},{\"id\":\"id2\",\"name\":\"name2\"}]",
      "{\"id\":\"id3\",\"name\":\"name3\"}");

    final Object actualParamValue = e.extract(catchResult, tunnel, null);

    assertThat(actualParamValue).isInstanceOf(List.class);

    @SuppressWarnings("unchecked")
    List<Client> actual = (List<Client>) actualParamValue;

    assertThat(actual).hasSize(3);
    assertThat(actual.get(0).id).isEqualTo("id1");
    assertThat(actual.get(0).name).isEqualTo("name1");
    assertThat(actual.get(1).id).isEqualTo("id2");
    assertThat(actual.get(1).name).isEqualTo("name2");
    assertThat(actual.get(2).id).isEqualTo("id3");
    assertThat(actual.get(2).name).isEqualTo("name3");
  }

  @Test
  public void forJsonStrRequestParam_ClientList_goOne() throws Exception {
    final Method method = TestUtil.getMethod(ForJsonStrRequestParam_ClientList.class, "forTest");

    final MethodParamExtractor e = MethodParameterMeta.create(method, null).get(0);

    final TestMappingResult catchResult = new TestMappingResult();

    TestTunnel tunnel = new TestTunnel();

    String paramValue = "{\"id\":\"id1\",\"name\":\"name1\"}";

    tunnel.setParam("param", paramValue);

    final Object actualParamValue = e.extract(catchResult, tunnel, null);

    assertThat(actualParamValue).isInstanceOf(List.class);

    @SuppressWarnings("unchecked")
    List<Client> actual = (List<Client>) actualParamValue;

    assertThat(actual).hasSize(1);
    assertThat(actual.get(0).id).isEqualTo("id1");
    assertThat(actual.get(0).name).isEqualTo("name1");
  }

  class ForJsonStrRequestParam_Client {
    @SuppressWarnings({"unused", "EmptyMethod"})
    public void forTest(@Par("param") @Json Client param) {}
  }

  @DataProvider
  public Object[][] forJsonStrRequestParam_Client_data() {
    return new Object[][]{
      new Object[]{"[{\"id\":\"id1\",\"name\":\"name1\"},{\"id\":\"id2\",\"name\":\"name2\"}]"},
      new Object[]{"{\"id\":\"id1\",\"name\":\"name1\"}]"},};
  }

  @Test(dataProvider = "forJsonStrRequestParam_Client_data")
  public void forJsonStrRequestParam_Client(String paramValue) throws Exception {
    final Method method = TestUtil.getMethod(ForJsonStrRequestParam_Client.class, "forTest");

    final MethodParamExtractor e = MethodParameterMeta.create(method, null).get(0);

    final TestMappingResult catchResult = new TestMappingResult();

    TestTunnel tunnel = new TestTunnel();

    tunnel.setParam("param", paramValue, "left value");

    final Object actualParamValue = e.extract(catchResult, tunnel, null);

    assertThat(actualParamValue).isInstanceOf(Client.class);

    Client actual = (Client) actualParamValue;

    assertThat(actual.id).isEqualTo("id1");
    assertThat(actual.name).isEqualTo("name1");
  }

  @SuppressWarnings("unused")
  class ForParPathStr {
    @SuppressWarnings({"unused", "EmptyMethod"})
    public void forTest(@ParPath("param") String param) {}
  }

  @Test
  public void parPathStr() throws Exception {
    final Method method = TestUtil.getMethod(ForParPathStr.class, "forTest");

    final MethodParamExtractor e = MethodParameterMeta.create(method, null).get(0);

    final TestMappingResult catchResult = new TestMappingResult();

    String paramValue = RND.str(10);

    catchResult.params.put("param", paramValue);

    final Object actualParamValue = e.extract(catchResult, null, null);

    assertThat(actualParamValue).isEqualTo(paramValue);
  }

  @SuppressWarnings("unused")
  class ForParPathStringsJson {
    @SuppressWarnings({"unused", "EmptyMethod"})
    public void forTest(@ParPath("param") @Json List<String> param) {}
  }

  @Test
  public void parPathStringsJson() throws Exception {
    final Method method = TestUtil.getMethod(ForParPathStringsJson.class, "forTest");

    final MethodParamExtractor e = MethodParameterMeta.create(method, null).get(0);

    final TestMappingResult catchResult = new TestMappingResult();

    String arg1 = RND.str(10);
    String arg2 = RND.str(10);

    catchResult.params.put("param", ("['" + arg1 + "', '" + arg2 + "']").replaceAll("'", "%22"));

    final Object actualParamValue = e.extract(catchResult, null, null);

    assertThat(actualParamValue).isInstanceOf(List.class);
    List list = (List) actualParamValue;
    assertThat(list).hasSize(2);
    assertThat(list.get(0)).isEqualTo(arg1);
    assertThat(list.get(1)).isEqualTo(arg2);
  }

  @SuppressWarnings("unused")
  class ForRequestInput_String {
    @SuppressWarnings({"unused", "EmptyMethod"})
    public void forTest(@RequestInput String content) {}
  }

  @Test
  public void requestInput_String() throws Exception {
    final Method method = TestUtil.getMethod(ForRequestInput_String.class, "forTest");

    final MethodParamExtractor e = MethodParameterMeta.create(method, null).get(0);

    TestTunnel tunnel = new TestTunnel();
    tunnel.forGetRequestReader = RND.str(10);

    final Object actualParamValue = e.extract(null, tunnel, null);

    assertThat(actualParamValue).isInstanceOf(String.class);
    assertThat(actualParamValue).isEqualTo(tunnel.forGetRequestReader);
  }

  @SuppressWarnings("unused")
  class ForRequestInput_Json_Client {
    @SuppressWarnings({"unused", "EmptyMethod"})
    public void forTest(@RequestInput @Json Client content) {}
  }

  @DataProvider
  public Object[][] requestInput_json_client_DataProvider() {
    return new Object[][]{
      new Object[]{"{\"id\":\"id1\",\"name\":\"name1\"}"},
      new Object[]{"[{\"id\":\"id1\",\"name\":\"name1\"},{\"id\":\"id2\",\"name\":\"name2\"}]"},};
  }

  @Test(dataProvider = "requestInput_json_client_DataProvider")
  public void requestInput_json_client(String requestContent) throws Exception {
    final Method method = TestUtil.getMethod(ForRequestInput_Json_Client.class, "forTest");

    final MethodParamExtractor e = MethodParameterMeta.create(method, null).get(0);

    TestTunnel tunnel = new TestTunnel();
    tunnel.forGetRequestReader = requestContent;

    final Object actualParamValue = e.extract(null, tunnel, null);

    assertThat(actualParamValue).isInstanceOf(Client.class);

    Client actual = (Client) actualParamValue;
    assertThat(actual.id).isEqualTo("id1");
    assertThat(actual.name).isEqualTo("name1");
  }

  @Test
  public void requestInput_json_client_noRequestContent() throws Exception {
    final Method method = TestUtil.getMethod(ForRequestInput_Json_Client.class, "forTest");

    final MethodParamExtractor e = MethodParameterMeta.create(method, null).get(0);

    TestTunnel tunnel = new TestTunnel();
    tunnel.forGetRequestReader = "";

    final Object actualParamValue = e.extract(null, tunnel, null);

    assertThat(actualParamValue).isNull();
  }

  @SuppressWarnings("unused")
  class ForRequestInput_Json_ListClient {
    @SuppressWarnings({"unused", "EmptyMethod"})
    public void forTest(@RequestInput @Json List<Client> content) {}
  }

  @Test
  public void requestInput_json_listClient() throws Exception {
    final Method method = TestUtil.getMethod(ForRequestInput_Json_ListClient.class, "forTest");

    final MethodParamExtractor e = MethodParameterMeta.create(method, null).get(0);

    TestTunnel tunnel = new TestTunnel();
    tunnel.forGetRequestReader = "[{\"id\":\"id1\",\"name\":\"name1\"},{\"id\":\"id2\",\"name\":\"name2\"}]";

    final Object actualParamValue = e.extract(null, tunnel, null);

    assertThat(actualParamValue).isInstanceOf(List.class);

    @SuppressWarnings("unchecked")
    List<Client> actual = (List<Client>) actualParamValue;
    assertThat(actual).hasSize(2);
    assertThat(actual.get(0).id).isEqualTo("id1");
    assertThat(actual.get(0).name).isEqualTo("name1");
    assertThat(actual.get(1).id).isEqualTo("id2");
    assertThat(actual.get(1).name).isEqualTo("name2");
  }

  @Test
  public void requestInput_json_listClient_NoRequestContent() throws Exception {
    final Method method = TestUtil.getMethod(ForRequestInput_Json_ListClient.class, "forTest");

    final MethodParamExtractor e = MethodParameterMeta.create(method, null).get(0);

    TestTunnel tunnel = new TestTunnel();
    tunnel.forGetRequestReader = "";

    final Object actualParamValue = e.extract(null, tunnel, null);

    assertThat(actualParamValue).isInstanceOf(List.class);

    @SuppressWarnings("unchecked")
    List<Client> actual = (List<Client>) actualParamValue;
    assertThat(actual).isEmpty();
  }

  @SuppressWarnings("unused")
  class ForRequestInput_StringList {
    @SuppressWarnings({"unused", "EmptyMethod"})
    public void forTest(@RequestInput List<String> contentLines) {}
  }

  @Test
  public void requestInput_StringList() throws Exception {
    final Method method = TestUtil.getMethod(ForRequestInput_StringList.class, "forTest");

    final MethodParamExtractor e = MethodParameterMeta.create(method, null).get(0);

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
    public void forTest(@RequestInput byte[] content) {}
  }

  @Test
  public void requestInput_byteArray() throws Exception {
    final Method method = TestUtil.getMethod(ForRequestInput_byteArray.class, "forTest");

    final MethodParamExtractor e = MethodParameterMeta.create(method, null).get(0);

    TestTunnel tunnel = new TestTunnel();
    tunnel.forGetRequestInputStream = RND.byteArray(100);

    final Object actualParamValue = e.extract(null, tunnel, null);

    assertThat(actualParamValue).isInstanceOf(byte[].class);
    assertThat(actualParamValue).isEqualTo(tunnel.forGetRequestInputStream);

  }

  @SuppressWarnings("unused")
  class ForRequestInput_InputStream {
    @SuppressWarnings({"unused", "EmptyMethod"})
    public void forTest(@RequestInput InputStream requestContentInputStream) {}
  }

  @Test
  public void requestInput_InputStream() throws Exception {
    final Method method = TestUtil.getMethod(ForRequestInput_InputStream.class, "forTest");

    final MethodParamExtractor e = MethodParameterMeta.create(method, null).get(0);

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
    public void forTest1(@RequestInput BufferedReader requestContentReader) {}

    @SuppressWarnings({"EmptyMethod", "unused"})
    public void forTest2(@RequestInput Reader requestContentReader) {}
  }

  @SuppressWarnings("unused")
  @DataProvider
  public Object[][] methodsIn_ForRequestInput_BufferedReader() {
    return new Object[][]{new Object[]{"forTest1"}, new Object[]{"forTest2"},};
  }

  @Test(dataProvider = "methodsIn_ForRequestInput_BufferedReader")
  public void requestInput_BufferedReader(String methodName) throws Exception {
    final Method method1 = TestUtil.getMethod(ForRequestInput_BufferedReader.class, methodName);
    final MethodParamExtractor e1 = MethodParameterMeta.create(method1, null).get(0);

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
    public void forTest(@RequestInput RequestTunnel requestTunnel) {}
  }

  @Test
  public void requestInput_RequestTunnel() throws Exception {
    final Method method = TestUtil.getMethod(ForRequestInput_RequestTunnel.class, "forTest");

    final MethodParamExtractor e = MethodParameterMeta.create(method, null).get(0);

    TestTunnel tunnel = new TestTunnel();

    final Object actualParamValue = e.extract(null, tunnel, null);

    assertThat(actualParamValue).isInstanceOf(RequestTunnel.class);

    assertThat(identityHashCode(actualParamValue)).isEqualTo(identityHashCode(tunnel));
  }

  @SuppressWarnings("unused")
  class NakedRequestTunnel {
    @SuppressWarnings({"unused", "EmptyMethod"})
    public void forTest(RequestTunnel requestTunnel) {}
  }

  @Test
  public void nakedRequestTunnel() throws Exception {
    final Method method = TestUtil.getMethod(NakedRequestTunnel.class, "forTest");

    final MethodParamExtractor e = MethodParameterMeta.create(method, null).get(0);

    TestTunnel tunnel = new TestTunnel();

    final Object actualParamValue = e.extract(null, tunnel, null);

    assertThat(actualParamValue).isInstanceOf(RequestTunnel.class);

    assertThat(identityHashCode(actualParamValue)).isEqualTo(identityHashCode(tunnel));
  }

  @SuppressWarnings("unused")
  class ForMvcModel {
    @SuppressWarnings({"unused", "EmptyMethod"})
    public void forTest(MvcModel model) {}
  }

  @Test
  public void mvcModel() throws Exception {
    final Method method = TestUtil.getMethod(ForMvcModel.class, "forTest");

    final List<MethodParamExtractor> ee = MethodParameterMeta.create(method, null);
    final MethodParamExtractor e = ee.get(0);

    MvcModelData model = new MvcModelData();

    final Object actualParamValue = e.extract(null, null, model);

    assertThat(identityHashCode(actualParamValue)).isEqualTo(identityHashCode(model));

  }

  class ForUpload {
    @SuppressWarnings({"unused", "EmptyMethod"})
    public void forTest(@Par("abra") Upload upload) {}
  }

  @Test
  public void upload() throws Exception {
    final Method method = TestUtil.getMethod(ForUpload.class, "forTest");

    final List<MethodParamExtractor> ee = MethodParameterMeta.create(method, null);
    final MethodParamExtractor e = ee.get(0);

    TestUpload abra = new TestUpload("abra");

    TestTunnel tunnel = new TestTunnel();
    tunnel.appendTestUpload(abra);
    tunnel.appendTestUpload(new TestUpload("left"));

    final Object actualParamValue = e.extract(null, tunnel, null);

    assertThat(identityHashCode(actualParamValue)).isEqualTo(identityHashCode(abra));

  }

  @SuppressWarnings("unused")
  private class ForTunnelCookies {
    @SuppressWarnings({"EmptyMethod", "UnusedParameters"})
    public void forTest(TunnelCookies cookies) {}
  }

  @Test
  public void tunnelCookies() throws Exception {
    final Method method = TestUtil.getMethod(ForTunnelCookies.class, "forTest");

    final List<MethodParamExtractor> ee = MethodParameterMeta.create(method, null);
    final MethodParamExtractor e = ee.get(0);

    TestTunnel tunnel = new TestTunnel();

    final Object actualParamValue = e.extract(null, tunnel, null);

    assertThat(actualParamValue).isInstanceOf(TunnelCookies.class);

    TunnelCookies ex = (TunnelCookies) actualParamValue;

    String cookieName = "A" + RND.strInt(10);
    tunnel.testCookies.requestCookies = new Cookie[]{
      new Cookie(cookieName, RND.str(10))
    };

    assertThat(ex.name(cookieName).value()).isEqualTo(tunnel.testCookies.requestCookies[0].getValue());

    ex.forName("asd").saveValue("asd_value");
    ex.forName("dsa").remove();

    assertThat(tunnel.testCookies.addedCookies).isEmpty();

    tunnel.eventBeforeCompleteHeaders().fire();

    assertThat(tunnel.testCookies.addedCookies).hasSize(2);
    assertThat(tunnel.testCookies.addedCookies.get(0).getName()).isEqualTo("asd");
    assertThat(tunnel.testCookies.addedCookies.get(0).getValue()).isEqualTo("asd_value");
    assertThat(tunnel.testCookies.addedCookies.get(1).getName()).isEqualTo("dsa");
    assertThat(tunnel.testCookies.addedCookies.get(1).getValue()).isNull();
    assertThat(tunnel.testCookies.addedCookies.get(1).getMaxAge()).isZero();

    ex.forName("pom").remove();

    assertThat(tunnel.testCookies.addedCookies).hasSize(3);
    assertThat(tunnel.testCookies.addedCookies.get(2).getName()).isEqualTo("pom");
    assertThat(tunnel.testCookies.addedCookies.get(2).getValue()).isNull();
    assertThat(tunnel.testCookies.addedCookies.get(2).getMaxAge()).isZero();
  }

  private class ForParCookie {
    @SuppressWarnings({"unused", "EmptyMethod", "UnusedParameters"})
    public void forTest(@ParCookie("asd") String asd) {}
  }

  @Test
  public void forParCookie_str() throws Exception {
    final Method method = TestUtil.getMethod(ForParCookie.class, "forTest");

    final List<MethodParamExtractor> ee = MethodParameterMeta.create(method, null);
    MethodParamExtractor e = ee.get(0);

    TestTunnel tunnel = new TestTunnel();

    String value = RND.str(10);

    tunnel.testCookies.requestCookies = new Cookie[]{new Cookie("asd", CookieUtil.objectToStr(value))};

    final Object extractedValue = e.extract(null, tunnel, null);

    assertThat(extractedValue).isInstanceOf(String.class);

    String actual = (String) extractedValue;
    assertThat(actual).isEqualTo(value);
  }

  private class ForParCookie_asIs {
    @SuppressWarnings({"unused", "EmptyMethod", "UnusedParameters"})
    public void forTest(@ParCookie(value = "asd", asIs = true) String asd) {}
  }

  @Test
  public void forParCookie_asIs() throws Exception {
    final Method method = TestUtil.getMethod(ForParCookie_asIs.class, "forTest");

    final List<MethodParamExtractor> ee = MethodParameterMeta.create(method, null);
    MethodParamExtractor e = ee.get(0);

    TestTunnel tunnel = new TestTunnel();

    String value = RND.str(10);

    tunnel.testCookies.requestCookies = new Cookie[]{new Cookie("asd", value)};

    final Object extractedValue = e.extract(null, tunnel, null);

    assertThat(extractedValue).isInstanceOf(String.class);

    String actual = (String) extractedValue;
    assertThat(actual).isEqualTo(value);
  }

  public static class SomeObject implements Serializable {
    public int intField;
    public String strField;
    public Date dateField;

    @Override
    public String toString() {
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
      return "SomeObject{" + "intField=" + intField + ", strField='" + strField + '\''
        + ", dateField=" + (dateField == null ? "null" : sdf.format(dateField)) + '}';
    }
  }

  private class ForParCookie_SomeObject {
    @SuppressWarnings({"unused", "EmptyMethod", "UnusedParameters"})
    public void forTest(@ParCookie("asd") SomeObject asd) {}
  }

  @Test
  public void forParCookie_SomeObject() throws Exception {
    final Method method = TestUtil.getMethod(ForParCookie_SomeObject.class, "forTest");

    final List<MethodParamExtractor> ee = MethodParameterMeta.create(method, null);
    MethodParamExtractor e = ee.get(0);

    SomeObject original = new SomeObject();
    original.intField = RND.plusInt(1_000_000_000);
    original.dateField = RND.dateDays(-10_000, 100);
    original.strField = RND.str(30);

    TestTunnel tunnel = new TestTunnel();

    tunnel.testCookies.requestCookies = new Cookie[]{new Cookie("asd", CookieUtil.objectToStr(original))};

    final Object extractedValue = e.extract(null, tunnel, null);

    assertThat(extractedValue).isInstanceOf(SomeObject.class);

    SomeObject actual = (SomeObject) extractedValue;
    assertThat(actual.toString()).isEqualTo(original.toString());
  }

  private class ForParCookie_AsIsOnlyForString {
    @SuppressWarnings({"unused", "EmptyMethod", "UnusedParameters"})
    public void forTest(@ParCookie(value = "asd", asIs = true) SomeObject asd) {}
  }

  @Test(expectedExceptions = AsIsOnlyForString.class)
  public void forParCookie_AsIsOnlyForString() {
    final Method method = TestUtil.getMethod(ForParCookie_AsIsOnlyForString.class, "forTest");

    MethodParameterMeta.create(method, null);
  }

  private class ForRequestMethod {
    @SuppressWarnings({"unused", "EmptyMethod", "UnusedParameters"})
    public void forTest(RequestMethod requestMethod) {}
  }

  @Test
  public void forRequestMethod() throws Exception {
    final Method method = TestUtil.getMethod(ForRequestMethod.class, "forTest");

    final List<MethodParamExtractor> ee = MethodParameterMeta.create(method, null);
    MethodParamExtractor e = ee.get(0);

    TestTunnel tunnel = new TestTunnel(RND.someEnum(RequestMethod.values()));

    final Object extractedValue = e.extract(null, tunnel, null);

    assertThat(extractedValue).isEqualTo(tunnel.getRequestMethod());
  }

  private class ForBinResponse {
    @SuppressWarnings({"unused", "EmptyMethod", "UnusedParameters"})
    public void forTest(BinResponse binResponse) {}
  }

  @Test
  public void forBinResponse() throws Exception {
    final Method method = TestUtil.getMethod(ForBinResponse.class, "forTest");

    final List<MethodParamExtractor> ee = MethodParameterMeta.create(method, null);
    MethodParamExtractor e = ee.get(0);

    TestTunnel tunnel = new TestTunnel();

    final Object extractedValue = e.extract(null, tunnel, null);

    assertThat(extractedValue).isInstanceOf(BinResponse.class);

    BinResponse binResponse = (BinResponse) extractedValue;

    binResponse.setContentType("hello content type");
    assertThat(tunnel.responseContentType).isEqualTo("hello content type");

    binResponse.setFilename("hello.txt");

    assertThat(tunnel.responseHeaders).contains(
      MapEntry.entry("Content-Disposition", "attachment; filename=\"hello.txt\"")
    );

    binResponse.out().write("test out string".getBytes(StandardCharsets.UTF_8));
    assertThat(tunnel.responseBinText()).isEqualTo("test out string");

    tunnel.flushBuffersCalled = false;
    binResponse.flushBuffers();
    assertThat(tunnel.flushBuffersCalled).isTrue();
  }

  @Test(expectedExceptions = DoNotSetContentTypeAfterOut.class)
  public void forBinResponse_DoNotSetContentTypeAfterOut() throws Exception {
    final Method method = TestUtil.getMethod(ForBinResponse.class, "forTest");

    final List<MethodParamExtractor> ee = MethodParameterMeta.create(method, null);
    MethodParamExtractor e = ee.get(0);

    TestTunnel tunnel = new TestTunnel();

    final Object extractedValue = e.extract(null, tunnel, null);

    assertThat(extractedValue).isInstanceOf(BinResponse.class);

    BinResponse binResponse = (BinResponse) extractedValue;

    binResponse.out();

    binResponse.setContentType("asd");
  }

  @Test(expectedExceptions = DoNotSetFilenameAfterOut.class)
  public void forBinResponse_DoNotSetFilenameTypeAfterOut() throws Exception {
    final Method method = TestUtil.getMethod(ForBinResponse.class, "forTest");

    final List<MethodParamExtractor> ee = MethodParameterMeta.create(method, null);
    MethodParamExtractor e = ee.get(0);

    TestTunnel tunnel = new TestTunnel();

    final Object extractedValue = e.extract(null, tunnel, null);

    assertThat(extractedValue).isInstanceOf(BinResponse.class);

    BinResponse binResponse = (BinResponse) extractedValue;

    binResponse.out();

    binResponse.setFilename("asd");
  }

  public static class TestParamsDatesAcceptor {
    public Date date1, date2, date3 = null;

    @SkipParameter
    public Date date4 = null, date6 = null;

    private boolean date2SetterCalled = false;

    @SuppressWarnings("unused")
    public void setDate2(Date date2) {
      this.date2 = date2;
      date2SetterCalled = true;
    }

    @SuppressWarnings("unused")
    @SkipParameter
    public void setDate5(Date ignore) {
      Assertions.fail("date5 must not be set because @SkipParameter");
    }

    public void asserts() {
      assertThat(date2SetterCalled).describedAs("date2 must be set with setter").isTrue();
    }

    @SuppressWarnings("unused")
    public void setDate6(Date date6) {
      this.date6 = date6;
    }
  }

  private class ForParamsToWithDates {
    @SuppressWarnings({"unused", "EmptyMethod", "UnusedParameters"})
    public void forTest(@ParamsTo TestParamsDatesAcceptor argument) {}
  }

  @Test
  public void forParamsToWithDates() throws Exception {
    final Method method = TestUtil.getMethod(ForParamsToWithDates.class, "forTest");

    final List<MethodParamExtractor> ee = MethodParameterMeta.create(method, null);
    MethodParamExtractor e1 = ee.get(0);

    TestTunnel tunnel = new TestTunnel();

    final Date date1 = RND.dateYears(-100, 0);
    final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    final String dateStr1 = sdf1.format(date1);
    tunnel.setParam("date1", dateStr1, "left value");

    final Date date2 = RND.dateYears(-100, 0);
    final SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    final String dateStr2 = sdf2.format(date2);
    tunnel.setParam("date2", dateStr2, "left value");

    final Date date4 = RND.dateYears(-100, 0);
    final SimpleDateFormat sdf4 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    final String dateStr4 = sdf4.format(date4);
    tunnel.setParam("date4", dateStr4, "left value");
    tunnel.setParam("date5", dateStr4, "left value");
    tunnel.setParam("date6", dateStr4, "left value");

    final Object actualArgument = e1.extract(null, tunnel, null);
    assertThat(actualArgument).isInstanceOf(TestParamsDatesAcceptor.class);
    TestParamsDatesAcceptor argument = (TestParamsDatesAcceptor) actualArgument;
    argument.asserts();
    assertThat(sdf1.format(argument.date1)).isEqualTo(dateStr1);
    assertThat(sdf2.format(argument.date2)).isEqualTo(dateStr2);
    assertThat(argument.date3).isNull();
    assertThat(argument.date4).isNull();
    assertThat(argument.date6).describedAs("date6 must be skipped because of annotation @SkipParameter").isNull();
  }

  public static class TestParamsStrsAcceptor {
    public String str1, str2;

    @SkipParameter
    public String str3;
    public String str4;
    @SkipParameter
    public String str5;

    public List<String> strs1 = new ArrayList<>();

    @SuppressWarnings("unused")
    public void setStr2(String str2) {
      this.str2 = str2 + " from setter";
    }

    @SuppressWarnings("unused")
    @SkipParameter
    public void setStr4(String str4) {
      Assertions.fail("str4 must not be set because @SkipParameter");
    }

    @SuppressWarnings("unused")
    public void setStr5(String str5) {
      Assertions.fail("str5 must not be set because @SkipParameter");
    }

    @SuppressWarnings("unused")
    public void setStr6(String str6) {
      Assertions.fail("no str6 in parameters, and it must not be set");
    }
  }

  private class ForParamsToWithStrs {
    @SuppressWarnings({"unused", "EmptyMethod", "UnusedParameters"})
    public void forTest(@ParamsTo TestParamsStrsAcceptor argument) {}
  }

  @Test
  public void forParamsToWithStrs() throws Exception {
    final Method method = TestUtil.getMethod(ForParamsToWithStrs.class, "forTest");

    final List<MethodParamExtractor> ee = MethodParameterMeta.create(method, null);
    MethodParamExtractor e1 = ee.get(0);

    TestTunnel tunnel = new TestTunnel();

    final String str1 = RND.str(10);
    tunnel.setParam("str1", str1, "left value");

    final String str2 = RND.str(10);
    tunnel.setParam("str2", str2, "left value");

    tunnel.setParam("str3", "asd");
    tunnel.setParam("str4", "asd");
    tunnel.setParam("str5", "asd");

    final String strs1_1 = RND.str(10), strs1_2 = RND.str(10), strs1_3 = RND.str(10);
    tunnel.setParam("strs1", strs1_1, strs1_2, strs1_3);

    final Object actualArgument = e1.extract(null, tunnel, null);
    assertThat(actualArgument).isInstanceOf(TestParamsStrsAcceptor.class);
    TestParamsStrsAcceptor argument = (TestParamsStrsAcceptor) actualArgument;
    assertThat(argument.str1).isEqualTo(str1);
    assertThat(argument.str2).isEqualTo(str2 + " from setter");
    assertThat(argument.str3).isNull();
    assertThat(argument.str4).isNull();
    assertThat(argument.str5).isNull();
    assertThat(argument.strs1).isEqualTo(Arrays.asList(strs1_1, strs1_2, strs1_3));
  }

  class ForParSession {
    @SuppressWarnings({"unused", "EmptyMethod"})
    public void forTest(@ParSession("strParamAsd") String strParam) {}
  }

  @Test
  public void parSession() throws Exception {
    final Method method = TestUtil.getMethod(ForParSession.class, "forTest");

    final String paramValue = RND.str(10);

    final MethodParamExtractor e = MethodParameterMeta.create(method, (context, tunnel) -> {
      assertThat(context.expectedReturnType()).isEqualTo(String.class);
      assertThat(context.json()).isNull();
      assertThat(context.parameterName()).isEqualTo("strParamAsd");
      return paramValue;
    }).get(0);

    final TestMappingResult catchResult = new TestMappingResult();

    TestTunnel tunnel = new TestTunnel();

    final Object actualParamValue = e.extract(catchResult, tunnel, null);

    assertThat(actualParamValue).isEqualTo(paramValue);
  }

  class ForParSessionWithJson {
    @SuppressWarnings({"unused", "EmptyMethod"})
    public void forTest(@ParSession("strParamHello") @Json String strParam) {}
  }

  @Test
  public void parSessionWithJson() throws Exception {
    final Method method = TestUtil.getMethod(ForParSessionWithJson.class, "forTest");

    final String paramValue = RND.str(10);

    TestTunnel tunnel = new TestTunnel();

    final MethodParamExtractor e = MethodParameterMeta.create(method, (context, handlerTunnel) -> {
      assertThat(context.expectedReturnType()).isEqualTo(String.class);
      assertThat(context.json()).isNotNull();
      assertThat(context.parameterName()).isEqualTo("strParamHello");
      assertThat(context.controllerMethod()).isEqualTo(method);
      assertThat(handlerTunnel).isSameAs(tunnel);
      return paramValue;
    }).get(0);

    final Object actualParamValue = e.extract(null, tunnel, null);

    assertThat(actualParamValue).isSameAs(paramValue);
  }
}
