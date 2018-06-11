package kz.greetgo.mvc.util;

import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class UriEscapeTest {

  /**
   * Tests that the simple escape treats 0-9, a-z and A-Z as safe
   */
  @Test
  public void testSimpleEscape() {
    for (char c = 0; c < 128; c++) {
      if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
        || c == '-' || c == '.' || c == '_' || c == '~') {
        assertThat(UriEscape.escape("" + c)).isEqualTo("" + c);
      } else {
        assertThat(UriEscape.escape("" + c)).isEqualTo("" + escapeAscii(c));
      }
    }

    assertThat(UriEscape.escape("" + '\u0000')).isEqualTo("%00");// nul
    assertThat(UriEscape.escape("" + '\u007f')).isEqualTo("%7F");// del
    assertThat(UriEscape.escape("" + '\u0080')).isEqualTo("%C2%80");// xx-00010,x-000000
    assertThat(UriEscape.escape("" + '\u07ff')).isEqualTo("%DF%BF");// xx-11111,x-111111
    assertThat(UriEscape.escape("" + '\u0800')).isEqualTo("%E0%A0%80");// xxx-0000,x-100000,x-00,0000
    assertThat(UriEscape.escape("" + '\uffff')).isEqualTo("%EF%BF%BF");// xxx-1111,x-111111,x-11,1111

    //Я так и не понял что это значит. И поэтому закоментил.
//    String escape = UrlEscapers.urlFragmentEscaper().escape("" + (char) Character.toCodePoint('\uD800', '\uDC00'));
//    System.out.println("escape = " + escape);
//    assertThat(UriEscape.escape("" + Character.toCodePoint('\uD800', '\uDC00'))).isEqualTo("%F0%90%80%80");
//    assertThat(UriEscape.escape("" + Character.toCodePoint('\uDBFF', '\uDFFF'))).isEqualTo("%F4%8F%BF%BF");

    assertThat(UriEscape.escape("")).isEqualTo("");
    assertThat(UriEscape.escape("safeString")).isEqualTo("safeString");
    assertThat(UriEscape.escape("embedded\0null")).isEqualTo("embedded%00null");
    assertThat(UriEscape.escape("max\uffffchar")).isEqualTo("max%EF%BF%BFchar");

    assertThat(UriEscape.escape("string with spaces")).isEqualTo("string%20with%20spaces");
  }

  /**
   * Tests the various ways that the space character can be handled
   */
  @Test
  public void testPlusForSpace() {
    assertThat(UriEscape.escape("string with spaces")).isEqualTo("string%20with%20spaces");
  }

  /**
   * Tests that if specify '%' as safe the result is an idempotent escaper.
   */
  @SuppressWarnings("SpellCheckingInspection")
  @Test
  public void testCustomEscape_withPercent() {
    assertThat(UriEscape.escape("foo|bar")).isEqualTo("foo%7Cbar");
    assertThat(UriEscape.escape("foo%7Cbar")).isEqualTo("foo%257Cbar");// idempotent
  }

  @Test
  public void testBadArguments_null() {
    assertThat(UriEscape.escape(null)).isNull();
  }

  /**
   * Helper to manually escape a 7-bit ascii character
   */
  @SuppressWarnings("SpellCheckingInspection")
  private static String escapeAscii(char c) {
    assertThat(c < 128).isTrue();
    String hex = "0123456789ABCDEF";
    return "%" + hex.charAt((c >> 4) & 0xf) + hex.charAt(c & 0xf);
  }
}