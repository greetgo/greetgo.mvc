package kz.greetgo.mvc.util.setters;

import kz.greetgo.mvc.annotations.SkipParameter;
import kz.greetgo.util.RND;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;

public class FieldSettersCreatorTest {

  @SuppressWarnings("unused")
  public static class FieldClassStr {

    public String strField1, strField2;

    @SkipParameter
    public String strField3, strField4;
    public String strField5;

    public void setStrField2(String strField2) {
      this.strField2 = strField2 + " from setter";
    }

    public void setStrField4(String strField4) {
      this.strField4 = strField4;
    }

    @SkipParameter
    public void setStrField5(String strField5) {
      this.strField5 = strField5;
    }
  }

  @Test
  public void str() throws Exception {
    FieldSetters fieldSetters = FieldSettersCreator.extractFrom(FieldClassStr.class);
    assertThat(fieldSetters).isNotNull();
    assertThat(fieldSetters.names()).contains("strField1", "strField2");
    assertThat(fieldSetters.names()).hasSize(2);

    FieldClassStr model = new FieldClassStr();

    FieldSetter strField1 = fieldSetters.get("strField1");
    assertThat(strField1).isNotNull();

    String str1 = RND.str(10), str2 = RND.str(10);

    strField1.setFromStrs(model, new String[]{str1, "left value"});

    assertThat(model.strField1).isEqualTo(str1);

    FieldSetter strField2 = fieldSetters.get("strField2");
    strField2.setFromStrs(model, new String[]{str2, "left value"});

    assertThat(model.strField2).isEqualTo(str2 + " from setter");

  }

  public static class FieldClassStrs {

    public List<String> strs1 = new ArrayList<>();
    public final List<String> strs2 = new ArrayList<>();

    private final List<String> strs3 = new ArrayList<>();
    public List<String> strs4 = new ArrayList<>();

    public List<String> getStrs3() {
      return strs3;
    }

    @SuppressWarnings("unused")
    public void setStrs4(List<String> strs4) {
      this.strs4 = strs4;
      this.strs4.add("from setter");
    }
  }

  @Test
  public void strs() throws Exception {
    FieldSetters fieldSetters = FieldSettersCreator.extractFrom(FieldClassStrs.class);
    assertThat(fieldSetters).isNotNull();
    assertThat(fieldSetters.names()).contains("strs1", "strs2", "strs3", "strs4");
    assertThat(fieldSetters.names()).hasSize(4);

    {
      FieldSetter setter = fieldSetters.get("strs1");
      assertThat(setter).isNotNull();
      FieldClassStrs model = new FieldClassStrs();
      String str1 = RND.str(10), str2 = RND.str(10), str3 = RND.str(10);
      setter.setFromStrs(model, new String[]{str1, str2, str3});
      assertThat(model.strs1).containsExactly(str1, str2, str3);
    }
    {
      FieldSetter setter = fieldSetters.get("strs2");
      assertThat(setter).isNotNull();
      FieldClassStrs model = new FieldClassStrs();
      String str1 = RND.str(10), str2 = RND.str(10), str3 = RND.str(10);
      setter.setFromStrs(model, new String[]{str1, str2, str3});
      assertThat(model.strs2).containsExactly(str1, str2, str3);
    }
    {
      FieldSetter setter = fieldSetters.get("strs3");
      assertThat(setter).isNotNull();
      FieldClassStrs model = new FieldClassStrs();
      String str1 = RND.str(10), str2 = RND.str(10), str3 = RND.str(10);
      setter.setFromStrs(model, new String[]{str1, str2, str3});
      assertThat(model.getStrs3()).containsExactly(str1, str2, str3);
    }
    {
      FieldSetter setter = fieldSetters.get("strs4");
      assertThat(setter).isNotNull();
      FieldClassStrs model = new FieldClassStrs();
      String str1 = RND.str(10), str2 = RND.str(10), str3 = RND.str(10);
      setter.setFromStrs(model, new String[]{str1, str2, str3});
      assertThat(model.strs4).containsExactly(str1, str2, str3, "from setter");
    }
  }

  public static class FieldClassNumbers {
    public int intField;
    public Integer integerField;
    public long longField;
    public Long longBoxedField;
    public boolean booleanField;
    public Boolean booleanBoxedField;
  }

  @Test
  public void numbers() throws Exception {
    FieldSetters fieldSetters = FieldSettersCreator.extractFrom(FieldClassNumbers.class);
    assertThat(fieldSetters).isNotNull();
    assertThat(fieldSetters.names()).contains(
      "intField", "integerField",
      "longField", "longBoxedField",
      "booleanField", "booleanBoxedField"
    );
    assertThat(fieldSetters.names()).hasSize(6);

    {
      FieldSetter setter = fieldSetters.get("intField");
      assertThat(setter).isNotNull();
      FieldClassNumbers numbers = new FieldClassNumbers();
      setter.setFromStrs(numbers, new String[]{"213", "left value"});
      assertThat(numbers.intField).isEqualTo(213);
    }

    {
      FieldSetter setter = fieldSetters.get("integerField");
      assertThat(setter).isNotNull();
      FieldClassNumbers numbers = new FieldClassNumbers();
      setter.setFromStrs(numbers, new String[]{"2131", "left value"});
      assertThat(numbers.integerField).isEqualTo(2131);
    }

    {
      FieldSetter setter = fieldSetters.get("longField");
      assertThat(setter).isNotNull();
      FieldClassNumbers numbers = new FieldClassNumbers();
      setter.setFromStrs(numbers, new String[]{"97811", "left value"});
      assertThat(numbers.longField).isEqualTo(97811);
    }

    {
      FieldSetter setter = fieldSetters.get("longBoxedField");
      assertThat(setter).isNotNull();
      FieldClassNumbers numbers = new FieldClassNumbers();
      setter.setFromStrs(numbers, new String[]{"76487658764", "left value"});
      assertThat(numbers.longBoxedField).isEqualTo(76487658764L);
    }

    {
      FieldSetter setter = fieldSetters.get("booleanField");
      assertThat(setter).isNotNull();
      FieldClassNumbers numbers = new FieldClassNumbers();
      setter.setFromStrs(numbers, new String[]{"true", "left value"});
      assertThat(numbers.booleanField).isTrue();
    }

    {
      FieldSetter setter = fieldSetters.get("booleanBoxedField");
      assertThat(setter).isNotNull();
      FieldClassNumbers numbers = new FieldClassNumbers();
      setter.setFromStrs(numbers, new String[]{"false", "left value"});
      assertThat(numbers.booleanBoxedField).isFalse();
    }
  }
}
