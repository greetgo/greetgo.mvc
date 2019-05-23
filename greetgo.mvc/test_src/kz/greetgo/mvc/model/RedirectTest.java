package kz.greetgo.mvc.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.testng.annotations.Test;

public class RedirectTest {
  
  @Test
  public void addCookie() {
    //noinspection ThrowableResultOfMethodCallIgnored
    final Redirect redirect = Redirect.to("some reference").addCookie("asd", "asd value")
        .addCookie("dsa", "dsa object value");
    
    assertThat(redirect.reference).isEqualTo("some reference");
    assertThat(redirect.savingCookiesToResponse.get("asd")).isEqualTo("asd value");
    assertThat(redirect.savingCookiesToResponse.get("dsa")).isEqualTo("dsa object value");
  }
  
}
