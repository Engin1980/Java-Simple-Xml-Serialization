package eng.eSystem.xmlSerialization.meta;

import eng.eSystem.collections.EList;
import eng.eSystem.collections.IList;
import eng.eSystem.collections.IReadOnlyList;
import eng.eSystem.xmlSerialization.annotations.*;
import eng.eSystem.xmlSerialization.exceptions.XmlSerializationException;
import eng.eSystem.xmlSerialization.supports.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class TypeMetaInfo {
  private Class type;
  private IValueParser customValueParser;
  private IElementParser customElementParser;
  private IList<FieldMetaInfo> fields;
  private IFactory customFactory;
  private ItemIgnoreList itemIgnores;
  private MappingList itemMappings;
  private MappingList keyMappings;
  private MappingList valueMappings;

  public ItemIgnoreList getItemIgnores() {
    return itemIgnores;
  }

  public static TypeMetaInfo decode(Class type) {

    if (type == null) {
      throw new IllegalArgumentException("Value of {type} cannot not be null.");
    }

    TypeMetaInfo ret;

    IValueParser customValueParser = getCustomValueParser(type);
    IElementParser customElementParser = getCustomElementParser(type);
    if (customValueParser != null || customElementParser != null)
      ret = new TypeMetaInfo(type, customValueParser, customElementParser);
    else {
      IFactory factoryBase = getXmlFactoryBase(type);
      IList<FieldMetaInfo> fieldMetaInfos = getFieldMetaInfos(type);

      IList<Mapping> itemMappings = new EList<>();
      itemMappings.add(getItemAtributeMappings(type));
      itemMappings.add(getItemElementMappings(type));

      IList<Mapping> keyMappings = new EList<>();
      keyMappings.add(getKeyAttributeMappings(type));
      keyMappings.add(getKeyElementMappings(type));

      IList<Mapping> valueMappings = new EList<>();
      valueMappings.add(getValueAttributeMappings(type));
      valueMappings.add(getValueElementMappings(type));

      IList<ItemIgnore> itemIgnores = new EList<>();
      itemIgnores.add(getItemIgnoreElements(type));
      itemIgnores.add(getItemIgnoreTypes(type));

      ret = new TypeMetaInfo(type, fieldMetaInfos, factoryBase, itemMappings, itemIgnores, keyMappings, valueMappings);
    }

    return ret;
  }

  private static IList<ItemIgnore> getItemIgnoreTypes(Class clz) {
    IList<ItemIgnore> ret = new EList<>();
    for (Annotation ann : clz.getDeclaredAnnotationsByType(XmlItemIgnoreType.class)) {
      XmlItemIgnoreType item = (XmlItemIgnoreType) ann;
      ItemIgnore ii = new ItemIgnoreType(item.type(), item.subClassIncluded());
      ret.add(ii);
    }
    return ret;
  }

  private static IList<ItemIgnore> getItemIgnoreElements(Class clz) {
    IList<ItemIgnore> ret = new EList<>();
    for (Annotation ann : clz.getDeclaredAnnotationsByType(XmlItemIgnoreElement.class)) {
      XmlItemIgnoreElement item = (XmlItemIgnoreElement) ann;
      ItemIgnore ii = new ItemIgnoreElement(item.elementName());
      ret.add(ii);
    }
    return ret;
  }

  public IReadOnlyList<Mapping> getKeyMappings() {
    return keyMappings;
  }

  public IReadOnlyList<Mapping> getValueMappings() {
    return valueMappings;
  }

  private static IList<Mapping> getItemElementMappings(Class clz) {
    IList<Mapping> mappings = new EList<>();
    for (Annotation ann : clz.getDeclaredAnnotationsByType(XmlItemElement.class)) {
      XmlItemElement item = (XmlItemElement) ann;
      Mapping m = Mapping.create(item);
      mappings.add(m);
    }
    return mappings;
  }

  private static IList<Mapping> getItemAtributeMappings(Class clz) {
    IList<Mapping> itemMappings = new EList<>();
    for (Annotation ann : clz.getDeclaredAnnotationsByType(XmlItemAttribute.class)) {
      XmlItemAttribute item = (XmlItemAttribute) ann;
      String elementName = item.attributeName();
      Class type = item.type();

      IParser customParser;
      if (Empty.isEmpty(item.parser()))
        customParser = null;
      else
        customParser = Shared.createInstance(item.parser());

      boolean subs = item.subclassesIncluded();

      Mapping itemMapping = new Mapping(elementName, type, true, subs, customParser);
      itemMappings.add(itemMapping);
    }
    return itemMappings;
  }

  private static IList<FieldMetaInfo> getFieldMetaInfos(Class type) {
    IReadOnlyList<Field> fields = getDeclaredFields(type);
    IList<FieldMetaInfo> fmts = new EList<>();
    for (Field field : fields) {
      if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) continue; // static are skipped
      FieldMetaInfo fmi = FieldMetaInfo.decode(field);
      fmts.add(fmi);
    }
    return fmts;
  }

  private static IFactory getXmlFactoryBase(Class type) {
    Class<? extends IFactory> customFactoryType;
    XmlFactory anFac = (XmlFactory) type.getDeclaredAnnotation(XmlFactory.class);
    customFactoryType = anFac == null ? null : anFac.value();
    IFactory factoryBase = null;
    if (customFactoryType != null)
      try {
        factoryBase = Shared.createInstance(customFactoryType);
      } catch (Exception ex) {
        throw new XmlSerializationException("Failed to create custom factory from " + customFactoryType.getName(), ex);
      }
    return factoryBase;
  }

  private static IValueParser getCustomValueParser(Class type) {
    IValueParser ret;
    XmlValueParser ann = (XmlValueParser) type.getDeclaredAnnotation(XmlValueParser.class);
    if (ann != null)
      try {
        ret = Shared.createInstance(ann.value());
      } catch (Exception ex) {
        throw new XmlSerializationException("Failed to create custom value parser from " + ann.value().getName() + " for type " + type.getName(), ex);
      }
    else
      ret = null;
    return ret;
  }

  private static IElementParser getCustomElementParser(Class type) {
    IElementParser ret;
    XmlElementParser ann = (XmlElementParser) type.getDeclaredAnnotation(XmlElementParser.class);
    if (ann != null)
      try {
        ret = Shared.createInstance(ann.value());
      } catch (Exception ex) {
        throw new XmlSerializationException("Failed to create custom element parser from " + ann.value().getName() + " for type " + type.getName(), ex);
      }
    else
      ret = null;
    return ret;
  }

  private static IReadOnlyList<Field> getDeclaredFields(Class c) {
    IList<Field> ret = new EList<>();
    Field[] fs;

    while (c != null) {
      fs = c.getDeclaredFields();
      for (Field f : fs) {
        ret.add(f);
      }
      c = c.getSuperclass();
    }
    return ret;
  }

  private static IList<Mapping> getKeyElementMappings(Class clz) {
    IList<Mapping> mappings = new EList<>();
    for (Annotation ann : clz.getDeclaredAnnotationsByType(XmlMapKeyElement.class)) {
      XmlMapKeyElement item = (XmlMapKeyElement) ann;
      Mapping m = Mapping.create(item);
      mappings.add(m);
    }
    return mappings;
  }

  private static IList<Mapping> getKeyAttributeMappings(Class clz) {
    IList<Mapping> mappings = new EList<>();
    for (Annotation ann : clz.getDeclaredAnnotationsByType(XmlMapKeyAttribute.class)) {
      XmlMapKeyAttribute item = (XmlMapKeyAttribute) ann;
      Mapping m = Mapping.create(item);
      mappings.add(m);
    }
    return mappings;
  }

  private static IList<Mapping> getValueElementMappings(Class clz) {
    IList<Mapping> mappings = new EList<>();
    for (Annotation ann : clz.getDeclaredAnnotationsByType(XmlMapValueElement.class)) {
      XmlMapValueElement item = (XmlMapValueElement) ann;
      Mapping m = Mapping.create(item);
      mappings.add(m);
    }
    return mappings;
  }

  private static IList<Mapping> getValueAttributeMappings(Class clz) {
    IList<Mapping> mappings = new EList<>();
    for (Annotation ann : clz.getDeclaredAnnotationsByType(XmlMapValueAttribute.class)) {
      XmlMapValueAttribute item = (XmlMapValueAttribute) ann;
      Mapping m = Mapping.create(item);
      mappings.add(m);
    }
    return mappings;
  }

  public TypeMetaInfo(Class type, IValueParser customValueParser, IElementParser customElementParser) {
    this.type = type;
    this.customValueParser = customValueParser;
    if (customValueParser != null && customElementParser == null)
      this.customElementParser = new WrappedValueParser(this.customValueParser);
    else
      this.customElementParser = customElementParser;
    this.fields = null;
    this.customFactory = null;
    this.keyMappings = null;
    this.valueMappings = null;
  }

  public TypeMetaInfo(Class type, IList<FieldMetaInfo> fields, IFactory customFactory, IList<Mapping> itemMappings, IList<ItemIgnore> itemIgnores, IList<Mapping> keyMappings, IList<Mapping> valueMappings) {
    this.type = type;
    this.fields = fields;
    this.customFactory = customFactory;
    this.itemMappings = new MappingList(itemMappings);
    this.keyMappings = new MappingList(keyMappings);
    this.valueMappings = new MappingList(valueMappings);
    this.itemIgnores = new ItemIgnoreList(itemIgnores);
    this.customValueParser = null;
    this.customElementParser = null;
  }

  public IReadOnlyList<Mapping> getItemMappings() {
    return itemMappings;
  }

  public Class getType() {
    return type;
  }

  public IFactory getCustomFactory() {
    return customFactory;
  }

  public IValueParser getCustomValueParser() {
    return customValueParser;
  }

  public IElementParser getCustomElementParser() {
    return customElementParser;
  }

  public IReadOnlyList<FieldMetaInfo> getFields() {
    return fields;
  }

  @Override
  public String toString() {
    return type.getName() + "{tmi}";
  }

  public void updateXmlItemElementMapping(String name, Class type, boolean isTypeSubtypeIncluded, IElementParser parser) {
    this.itemMappings.removeOverlying(false, name, type);
    Mapping m = Mapping.createCustom(name, type, isTypeSubtypeIncluded, false, parser);
    this.itemMappings.add(m);
  }

  public void updateXmlItemAttributeMapping(String name, Class type, boolean isTypeSubtypeIncluded, IValueParser parser) {
    this.itemMappings.removeOverlying(true, name, type);
    Mapping m = Mapping.createCustom(name, type, isTypeSubtypeIncluded, true, parser);
    this.itemMappings.add(m);
  }

  public void updateXmlMapKeyElementMapping(String name, Class type, boolean isTypeSubtypeIncluded, IElementParser parser) {
    this.keyMappings.removeOverlying(false, name, type);
    Mapping m = Mapping.createCustom(name, type, isTypeSubtypeIncluded, false, parser);
    this.keyMappings.add(m);
  }

  public void updateXmlMapKeyAttributeMapping(String name, Class type, boolean isTypeSubtypeIncluded, IValueParser parser) {
    this.keyMappings.removeOverlying(true, name, type);
    Mapping m = Mapping.createCustom(name, type, isTypeSubtypeIncluded, true, parser);
    this.keyMappings.add(m);
  }

  public void updateXmlMapValueElementMapping(String name, Class type, boolean isTypeSubtypeIncluded, IElementParser parser) {
    this.valueMappings.removeOverlying(false, name, type);
    Mapping m = Mapping.createCustom(name, type, isTypeSubtypeIncluded, false, parser);
    this.valueMappings.add(m);
  }

  public void updateXmlMapValueAttributeMapping(String name, Class type, boolean isTypeSubtypeIncluded, IValueParser parser) {
    this.valueMappings.removeOverlying(true, name, type);
    Mapping m = Mapping.createCustom(name, type, isTypeSubtypeIncluded, true, parser);
    this.valueMappings.add(m);
  }

  public void updateCustomValueParser(IValueParser parser) {
    this.customValueParser = parser;
    if (this.customElementParser == null)
      this.customElementParser = new WrappedValueParser(parser);
  }

  public void updateCustomElementParser(IElementParser parser) {
    this.customElementParser = parser;
  }

  public void updateCustomFactory(IFactory factory) {
    this.customFactory = factory;
  }
}
