package eng.eSystem.xmlSerialization.meta.newe;

import eng.eSystem.xmlSerialization.supports.IFactory;

public class Factory extends MetaItem {
  public final Class type;
  public final IFactory factory;

  public Factory(Class type, IFactory factory) {
    assert type != null;
    assert factory != null;

    this.type = type;
    this.factory = factory;
  }
}
