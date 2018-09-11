package eng.eSystem.xmlSerialization;

import eng.eSystem.collections.EList;
import eng.eSystem.collections.IList;
import eng.eSystem.xmlSerialization.exceptions.XmlSerializationException;
import eng.eSystem.xmlSerialization.meta.FieldMetaInfo;
import eng.eSystem.xmlSerialization.meta.ItemIgnoreElement;
import eng.eSystem.xmlSerialization.meta.MetaManager;
import eng.eSystem.xmlSerialization.meta.TypeMetaInfo;
import eng.eSystem.xmlSerialization.supports.IElementParser;
import eng.eSystem.xmlSerialization.supports.IFactory;
import eng.eSystem.xmlSerialization.supports.IValueParser;

import java.lang.reflect.Field;

import static eng.eSystem.utilites.FunctionShortcuts.sf;

public class XmlSettings {

  private Log.LogLevel logLevel = Log.LogLevel.warning;
  private String nullString = "(null)";
  private boolean useSimpleTypeNamesInReferences = true;
  private final IList<IFactory> factories = new EList<>();
  private final MetaManager preparedMetaManager = new MetaManager();
  private final MetaAcc metaAcc = this.new MetaAcc();

  public class MetaAcc {
    public void registerFieldNecessity(Field field, FieldMetaInfo.eNecessity necessity) {
      FieldMetaInfo fmi = _getField(field);
      fmi.updateNecessity(necessity);
    }

    private FieldMetaInfo _getField(Field field) {
      TypeMetaInfo tmi = preparedMetaManager.getTypeMetaInfo(field.getDeclaringClass());
      FieldMetaInfo fmi;
      try {
        fmi = tmi.getFields().getFirst(q -> q.getField().equals(field));
      } catch (Exception ex) {
        throw new XmlSerializationException(sf("Field '%s' not found in type '%s'.", field.getName(), tmi.getType().getName()), ex);
      }
      return fmi;
    }

    public void registerXmlElement(Field field, String name) {
      _registerXmlElement(field, name, null, true, null);
    }

    public void registerXmlElement(Field field, Class type, boolean isTypeSubtypeIncluded) {
      _registerXmlElement(field, null, type, isTypeSubtypeIncluded, null);
    }

    public void registerXmlElement(Field field, Class type, boolean isTypeSubtypeIncluded, IElementParser parser) {
      _registerXmlElement(field, null, type, isTypeSubtypeIncluded, parser);
    }

    public void registerXmlElement(Field field, String name, Class type, boolean isTypeSubtypeIncluded, IElementParser parser) {
      _registerXmlElement(field, name, type, isTypeSubtypeIncluded, parser);
    }

    private void _registerXmlElement(Field field, String name, Class type, boolean isTypeSubtypeIncluded, IElementParser parser) {
      FieldMetaInfo fmi = _getField(field);
      fmi.updateXmlElementMapping(name, type, isTypeSubtypeIncluded, parser);
    }

    public void registerXmlAttribute(Field field, String name) {
      _registerXmlAttribute(field, name, null, true, null);
    }

    public void registerXmlAttribute(Field field, Class type, boolean isTypeSubtypeIncluded) {
      _registerXmlAttribute(field, null, type, isTypeSubtypeIncluded, null);
    }

    public void registerXmlAttribute(Field field, Class type, boolean isTypeSubtypeIncluded, IValueParser parser) {
      _registerXmlAttribute(field, null, type, isTypeSubtypeIncluded, parser);
    }

    public void registerXmlAttribute(Field field, String name, Class type, boolean isTypeSubtypeIncluded, IValueParser parser) {
      _registerXmlAttribute(field, name, type, isTypeSubtypeIncluded, parser);
    }

    private void _registerXmlAttribute(Field field, String name, Class type, boolean isTypeSubtypeIncluded, IValueParser parser) {
      FieldMetaInfo fmi = _getField(field);
      fmi.updateXmlAttributeMapping(name, type, isTypeSubtypeIncluded, parser);
    }


    public void registerXmlItemElement(Field field, String name) {
      _registerXmlItemElement(field, name, null, true, null);
    }

    public void registerXmlItemElement(Field field, Class type, boolean isTypeSubtypeIncluded) {
      _registerXmlItemElement(field, null, type, isTypeSubtypeIncluded, null);
    }

    public void registerXmlItemElement(Field field, Class type, boolean isTypeSubtypeIncluded, IElementParser parser) {
      _registerXmlItemElement(field, null, type, isTypeSubtypeIncluded, parser);
    }

    public void registerXmlItemElement(Field field, String name, Class type, boolean isTypeSubtypeIncluded, IElementParser parser) {
      _registerXmlItemElement(field, name, type, isTypeSubtypeIncluded, parser);
    }

    private void _registerXmlItemElement(Field field, String name, Class type, boolean isTypeSubtypeIncluded, IElementParser parser) {
      FieldMetaInfo fmi = _getField(field);
      fmi.updateXmlItemElementMapping(name, type, isTypeSubtypeIncluded, parser);
    }

    public void registerXmlItemAttribute(Field field, String name) {
      _registerXmlItemAttribute(field, name, null, true, null);
    }

    public void registerXmlItemAttribute(Field field, Class type, boolean isTypeSubtypeIncluded) {
      _registerXmlItemAttribute(field, null, type, isTypeSubtypeIncluded, null);
    }

    public void registerXmlItemAttribute(Field field, Class type, boolean isTypeSubtypeIncluded, IValueParser parser) {
      _registerXmlItemAttribute(field, null, type, isTypeSubtypeIncluded, parser);
    }

    public void registerXmlItemAttribute(Field field, String name, Class type, boolean isTypeSubtypeIncluded, IValueParser parser) {
      _registerXmlItemAttribute(field, name, type, isTypeSubtypeIncluded, parser);
    }

    private void _registerXmlItemAttribute(Field field, String name, Class type, boolean isTypeSubtypeIncluded, IValueParser parser) {
      FieldMetaInfo fmi = _getField(field);
      fmi.updateXmlItemAttributeMapping(name, type, isTypeSubtypeIncluded, parser);
    }


    public void registerXmlMapKeyElement(Field field, String name) {
      _registerXmlMapKeyElement(field, name, null, true, null);
    }

    public void registerXmlMapKeyElement(Field field, Class type, boolean isTypeSubtypeIncluded) {
      _registerXmlMapKeyElement(field, null, type, isTypeSubtypeIncluded, null);
    }

    public void registerXmlMapKeyElement(Field field, Class type, boolean isTypeSubtypeIncluded, IElementParser parser) {
      _registerXmlMapKeyElement(field, null, type, isTypeSubtypeIncluded, parser);
    }

    public void registerXmlMapKeyElement(Field field, String name, Class type, boolean isTypeSubtypeIncluded, IElementParser parser) {
      _registerXmlMapKeyElement(field, name, type, isTypeSubtypeIncluded, parser);
    }

    private void _registerXmlMapKeyElement(Field field, String name, Class type, boolean isTypeSubtypeIncluded, IElementParser parser) {
      FieldMetaInfo fmi = _getField(field);
      fmi.updateXmlMapKeyElementMapping(name, type, isTypeSubtypeIncluded, parser);
    }

    public void registerXmlMapKeyAttribute(Field field, String name) {
      _registerXmlMapKeyAttribute(field, name, null, true, null);
    }

    public void registerXmlMapKeyAttribute(Field field, Class type, boolean isTypeSubtypeIncluded) {
      _registerXmlMapKeyAttribute(field, null, type, isTypeSubtypeIncluded, null);
    }

    public void registerXmlMapKeyAttribute(Field field, Class type, boolean isTypeSubtypeIncluded, IValueParser parser) {
      _registerXmlMapKeyAttribute(field, null, type, isTypeSubtypeIncluded, parser);
    }

    public void registerXmlMapKeyAttribute(Field field, String name, Class type, boolean isTypeSubtypeIncluded, IValueParser parser) {
      _registerXmlMapKeyAttribute(field, name, type, isTypeSubtypeIncluded, parser);
    }

    private void _registerXmlMapKeyAttribute(Field field, String name, Class type, boolean isTypeSubtypeIncluded, IValueParser parser) {
      FieldMetaInfo fmi = _getField(field);
      fmi.updateXmlMapKeyAttributeMapping(name, type, isTypeSubtypeIncluded, parser);
    }


    public void registerXmlMapValueElement(Field field, String name) {
      _registerXmlMapValueElement(field, name, null, true, null);
    }

    public void registerXmlMapValueElement(Field field, Class type, boolean isTypeSubtypeIncluded) {
      _registerXmlMapValueElement(field, null, type, isTypeSubtypeIncluded, null);
    }

    public void registerXmlMapValueElement(Field field, Class type, boolean isTypeSubtypeIncluded, IElementParser parser) {
      _registerXmlMapValueElement(field, null, type, isTypeSubtypeIncluded, parser);
    }

    public void registerXmlMapValueElement(Field field, String name, Class type, boolean isTypeSubtypeIncluded, IElementParser parser) {
      _registerXmlMapValueElement(field, name, type, isTypeSubtypeIncluded, parser);
    }

    private void _registerXmlMapValueElement(Field field, String name, Class type, boolean isTypeSubtypeIncluded, IElementParser parser) {
      FieldMetaInfo fmi = _getField(field);
      fmi.updateXmlMapValueElementMapping(name, type, isTypeSubtypeIncluded, parser);
    }

    public void registerXmlMapValueAttribute(Field field, String name) {
      _registerXmlMapValueAttribute(field, name, null, true, null);
    }

    public void registerXmlMapValueAttribute(Field field, Class type, boolean isTypeSubtypeIncluded) {
      _registerXmlMapValueAttribute(field, null, type, isTypeSubtypeIncluded, null);
    }

    public void registerXmlMapValueAttribute(Field field, Class type, boolean isTypeSubtypeIncluded, IValueParser parser) {
      _registerXmlMapValueAttribute(field, null, type, isTypeSubtypeIncluded, parser);
    }

    public void registerXmlMapValueAttribute(Field field, String name, Class type, boolean isTypeSubtypeIncluded, IValueParser parser) {
      _registerXmlMapValueAttribute(field, name, type, isTypeSubtypeIncluded, parser);
    }

    private void _registerXmlMapValueAttribute(Field field, String name, Class type, boolean isTypeSubtypeIncluded, IValueParser parser) {
      FieldMetaInfo fmi = _getField(field);
      fmi.updateXmlMapValueAttributeMapping(name, type, isTypeSubtypeIncluded, parser);
    }


    public void registerXmlItemElement(Class parentType, String name) {
      _registerXmlItemElement(parentType, name, null, true, null);
    }

    public void registerXmlItemElement(Class parentType, Class type, boolean isTypeSubtypeIncluded) {
      _registerXmlItemElement(parentType, null, type, isTypeSubtypeIncluded, null);
    }

    public void registerXmlItemElement(Class parentType, Class type, boolean isTypeSubtypeIncluded, IElementParser parser) {
      _registerXmlItemElement(parentType, null, type, isTypeSubtypeIncluded, parser);
    }

    public void registerXmlItemElement(Class parentType, String name, Class type, boolean isTypeSubtypeIncluded, IElementParser parser) {
      _registerXmlItemElement(parentType, name, type, isTypeSubtypeIncluded, parser);
    }

    private void _registerXmlItemElement(Class parentType, String name, Class type, boolean isTypeSubtypeIncluded, IElementParser parser) {
      TypeMetaInfo tmi = _getType(parentType);
      tmi.updateXmlItemElementMapping(name, type, isTypeSubtypeIncluded, parser);
    }

    public void registerXmlItemAttribute(Class parentType, String name) {
      _registerXmlItemAttribute(parentType, name, null, true, null);
    }

    public void registerXmlItemAttribute(Class parentType, Class type, boolean isTypeSubtypeIncluded) {
      _registerXmlItemAttribute(parentType, null, type, isTypeSubtypeIncluded, null);
    }

    public void registerXmlItemAttribute(Class parentType, Class type, boolean isTypeSubtypeIncluded, IValueParser parser) {
      _registerXmlItemAttribute(parentType, null, type, isTypeSubtypeIncluded, parser);
    }

    public void registerXmlItemAttribute(Class parentType, String name, Class type, boolean isTypeSubtypeIncluded, IValueParser parser) {
      _registerXmlItemAttribute(parentType, name, type, isTypeSubtypeIncluded, parser);
    }

    private void _registerXmlItemAttribute(Class parentType, String name, Class type, boolean isTypeSubtypeIncluded, IValueParser parser) {
      TypeMetaInfo tmi = _getType(parentType);
      tmi.updateXmlItemAttributeMapping(name, type, isTypeSubtypeIncluded, parser);
    }

    private TypeMetaInfo _getType(Class type) {
      TypeMetaInfo ret = preparedMetaManager.getTypeMetaInfo(type);
      return ret;
    }


    public void registerXmlMapKeyElement(Class parentType, String name) {
      _registerXmlMapKeyElement(parentType, name, null, true, null);
    }

    public void registerXmlMapKeyElement(Class parentType, Class type, boolean isTypeSubtypeIncluded) {
      _registerXmlMapKeyElement(parentType, null, type, isTypeSubtypeIncluded, null);
    }

    public void registerXmlMapKeyElement(Class parentType, Class type, boolean isTypeSubtypeIncluded, IElementParser parser) {
      _registerXmlMapKeyElement(parentType, null, type, isTypeSubtypeIncluded, parser);
    }

    public void registerXmlMapKeyElement(Class parentType, String name, Class type, boolean isTypeSubtypeIncluded, IElementParser parser) {
      _registerXmlMapKeyElement(parentType, name, type, isTypeSubtypeIncluded, parser);
    }

    private void _registerXmlMapKeyElement(Class parentType, String name, Class type, boolean isTypeSubtypeIncluded, IElementParser parser) {
      TypeMetaInfo tmi = _getType(parentType);
      tmi.updateXmlMapKeyElementMapping(name, type, isTypeSubtypeIncluded, parser);
    }

    public void registerXmlMapKeyAttribute(Class parentType, String name) {
      _registerXmlMapKeyAttribute(parentType, name, null, true, null);
    }

    public void registerXmlMapKeyAttribute(Class parentType, Class type, boolean isTypeSubtypeIncluded) {
      _registerXmlMapKeyAttribute(parentType, null, type, isTypeSubtypeIncluded, null);
    }

    public void registerXmlMapKeyAttribute(Class parentType, Class type, boolean isTypeSubtypeIncluded, IValueParser parser) {
      _registerXmlMapKeyAttribute(parentType, null, type, isTypeSubtypeIncluded, parser);
    }

    public void registerXmlMapKeyAttribute(Class parentType, String name, Class type, boolean isTypeSubtypeIncluded, IValueParser parser) {
      _registerXmlMapKeyAttribute(parentType, name, type, isTypeSubtypeIncluded, parser);
    }

    private void _registerXmlMapKeyAttribute(Class parentType, String name, Class type, boolean isTypeSubtypeIncluded, IValueParser parser) {
      TypeMetaInfo tmi = _getType(parentType);
      tmi.updateXmlMapKeyAttributeMapping(name, type, isTypeSubtypeIncluded, parser);
    }


    public void registerXmlMapValueElement(Class parentType, String name) {
      _registerXmlMapValueElement(parentType, name, null, true, null);
    }

    public void registerXmlMapValueElement(Class parentType, Class type, boolean isTypeSubtypeIncluded) {
      _registerXmlMapValueElement(parentType, null, type, isTypeSubtypeIncluded, null);
    }

    public void registerXmlMapValueElement(Class parentType, Class type, boolean isTypeSubtypeIncluded, IElementParser parser) {
      _registerXmlMapValueElement(parentType, null, type, isTypeSubtypeIncluded, parser);
    }

    public void registerXmlMapValueElement(Class parentType, String name, Class type, boolean isTypeSubtypeIncluded, IElementParser parser) {
      _registerXmlMapValueElement(parentType, name, type, isTypeSubtypeIncluded, parser);
    }

    private void _registerXmlMapValueElement(Class parentType, String name, Class type, boolean isTypeSubtypeIncluded, IElementParser parser) {
      TypeMetaInfo tmi = _getType(parentType);
      tmi.updateXmlMapValueElementMapping(name, type, isTypeSubtypeIncluded, parser);
    }

    public void registerXmlMapValueAttribute(Class parentType, String name) {
      _registerXmlMapValueAttribute(parentType, name, null, true, null);
    }

    public void registerXmlMapValueAttribute(Class parentType, Class type, boolean isTypeSubtypeIncluded) {
      _registerXmlMapValueAttribute(parentType, null, type, isTypeSubtypeIncluded, null);
    }

    public void registerXmlMapValueAttribute(Class parentType, Class type, boolean isTypeSubtypeIncluded, IValueParser parser) {
      _registerXmlMapValueAttribute(parentType, null, type, isTypeSubtypeIncluded, parser);
    }

    public void registerXmlMapValueAttribute(Class parentType, String name, Class type, boolean isTypeSubtypeIncluded, IValueParser parser) {
      _registerXmlMapValueAttribute(parentType, name, type, isTypeSubtypeIncluded, parser);
    }

    private void _registerXmlMapValueAttribute(Class parentType, String name, Class type, boolean isTypeSubtypeIncluded, IValueParser parser) {
      TypeMetaInfo tmi = _getType(parentType);
      tmi.updateXmlMapValueAttributeMapping(name, type, isTypeSubtypeIncluded, parser);
    }

    public void registerFactory(IFactory factory) {
      if (factory == null) {
        throw new IllegalArgumentException("Value of {factory} cannot not be null.");
      }
      factories.add(factory);
    }


    public void registerCustomParser(Class type, boolean applyOnSubclasses, IValueParser parser) {
      assert parser != null;
      TypeMetaInfo tmi = _getType(type);
      tmi.updateCustomValueParser(parser, applyOnSubclasses);
    }

    public void registerCustomParser(Class type, boolean applyOnSubclasses, IElementParser parser) {
      assert parser != null;
      TypeMetaInfo tmi = _getType(type);
      tmi.updateCustomElementParser(parser, applyOnSubclasses);
    }

    public void registerXmlItemIgnoredElement(Class parentType, String itemElementName){
      TypeMetaInfo tmi = _getType(parentType);
      tmi.getItemIgnores().add(new ItemIgnoreElement(itemElementName));
    }
  }

  IList<IFactory> getFactories() {
    return factories;
  }

  MetaManager getMetaManager() {
    return preparedMetaManager;
  }

  public boolean isUseSimpleTypeNamesInReferences() {
    return useSimpleTypeNamesInReferences;
  }

  public void setUseSimpleTypeNamesInReferences(boolean useSimpleTypeNamesInReferences) {
    this.useSimpleTypeNamesInReferences = useSimpleTypeNamesInReferences;
  }

  public String getNullString() {
    return nullString;
  }

  public void setNullString(String nullString) {
    this.nullString = nullString;
  }

  public Log.LogLevel getLogLevel() {
    return logLevel;
  }

  public void setLogLevel(Log.LogLevel logLevel) {
    this.logLevel = logLevel;
  }

  public MetaAcc getMeta() {
    return metaAcc;
  }
}
