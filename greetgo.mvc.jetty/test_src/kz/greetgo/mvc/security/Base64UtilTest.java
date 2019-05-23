package kz.greetgo.mvc.security;

import kz.greetgo.mvc.util.Base64Util;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class Base64UtilTest {

  @Test
  public void bytesToBase64_base64ToBytes() throws Exception {
    final byte[] bytes = RND.byteArray(123);

    //
    //
    final String base64str = Base64Util.bytesToBase64(bytes);
    //
    //

    //
    //
    final byte[] bytesActual = Base64Util.base64ToBytes(base64str);
    //
    //

    assertThat(bytesActual).isEqualTo(bytes);
  }

  @Test
  public void base64ToBytes_left() throws Exception {
    //
    //
    final byte[] bytes = Base64Util.base64ToBytes("Левая строка");
    //
    //

    assertThat(bytes).isNull();
  }

  @Test
  public void base64ToBytes_left2() throws Exception {
    //
    //
    final byte[] bytes = Base64Util.base64ToBytes("       ");
    //
    //

    assertThat(bytes).isNull();
  }

  @Test
  public void base64ToBytes_empty() throws Exception {
    //
    //
    final byte[] bytes = Base64Util.base64ToBytes("");
    //
    //

    assertThat(bytes).isNull();
  }

  @Test
  public void base64ToBytes_null() throws Exception {
    //
    //
    final byte[] bytes = Base64Util.base64ToBytes(null);
    //
    //

    //noinspection ConstantConditions
    assertThat(bytes).isNull();
  }

  @Test
  public void bytesToBase64_null() throws Exception {
    //
    //
    final String str = Base64Util.bytesToBase64(null);
    //
    //

    //noinspection ConstantConditions
    assertThat(str).isNull();
  }
}
