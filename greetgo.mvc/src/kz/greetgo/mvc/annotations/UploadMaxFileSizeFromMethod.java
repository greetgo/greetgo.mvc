package kz.greetgo.mvc.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UploadMaxFileSizeFromMethod {
  /**
   * Method must return long, int or String. If method returns string then string converts like in following table:
   * <table summary=""><tr><td colspan="2">
   * Examples:
   * </td></tr>
   * <tr><td>10_000</td><td>--&gt;</td><td> 10000 bytes</td></tr>
   * <tr><td>103</td><td>--&gt;</td><td> 103 bytes</td></tr>
   * <tr><td>10k  </td> <td>--&gt;</td><td>10*1024 bytes</td></tr>
   * <tr><td>11K  </td> <td>--&gt;</td><td>11*1024 bytes</td></tr>
   * <tr><td>13KB </td> <td>--&gt;</td><td>13*1024 bytes</td></tr>
   * <tr><td>15M  </td> <td>--&gt;</td><td>15*1024*1024 bytes</td></tr>
   * <tr><td>17G  </td> <td>--&gt;</td><td>17*1024*1024*1024 bytes</td></tr>
   * </table>
   *
   * @return string representation of amount of bytes
   */
  String value();
}
