package kz.greetgo.mvc.core;

import kz.greetgo.mvc.annotations.MethodFilter;
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

  @Test
  public void catchTarget_requestMethods() throws Exception {

    MethodFilter mf = new MethodFilter() {
      @Override
      public Class<? extends Annotation> annotationType() {
        return null;
      }

      @Override
      public RequestMethod[] value() {
        return new RequestMethod[]{GET, DELETE};
      }
    };

    TargetMapper targetMapper = new TargetMapper("/asd", mf);

    {
      final MappingResult mappingResult = targetMapper.mapTarget(tunnel("/asd", GET));
      assertThat(mappingResult).isNotNull();
      assertThat(mappingResult.ok()).isTrue();
    }

    {
      final MappingResult mappingResult = targetMapper.mapTarget(tunnel("/asd", POST));
      assertThat(mappingResult).isNotNull();
      assertThat(mappingResult.ok()).isFalse();
    }
  }
}