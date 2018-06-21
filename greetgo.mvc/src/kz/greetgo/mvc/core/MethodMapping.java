package kz.greetgo.mvc.core;

import kz.greetgo.mvc.annotations.on_methods.onCONNECT;
import kz.greetgo.mvc.annotations.on_methods.onDELETE;
import kz.greetgo.mvc.annotations.on_methods.onGET;
import kz.greetgo.mvc.annotations.on_methods.onHEAD;
import kz.greetgo.mvc.annotations.on_methods.onMOVE;
import kz.greetgo.mvc.annotations.on_methods.onOPTIONS;
import kz.greetgo.mvc.annotations.on_methods.onPOST;
import kz.greetgo.mvc.annotations.on_methods.onPRI;
import kz.greetgo.mvc.annotations.on_methods.onPROXY;
import kz.greetgo.mvc.annotations.on_methods.onPUT;
import kz.greetgo.mvc.annotations.on_methods.onTRACE;

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
      onPOST a = method.getAnnotation(onPOST.class);
      if (a != null) ret.add(new MethodMapping(RequestMethod.POST, a.value(), method, a));
    }
    {
      onGET a = method.getAnnotation(onGET.class);
      if (a != null) ret.add(new MethodMapping(RequestMethod.GET, a.value(), method, a));
    }
    {
      onCONNECT a = method.getAnnotation(onCONNECT.class);
      if (a != null) ret.add(new MethodMapping(RequestMethod.CONNECT, a.value(), method, a));
    }
    {
      onDELETE a = method.getAnnotation(onDELETE.class);
      if (a != null) ret.add(new MethodMapping(RequestMethod.DELETE, a.value(), method, a));
    }
    {
      onHEAD a = method.getAnnotation(onHEAD.class);
      if (a != null) ret.add(new MethodMapping(RequestMethod.HEAD, a.value(), method, a));
    }
    {
      onMOVE a = method.getAnnotation(onMOVE.class);
      if (a != null) ret.add(new MethodMapping(RequestMethod.MOVE, a.value(), method, a));
    }
    {
      onOPTIONS a = method.getAnnotation(onOPTIONS.class);
      if (a != null) ret.add(new MethodMapping(RequestMethod.OPTIONS, a.value(), method, a));
    }
    {
      onPRI a = method.getAnnotation(onPRI.class);
      if (a != null) ret.add(new MethodMapping(RequestMethod.PRI, a.value(), method, a));
    }
    {
      onPROXY a = method.getAnnotation(onPROXY.class);
      if (a != null) ret.add(new MethodMapping(RequestMethod.PROXY, a.value(), method, a));
    }
    {
      onPUT a = method.getAnnotation(onPUT.class);
      if (a != null) ret.add(new MethodMapping(RequestMethod.PUT, a.value(), method, a));
    }
    {
      onTRACE a = method.getAnnotation(onTRACE.class);
      if (a != null) ret.add(new MethodMapping(RequestMethod.TRACE, a.value(), method, a));
    }

    return ret;
  }
}
