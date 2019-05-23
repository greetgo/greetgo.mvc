package kz.greetgo.mvc.util.setters;

import kz.greetgo.mvc.annotations.SkipParameter;
import kz.greetgo.util.RND;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.AccessibleObject;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
    FieldSetters fieldSetters = FieldSettersCreator.create(FieldClassStr.class);
    assertThat(fieldSetters).isNotNull();
    assertThat(fieldSetters.names()).contains("strField1", "strField2");
    assertThat(fieldSetters.names()).hasSize(2);

    FieldClassStr model = new FieldClassStr();

    FieldSetter strField1 = fieldSetters.get("strField1");
    assertThat(strField1).isNotNull();

    String str1 = RND.str(10), str2 = RND.str(10);

    strField1.setFromStrings(model, new String[]{str1, "left value"});

    assertThat(model.strField1).isEqualTo(str1);

    FieldSetter strField2 = fieldSetters.get("strField2");
    strField2.setFromStrings(model, new String[]{str2, "left value"});

    assertThat(model.strField2).isEqualTo(str2 + " from setter");

  }

  public static class FieldClassStrings {

    public List<String> strings1 = new ArrayList<>();
    public final List<String> strings2 = new ArrayList<>();

    private final List<String> strings3 = new ArrayList<>();
    public List<String> strings4 = new ArrayList<>();

    public List<String> getStrings3() {
      return strings3;
    }

    @SuppressWarnings("unused")
    public void setStrings4(List<String> strings4) {
      this.strings4 = strings4;
      this.strings4.add("from setter");
    }
  }

  @Test
  public void strings() throws Exception {
    FieldSetters fieldSetters = FieldSettersCreator.create(FieldClassStrings.class);
    assertThat(fieldSetters).isNotNull();
    assertThat(fieldSetters.names()).hasSize(4);
    assertThat(fieldSetters.names()).contains("strings1", "strings2", "strings3", "strings4");

    {
      FieldSetter setter = fieldSetters.get("strings1");
      assertThat(setter).isNotNull();
      FieldClassStrings model = new FieldClassStrings();
      String str1 = RND.str(10), str2 = RND.str(10), str3 = RND.str(10);
      setter.setFromStrings(model, new String[]{str1, str2, str3});
      assertThat(model.strings1).containsExactly(str1, str2, str3);
    }
    {
      FieldSetter setter = fieldSetters.get("strings2");
      assertThat(setter).isNotNull();
      FieldClassStrings model = new FieldClassStrings();
      String str1 = RND.str(10), str2 = RND.str(10), str3 = RND.str(10);
      setter.setFromStrings(model, new String[]{str1, str2, str3});
      assertThat(model.strings2).containsExactly(str1, str2, str3);
    }
    {
      FieldSetter setter = fieldSetters.get("strings3");
      assertThat(setter).isNotNull();
      FieldClassStrings model = new FieldClassStrings();
      String str1 = RND.str(10), str2 = RND.str(10), str3 = RND.str(10);
      setter.setFromStrings(model, new String[]{str1, str2, str3});
      assertThat(model.getStrings3()).containsExactly(str1, str2, str3);
    }
    {
      FieldSetter setter = fieldSetters.get("strings4");
      assertThat(setter).isNotNull();
      FieldClassStrings model = new FieldClassStrings();
      String str1 = RND.str(10), str2 = RND.str(10), str3 = RND.str(10);
      setter.setFromStrings(model, new String[]{str1, str2, str3});
      assertThat(model.strings4).containsExactly(str1, str2, str3);
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
    FieldSetters fieldSetters = FieldSettersCreator.create(FieldClassNumbers.class);
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
      setter.setFromStrings(numbers, new String[]{"213", "left value"});
      assertThat(numbers.intField).isEqualTo(213);
    }

    {
      FieldSetter setter = fieldSetters.get("integerField");
      assertThat(setter).isNotNull();
      FieldClassNumbers numbers = new FieldClassNumbers();
      setter.setFromStrings(numbers, new String[]{"2131", "left value"});
      assertThat(numbers.integerField).isEqualTo(2131);
    }

    {
      FieldSetter setter = fieldSetters.get("longField");
      assertThat(setter).isNotNull();
      FieldClassNumbers numbers = new FieldClassNumbers();
      setter.setFromStrings(numbers, new String[]{"97811", "left value"});
      assertThat(numbers.longField).isEqualTo(97811);
    }

    {
      FieldSetter setter = fieldSetters.get("longBoxedField");
      assertThat(setter).isNotNull();
      FieldClassNumbers numbers = new FieldClassNumbers();
      setter.setFromStrings(numbers, new String[]{"76487658764", "left value"});
      assertThat(numbers.longBoxedField).isEqualTo(76487658764L);
    }

    {
      FieldSetter setter = fieldSetters.get("booleanField");
      assertThat(setter).isNotNull();
      FieldClassNumbers numbers = new FieldClassNumbers();
      setter.setFromStrings(numbers, new String[]{"true", "left value"});
      assertThat(numbers.booleanField).isTrue();
    }

    {
      FieldSetter setter = fieldSetters.get("booleanBoxedField");
      assertThat(setter).isNotNull();
      FieldClassNumbers numbers = new FieldClassNumbers();
      setter.setFromStrings(numbers, new String[]{"false", "left value"});
      assertThat(numbers.booleanBoxedField).isFalse();
    }
  }

  public static class ClassWithLists {
    public List<Long> list1 = new ArrayList<>();

    public List<BigDecimal> list2;
  }

  @Test
  public void testClassWithLists_withInitiatorInside() throws Exception {

    FieldSetters fieldSetters = FieldSettersCreator.create(ClassWithLists.class);


    FieldSetter list1 = fieldSetters.get("list1");
    ClassWithLists a = new ClassWithLists();
    list1.setFromStrings(a, new String[]{"123", "432", "4563"});

    assertThat(a.list1).hasSize(3);
    assertThat(a.list1.get(0)).isEqualTo(123L);
    assertThat(a.list1.get(1)).isEqualTo(432L);
    assertThat(a.list1.get(2)).isEqualTo(4563L);
  }

  @Test
  public void testClassWithLists_withoutInitiatorInside() throws Exception {
    FieldSetters fieldSetters = FieldSettersCreator.create(ClassWithLists.class);

    FieldSetter list2 = fieldSetters.get("list2");
    assertThat(list2).isNotNull();
    ClassWithLists a = new ClassWithLists();
    list2.setFromStrings(a, new String[]{" 54326.76", "12.4356 ", "5465.34  54", "43_55,435 46"});

    assertThat(a.list2).hasSize(4);
    assertThat(a.list2.get(0)).isEqualByComparingTo("54326.76");
    assertThat(a.list2.get(1)).isEqualByComparingTo("12.4356");
    assertThat(a.list2.get(2)).isEqualByComparingTo("5465.3454");
    assertThat(a.list2.get(3)).isEqualByComparingTo("4355.43546");
  }

  @SuppressWarnings("unused")
  public static class Tmp {
    public List<String> listField;

    public String leftField;

    public void setListSetter(List<Long> list) {}

    public void setLeftSetter(int left) {}

    public ArrayList<String> arrayListField;

    public void setArrayListSetter(ArrayList<BigDecimal> list) {}

    public List<Date> getSomeListDate() {return null;}

    public Date getSomeDate() {return null;}
  }

  @DataProvider
  public Object[][] createCollectionManager_DataProvider() throws Exception {
    List<Object[]> toRet = new ArrayList<>();

    toRet.add(new Object[]{Tmp.class.getField("listField"), ArrayList.class, String.class});
    toRet.add(new Object[]{Tmp.class.getField("leftField"), null, null});
    toRet.add(new Object[]{Tmp.class.getMethod("setListSetter", List.class), ArrayList.class, Long.class});
    toRet.add(new Object[]{Tmp.class.getMethod("setLeftSetter", Integer.TYPE), null, null});
    toRet.add(new Object[]{Tmp.class.getMethod("setArrayListSetter", ArrayList.class), ArrayList.class, BigDecimal.class});
    toRet.add(new Object[]{Tmp.class.getField("arrayListField"), ArrayList.class, String.class});
    toRet.add(new Object[]{Tmp.class.getMethod("getSomeListDate"), ArrayList.class, Date.class});
    toRet.add(new Object[]{Tmp.class.getMethod("getSomeDate"), null, null});

    return toRet.toArray(new Object[toRet.size()][]);
  }

  @Test(dataProvider = "createCollectionManager_DataProvider")
  public void createCollectionManager(AccessibleObject accessibleObject,
                                      Class<?> expectedInstanceOf,
                                      Class<?> expectedElementType
  ) throws Exception {
    FieldSettersCreator.CollectionManager instance = FieldSettersCreator.createCollectionManager(accessibleObject);
    if (expectedInstanceOf == null) {
      assertThat(instance).isNull();
    } else {
      assertThat(instance).isNotNull();
      //noinspection ConstantConditions,unchecked
      assertThat(instance.createInstance()).isInstanceOf(expectedInstanceOf);
      assertThat(instance.elementType().getName()).isEqualTo(expectedElementType.getName());
    }
  }

  public static class TestField_int {
    public int field;

    public void assertValue(int expected) {
      assertThat(field).isEqualTo(expected);
    }
  }

  @Test
  public void testField_int_ok() throws Exception {
    FieldSetters fieldSetters = FieldSettersCreator.create(TestField_int.class);
    assertThat(fieldSetters).isNotNull();
    assertThat(fieldSetters.names()).containsOnly("field");

    FieldSetter fieldSetter = fieldSetters.get("field");
    assertThat(fieldSetter).isNotNull();

    int rndInt = RND.plusInt(20_000_000);
    {
      TestField_int tmp = new TestField_int();
      fieldSetter.setFromStrings(tmp, new String[]{"" + rndInt, "Left value"});
      tmp.assertValue(rndInt);
    }
    rndInt = -rndInt;
    {
      TestField_int tmp = new TestField_int();
      fieldSetter.setFromStrings(tmp, new String[]{"" + rndInt, "Left value"});
      tmp.assertValue(rndInt);
    }
  }

  @Test
  public void testField_int_empty() throws Exception {
    FieldSetters fieldSetters = FieldSettersCreator.create(TestField_int.class);
    assertThat(fieldSetters).isNotNull();
    assertThat(fieldSetters.names()).containsOnly("field");

    FieldSetter fieldSetter = fieldSetters.get("field");
    assertThat(fieldSetter).isNotNull();

    TestField_int tmp = new TestField_int();
    fieldSetter.setFromStrings(tmp, new String[]{"", "Left value"});
    tmp.assertValue(0);
  }

  public static class TestField_Integer {
    public Integer field;
  }

  @Test
  public void testField_Integer_empty() throws Exception {
    FieldSetters fieldSetters = FieldSettersCreator.create(TestField_Integer.class);
    assertThat(fieldSetters).isNotNull();
    assertThat(fieldSetters.names()).containsOnly("field");

    FieldSetter fieldSetter = fieldSetters.get("field");
    assertThat(fieldSetter).isNotNull();

    TestField_Integer tmp = new TestField_Integer();
    fieldSetter.setFromStrings(tmp, new String[]{"", "Left value"});
    assertThat(tmp.field).isNull();
  }

  @Test(expectedExceptions = java.lang.NumberFormatException.class)
  public void testField_int_leftValue() throws Exception {
    FieldSetters fieldSetters = FieldSettersCreator.create(TestField_int.class);
    FieldSetter fieldSetter = fieldSetters.get("field");
    TestField_int tmp = new TestField_int();
    fieldSetter.setFromStrings(tmp, new String[]{"Left value"});
  }

  public static class TestField_ListInteger {
    public List<Integer> field = null;

    public void assertEqualsTo(int... ii) {
      for (int i = 0, c = ii.length; i < c; i++) {
        assertThat(field.get(i)).describedAs("i = " + i).isEqualTo(ii[i]);
      }
    }
  }

  @Test
  public void testField_ListInteger() throws Exception {
    FieldSetters fieldSetters = FieldSettersCreator.create(TestField_ListInteger.class);
    assertThat(fieldSetters).isNotNull();
    assertThat(fieldSetters.names()).containsOnly("field");

    FieldSetter fieldSetter = fieldSetters.get("field");
    assertThat(fieldSetter).isNotNull();

    int i1 = RND.plusInt(20_000_000) - 10_000_000;
    int i2 = RND.plusInt(20_000_000) - 10_000_000;
    int i3 = RND.plusInt(20_000_000) - 10_000_000;
    int i4 = RND.plusInt(20_000_000) - 10_000_000;
    int i5 = RND.plusInt(20_000_000) - 10_000_000;

    TestField_ListInteger tmp = new TestField_ListInteger();
    fieldSetter.setFromStrings(tmp, new String[]{"" + i1, "" + i2, "" + i3, "" + i4, "" + i5,});
    tmp.assertEqualsTo(i1, i2, i3, i4, i5);
  }

  public static class TestField_ListIntegerAlreadyInitiated {
    public List<Integer> field = new ArrayList<>();

    {
      field.add(RND.plusInt(20_000_000) - 10_000_000);
    }

    public void assertEqualsTo(int... ii) {
      for (int i = 0, c = ii.length; i < c; i++) {
        assertThat(field.get(i)).describedAs("i = " + i).isEqualTo(ii[i]);
      }
    }
  }

  @Test
  public void testField_ListIntegerAlreadyInitiated() throws Exception {
    FieldSetters fieldSetters = FieldSettersCreator.create(TestField_ListIntegerAlreadyInitiated.class);
    assertThat(fieldSetters).isNotNull();
    assertThat(fieldSetters.names()).containsOnly("field");

    FieldSetter fieldSetter = fieldSetters.get("field");
    assertThat(fieldSetter).isNotNull();

    int i1 = RND.plusInt(20_000_000) - 10_000_000;
    int i2 = RND.plusInt(20_000_000) - 10_000_000;
    int i3 = RND.plusInt(20_000_000) - 10_000_000;
    int i4 = RND.plusInt(20_000_000) - 10_000_000;
    int i5 = RND.plusInt(20_000_000) - 10_000_000;

    TestField_ListIntegerAlreadyInitiated tmp = new TestField_ListIntegerAlreadyInitiated();
    List<Integer> oldField = tmp.field;
    fieldSetter.setFromStrings(tmp, new String[]{"" + i1, "" + i2, "" + i3, "" + i4, "" + i5,});
    tmp.assertEqualsTo(i1, i2, i3, i4, i5);

    assertThat(oldField).describedAs("Очень важно чтобы использовался тот список, который уже есть").isSameAs(tmp.field);
  }

  public static class TestSetter_int {
    public int field;

    @SuppressWarnings("unused")
    public void setField(int field) {
      this.field = field + 5;
    }

    public void assertValue(int expected) {
      assertThat(field).isEqualTo(expected + 5);
    }
  }

  @Test
  public void TestSetter_int_ok() throws Exception {
    FieldSetters fieldSetters = FieldSettersCreator.create(TestSetter_int.class);
    assertThat(fieldSetters).isNotNull();
    assertThat(fieldSetters.names()).containsOnly("field");

    FieldSetter fieldSetter = fieldSetters.get("field");
    assertThat(fieldSetter).isNotNull();

    int rndInt = RND.plusInt(20_000_000);
    {
      TestSetter_int tmp = new TestSetter_int();
      fieldSetter.setFromStrings(tmp, new String[]{"" + rndInt, "Left value"});
      tmp.assertValue(rndInt);
    }
    rndInt = -rndInt;
    {
      TestSetter_int tmp = new TestSetter_int();
      fieldSetter.setFromStrings(tmp, new String[]{"" + rndInt, "Left value"});
      tmp.assertValue(rndInt);
    }
  }

  public static class TestSetter_ListInteger {
    public List<Integer> field = null;
    List<Integer> __fieldFromSetter__ = null;

    int getterCallCount = 0;

    @SuppressWarnings("unused")
    public List<Integer> getField() {
      getterCallCount++;
      return field;
    }

    @SuppressWarnings("unused")
    public void setField(List<Integer> field) {
      this.__fieldFromSetter__ = field;
      this.field = field;
    }

    public void assertEqualsTo(int... ii) {
      for (int i = 0, c = ii.length; i < c; i++) {
        assertThat(field.get(i)).describedAs("i = " + i).isEqualTo(ii[i]);
      }
    }
  }

  @Test
  public void testSetter_ListInteger() throws Exception {
    FieldSetters fieldSetters = FieldSettersCreator.create(TestSetter_ListInteger.class);
    assertThat(fieldSetters).isNotNull();
    assertThat(fieldSetters.names()).containsOnly("field");

    FieldSetter fieldSetter = fieldSetters.get("field");
    assertThat(fieldSetter).isNotNull();

    int i1 = RND.plusInt(20_000_000) - 10_000_000;
    int i2 = RND.plusInt(20_000_000) - 10_000_000;
    int i3 = RND.plusInt(20_000_000) - 10_000_000;
    int i4 = RND.plusInt(20_000_000) - 10_000_000;
    int i5 = RND.plusInt(20_000_000) - 10_000_000;

    TestSetter_ListInteger tmp = new TestSetter_ListInteger();
    fieldSetter.setFromStrings(tmp, new String[]{"" + i1, "" + i2, "" + i3, "" + i4, "" + i5,});
    tmp.assertEqualsTo(i1, i2, i3, i4, i5);

    assertThat(tmp.getterCallCount).isEqualTo(1);
    assertThat(tmp.field)
      .describedAs("Установка должна производиться только через setter")
      .isSameAs(tmp.__fieldFromSetter__);
  }

  public static class TestSetter_ListIntegerAlreadyInitiated {
    public List<Integer> field = new ArrayList<>();

    int getterCallCount = 0;

    @SuppressWarnings("unused")
    public List<Integer> getField() {
      getterCallCount++;
      return field;
    }

    @SuppressWarnings("unused")
    public void setField(List<Integer> field) {
      throw new RuntimeException("Когда поле уже определено, setter вызываться не должен");
    }

    public void assertEqualsTo(int... ii) {
      for (int i = 0, c = ii.length; i < c; i++) {
        assertThat(field.get(i)).describedAs("i = " + i).isEqualTo(ii[i]);
      }
    }
  }

  @Test
  public void testSetter_ListIntegerAlreadyInitiated() throws Exception {
    FieldSetters fieldSetters = FieldSettersCreator.create(TestSetter_ListIntegerAlreadyInitiated.class);
    assertThat(fieldSetters).isNotNull();
    assertThat(fieldSetters.names()).containsOnly("field");

    FieldSetter fieldSetter = fieldSetters.get("field");
    assertThat(fieldSetter).isNotNull();

    int i1 = RND.plusInt(20_000_000) - 10_000_000;
    int i2 = RND.plusInt(20_000_000) - 10_000_000;
    int i3 = RND.plusInt(20_000_000) - 10_000_000;
    int i4 = RND.plusInt(20_000_000) - 10_000_000;
    int i5 = RND.plusInt(20_000_000) - 10_000_000;

    TestSetter_ListIntegerAlreadyInitiated tmp = new TestSetter_ListIntegerAlreadyInitiated();
    List<Integer> oldField = tmp.field;
    fieldSetter.setFromStrings(tmp, new String[]{"" + i1, "" + i2, "" + i3, "" + i4, "" + i5,});
    tmp.assertEqualsTo(i1, i2, i3, i4, i5);

    assertThat(oldField)
      .describedAs("Очень важно чтобы использовался тот же список, который уже есть")
      .isSameAs(tmp.field);
    assertThat(tmp.getterCallCount).isEqualTo(1);
  }

  public static class TestSetter_ListIntegerNoGetter {
    private List<Integer> field = new ArrayList<>();

    int setterCallCount = 0;

    @SuppressWarnings("unused")
    public void setField(List<Integer> field) {
      this.field = field;
      setterCallCount++;
    }

    public void assertEqualsTo(int... ii) {
      for (int i = 0, c = ii.length; i < c; i++) {
        assertThat(field.get(i)).describedAs("i = " + i).isEqualTo(ii[i]);
      }
    }
  }

  @Test
  public void testSetter_ListIntegerNoGetter() throws Exception {
    FieldSetters fieldSetters = FieldSettersCreator.create(TestSetter_ListIntegerNoGetter.class);
    assertThat(fieldSetters).isNotNull();
    assertThat(fieldSetters.names()).containsOnly("field");

    FieldSetter fieldSetter = fieldSetters.get("field");
    assertThat(fieldSetter).isNotNull();

    int i1 = RND.plusInt(20_000_000) - 10_000_000;
    int i2 = RND.plusInt(20_000_000) - 10_000_000;
    int i3 = RND.plusInt(20_000_000) - 10_000_000;
    int i4 = RND.plusInt(20_000_000) - 10_000_000;
    int i5 = RND.plusInt(20_000_000) - 10_000_000;

    TestSetter_ListIntegerNoGetter tmp = new TestSetter_ListIntegerNoGetter();
    List<Integer> oldField = tmp.field;
    fieldSetter.setFromStrings(tmp, new String[]{"" + i1, "" + i2, "" + i3, "" + i4, "" + i5,});
    tmp.assertEqualsTo(i1, i2, i3, i4, i5);

    assertThat(oldField)
      .describedAs("Здесь не получиьтся оставить прежнее значение, так как нету getter-а")
      .isNotSameAs(tmp.field);
    assertThat(tmp.setterCallCount).isEqualTo(1);
  }

  public static class TestField_ListIntegerFinal {
    public final List<Integer> field = new ArrayList<>();

    {
      field.add(RND.plusInt(20_000_000) - 10_000_000);
    }

    public void assertEqualsTo(int... ii) {
      for (int i = 0, c = ii.length; i < c; i++) {
        assertThat(field.get(i)).describedAs("i = " + i).isEqualTo(ii[i]);
      }
    }
  }

  @Test
  public void testField_ListIntegerFinal() throws Exception {
    FieldSetters fieldSetters = FieldSettersCreator.create(TestField_ListIntegerFinal.class);
    assertThat(fieldSetters).isNotNull();
    assertThat(fieldSetters.names()).containsOnly("field");

    FieldSetter fieldSetter = fieldSetters.get("field");
    assertThat(fieldSetter).isNotNull();

    int i1 = RND.plusInt(20_000_000) - 10_000_000;
    int i2 = RND.plusInt(20_000_000) - 10_000_000;
    int i3 = RND.plusInt(20_000_000) - 10_000_000;
    int i4 = RND.plusInt(20_000_000) - 10_000_000;
    int i5 = RND.plusInt(20_000_000) - 10_000_000;

    TestField_ListIntegerFinal tmp = new TestField_ListIntegerFinal();
    fieldSetter.setFromStrings(tmp, new String[]{"" + i1, "" + i2, "" + i3, "" + i4, "" + i5,});
    tmp.assertEqualsTo(i1, i2, i3, i4, i5);

  }

  public static class TestGetter_ListIntegerNoSetter {
    private final List<Integer> field = new ArrayList<>();

    {
      field.add(RND.plusInt(20_000_000) - 10_000_000);
    }

    int getterCallCount = 0;

    @SuppressWarnings("unused")
    public List<Integer> getField() {
      getterCallCount++;
      return field;
    }

    public void assertEqualsTo(int... ii) {
      for (int i = 0, c = ii.length; i < c; i++) {
        assertThat(field.get(i)).describedAs("i = " + i).isEqualTo(ii[i]);
      }
    }
  }

  @Test
  public void testGetter_ListIntegerNoSetter() throws Exception {
    FieldSetters fieldSetters = FieldSettersCreator.create(TestGetter_ListIntegerNoSetter.class);
    assertThat(fieldSetters).isNotNull();
    assertThat(fieldSetters.names()).containsOnly("field");

    FieldSetter fieldSetter = fieldSetters.get("field");
    assertThat(fieldSetter).isNotNull();

    int i1 = RND.plusInt(20_000_000) - 10_000_000;
    int i2 = RND.plusInt(20_000_000) - 10_000_000;
    int i3 = RND.plusInt(20_000_000) - 10_000_000;
    int i4 = RND.plusInt(20_000_000) - 10_000_000;
    int i5 = RND.plusInt(20_000_000) - 10_000_000;

    TestGetter_ListIntegerNoSetter tmp = new TestGetter_ListIntegerNoSetter();
    fieldSetter.setFromStrings(tmp, new String[]{"" + i1, "" + i2, "" + i3, "" + i4, "" + i5,});
    tmp.assertEqualsTo(i1, i2, i3, i4, i5);

    assertThat(tmp.getterCallCount).isEqualTo(1);
  }

  public static class SkipByField {
    @SkipParameter
    @SuppressWarnings("unused")
    public String field;
  }

  @Test
  public void testSkipByField() throws Exception {
    FieldSetters fieldSetters = FieldSettersCreator.create(SkipByField.class);
    assertThat(fieldSetters).isNotNull();
    assertThat(fieldSetters.names()).isEmpty();
  }

  public static class SkipByFieldWithGetterAndSetter {
    @SkipParameter
    public String field;

    @SuppressWarnings("unused")
    public String getField() {
      return field;
    }

    @SuppressWarnings("unused")
    public void setField(String field) {
      this.field = field;
    }
  }

  @Test
  public void testSkipByFieldWithGetterAndSetter() throws Exception {
    FieldSetters fieldSetters = FieldSettersCreator.create(SkipByFieldWithGetterAndSetter.class);
    assertThat(fieldSetters).isNotNull();
    assertThat(fieldSetters.names()).isEmpty();
  }

  public static class AnnotationSkipParameterDoesNotDoWithPrivateField {
    @SkipParameter
    private String field;

    @SuppressWarnings("unused")
    public String getField() {
      return field;
    }

    @SuppressWarnings("unused")
    public void setField(String field) {
      this.field = field;
    }
  }

  @Test
  public void testAnnotationSkipParameterDoesNotDoWithPrivateField() throws Exception {
    FieldSetters fieldSetters = FieldSettersCreator.create(AnnotationSkipParameterDoesNotDoWithPrivateField.class);
    assertThat(fieldSetters).isNotNull();
    assertThat(fieldSetters.names()).isNotEmpty();
  }

  public static class SkipByGetter {
    @SuppressWarnings("unused")
    @SkipParameter
    public String getField() {
      return null;
    }

    @SuppressWarnings("unused")
    public void setField(String field) {}
  }

  @Test
  public void testSkipByGetter() throws Exception {
    FieldSetters fieldSetters = FieldSettersCreator.create(SkipByGetter.class);
    assertThat(fieldSetters).isNotNull();
    assertThat(fieldSetters.names()).isEmpty();
  }

  public static class SkipBySetter {
    @SuppressWarnings("unused")
    public String getField() {
      return null;
    }

    @SkipParameter
    @SuppressWarnings("unused")
    public void setField(String field) {}
  }

  @Test
  public void testSkipBySetter() throws Exception {
    FieldSetters fieldSetters = FieldSettersCreator.create(SkipBySetter.class);
    assertThat(fieldSetters).isNotNull();
    assertThat(fieldSetters.names()).isEmpty();
  }
}
