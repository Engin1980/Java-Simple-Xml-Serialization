package eng.eSystem.xmlSerialization.meta.newe;

import eng.eSystem.xmlSerialization.TypeMappingManager;
import eng.eSystem.xmlSerialization.meta.TypeMetaInfo;
import eng.eSystem.xmlSerialization.supports.IParser;

public class Applicator {
  private String name;
  private Class type;
  private IParser parser;
  private boolean attribute;

  public Applicator(String name, Class type, IParser parser, boolean attribute) {
    this.name = name;
    this.type = wrapType(type);
    this.parser = parser;
    this.attribute = attribute;
  }

  public boolean isAttribute() {
    return attribute;
  }

  public String getName() {
    return name;
  }

  public Class getType() {
    return type;
  }

  public IParser getParser() {
    return parser;
  }

  public IParser getCustomParser() {
    return parser;
  }

  public <T extends IParser> T getCustomParser(Class<? extends T> cls) {
    return (T) parser;
  }

  public void updateType(Class realType) {
    assert realType != null;
    this.type = wrapType(realType);
  }


  private static Class wrapType(Class realType) {
    if (realType.isPrimitive())
      return TypeMappingManager.wrapPrimitiveType(realType);
    else
      return realType;
  }


  public void updateParser(IParser parser) {
    this.parser = parser;
  }

  public void updateParserIfRequired(TypeMetaInfo tmi) {
    if (this.parser == null)
      this.parser = this.isAttribute() ? tmi.getCustomValueParser() : tmi.getCustomElementParser();
  }
}
