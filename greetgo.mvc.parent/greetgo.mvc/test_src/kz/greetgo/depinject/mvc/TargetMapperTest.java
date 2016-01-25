package kz.greetgo.depinject.mvc;

import kz.greetgo.depinject.mvc.MappingResult;
import kz.greetgo.depinject.mvc.TargetMapper;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

public class TargetMapperTest {

  @Test
  public void catchTarget_simple() throws Exception {
    TargetMapper targetMapper = new TargetMapper("/asd/dsa");

    {
      final MappingResult mappingResult = targetMapper.mapTarget("/asd");
      assertThat(mappingResult).isNotNull();
      assertThat(mappingResult.ok()).isFalse();
    }
    {
      final MappingResult mappingResult = targetMapper.mapTarget("/asd/dsa");
      assertThat(mappingResult).isNotNull();
      assertThat(mappingResult.ok()).isTrue();
    }
  }

  @Test
  public void catchTarget_params() throws Exception {
    TargetMapper targetMapper = new TargetMapper("/asd/{clientId}/cool-phase/{phone}");

    {
      final MappingResult mappingResult = targetMapper.mapTarget("/left");
      assertThat(mappingResult).isNotNull();
      assertThat(mappingResult.ok()).isFalse();
    }
    {
      final MappingResult mappingResult = targetMapper.mapTarget("/asd/4321554/cool-phase/87071112233");
      assertThat(mappingResult).isNotNull();
      assertThat(mappingResult.ok()).isTrue();
      assertThat(mappingResult.getParam("clientId")).isEqualTo("4321554");
      assertThat(mappingResult.getParam("phone")).isEqualTo("87071112233");
    }
    {
      final MappingResult mappingResult = targetMapper.mapTarget("/asd//cool-phase/87071112233");
      assertThat(mappingResult).isNotNull();
      assertThat(mappingResult.ok()).isTrue();
      assertThat(mappingResult.getParam("clientId")).isEmpty();
      assertThat(mappingResult.getParam("phone")).isEqualTo("87071112233");
    }
  }
}