package kz.greetgo.mvc.errors;

public class CompatibleTargetMapping extends RuntimeException {
  public final String mappingInfo1;
  public final String mappingInfo2;

  public CompatibleTargetMapping(String mappingInfo1, String mappingInfo2) {
    super("Имеется неоднозначность выбора метода для запроса между следующими методами контроллеров:\n\t"
      + mappingInfo1 + "\n\t" + mappingInfo2);
    this.mappingInfo1 = mappingInfo1;
    this.mappingInfo2 = mappingInfo2;
  }
}
