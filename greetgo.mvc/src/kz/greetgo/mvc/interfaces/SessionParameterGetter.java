package kz.greetgo.mvc.interfaces;

/**
 * Used to access to session parameter
 */
public interface SessionParameterGetter {

  /**
   * Called when session parameter value is needed
   *
   * @param context session parameter context
   * @param tunnel  request tunnel, witch may be used to access to session
   * @return session parameter value. Must be instance of <code>expectedParameterReturnType</code>
   */
  Object getSessionParameter(ParameterContext context, RequestTunnel tunnel);
}
