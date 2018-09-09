package eng.eSystem.xmlSerialization.meta.newe;

import eng.eSystem.xmlSerialization.supports.IValueParser;

public class CustomValueParser extends CustomParser {
  public final IValueParser parser;

  public CustomValueParser(Class type, Class parentType, IValueParser parser) {
    super(type, parentType);
    assert parser != null;
    this.parser = parser;
  }
}
