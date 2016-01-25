package kz.greetgo.depinject.mvc.error;

import java.util.Map;

public class NoPathParam extends RuntimeException {
  public final String pathParamName;
  public final Map<String, String> pathParams;

  public NoPathParam(String pathParamName, Map<String, String> pathParams) {
    super("No path param " + pathParamName + " in " + pathParams);
    this.pathParamName = pathParamName;
    this.pathParams = pathParams;
  }
}
