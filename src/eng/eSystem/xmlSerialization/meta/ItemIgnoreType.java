package eng.eSystem.xmlSerialization.meta;

public class ItemIgnoreType extends ItemIgnore {
  private Class type;
  private boolean subtypeIncluded;

  public ItemIgnoreType(Class type, boolean subtypeIncluded) {
    this.type = type;
    this.subtypeIncluded = subtypeIncluded;
  }

  public Class getType() {
    return type;
  }

  public boolean isSubtypeIncluded() {
    return subtypeIncluded;
  }
}
