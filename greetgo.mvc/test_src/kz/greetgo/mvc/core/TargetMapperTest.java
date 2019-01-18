package kz.greetgo.mvc.core;

import kz.greetgo.mvc.errors.NoPathParam;
import kz.greetgo.mvc.interfaces.MappingResult;
import kz.greetgo.mvc.interfaces.RequestTunnel;
import kz.greetgo.mvc.utils.TestTunnel;
import org.testng.annotations.Test;

import static kz.greetgo.mvc.core.RequestMethod.GET;
import static kz.greetgo.mvc.core.RequestMethod.POST;
import static org.fest.assertions.api.Assertions.assertThat;

public class TargetMapperTest {

  @Test
  public void catchTarget_simple() {
    TargetMapper targetMapper = new TargetMapper("/asd/dsa", GET);

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
    TestTunnel tunnel = new TestTunnel(GET);
    tunnel.target = target;
    return tunnel;
  }

  private static RequestTunnel tunnel(String target, RequestMethod method) {
    TestTunnel tunnel = new TestTunnel(method);
    tunnel.target = target;
    return tunnel;
  }

  @Test
  public void catchTarget_params() {
    TargetMapper targetMapper = new TargetMapper("/asd/{clientId}/cool-phase/{phone}", GET);

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
  public void catchTarget_requestMethods() {

    TargetMapper targetMapper = new TargetMapper("/asd1", GET);

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
  public void parseManyParams() {
    TargetMapper targetMapper = new TargetMapper("/page/{pageNumber}/pageSize/{pageSize}/sort/{direction}/column/{nameOfColumn}", GET);


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
  public void throws_NoPathParam() {
    TargetMapper targetMapper = new TargetMapper("/page/{pageNumber}/pageSize/{pageSize}/sort/{direction}/column/{nameOfColumn}", GET);


    final MappingResult mappingResult = targetMapper.mapTarget(tunnel(
      "/page/6/pageSize/3/sort/asc/column/date"
    ));

    mappingResult.getParam("asd");

  }

  @Test
  public void toTargetMapperIdentity_1() {
    String a = "/page/{pageNumber}/pageSize/{pageSize}/sort/{direction}/column/{nameOfColumn}";
    String b = "/page/*/pageSize/*/sort/*/column/*";

    assertThat(TargetMapper.toTargetMapperIdentity(a)).isEqualTo(b);
  }

  @Test
  public void toTargetMapperIdentity_2() {
    String a = "/asd";
    String b = "/asd";

    assertThat(TargetMapper.toTargetMapperIdentity(a)).isEqualTo(b);
  }

  @Test
  public void toTargetMapperIdentity_3() {
    String a = "/hello{stone}/xxx/fix{fix}s/it";
    String b = "/hello*/xxx/fix*s/it";

    assertThat(TargetMapper.toTargetMapperIdentity(a)).isEqualTo(b);
  }

  @Test
  public void toTargetMapperIdentity_4() {
    String a = "/page/{pageNumber}/pageSize/{pageSize}/sort/{direction}/column/{nameOfColumn}/DETAILS";
    String b = "/page/*/pageSize/*/sort/*/column/*/DETAILS";

    assertThat(TargetMapper.toTargetMapperIdentity(a)).isEqualTo(b);
  }

}
