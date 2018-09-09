package eng.eSystem.xmlSerialization.meta.newe;

public class CustomParser extends MetaItem {
  public final Class type;
  public final Class parentType;

  public CustomParser(Class type, Class parentType) {
    assert type != null;
    this.type = type;
    this.parentType = parentType;
  }
}
