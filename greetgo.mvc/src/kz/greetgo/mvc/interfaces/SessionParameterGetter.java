package kz.greetgo.mvc.interfaces;

import kz.greetgo.mvc.annotations.Json;

import java.lang.reflect.Type;

/**
 * Used to access to session parameter
 */
public interface SessionParameterGetter {

  /**
   * Controller method parameter context
   */
  interface ParameterContext {
    /**
     * Returns parameter name
     *
     * @return parameter name
     */
    String parameterName();

    /**
     * Returns type of parameter
     *
     * @return type of parameter
     */
    Type expectedReturnType();

    /**
     * Returns json annotation of parameter or <code>null</code>
     *
     * @return json annotation of parameter or <code>null</code>
     */
    Json json();
  }

  /**
   * Called when session parameter value is needed
   *
   * @param context session parameter context
   * @param tunnel  request tunnel, witch may be used to access to session
   * @return session parameter value. Must be instance of <code>expectedParameterReturnType</code>
   */
  Object getSessionParameter(ParameterContext context, RequestTunnel tunnel);
}
