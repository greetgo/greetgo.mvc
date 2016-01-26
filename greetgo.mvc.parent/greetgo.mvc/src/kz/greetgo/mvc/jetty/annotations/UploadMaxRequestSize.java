package kz.greetgo.mvc.jetty.annotations;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UploadMaxRequestSize {
  /**
   * <table><tr><td colspan="2">
   * Examples:
   * </td></tr>
   * <tr><td>10_000</td><td>--&gt;</td><td> 10000 байт</td></tr>
   * <tr><td>103</td><td>--&gt;</td><td> 103 байта</td></tr>
   * <tr><td>10k  </td> <td>--&gt;</td><td>10*1024 байт</td></tr>
   * <tr><td>11K  </td> <td>--&gt;</td><td>11*1024 байт</td></tr>
   * <tr><td>13KB </td> <td>--&gt;</td><td>13*1024 байт</td></tr>
   * <tr><td>15M  </td> <td>--&gt;</td><td>15*1024*1024 байт</td></tr>
   * <tr><td>17G  </td> <td>--&gt;</td><td>17*1024*1024*1024 байт</td></tr>
   * </table>
   *
   * @return string representation of amount of bytes
   */
  String value();
}
