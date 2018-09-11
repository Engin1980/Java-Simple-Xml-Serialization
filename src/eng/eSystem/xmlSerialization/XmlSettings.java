package eng.eSystem.xmlSerialization;

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

  public class TypeMeta {

    private final TypeMetaInfo tmi;

    TypeMeta(TypeMetaInfo tmi) {
      assert tmi != null;
      this.tmi = tmi;
    }

    public FieldMeta forField(String fieldName) {
      FieldMetaInfo fmi = tmi.getFields().tryGetFirst(q -> q.getField().getName().equals(fieldName));
      if (fmi == null)
        throw new XmlSerializationException(sf("Unable to find field named '%s' in class '%s'.", fieldName, tmi.getType().getName()));
      FieldMeta ret = new FieldMeta(fmi);
      return ret;
    }

    public FieldMeta forField(Field field) {
      if (field == null) {
        throw new IllegalArgumentException("Value of {field} cannot not be null.");
      }
      if (field.getDeclaringClass().equals(tmi.getType()) == false)
        throw new IllegalArgumentException(sf("Value of {field} must refer to field of '%s' class.", tmi.getType().getName()));

      FieldMetaInfo fmi = tmi.getFields().getFirst(q -> q.getField().equals(field));
      FieldMeta ret = new FieldMeta(fmi);
      return ret;
    }


    public TypeMeta addXmlItemElement(String name, Class type, boolean isTypeSubtypeIncluded, IElementParser parser) {
      this.tmi.updateXmlItemElementMapping(name, type, isTypeSubtypeIncluded, parser);
      return this;
    }
    public TypeMeta addXmlItemAttribute(String name, Class type, boolean isTypeSubtypeIncluded, IValueParser parser) {
      this.tmi.updateXmlItemAttributeMapping(name, type, isTypeSubtypeIncluded, parser);
      return this;
    }
    public TypeMeta addXmlMapKeyElement(String name, Class type, boolean isTypeSubtypeIncluded, IElementParser parser) {
      this.tmi.updateXmlMapKeyElementMapping(name, type, isTypeSubtypeIncluded, parser);
      return this;
    }
    public TypeMeta addXmlMapKeyAttribute(String name, Class type, boolean isTypeSubtypeIncluded, IValueParser parser) {
      this.tmi.updateXmlMapKeyAttributeMapping(name, type, isTypeSubtypeIncluded, parser);
      return this;
    }
    public TypeMeta addXmlMapValueElement(String name, Class type, boolean isTypeSubtypeIncluded, IElementParser parser) {
      this.tmi.updateXmlMapValueElementMapping(name, type, isTypeSubtypeIncluded, parser);
      return this;
    }
    public TypeMeta addXmlMapValueAttribute(String name, Class type, boolean isTypeSubtypeIncluded, IValueParser parser) {
      this.tmi.updateXmlMapValueAttributeMapping(name, type, isTypeSubtypeIncluded, parser);
      return this;
    }

    public TypeMeta setFactory(IFactory factory) {
      this.tmi.updateCustomFactory(factory);
      return this;
    }

    public TypeMeta setCustomParser(IValueParser parser) {
      this.tmi.updateCustomValueParser(parser);
      return this;
    }

    public TypeMeta setCustomParser(IElementParser parser) {
      this.tmi.updateCustomElementParser(parser);
      return this;
    }

    public TypeMeta addXmlItemIgnoredElement(String itemElementName) {
      this.tmi.getItemIgnores().add(new ItemIgnoreElement(itemElementName));
      return this;
    }
  }

  public class FieldMeta {
    private final FieldMetaInfo fmi;

    FieldMeta(FieldMetaInfo fmi) {
      assert fmi != null;
      this.fmi = fmi;
    }

    public FieldMeta setNecessity(FieldMetaInfo.eNecessity necessity) {
      this.fmi.updateNecessity(necessity);
      return this;
    }

    public FieldMeta addXmlElement(String name, Class type, boolean isTypeSubtypeIncluded, IElementParser parser) {
      this.fmi.updateXmlElementMapping(name, type, isTypeSubtypeIncluded, parser);
      return this;
    }

    public FieldMeta addXmlAttribute(String name, Class type, boolean isTypeSubtypeIncluded, IValueParser parser) {
      this.fmi.updateXmlAttributeMapping(name, type, isTypeSubtypeIncluded, parser);
      return this;
    }

    public FieldMeta addXmlItemElement(String name, Class type, boolean isTypeSubtypeIncluded, IElementParser parser) {
      this.fmi.updateXmlItemElementMapping(name, type, isTypeSubtypeIncluded, parser);
      return this;
    }

    public FieldMeta addXmlItemAttribute(String name, Class type, boolean isTypeSubtypeIncluded, IValueParser parser) {
      this.fmi.updateXmlItemAttributeMapping(name, type, isTypeSubtypeIncluded, parser);
      return this;
    }

    public FieldMeta addXmlMapKeyElement(String name, Class type, boolean isTypeSubtypeIncluded, IElementParser parser) {
      this.fmi.updateXmlMapKeyElementMapping(name, type, isTypeSubtypeIncluded, parser);
      return this;
    }
    public FieldMeta addXmlMapKeyAttribute(String name, Class type, boolean isTypeSubtypeIncluded, IValueParser parser) {
      this.fmi.updateXmlMapKeyAttributeMapping(name, type, isTypeSubtypeIncluded, parser);
      return this;
    }
    public FieldMeta addXmlMapValueElement(String name, Class type, boolean isTypeSubtypeIncluded, IElementParser parser) {
      this.fmi.updateXmlMapValueElementMapping(name, type, isTypeSubtypeIncluded, parser);
      return this;
    }
    public FieldMeta addXmlMapValueAttribute(String name, Class type, boolean isTypeSubtypeIncluded, IValueParser parser) {
      this.fmi.updateXmlMapValueAttributeMapping(name, type, isTypeSubtypeIncluded, parser);
      return this;
    }

  }

  private Log.LogLevel logLevel = Log.LogLevel.warning;
  private String nullString = "(null)";
  private boolean useSimpleTypeNamesInReferences = true;
  private final MetaManager preparedMetaManager = new MetaManager();

  public TypeMeta forType(Class cls) {
    TypeMetaInfo tmi = preparedMetaManager.getTypeMetaInfo(cls);
    TypeMeta ret = new TypeMeta(tmi);
    return ret;
  }

  public TypeMeta forType(String className) {
    Class cls;

    try {
      cls = Class.forName(className);
    } catch (ClassNotFoundException ex) {
      throw new XmlSerializationException("Unable to find class named " + className, ex);
    }

    TypeMeta ret = forType(cls);
    return ret;
  }

  public FieldMeta forField(Field field) {
    FieldMeta ret = forType(field.getDeclaringClass()).forField(field);
    return ret;
  }

  public FieldMeta forField(String className, String fieldName) {
    TypeMeta tm = forType(className);
    FieldMeta fm = tm.forField(fieldName);
    return fm;
  }

  public FieldMeta forField(Class cls, String fieldName) {
    TypeMeta tm = forType(cls);
    FieldMeta fm = tm.forField(fieldName);
    return fm;
  }

  public FieldMeta forField(String fullDotPath) {
    int lastDotIndex = fullDotPath.lastIndexOf('.');
    String className = fullDotPath.substring(0, lastDotIndex);
    String fieldName = fullDotPath.substring(lastDotIndex + 1);
    FieldMeta ret = forField(className, fieldName);
    return ret;
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
}
