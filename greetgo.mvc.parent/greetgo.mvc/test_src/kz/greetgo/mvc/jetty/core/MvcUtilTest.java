package kz.greetgo.mvc.jetty.core;

import kz.greetgo.mvc.jetty.errors.IllegalChar;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

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

}