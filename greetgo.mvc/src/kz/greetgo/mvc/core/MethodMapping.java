package kz.greetgo.mvc.core;

import kz.greetgo.mvc.annotations.on_methods.OnConnect;
import kz.greetgo.mvc.annotations.on_methods.OnDelete;
import kz.greetgo.mvc.annotations.on_methods.OnGet;
import kz.greetgo.mvc.annotations.on_methods.OnHead;
import kz.greetgo.mvc.annotations.on_methods.OnMove;
import kz.greetgo.mvc.annotations.on_methods.OnOptions;
import kz.greetgo.mvc.annotations.on_methods.OnPost;
import kz.greetgo.mvc.annotations.on_methods.OnPri;
import kz.greetgo.mvc.annotations.on_methods.OnProxy;
import kz.greetgo.mvc.annotations.on_methods.OnPut;
import kz.greetgo.mvc.annotations.on_methods.OnTrace;

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
      OnPost a = method.getAnnotation(OnPost.class);
      if (a != null) ret.add(new MethodMapping(RequestMethod.POST, a.value(), method, a));
    }
    {
      OnGet a = method.getAnnotation(OnGet.class);
      if (a != null) ret.add(new MethodMapping(RequestMethod.GET, a.value(), method, a));
    }
    {
      OnConnect a = method.getAnnotation(OnConnect.class);
      if (a != null) ret.add(new MethodMapping(RequestMethod.CONNECT, a.value(), method, a));
    }
    {
      OnDelete a = method.getAnnotation(OnDelete.class);
      if (a != null) ret.add(new MethodMapping(RequestMethod.DELETE, a.value(), method, a));
    }
    {
      OnHead a = method.getAnnotation(OnHead.class);
      if (a != null) ret.add(new MethodMapping(RequestMethod.HEAD, a.value(), method, a));
    }
    {
      OnMove a = method.getAnnotation(OnMove.class);
      if (a != null) ret.add(new MethodMapping(RequestMethod.MOVE, a.value(), method, a));
    }
    {
      OnOptions a = method.getAnnotation(OnOptions.class);
      if (a != null) ret.add(new MethodMapping(RequestMethod.OPTIONS, a.value(), method, a));
    }
    {
      OnPri a = method.getAnnotation(OnPri.class);
      if (a != null) ret.add(new MethodMapping(RequestMethod.PRI, a.value(), method, a));
    }
    {
      OnProxy a = method.getAnnotation(OnProxy.class);
      if (a != null) ret.add(new MethodMapping(RequestMethod.PROXY, a.value(), method, a));
    }
    {
      OnPut a = method.getAnnotation(OnPut.class);
      if (a != null) ret.add(new MethodMapping(RequestMethod.PUT, a.value(), method, a));
    }
    {
      OnTrace a = method.getAnnotation(OnTrace.class);
      if (a != null) ret.add(new MethodMapping(RequestMethod.TRACE, a.value(), method, a));
    }

    return ret;
  }
}
