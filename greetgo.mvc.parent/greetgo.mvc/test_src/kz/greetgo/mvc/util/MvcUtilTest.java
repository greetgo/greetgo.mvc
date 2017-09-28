package kz.greetgo.mvc.util;

import kz.greetgo.mvc.errors.CannotConvertToDate;
import kz.greetgo.mvc.errors.IllegalChar;
import kz.greetgo.mvc.utils.TestEnum;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.fest.assertions.api.Assertions.assertThat;

public class MvcUtilTest {

  @DataProvider
  public Object[][] amountBytesToLong_dataProvider() {
    return new Object[][]{

      new Object[]{" - 123 4 ", -1234L},

      new Object[]{" 123  ", 123L},
      new Object[]{" 123 B", 123L},
      new Object[]{" 123B ", 123L},
      new Object[]{" 123 b", 123L},
      new Object[]{" 123b ", 123L},
      new Object[]{" 123 657 876b ", 123657876L},
      new Object[]{" 123 657 876 b", 123657876L},

      new Object[]{" 12 752K  ", 12_752L * 1024L},
      new Object[]{" 12 752 K ", 12_752L * 1024L},
      new Object[]{" 12 752k  ", 12_752L * 1024L},
      new Object[]{" 12 752 k ", 12_752L * 1024L},

      new Object[]{" - 12 752Kb  ", -12_752L * 1024L},
      new Object[]{" - 12_752 Kb ", -12_752L * 1024L},
      new Object[]{" - 12 752kb  ", -12_752L * 1024L},
      new Object[]{" - 12 752 kb ", -12_752L * 1024L},

      new Object[]{" - 12_752KB  ", -12_752L * 1024L},
      new Object[]{" - 12 752 KB ", -12_752L * 1024L},
      new Object[]{" - 12_752kB  ", -12_752L * 1024L},
      new Object[]{" - 12 752 kB ", -12_752L * 1024L},

      new Object[]{" 13 752MB  ", 13_752L * 1024L * 1024L},
      new Object[]{" 13_752 MB ", 13_752L * 1024L * 1024L},
      new Object[]{" 13 752 M  ", 13_752L * 1024L * 1024L},
      new Object[]{" 13_752M   ", 13_752L * 1024L * 1024L},

      new Object[]{" 13 752GB  ", 13_752L * 1024L * 1024L * 1024L},
      new Object[]{" 13_752 GB ", 13_752L * 1024L * 1024L * 1024L},
      new Object[]{" 13 752 G  ", 13_752L * 1024L * 1024L * 1024L},
      new Object[]{" 13_752G   ", 13_752L * 1024L * 1024L * 1024L},

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
}