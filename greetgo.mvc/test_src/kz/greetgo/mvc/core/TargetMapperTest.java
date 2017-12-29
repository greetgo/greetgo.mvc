package kz.greetgo.mvc.core;

import kz.greetgo.mvc.annotations.MethodFilter;
import kz.greetgo.mvc.errors.NoPathParam;
import kz.greetgo.mvc.interfaces.MappingResult;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.mvc.utils.TestTunnel;
import org.testng.annotations.Test;

import java.lang.annotation.Annotation;

import static kz.greetgo.mvc.core.RequestMethod.DELETE;
import static kz.greetgo.mvc.core.RequestMethod.GET;
import static kz.greetgo.mvc.core.RequestMethod.POST;
import static org.fest.assertions.api.Assertions.assertThat;

public class TargetMapperTest {

  @Test
  public void catchTarget_simple() throws Exception {
    TargetMapper targetMapper = new TargetMapper("/asd/dsa", null);

    {
      final MappingResult mappingResult = targetMapper.mapTarget(tunnel("/asd"));
      assertThat(mappingResult).isNotNull();
      assertThat(mappingResult.ok()).isFalse();
    }
    {
      final MappingResult mappingResult = targetMapper.mapTarget(tunnel("/asd/dsa"));
      assertThat(mappingResult).isNotNull();
      assertThat(mappingResult.ok()).isTrue();
    }
  }

  private static RequestTunnel tunnel(String target) {
    TestTunnel tunnel = new TestTunnel();
    tunnel.target = target;
    return tunnel;
  }

  private static RequestTunnel tunnel(String target, RequestMethod method) {
    TestTunnel tunnel = new TestTunnel();
    tunnel.target = target;
    tunnel.requestMethod = method;
    return tunnel;
  }

  @Test
  public void catchTarget_params() throws Exception {
    TargetMapper targetMapper = new TargetMapper("/asd/{clientId}/cool-phase/{phone}", null);

    {
      final MappingResult mappingResult = targetMapper.mapTarget(tunnel("/left"));
      assertThat(mappingResult).isNotNull();
      assertThat(mappingResult.ok()).isFalse();
    }
    {
      final MappingResult mappingResult = targetMapper.mapTarget(tunnel("/asd/4321554/cool-phase/87071112233"));
      assertThat(mappingResult).isNotNull();
      assertThat(mappingResult.ok()).isTrue();
      assertThat(mappingResult.getParam("clientId")).isEqualTo("4321554");
      assertThat(mappingResult.getParam("phone")).isEqualTo("87071112233");
    }
    {
      final MappingResult mappingResult = targetMapper.mapTarget(tunnel("/asd//cool-phase/87071112233"));
      assertThat(mappingResult).isNotNull();
      assertThat(mappingResult.ok()).isTrue();
      assertThat(mappingResult.getParam("clientId")).isEmpty();
      assertThat(mappingResult.getParam("phone")).isEqualTo("87071112233");
    }
  }

  private static MethodFilter methodFilter(RequestMethod... methods) {
    return new MethodFilter() {
      @Override
      public Class<? extends Annotation> annotationType() {
        return null;
      }

      @Override
      public RequestMethod[] value() {
        return methods;
      }
    };
  }

  @Test
  public void catchTarget_requestMethods() throws Exception {

    TargetMapper targetMapper = new TargetMapper("/asd1", methodFilter(GET, DELETE));

    {
      final MappingResult mappingResult = targetMapper.mapTarget(tunnel("/asd1", GET));
      assertThat(mappingResult).isNotNull();
      assertThat(mappingResult.ok()).isTrue();
    }

    {
      final MappingResult mappingResult = targetMapper.mapTarget(tunnel("/asd2", POST));
      assertThat(mappingResult).isNotNull();
      assertThat(mappingResult.ok()).isFalse();
    }
  }

  @Test
  public void parseManyParams() throws Exception {
    TargetMapper targetMapper = new TargetMapper("/page/{pageNumber}/pageSize/{pageSize}/sort/{direction}/column/{nameOfColumn}", null);


    final MappingResult mappingResult = targetMapper.mapTarget(tunnel(
      "/page/6/pageSize/3/sort/asc/column/date"
    ));
    assertThat(mappingResult).isNotNull();
    assertThat(mappingResult.ok()).isTrue();
    assertThat(mappingResult.getParam("pageNumber")).isEqualTo("6");
    assertThat(mappingResult.getParam("pageSize")).isEqualTo("3");
    assertThat(mappingResult.getParam("direction")).isEqualTo("asc");
    assertThat(mappingResult.getParam("nameOfColumn")).isEqualTo("date");

  }

  @Test(expectedExceptions = NoPathParam.class)
  public void throws_NoPathParam() throws Exception {
    TargetMapper targetMapper = new TargetMapper("/page/{pageNumber}/pageSize/{pageSize}/sort/{direction}/column/{nameOfColumn}", null);


    final MappingResult mappingResult = targetMapper.mapTarget(tunnel(
      "/page/6/pageSize/3/sort/asc/column/date"
    ));

    mappingResult.getParam("asd");

  }
}
