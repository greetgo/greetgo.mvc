package kz.greetgo.mvc.annotations;

import java.lang.annotation.*;

/**
 * Takes parameter from cookies.
 */
@Documented
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ParCookie {
  /**
   * @return cookie name
   */
  String value();

  /**
   * <p>
   * If equals to <code>false</code>, then parameter value has been converted using method
   * {@link kz.greetgo.mvc.util.CookieUtil#strToObject(String)}
   * </p>
   * <p>
   * If equals to <code>true</code>, then parameter value takes as is.
   * This field can be <code>true</code> only for arguments with type <code>String</code>
   * </p>
   *
   * @return use as is - <code>true</code>; or no - <code>false</code>
   */
  boolean asIs() default false;
}
