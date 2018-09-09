package eng.eSystem.xmlSerialization.meta.newe;

public class Mapping extends MetaItem {
  public final String name;
  public final Class type;
  public final boolean isAttribute;
  public final boolean isTypeSubclassIncluded;

  public Mapping(String name, Class type, boolean isAttribute, boolean isTypeSubclassIncluded) {
    this.name = name;
    this.type = type;
    this.isAttribute = isAttribute;
    this.isTypeSubclassIncluded = isTypeSubclassIncluded;
  }
}
