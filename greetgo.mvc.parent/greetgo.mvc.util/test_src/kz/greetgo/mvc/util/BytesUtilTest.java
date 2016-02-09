package kz.greetgo.mvc.util;

import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.fest.assertions.api.Assertions.assertThat;

public class BytesUtilTest {

  @Test(invocationCount = 10)
  public void putLong_takeLong() throws Exception {

    long longValue = RND.rnd.nextLong();

    byte[] buf = new byte[100];

    int offset = RND.plusInt(buf.length / 2);

    //
    //
    BytesUtil.putLong(buf, offset, longValue);
    //
    //

    Arrays.fill(buf, 0, offset, (byte) 0);
    Arrays.fill(buf, offset + Long.SIZE / 8, buf.length, (byte) 0);

    //
    //
    final long actualLongValue = BytesUtil.extractLong(buf, offset);
    //
    //

    assertThat(actualLongValue).isEqualTo(longValue);
  }

}