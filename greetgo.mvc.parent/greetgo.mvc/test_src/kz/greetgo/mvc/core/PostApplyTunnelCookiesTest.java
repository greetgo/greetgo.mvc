package kz.greetgo.mvc.core;

import kz.greetgo.util.RND;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class PostApplyTunnelCookiesTest {

  TestTunnelCookies target;
  PostApplyTunnelCookies main;

  @BeforeMethod
  public void setup() {
    target = new TestTunnelCookies();
    main = new PostApplyTunnelCookies(target);
  }

  @Test
  public void getRequestCookieValue() {

    target.getRequestCookieValue_return = RND.str(10);

    String name = RND.str(10);

    //
    //
    final String value = main.getRequestCookieValue(name);
    //
    //

    assertThat(value).isEqualTo(target.getRequestCookieValue_return);
    assertThat(target.getRequestCookieValue_name).isEqualTo(name);
  }

  @Test
  public void apply() {

    main.saveCookieToResponse("asd", "hello");
    main.removeCookieFromResponse("good_by");

    assertThat(target.calls).isEmpty();

    main.apply();
    main.apply();

    assertThat(target.calls).containsExactly("saveCookieToResponse asd hello", "removeCookieFromResponse good_by");

    main.removeCookieFromResponse("hi");

    assertThat(target.calls).containsExactly("saveCookieToResponse asd hello", "removeCookieFromResponse good_by",
      "removeCookieFromResponse hi");

  }
}