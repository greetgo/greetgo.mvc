package kz.greetgo.mvc.util;

import java.net.URLEncoder;
import java.util.Objects;

/**
 * Эскэйпит URI-параметры. Взято из google guava (com.google.common.net.PercentEscaper)
 */
@SuppressWarnings("SpellCheckingInspection")
public class UriEscape {

  public static void main(String[] args) throws Exception {
    System.out.println(URLEncoder.encode(".", "UTF-8"));
  }

  private static boolean[] createSafeOctets() {
    char[] safeCharArray = ("-._~"            // Unreserved characters.
      + "abcdefghijklmnopqrstuvwxyz"
      + "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
      + "0123456789").toCharArray();
    int maxChar = -1;
    for (char c : safeCharArray) {
      maxChar = Math.max(c, maxChar);
    }
    boolean[] octets = new boolean[maxChar + 1];
    for (char c : safeCharArray) {
      octets[c] = true;
    }
    return octets;
  }

  private static final boolean[] safeOctets = createSafeOctets();

  public static String escape(String str) {
    if (str == null) return null;

    int slen = str.length();
    for (int index = 0; index < slen; index++) {
      char c = str.charAt(index);
      if (c >= safeOctets.length || !safeOctets[c]) {
        return escapeSlow(str, index);
      }
    }
    return str;
  }

  private static final ThreadLocal<char[]> DEST_TL = ThreadLocal.withInitial(() -> new char[1024]);

  private static int codePointAt(CharSequence seq, int index, int end) {
    Objects.requireNonNull(seq);
    if (index < end) {
      char c1 = seq.charAt(index++);
      if (c1 < Character.MIN_HIGH_SURROGATE ||
        c1 > Character.MAX_LOW_SURROGATE) {
        // Fast path (first test is probably all we need to do)
        return c1;
      } else if (c1 <= Character.MAX_HIGH_SURROGATE) {
        // If the high surrogate was the last character, return its inverse
        if (index == end) {
          return -c1;
        }
        // Otherwise look for the low surrogate following it
        char c2 = seq.charAt(index);
        if (Character.isLowSurrogate(c2)) {
          return Character.toCodePoint(c1, c2);
        }
        throw new IllegalArgumentException(
          "Expected low surrogate but got char '" + c2 +
            "' with value " + (int) c2 + " at index " + index +
            " in '" + seq + "'");
      } else {
        throw new IllegalArgumentException(
          "Unexpected low surrogate character '" + c1 +
            "' with value " + (int) c1 + " at index " + (index - 1) +
            " in '" + seq + "'");
      }
    }
    throw new IndexOutOfBoundsException("Index exceeds specified range");
  }

  private static final char[] UPPER_HEX_DIGITS =
    "0123456789ABCDEF".toCharArray();

  static char[] escape(int cp) {
    // We should never get negative values here but if we do it will throw an
    // IndexOutOfBoundsException, so at least it will get spotted.
    if (cp < safeOctets.length && safeOctets[cp]) {
      return null;
    } else if (cp <= 0x7F) {
      // Single byte UTF-8 characters
      // Start with "%--" and fill in the blanks
      char[] dest = new char[3];
      dest[0] = '%';
      dest[2] = UPPER_HEX_DIGITS[cp & 0xF];
      dest[1] = UPPER_HEX_DIGITS[cp >>> 4];
      return dest;
    } else if (cp <= 0x7ff) {
      // Two byte UTF-8 characters [cp >= 0x80 && cp <= 0x7ff]
      // Start with "%--%--" and fill in the blanks
      char[] dest = new char[6];
      dest[0] = '%';
      dest[3] = '%';
      dest[5] = UPPER_HEX_DIGITS[cp & 0xF];
      cp >>>= 4;
      dest[4] = UPPER_HEX_DIGITS[0x8 | (cp & 0x3)];
      cp >>>= 2;
      dest[2] = UPPER_HEX_DIGITS[cp & 0xF];
      cp >>>= 4;
      dest[1] = UPPER_HEX_DIGITS[0xC | cp];
      return dest;
    } else if (cp <= 0xffff) {
      // Three byte UTF-8 characters [cp >= 0x800 && cp <= 0xffff]
      // Start with "%E-%--%--" and fill in the blanks
      char[] dest = new char[9];
      dest[0] = '%';
      dest[1] = 'E';
      dest[3] = '%';
      dest[6] = '%';
      dest[8] = UPPER_HEX_DIGITS[cp & 0xF];
      cp >>>= 4;
      dest[7] = UPPER_HEX_DIGITS[0x8 | (cp & 0x3)];
      cp >>>= 2;
      dest[5] = UPPER_HEX_DIGITS[cp & 0xF];
      cp >>>= 4;
      dest[4] = UPPER_HEX_DIGITS[0x8 | (cp & 0x3)];
      cp >>>= 2;
      dest[2] = UPPER_HEX_DIGITS[cp];
      return dest;
    } else if (cp <= 0x10ffff) {
      char[] dest = new char[12];
      // Four byte UTF-8 characters [cp >= 0xffff && cp <= 0x10ffff]
      // Start with "%F-%--%--%--" and fill in the blanks
      dest[0] = '%';
      dest[1] = 'F';
      dest[3] = '%';
      dest[6] = '%';
      dest[9] = '%';
      dest[11] = UPPER_HEX_DIGITS[cp & 0xF];
      cp >>>= 4;
      dest[10] = UPPER_HEX_DIGITS[0x8 | (cp & 0x3)];
      cp >>>= 2;
      dest[8] = UPPER_HEX_DIGITS[cp & 0xF];
      cp >>>= 4;
      dest[7] = UPPER_HEX_DIGITS[0x8 | (cp & 0x3)];
      cp >>>= 2;
      dest[5] = UPPER_HEX_DIGITS[cp & 0xF];
      cp >>>= 4;
      dest[4] = UPPER_HEX_DIGITS[0x8 | (cp & 0x3)];
      cp >>>= 2;
      dest[2] = UPPER_HEX_DIGITS[cp & 0x7];
      return dest;
    } else {
      // If this ever happens it is due to bug in UnicodeEscaper, not bad input.
      throw new IllegalArgumentException(
        "Invalid unicode character value " + cp);
    }
  }

  /**
   * The amount of padding (chars) to use when growing the escape buffer.
   */
  private static final int DEST_PAD = 32;

  private static char[] growBuffer(char[] dest, int index, int size) {
    char[] copy = new char[size];
    if (index > 0) {
      System.arraycopy(dest, 0, copy, 0, index);
    }
    return copy;
  }

  private static int nextEscapeIndex(CharSequence csq, int start, int end) {
    int index = start;
    while (index < end) {
      int cp = codePointAt(csq, index, end);
      if (cp < 0 || escape(cp) != null) {
        break;
      }
      index += Character.isSupplementaryCodePoint(cp) ? 2 : 1;
    }
    return index;
  }

  private static String escapeSlow(String s, int index) {
    int end = s.length();

    // Get a destination buffer and setup some loop variables.
    char[] dest = DEST_TL.get();
    int destIndex = 0;
    int unescapedChunkStart = 0;

    while (index < end) {
      int cp = codePointAt(s, index, end);
      if (cp < 0) {
        throw new IllegalArgumentException(
          "Trailing high surrogate at end of input");
      }
      // It is possible for this to return null because nextEscapeIndex() may
      // (for performance reasons) yield some false positives but it must never
      // give false negatives.
      char[] escaped = escape(cp);
      int nextIndex = index + (Character.isSupplementaryCodePoint(cp) ? 2 : 1);
      if (escaped != null) {
        int charsSkipped = index - unescapedChunkStart;

        // This is the size needed to add the replacement, not the full
        // size needed by the string.  We only regrow when we absolutely must.
        int sizeNeeded = destIndex + charsSkipped + escaped.length;
        if (dest.length < sizeNeeded) {
          int destLength = sizeNeeded + (end - index) + DEST_PAD;
          dest = growBuffer(dest, destIndex, destLength);
        }
        // If we have skipped any characters, we need to copy them now.
        if (charsSkipped > 0) {
          s.getChars(unescapedChunkStart, index, dest, destIndex);
          destIndex += charsSkipped;
        }
        if (escaped.length > 0) {
          System.arraycopy(escaped, 0, dest, destIndex, escaped.length);
          destIndex += escaped.length;
        }
        // If we dealt with an escaped character, reset the unescaped range.
        unescapedChunkStart = nextIndex;
      }
      index = nextEscapeIndex(s, nextIndex, end);
    }

    // Process trailing unescaped characters - no need to account for escaped
    // length or padding the allocation.
    int charsSkipped = end - unescapedChunkStart;
    if (charsSkipped > 0) {
      int endIndex = destIndex + charsSkipped;
      if (dest.length < endIndex) {
        dest = growBuffer(dest, destIndex, endIndex);
      }
      s.getChars(unescapedChunkStart, end, dest, destIndex);
      destIndex = endIndex;
    }
    return new String(dest, 0, destIndex);
  }

}
