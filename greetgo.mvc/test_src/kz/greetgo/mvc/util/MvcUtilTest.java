package kz.greetgo.mvc.util;

import kz.greetgo.mvc.annotations.MethodFilter;
import kz.greetgo.mvc.core.MappingIdentity;
import kz.greetgo.mvc.core.RequestMethod;
import kz.greetgo.mvc.errors.CannotConvertToDate;
import kz.greetgo.mvc.errors.CompatibleTargetMapping;
import kz.greetgo.mvc.errors.DoublePathPar;
import kz.greetgo.mvc.errors.IllegalChar;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.mvc.interfaces.TunnelExecutor;
import kz.greetgo.mvc.interfaces.TunnelExecutorGetter;
import kz.greetgo.mvc.utils.TestEnum;
import org.fest.assertions.api.Assertions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;

public class MvcUtilTest {

  @DataProvider
  public Object[][] amountBytesToLong_dataProvider() {
    return new Object[][]{

      {" - 123 4 ", -1234L},

      {" 123  ", 123L},
      {" 123 B", 123L},
      {" 123B ", 123L},
      {" 123 b", 123L},
      {" 123b ", 123L},
      {" 123 657 876b ", 123657876L},
      {" 123 657 876 b", 123657876L},

      {" 12 752K  ", 12_752L * 1024L},
      {" 12 752 K ", 12_752L * 1024L},
      {" 12 752k  ", 12_752L * 1024L},
      {" 12 752 k ", 12_752L * 1024L},

      {" - 12 752Kb  ", -12_752L * 1024L},
      {" - 12_752 Kb ", -12_752L * 1024L},
      {" - 12 752kb  ", -12_752L * 1024L},
      {" - 12 752 kb ", -12_752L * 1024L},

      {" - 12_752KB  ", -12_752L * 1024L},
      {" - 12 752 KB ", -12_752L * 1024L},
      {" - 12_752kB  ", -12_752L * 1024L},
      {" - 12 752 kB ", -12_752L * 1024L},

      {" 13 752MB  ", 13_752L * 1024L * 1024L},
      {" 13_752 MB ", 13_752L * 1024L * 1024L},
      {" 13 752 M  ", 13_752L * 1024L * 1024L},
      {" 13_752M   ", 13_752L * 1024L * 1024L},

      {" 13 752GB  ", 13_752L * 1024L * 1024L * 1024L},
      {" 13_752 GB ", 13_752L * 1024L * 1024L * 1024L},
      {" 13 752 G  ", 13_752L * 1024L * 1024L * 1024L},
      {" 13_752G   ", 13_752L * 1024L * 1024L * 1024L},

    };
  }

  @Test(dataProvider = "amountBytesToLong_dataProvider")
  public void amountBytesToLong(String str, long expected) throws Exception {
    assertThat(MvcUtil.amountBytesToLong(str)).isEqualTo(expected);
  }

  @Test(expectedExceptions = IllegalChar.class)
  public void amountBytesToLong_IllegalChar() throws Exception {
    MvcUtil.amountBytesToLong("tmp");
  }

  @Test
  public void extractRedirect() {
    //noinspection ThrowableResultOfMethodCallIgnored
    MvcUtil.extractRedirect(null, 10);
  }

  @DataProvider
  public Object[][] convertStrToType_Boolean_dataProvider() {
    return new Object[][]{
      {"true", true}, {"yes", true}, {"y", true}, {"on", true},
      {"false", false}, {"no", false}, {"n", false}, {"off", false},

      {"TRUE", true}, {"YES", true}, {"Y", true}, {"ON", true},
      {"FALSE", false}, {"NO", false}, {"N", false}, {"OFF", false},


      {"1", true}, {"0", false}, {"", false}, {"2", true},
    };
  }

  @Test(dataProvider = "convertStrToType_Boolean_dataProvider")
  public void convertStrToType_Boolean(String str, Boolean bool) throws Exception {
    Object res = MvcUtil.convertStrToType(str, Boolean.class);
    assertThat(res).isInstanceOf(Boolean.class);
    assertThat(res).isEqualTo(bool);
  }

  @Test(dataProvider = "convertStrToType_Boolean_dataProvider")
  public void convertStringsToType_Boolean(String str, Boolean bool) throws Exception {
    Object res = MvcUtil.convertStringsToType(ss(str), Boolean.class);
    assertThat(res).isInstanceOf(Boolean.class);
    assertThat(res).isEqualTo(bool);
  }

  private static String[] ss(String str) {
    return new String[]{str};
  }

  @Test
  public void convertStrToType_Enum_SomeValue_byName() throws Exception {
    Object res = MvcUtil.convertStrToType(TestEnum.SOME_ENUM_VALUE1.name(), TestEnum.class);
    assertThat(res).isInstanceOf(TestEnum.class);
    assertThat(res).isEqualTo(TestEnum.SOME_ENUM_VALUE1);
  }

  @Test
  public void convertStrToType_Enum_SomeValue_byOrdinal() throws Exception {
    Object res = MvcUtil.convertStrToType("" + TestEnum.ENUM_VALUE2.ordinal(), TestEnum.class);
    assertThat(res).isInstanceOf(TestEnum.class);
    assertThat(res).isEqualTo(TestEnum.ENUM_VALUE2);
  }

  @Test
  public void convertStringsToType_Enum_SomeValue_byOrdinal() throws Exception {
    Object res = MvcUtil.convertStringsToType(ss("" + TestEnum.ENUM_VALUE2.ordinal()), TestEnum.class);
    assertThat(res).isInstanceOf(TestEnum.class);
    assertThat(res).isEqualTo(TestEnum.ENUM_VALUE2);
  }

  @Test
  public void convertStringsToType_Enum_SomeValue() throws Exception {
    Object res = MvcUtil.convertStringsToType(ss(TestEnum.SOME_ENUM_VALUE1.name()), TestEnum.class);
    assertThat(res).isInstanceOf(TestEnum.class);
    assertThat(res).isEqualTo(TestEnum.SOME_ENUM_VALUE1);
  }

  @Test
  public void convertStrToType_Enum_Null() throws Exception {
    Object res = MvcUtil.convertStrToType(null, TestEnum.class);
    assertThat(res).isNull();
  }

  @Test
  public void convertStringsToType_Enum_Null() throws Exception {
    Object res = MvcUtil.convertStringsToType(ss(null), TestEnum.class);
    assertThat(res).isNull();
  }

  @Test
  public void convertStrToType_Enum_EmptyStr() throws Exception {
    Object res = MvcUtil.convertStrToType("", TestEnum.class);
    assertThat(res).isNull();
  }

  @Test
  public void convertStringsToType_Enum_EmptyStr() throws Exception {
    Object res = MvcUtil.convertStringsToType(ss(""), TestEnum.class);
    assertThat(res).isNull();
  }

  @Test
  public void convertStrToType_BigDecimal_pointAndSpaces() throws Exception {
    Object res = MvcUtil.convertStrToType("123 098 345.876 878 987", BigDecimal.class);
    assertThat(res).isInstanceOf(BigDecimal.class);
    assertThat((BigDecimal) res).isEqualByComparingTo("123098345.876878987");
  }

  @Test
  public void convertStringsToType_BigDecimal_pointAndSpaces() throws Exception {
    Object res = MvcUtil.convertStringsToType(ss("123 098 345.876 878 987"), BigDecimal.class);
    assertThat(res).isInstanceOf(BigDecimal.class);
    assertThat((BigDecimal) res).isEqualByComparingTo("123098345.876878987");
  }

  @Test
  public void convertStrToType_BigDecimal_commaAndUnderscores() throws Exception {
    Object res = MvcUtil.convertStrToType("123_098_345,876_878_987", BigDecimal.class);
    assertThat(res).isInstanceOf(BigDecimal.class);
    assertThat((BigDecimal) res).isEqualByComparingTo("123098345.876878987");
  }

  @Test
  public void convertStrToType_BigDecimal_exponential() throws Exception {
    Object res = MvcUtil.convertStrToType("3.347689e347", BigDecimal.class);
    assertThat(res).isInstanceOf(BigDecimal.class);
    assertThat((BigDecimal) res).isEqualByComparingTo("3.347689e347");
  }

  @Test
  public void convertStrToType_BigDecimal_empty() throws Exception {
    Object res = MvcUtil.convertStrToType("", BigDecimal.class);
    assertThat(res).isNull();
  }

  @Test
  public void convertStrToType_BigDecimal_spaces() throws Exception {
    Object res = MvcUtil.convertStrToType("    ", BigDecimal.class);
    assertThat(res).isNull();
  }

  @Test
  public void convertStrToType_BigDecimal_null() throws Exception {
    Object res = MvcUtil.convertStrToType(null, BigDecimal.class);
    assertThat(res).isNull();
  }

  @DataProvider
  public Object[][] convertStrToType_Date_DataProvider() throws ParseException {
    SimpleDateFormat f1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat f2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    SimpleDateFormat f3 = new SimpleDateFormat("yyyy-MM-dd");

    return new Object[][]{
      {"1991-01-23 11:22:33", f1.parse("1991-01-23 11:22:33")},
      {"23.01.1991 11:22:33", f1.parse("1991-01-23 11:22:33")},
      {"23/01/1991 11:22:33", f1.parse("1991-01-23 11:22:33")},

      {"1991-01-23 11:22", f2.parse("1991-01-23 11:22")},
      {"23.01.1991 11:22", f2.parse("1991-01-23 11:22")},
      {"23/01/1991 11:22", f2.parse("1991-01-23 11:22")},

      {"1991-07-23", f3.parse("1991-07-23")},
      {"23.07.1991", f3.parse("1991-07-23")},
      {"23/07/1991", f3.parse("1991-07-23")},

    };
  }

  @Test(dataProvider = "convertStrToType_Date_DataProvider")
  public void convertStrToType_Date_ok(String str, Date expected) throws Exception {
    Object res = MvcUtil.convertStrToType(str, Date.class);
    assertThat(res).isNotNull();

    assertThat(res).isEqualTo(expected);
  }

  @Test
  public void convertStrToType_Date_spaces() throws Exception {
    Object res = MvcUtil.convertStrToType("   ", Date.class);
    assertThat(res).isNull();
  }

  @Test
  public void convertStrToType_Date_empty() throws Exception {
    Object res = MvcUtil.convertStrToType("", Date.class);
    assertThat(res).isNull();
  }

  @Test
  public void convertStrToType_Date_null() throws Exception {
    Object res = MvcUtil.convertStrToType(null, Date.class);
    assertThat(res).isNull();
  }

  @Test(expectedExceptions = CannotConvertToDate.class)
  public void convertStrToType_Date_leftStr() throws Exception {
    MvcUtil.convertStrToType("Left str", Date.class);
  }

  @Test
  public void convertStrToType_Double_ok() throws Exception {
    Object res = MvcUtil.convertStrToType("  23 098,458 765", Double.class);
    assertThat(res).isInstanceOf(Double.class);

    assertThat((double) res).isEqualTo(23_098.458_765);
  }

  @Test
  public void convertStrToType_Double_null() throws Exception {
    Object res = MvcUtil.convertStrToType(null, Double.class);
    assertThat(res).isNull();
  }

  @Test
  public void convertStrToType_double_ok() throws Exception {
    Object res = MvcUtil.convertStrToType("  23 098,458 765", Double.TYPE);
    assertThat(res).isInstanceOf(Double.class);

    assertThat((double) res).isEqualTo(23_098.458_765);
  }

  @Test
  public void convertStrToType_double_null() throws Exception {
    Object res = MvcUtil.convertStrToType(null, Double.TYPE);
    assertThat(res).isInstanceOf(Double.class);
    assertThat((double) res).isZero();
  }

  @Test
  public void convertStrToType_Float_ok() throws Exception {
    Object res = MvcUtil.convertStrToType("  23 098,458 765", Float.class);
    assertThat(res).isInstanceOf(Float.class);

    assertThat((float) res).isEqualTo(23_098.458_765f);
  }

  @Test
  public void convertStrToType_Float_null() throws Exception {
    Object res = MvcUtil.convertStrToType(null, Float.class);
    assertThat(res).isNull();
  }

  @Test
  public void convertStrToType_float_ok() throws Exception {
    Object res = MvcUtil.convertStrToType("  23 098,458 765", Float.TYPE);
    assertThat(res).isInstanceOf(Float.class);

    assertThat((float) res).isEqualTo(23_098.458_765f);
  }

  @Test
  public void convertStrToType_float_null() throws Exception {
    Object res = MvcUtil.convertStrToType(null, Float.TYPE);
    assertThat(res).isInstanceOf(Float.class);
    assertThat((float) res).isZero();
  }

  @Test
  public void convertStrToType_BigInteger_ok() throws Exception {
    Object res = MvcUtil.convertStrToType("  23 098 458 765 654 768 967 654 768", BigInteger.class);
    assertThat(res).isInstanceOf(BigInteger.class);

    assertThat((BigInteger) res).isEqualTo(new BigInteger("23098458765654768967654768"));
  }

  @Test
  public void convertStrToType_BigInteger_null() throws Exception {
    Object res = MvcUtil.convertStrToType(null, BigInteger.class);
    assertThat(res).isNull();
  }

  private static TunnelExecutorGetter createTestTEG(String requestMethods, String targetMapper) {
    return new TunnelExecutorGetter() {
      @Override
      public TunnelExecutor getTunnelExecutor(RequestTunnel tunnel) {
        throw new RuntimeException();
      }

      @Override
      public String infoStr() {
        return requestMethods + "::" + targetMapper;
      }

      @Override
      public MappingIdentity getMappingIdentity() {
        return new MappingIdentity() {
          @Override
          public String targetMapping() {
            return targetMapper;
          }

          @Override
          public MethodFilter methodFilter() {
            return requestMethods == null ? null : new MethodFilter() {
              @Override
              public Class<? extends Annotation> annotationType() {
                throw new UnsupportedOperationException();
              }

              @Override
              public RequestMethod[] value() {
                return Arrays.stream(requestMethods.split("-"))
                  .filter(n -> !n.isEmpty())
                  .map(RequestMethod::valueOf)
                  .toArray(RequestMethod[]::new);
              }
            };
          }
        };
      }
    };
  }

  @Test
  public void checkTunnelExecutorGetters_1() {
    List<TunnelExecutorGetter> list = new ArrayList<>();

    list.add(createTestTEG("POST-GET", "/asd/wow"));
    list.add(createTestTEG("HEAD-PUT", "/asd/wow"));

    MvcUtil.checkTunnelExecutorGetters(list);
  }

  @Test(expectedExceptions = CompatibleTargetMapping.class)
  public void checkTunnelExecutorGetters_2() {
    List<TunnelExecutorGetter> list = new ArrayList<>();

    list.add(createTestTEG(null, "/asd/dsa"));//  null - это отсутствие аннотации @MethodFilter - т.е. все методы
    list.add(createTestTEG("HEAD-PUT", "/asd/dsa"));

    MvcUtil.checkTunnelExecutorGetters(list);
  }

  @Test(expectedExceptions = CompatibleTargetMapping.class)
  public void checkTunnelExecutorGetters_3() {
    List<TunnelExecutorGetter> list = new ArrayList<>();

    list.add(createTestTEG("POST-GET", "/asd/dsa"));
    list.add(createTestTEG(null, "/asd/dsa"));

    MvcUtil.checkTunnelExecutorGetters(list);
  }

  @Test
  public void checkTunnelExecutorGetters_4() {
    List<TunnelExecutorGetter> list = new ArrayList<>();

    list.add(createTestTEG("POST-GET", "/asd/dsa"));
    list.add(createTestTEG("", "/asd/dsa"));//  значение "" - это аннотация с путым массивом - @MethodFilter({})

    MvcUtil.checkTunnelExecutorGetters(list);
  }

  @Test(expectedExceptions = CompatibleTargetMapping.class)
  public void checkTunnelExecutorGetters_5() {
    List<TunnelExecutorGetter> list = new ArrayList<>();

    list.add(createTestTEG("POST-GET", "/asd/dsa"));
    list.add(createTestTEG("GET", "/asd/dsa"));

    MvcUtil.checkTunnelExecutorGetters(list);
  }

  @DataProvider
  public Object[][] checkTunnelExecutorGetters_targetMapper_DP() {
    return new Object[][]{
      {"/asd/dsa", "/asd/dsa", false},
      {"/asd/dsa1", "/asd/dsa2", true},
      {"/asd/dsa/*", "/asd/dsa/*/asd", false},
      {"/asd/dsa/*/dsa", "/asd/dsa/*/asd", true},
      {"/asd/dsa/*/dsa", "/asd/dsa/hello/dsa", false},
      {"/asd/dsa/*/dsa", "/asd/dsa/hello/asd", true},
      {"/asd/*", "/asd/wow", false},
      {"/A/*/B/*/C/*/D1", "/A/*/B/*/C/*/D2", true},
      {"/A/*/B/*/C1/*/D", "/A/*/B/*/C2/*/D", false},
      {"/A/*/B/*/C1/*/D", "/A/*/B/*/C22/*/D", false},
      {"/A/*/B/*/C11/*/D", "/A/*/B/*/C2/*/D", false},
      {"/A/*/B/*/C/*/D", "/A/*/B/*/C/*/D", false},
      {"/A/*/B/*/C/*/D", "/A/*/B/*/C/*", false},
      {"*", "asd", false},
      {"*", "asd*", false},
      {"*", "asd*dsa", false},
      {"*/asd", "*/dsa", true},
      {"*/asd/*", "*/dsa/*", false},
      {"*/asd/*", "*/asd/*", false},

      {"ab*", "a*d", false},
      {"*ba", "d*a", false},
      {"/aaa/bbb/*", "/aaa/*ddd", false},

      {"xab*", "ya*d", true},
      {"x*ba", "yd*a", true},
      {"x/aaa/bbb/*", "y/aaa/*ddd", true},

      {"ab*2", "a*d1", true},
      {"*ba2", "d*a1", true},
      {"/aaa/bbb/*2", "/aaa/*ddd1", true},

      {"a", "b", true},
      {"a", "b*", true},
      {"a", "*b", true},
      {"a*", "*b", false},
      {"a*b", "*b", false},
      {"a*c", "*b", true},

      {"asd", "*asd*", false},
      {"abc", "*a*b*c*", false},
      {"abc", "*a*c*b*", false},
      {"abc", "*a*c*c*", false},
      {"abc", "*a*x*c*", false},

      {"abc123ABC098", "*a*b*c123*09*8*", false},
      {"abc123ABC098", "*a*b*c12j3*09*8*", false},

      {"*a*b*c123*09*8*", "abc123ABC098", false},
      {"*a*b*c12j3*09*8*", "abc123ABC098", false},

      {"*", "*", false},
      {"", "a", true},
      {"a", "", true},
      {"", "", false},
      {"*", "", false},
      {"", "*", false},
      {"", "*a", true},
      {"", "a*", true},

      {"*/asd/*/wow", "*/asd/*/qu/*xxx/wow", false},
      {"*/asd/*/wow", "*/asd/*/qu/*xxx/wow1", true},
    };
  }

  @Test(dataProvider = "checkTunnelExecutorGetters_targetMapper_DP")
  public void checkTunnelExecutorGetters_targetMapper(String tm1, String tm2, boolean ok) {
    List<TunnelExecutorGetter> list = new ArrayList<>();

    list.add(createTestTEG(null, tm1));
    list.add(createTestTEG(null, tm2));

    if (ok) {
      MvcUtil.checkTunnelExecutorGetters(list);
    } else try {
      MvcUtil.checkTunnelExecutorGetters(list);
      Assertions.fail("Must be exception CompatibleTargetMapping: tm1 = " + tm1 + ", tm2 = " + tm2);
    } catch (CompatibleTargetMapping ignore) {}
  }

  @Test(expectedExceptions = CompatibleTargetMapping.class)
  public void checkTunnelExecutorGetters_targetMapper_001() {
    List<TunnelExecutorGetter> list = new ArrayList<>();

    list.add(createTestTEG(null, "/asd/dsa/*/dsa"));
    list.add(createTestTEG(null, "/asd/dsa/hello/dsa"));

    MvcUtil.checkTunnelExecutorGetters(list);
  }

  @Test
  public void checkTunnelExecutorGetters_targetMapper_002() {
    List<TunnelExecutorGetter> list = new ArrayList<>();

    list.add(createTestTEG(null, "xab*"));
    list.add(createTestTEG(null, "ya*d"));

    MvcUtil.checkTunnelExecutorGetters(list);
  }

  @Test(expectedExceptions = DoublePathPar.class)
  public void checkTunnelExecutorGetters_doublePathPar() {
    List<TunnelExecutorGetter> list = new ArrayList<>();

    list.add(createTestTEG(null, "/asd/**/dsa/"));

    MvcUtil.checkTunnelExecutorGetters(list);
  }
}
