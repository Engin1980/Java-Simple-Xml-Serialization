package eng.eSystem.xmlSerialization.meta.newe;

import java.lang.reflect.Field;

public class CoercionMapping {
  public enum Coercion{
    ignored,
    optional
  }

  public final Field field;
  public final Coercion coercion;

  public CoercionMapping(Field field, Coercion coercion) {
    this.field = field;
    this.coercion = coercion;
  }
}
