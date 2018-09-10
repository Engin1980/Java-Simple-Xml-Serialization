package eng.eSystem.xmlSerialization.meta.newe;

import eng.eSystem.xmlSerialization.TypeMappingManager;
import eng.eSystem.xmlSerialization.meta.TypeMetaInfo;
import eng.eSystem.xmlSerialization.supports.IParser;

public class Applicator {
  private String name;
  private Class normalizedType;
  private Class originalType;
  private IParser parser;
  private boolean attribute;


  public Applicator(String name, Class type, IParser parser, boolean attribute) {
    this.name = name;
    this.originalType = type;
    this.normalizedType = wrapType(type);
    this.parser = parser;
    this.attribute = attribute;
  }

  public boolean isAttribute() {
    return attribute;
  }

  public String getName() {
    return name;
  }

  public Class getNormalizedType() {
    return normalizedType;
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
    this.normalizedType = wrapType(realType);
  }

  public Class getOriginalType() {
    return originalType;
  }

  private static Class wrapType(Class realType) {
    if (realType.isPrimitive())
      return TypeMappingManager.wrapPrimitiveType(realType);
    else
      return realType;
  }

  public void updateParserIfRequired(TypeMetaInfo tmi) {
    if (this.parser == null)
      this.parser = this.isAttribute() ? tmi.getCustomValueParser() : tmi.getCustomElementParser();
  }
}
