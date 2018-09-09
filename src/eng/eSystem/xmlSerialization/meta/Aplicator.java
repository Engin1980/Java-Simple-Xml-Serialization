package eng.eSystem.xmlSerialization.meta;

import eng.eSystem.xmlSerialization.TypeMappingManager;
import eng.eSystem.xmlSerialization.supports.IParser;
import eng.eSystem.xmlSerialization.supports.IValueParser;

import static eng.eSystem.utilites.FunctionShortcuts.coalesce;

public class Aplicator {
  private String name;
  private Class type;
  private IParser parser;
  private boolean attribute;

  public static Aplicator createEmpty(String name, Class type) {
    Aplicator ret = new Aplicator();

    ret.name = name;
    ret.type = wrapType(type);
    ret.parser = null;
    ret.attribute = false;

    return ret;
  }

  public static Aplicator create(Mapping mapping, String name, Class<?> type, IParser... parsers) {
    Aplicator ret = new Aplicator();


    if (mapping == null) {
      ret.parser = coalesce(parsers);
      ret.name = name;
      ret.type = wrapType(type);
      if (ret.parser == null) {
        ret.attribute = TypeMappingManager.isSimpleTypeOrEnum(ret.type);
      } else {
        ret.attribute = ret.parser instanceof IValueParser;
      }
    } else {
      ret.name = coalesce(mapping.getName(), name);
      ret.type = coalesce(mapping.getType(), type);
      ret.attribute = mapping.isAttribute();
      ret.parser = mapping.getCustomParser();
      if (ret.parser == null)
        ret.parser = coalesce(parsers);
    }


    return ret;
  }

  private Aplicator() {
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
