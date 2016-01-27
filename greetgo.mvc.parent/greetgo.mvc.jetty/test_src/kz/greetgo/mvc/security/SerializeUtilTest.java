package kz.greetgo.mvc.security;

import org.testng.annotations.Test;

import java.io.Serializable;

import static org.fest.assertions.api.Assertions.assertThat;

public class SerializeUtilTest {

  public static class TestSerialize implements Serializable {
    public String surname, name;
    public int age;
    public Long weight;
  }

  @Test
  public void serialize_deserialize() throws Exception {

    final TestSerialize object = new TestSerialize();
    object.surname = "the test surname for test";
    object.name = "Для тестов";
    object.age = 3487;
    object.weight = 12987L;

    //
    //
    final byte[] bytes = SerializeUtil.serialize(object);
    final TestSerialize actual = (TestSerialize) SerializeUtil.deserialize(bytes);
    //
    //

    assertThat(actual).isNotNull();
    //noinspection ConstantConditions
    assertThat(actual.surname).isEqualTo(object.surname);
    assertThat(actual.name).isEqualTo(object.name);
    assertThat(actual.age).isEqualTo(object.age);
    assertThat(actual.weight).isEqualTo(object.weight);

  }

  @Test
  public void deserialize_null() throws Exception {
    //
    //
    final Object actual = SerializeUtil.deserialize(null);
    //
    //

    assertThat(actual).isNull();
  }

  @Test
  public void serialize_null() throws Exception {
    //
    //
    final byte[] bytes = SerializeUtil.serialize(null);
    //
    //

    assertThat(bytes).isNull();
  }

  @Test
  public void deserialize_left_bytes() throws Exception {
    //
    //
    final Object actual = SerializeUtil.deserialize(new byte[]{(byte) 1, (byte) 2});
    //
    //

    assertThat(actual).isNull();
  }

  @Test
  public void deserialize_empty_bytes() throws Exception {
    //
    //
    final Object actual = SerializeUtil.deserialize(new byte[]{});
    //
    //

    assertThat(actual).isNull();
  }

  @Test
  public void deserialize_many_left_bytes1() throws Exception {
    byte[] bytes = new byte[1024 * 2];
    for (int i = 0, n = bytes.length; i < n; i++) {
      bytes[i] = (byte) (i + 100);
    }

    //
    //
    final Object actual = SerializeUtil.deserialize(bytes);
    //
    //

    assertThat(actual).isNull();
  }

  @Test
  public void deserialize_many_left_bytes2() throws Exception {
    byte[] bytes = new byte[1024];
    for (int i = 0, n = bytes.length; i < n; i++) {
      bytes[i] = (byte) (i - 100);
    }

    //
    //
    final Object actual = SerializeUtil.deserialize(bytes);
    //
    //

    assertThat(actual).isNull();
  }
}