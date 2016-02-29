package kz.greetgo.mvc.util;

import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.io.Serializable;

import static org.fest.assertions.api.Assertions.assertThat;

public class CookieUtilTest {

  @Test
  public void objectToFromStr_null() throws Exception {

    final String str = CookieUtil.objectToStr(null);

    assertThat(str).isNotNull();

    assertThat(CookieUtil.strToObject("")).isNull();
    assertThat(CookieUtil.strToObject("ЮЮЮЯЯЯ")).isNull();
    assertThat(CookieUtil.strToObject(null)).isNull();

    Object object = CookieUtil.strToObject(str);

    assertThat(object).isNull();
  }

  public static class TestObject implements Serializable {
    int intField;
    String strField;
    Integer integerField;
  }

  @Test
  public void objectToFromStr() throws Exception {

    TestObject object = new TestObject();
    object.integerField = RND.plusInt(1_000_000_000);
    object.intField = RND.plusInt(1_000_000_000);
    object.strField = RND.str(30);

    //
    //
    final String str = CookieUtil.objectToStr(object);
    //
    //

    assertThat(str).isNotEmpty();

    //
    //
    final TestObject actual = CookieUtil.strToObject(str);
    //
    //

    assertThat(actual).isNotNull();
    assertThat(actual.integerField).isEqualTo(object.integerField);
    assertThat(actual.intField).isEqualTo(object.intField);
    assertThat(actual.strField).isEqualTo(object.strField);

  }

  @Test
  public void objectToFromStr_str() throws Exception {

    String expected = "Привет мир!!! " + RND.str(10);

    //
    //
    final String str = CookieUtil.objectToStr(expected);
    //
    //

    assertThat(str).isNotEmpty();

    //
    //
    final String actual = CookieUtil.strToObject(str);
    //
    //

    assertThat(actual).isEqualTo(expected);
  }

}
