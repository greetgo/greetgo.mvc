package kz.greetgo.mvc.util;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JsonUtilTest {
  @SuppressWarnings("SameParameterValue")
  private static Type getType(Class<?> aClass, String methodName) {
    for (Method method : aClass.getMethods()) {
      if (method.getName().equals(methodName)) return method.getGenericParameterTypes()[0];
    }
    throw new RuntimeException("No found public method " + methodName + " in class " + aClass.getName());
  }

  public static class Client {
    public String id, name;
  }

  @SuppressWarnings("unused")
  static class TypeSource {
    public void client(Client client) {}

    public void listClient(List<Client> listClient) {}

    public void listExtendsClient(List<? extends Client> listClient) {}
  }

  @DataProvider
  public Object[][] convertStrsToType_client_DataSource() {
    return new Object[][]{
      new Object[]{"{\"id\":\"id1\",\"name\":\"name1\"}", "left param"},
      new Object[]{"[{\"id\":\"id1\",\"name\":\"name1\"},{\"id\":\"id2\",\"name\":\"name2\"}]", "left param"},
      new Object[]{null, null},
      new Object[]{"", null},
    };
  }

  @Test(dataProvider = "convertStrsToType_client_DataSource")
  public void convertStrsToType_client(String string1, String string2) {
    Type type = getType(TypeSource.class, "client");

    //
    //
    Object o = JsonUtil.convertStrsToType(new String[]{string1, string2}, type);
    //
    //

    if (string1 == null || string1.length() == 0) {
      assertThat(o).isNull();
      return;
    }

    assertThat(o).isInstanceOf(Client.class);

    Client actual = (Client) o;
    assertThat(actual.id).isEqualTo("id1");
    assertThat(actual.name).isEqualTo("name1");
  }

  @Test
  public void convertStrsToType_listClient() {
    Type type = getType(TypeSource.class, "listClient");

    String string1 = "[{\"id\":\"id1\",\"name\":\"name1\"},{\"id\":\"id2\",\"name\":\"name2\"}]";
    String string2 = "{\"id\":\"id3\",\"name\":\"name3\"}";

    //
    //
    Object o = JsonUtil.convertStrsToType(new String[]{string1, string2}, type);
    //
    //

    assertThat(o).isInstanceOf(List.class);

    @SuppressWarnings("unchecked")
    List<Client> actual = (List<Client>) o;
    assertThat(actual).hasSize(3);

    assertThat(actual.get(0).id).isEqualTo("id1");
    assertThat(actual.get(0).name).isEqualTo("name1");
    assertThat(actual.get(1).id).isEqualTo("id2");
    assertThat(actual.get(1).name).isEqualTo("name2");
    assertThat(actual.get(2).id).isEqualTo("id3");
    assertThat(actual.get(2).name).isEqualTo("name3");
  }

  @Test
  public void convertStrsToType_listClient_null() {
    Type type = getType(TypeSource.class, "listClient");

    //
    //
    Object o = JsonUtil.convertStrsToType(new String[]{null, null}, type);
    //
    //

    assertThat(o).isInstanceOf(List.class);

    @SuppressWarnings("unchecked")
    List<Client> actual = (List<Client>) o;
    assertThat(actual).isEmpty();
  }


  @Test
  public void convertStrsToType_listClient_empty() {
    Type type = getType(TypeSource.class, "listClient");

    //
    //
    Object o = JsonUtil.convertStrsToType(new String[]{"    "}, type);
    //
    //

    assertThat(o).isInstanceOf(List.class);

    @SuppressWarnings("unchecked")
    List<Client> actual = (List<Client>) o;
    assertThat(actual).isEmpty();
  }

  @Test
  public void convertStrsToType_listClient_empty1() {
    Type type = getType(TypeSource.class, "listExtendsClient");

    //
    //
    Object o = JsonUtil.convertStrsToType(new String[]{"    "}, type);
    //
    //

    assertThat(o).isInstanceOf(List.class);

    @SuppressWarnings("unchecked")
    List<Client> actual = (List<Client>) o;
    assertThat(actual).isEmpty();
  }
}
