package kz.greetgo.mvc.model;

import kz.greetgo.mvc.util.CookieUtil;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class RedirectTest {

  @Test
  public void addCookie() {
    //noinspection ThrowableResultOfMethodCallIgnored
    final Redirect redirect = Redirect.to("some reference")
      .addCookie("asd", "asd value").addCookie("dsa", "dsa object value");

    assertThat(redirect.reference).isEqualTo("some reference");
    assertThat(redirect.savingCookiesToResponse.get("asd")).isEqualTo("asd value");
    assertThat(redirect.savingCookiesToResponse.get("dsa")).isEqualTo(CookieUtil.objectToStr("dsa object value"));
  }

}
