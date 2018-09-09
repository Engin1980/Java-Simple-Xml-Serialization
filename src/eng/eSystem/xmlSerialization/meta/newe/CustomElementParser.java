package eng.eSystem.xmlSerialization.meta.newe;

import eng.eSystem.xmlSerialization.supports.IElementParser;

public class CustomElementParser extends CustomParser {
  public final IElementParser parser;

  public CustomElementParser(Class type, Class parentType, IElementParser parser) {
    super(type, parentType);
    assert parser != null;
    this.parser = parser;
  }
}
