package kz.greetgo.mvc.core;

import kz.greetgo.mvc.annotations.on_methods.HttpCONNECT;
import kz.greetgo.mvc.annotations.on_methods.HttpDELETE;
import kz.greetgo.mvc.annotations.on_methods.HttpGET;
import kz.greetgo.mvc.annotations.on_methods.HttpHEAD;
import kz.greetgo.mvc.annotations.on_methods.HttpMOVE;
import kz.greetgo.mvc.annotations.on_methods.HttpOPTIONS;
import kz.greetgo.mvc.annotations.on_methods.HttpPOST;
import kz.greetgo.mvc.annotations.on_methods.HttpPRI;
import kz.greetgo.mvc.annotations.on_methods.HttpPROXY;
import kz.greetgo.mvc.annotations.on_methods.HttpPUT;
import kz.greetgo.mvc.annotations.on_methods.HttpTRACE;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MethodMapping {
  public final RequestMethod httpMethod;
  public final String[] mappingStrArray;
  public final Method method;
  public final Annotation annotation;

  public MethodMapping(RequestMethod httpMethod, String[] mappingStrArray, Method method, Annotation annotation) {
    this.httpMethod = httpMethod;
    this.mappingStrArray = mappingStrArray;
    this.method = method;
    this.annotation = annotation;
  }

  public static List<MethodMapping> extract(final Method method) {

    final List<MethodMapping> ret = new ArrayList<>();
    {
      HttpPOST a = method.getAnnotation(HttpPOST.class);
      if (a != null) ret.add(new MethodMapping(RequestMethod.POST, a.value(), method, a));
    }
    {
      HttpGET a = method.getAnnotation(HttpGET.class);
      if (a != null) ret.add(new MethodMapping(RequestMethod.GET, a.value(), method, a));
    }
    {
      HttpCONNECT a = method.getAnnotation(HttpCONNECT.class);
      if (a != null) ret.add(new MethodMapping(RequestMethod.CONNECT, a.value(), method, a));
    }
    {
      HttpDELETE a = method.getAnnotation(HttpDELETE.class);
      if (a != null) ret.add(new MethodMapping(RequestMethod.DELETE, a.value(), method, a));
    }
    {
      HttpHEAD a = method.getAnnotation(HttpHEAD.class);
      if (a != null) ret.add(new MethodMapping(RequestMethod.HEAD, a.value(), method, a));
    }
    {
      HttpMOVE a = method.getAnnotation(HttpMOVE.class);
      if (a != null) ret.add(new MethodMapping(RequestMethod.MOVE, a.value(), method, a));
    }
    {
      HttpOPTIONS a = method.getAnnotation(HttpOPTIONS.class);
      if (a != null) ret.add(new MethodMapping(RequestMethod.OPTIONS, a.value(), method, a));
    }
    {
      HttpPRI a = method.getAnnotation(HttpPRI.class);
      if (a != null) ret.add(new MethodMapping(RequestMethod.PRI, a.value(), method, a));
    }
    {
      HttpPROXY a = method.getAnnotation(HttpPROXY.class);
      if (a != null) ret.add(new MethodMapping(RequestMethod.PROXY, a.value(), method, a));
    }
    {
      HttpPUT a = method.getAnnotation(HttpPUT.class);
      if (a != null) ret.add(new MethodMapping(RequestMethod.PUT, a.value(), method, a));
    }
    {
      HttpTRACE a = method.getAnnotation(HttpTRACE.class);
      if (a != null) ret.add(new MethodMapping(RequestMethod.TRACE, a.value(), method, a));
    }

    return ret;
  }
}
