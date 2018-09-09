package eng.eSystem.xmlSerialization.meta.newe;

public class TypeMapping extends Mapping {

  public final Class parentType;
  public final boolean isParentTypeSubtypeIncluded;

  public TypeMapping(String name, Class type, boolean isAttribute, boolean isTypeSubclassIncluded, Class parentType, boolean isParentTypeSubtypeIncluded) {
    super(name, type, isAttribute, isTypeSubclassIncluded);
    this.parentType = parentType;
    this.isParentTypeSubtypeIncluded = isParentTypeSubtypeIncluded;
  }
}
