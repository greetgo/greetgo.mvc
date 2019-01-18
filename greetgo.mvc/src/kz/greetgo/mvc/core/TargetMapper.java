package kz.greetgo.mvc.core;

import kz.greetgo.mvc.errors.AsteriskInTargetMapper;
import kz.greetgo.mvc.errors.NoPathParam;
import kz.greetgo.mvc.interfaces.MappingResult;
import kz.greetgo.mvc.interfaces.RequestTunnel;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TargetMapper {

  private final List<String> namesForGroups;
  private final Pattern mappingPattern;
  private final String targetMapping;
  private final RequestMethod requestMethod;
  private final MappingIdentity mappingIdentity;

  public MappingIdentity getMappingIdentity() {
    return mappingIdentity;
  }

  public TargetMapper(String targetMapping, RequestMethod requestMethod) {
    Objects.requireNonNull(requestMethod);
    this.targetMapping = targetMapping;
    this.requestMethod = requestMethod;
    this.mappingIdentity = new MappingIdentity() {
      final String mappingIdentity = toTargetMapperIdentity(targetMapping);

      @Override
      public String targetMapping() {
        return mappingIdentity;
      }

      @Override
      public RequestMethod requestMethod() {
        return requestMethod;
      }
    };

    StringBuilder pattern = new StringBuilder();
    int pos = 0;
    List<String> namesForGroups = new ArrayList<>();

    while (true) {

      final int open = targetMapping.indexOf('{', pos);
      if (open < 0) {
        pattern.append(quote(targetMapping.substring(pos)));
        break;
      }

      final int close = targetMapping.indexOf('}', open);
      if (close < 0) {
        pattern.append(quote(targetMapping.substring(pos)));
        break;
      }

      pattern.append(quote(targetMapping.substring(pos, open)));

      String name = targetMapping.substring(open + 1, close).trim();
      if (name.endsWith("+")) {
        name = name.substring(0, name.length() - 1).trim();
        pattern.append("(.+)");
      } else {
        pattern.append("(.*)");
      }
      namesForGroups.add(name);

      pos = close + 1;

    }

    this.namesForGroups = Collections.unmodifiableList(namesForGroups);
    this.mappingPattern = Pattern.compile(pattern.toString());
  }

  static String toTargetMapperIdentity(String targetMapper) {

    if (targetMapper.contains("*")) {
      throw new AsteriskInTargetMapper(targetMapper);
    }

    StringBuilder ret = new StringBuilder(targetMapper.length());

    int lastPos = 0;

    while (true) {

      int openIndex = targetMapper.indexOf('{', lastPos);
      if (openIndex < 0) break;

      int closeIndex = targetMapper.indexOf('}', openIndex);
      if (closeIndex < 0) break;

      ret.append(targetMapper, lastPos, openIndex).append('*');
      lastPos = closeIndex + 1;
    }

    ret.append(targetMapper, lastPos, targetMapper.length());

    return ret.toString();
  }

  public String infoStr() {
    return targetMapping + " for " + requestMethod;
  }

  private static StringBuilder quote(String str) {
    StringBuilder ret = new StringBuilder();
    for (int i = 0, C = str.length(); i < C; i++) {
      final char c = str.charAt(i);
      if (c == '-' || c == '\\' || c == '.' || c == '+' || c == '(' || c == ')' || c == '[' || c == ']') {
        ret.append('\\').append(c);
      } else if (c == '*') {
        ret.append(".*");
      } else {
        ret.append(c);
      }
    }
    return ret;
  }

  private static final MappingResult UNMAPPED_RESULT = new MappingResult() {
    @Override
    public boolean ok() {
      return false;
    }

    @Override
    public String getParam(String name) {
      throw new UnsupportedOperationException();
    }
  };

  public MappingResult mapTarget(RequestTunnel tunnel) {

    final Matcher matcher = mappingPattern.matcher(tunnel.getTarget());
    if (!matcher.matches()) return UNMAPPED_RESULT;

    if (!isMethodCorrect(tunnel.getRequestMethod())) return UNMAPPED_RESULT;

    Map<String, String> params = new HashMap<>();

    for (int i = 0, n = namesForGroups.size(); i < n; i++) {
      params.put(namesForGroups.get(i), matcher.group(i + 1));
    }

    final Map<String, String> unmodifiableParams = Collections.unmodifiableMap(params);

    return new MappingResult() {
      @Override
      public boolean ok() {
        return true;
      }

      @Override
      public String getParam(String name) {
        if (unmodifiableParams.containsKey(name)) return unmodifiableParams.get(name);
        throw new NoPathParam(name, unmodifiableParams);
      }
    };
  }

  private boolean isMethodCorrect(RequestMethod method) {
    return requestMethod == method;
  }
}
