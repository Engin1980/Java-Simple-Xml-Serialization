package eng.eSystem.xmlSerialization.meta.newe;

import java.lang.reflect.Field;

public class FieldMapping extends Mapping {
  public final Field field;

  public FieldMapping(String name, Class type, boolean isAttribute, boolean isTypeSubclassIncluded, Field field) {
    super(name, type, isAttribute, isTypeSubclassIncluded);
    this.field = field;
  }
}
