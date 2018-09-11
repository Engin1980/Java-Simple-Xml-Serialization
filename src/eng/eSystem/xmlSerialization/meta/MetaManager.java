package eng.eSystem.xmlSerialization.meta;


import eng.eSystem.Tuple;
import eng.eSystem.collections.*;
import eng.eSystem.eXml.XElement;
import eng.eSystem.xmlSerialization.TypeMappingManager;
import eng.eSystem.xmlSerialization.supports.IElementParser;
import eng.eSystem.xmlSerialization.supports.IParser;
import eng.eSystem.xmlSerialization.supports.IValueParser;

import java.util.TimerTask;

import static eng.eSystem.utilites.FunctionShortcuts.coalesce;

public class MetaManager {
  private IMap<Class, TypeMetaInfo> map = new EMap<>();


  public TypeMetaInfo getTypeMetaInfo(Class type) {
    if (map.containsKey(type) == false) {
      decodeTypeMetaInfo(type);
    }
    TypeMetaInfo ret = map.get(type);
    return ret;
  }

  public Applicator getFieldApplicator(FieldMetaInfo fmi, Object value) {
    Class declaredType = fmi.getField().getType();

    Class realType = value == null ? declaredType : value.getClass();
    TypeMetaInfo tmi = this.getTypeMetaInfo(realType);
    Mapping mapping = Mapping.tryGetBestByType(fmi.getMappings(), realType);

    String name;
    IParser parser;
    Class type;
    boolean isAttribute;

    if (mapping == null) {
      parser = coalesce(tmi.getCustomValueParser(), tmi.getCustomElementParser());
      name = fmi.getField().getName();
      type = fmi.getField().getType();
      if (parser == null)
        isAttribute = TypeMappingManager.isSimpleTypeOrEnum(type);
      else
        isAttribute = parser instanceof IValueParser;
    } else {
      name = coalesce(mapping.getName(), fmi.getField().getName());
      type = coalesce(mapping.getType(), value == null ? fmi.getField().getType() : value.getClass());
      isAttribute = mapping.isAttribute();
      parser = coalesce(mapping.getCustomParser(), tmi.getCustomValueParser(), tmi.getCustomElementParser());
    }

    Applicator ret = new Applicator(name, type, parser, isAttribute);

    return ret;
  }

  public Applicator getFieldApplicator(XElement element, FieldMetaInfo fmi) {
    Applicator ret;
    IList<String> attNames = element.getAttributes().getKeys().toList();
    IList<String> elmNames = element.getChildren().select(q -> q.getName());
    Mapping mapping;

    String name;
    IParser parser;
    Class type;
    boolean isAttribute;


    mapping = Mapping.tryGetBestByName(fmi.getMappings(), attNames, elmNames);
    if (mapping == null) {
      // not explicit mapping
      if (attNames.contains(fmi.getField().getName())) {
        // default attribute mapping
        mapping = Mapping.createDefault(fmi.getField().getName(), fmi.getField().getType(), true);
      } else if (elmNames.contains(fmi.getField().getName())) {
        // default element mapping
        mapping = Mapping.createDefault(fmi.getField().getName(), fmi.getField().getType(), false);
      } else {
        // source not found
        return null;
      }
    }

    name = coalesce(mapping.getName(), fmi.getField().getName());
    type = coalesce(mapping.getType(), fmi.getField().getType());
    isAttribute = mapping.isAttribute();
    parser = mapping.getCustomParser();

    if (isAttribute) {
      if (attNames.contains(name) == false) return null;
    } else {
      if (elmNames.contains(name) == false) return null;
    }

    if (mapping.isElement()) {
      Class realType = TypeMappingManager.tryGetCustomTypeByElement(element.getChildren(name).getFirst());
      if (realType != null) type = realType;
    }

    TypeMetaInfo tmi = this.getTypeMetaInfo(type);
    if (parser == null)
      parser = mapping.isAttribute() ? tmi.getCustomValueParser() : tmi.getCustomElementParser();

    ret = new Applicator(name, type, parser, isAttribute);
    return ret;
  }

  public Applicator getEmptyElementApplicator(String name, Class type) {
    Applicator ret = new Applicator(name, type, null, false);
    return ret;
  }

  public Applicator getItemApplicator2(XElement element, Class expectedItemType, FieldMetaInfo fmi, TypeMetaInfo parentTmi) {
    Applicator app = this.getComplexApplicator2(
        element,
        fmi == null ? null : fmi.getItemMappings(),
        parentTmi == null ? null : parentTmi.getItemMappings(),
        "item", "item", expectedItemType, false);
    return app;
  }

  public Applicator getMapKeyApplicator2(XElement element, Class expectedItemType, FieldMetaInfo fmi, TypeMetaInfo parentTmi) {
    Applicator app = this.getComplexApplicator2(element,
        fmi == null ? null : fmi.getMapKeyMappings(),
        parentTmi == null ? null : parentTmi.getKeyMappings(),
        "key", "key", expectedItemType, true);
    return app;
  }

  public Applicator getMapValueApplicator2(XElement element, Class expectedItemType, FieldMetaInfo fmi, TypeMetaInfo parentTmi) {
    Applicator app = this.getComplexApplicator2(element,
        fmi == null ? null : fmi.getMapValueMappings(),
        parentTmi == null ? null : parentTmi.getValueMappings(),
        "value", "value", expectedItemType, true);

    return app;
  }

  public Applicator getItemApplicator(Object item,
                                      Class declaredItemType, TypeMetaInfo parentIterableTmi, FieldMetaInfo relativeFmi) {
    Applicator ret = getComplexApplicator(item, "item", declaredItemType,
        relativeFmi == null ? null : relativeFmi.getItemMappings(),
        parentIterableTmi == null ? null : parentIterableTmi.getItemMappings());
    return ret;
  }

  public Applicator getMapKeyApplicator(Object key, Class declaredItemType, TypeMetaInfo parentMapTmi, FieldMetaInfo relativeFmi) {

    Applicator ret = getComplexApplicator(key, "key", declaredItemType,
        relativeFmi == null ? null : relativeFmi.getMapKeyMappings(),
        parentMapTmi == null ? null : parentMapTmi.getKeyMappings());
    return ret;
  }

  public Applicator getMapValueApplicator(Object value, Class declaredItemType, TypeMetaInfo parentMapTmi, FieldMetaInfo relativeFmi) {

    Applicator ret = getComplexApplicator(value, "value", declaredItemType,
        relativeFmi == null ? null : relativeFmi.getMapValueMappings(),
        parentMapTmi == null ? null : parentMapTmi.getValueMappings());
    return ret;
  }

  public boolean isIgnoredItemElement(XElement itemElement, FieldMetaInfo relativeFmi, TypeMetaInfo parentTmi, boolean isMap) {
    String name = itemElement.getName();
    if (relativeFmi != null) {
      if (relativeFmi.getItemIgnores().getElements().isAny(q -> q.getElementName().equals(name)))
        return true;
      if (!isMap)
        if (relativeFmi.getItemMappings().isAny(q -> q.getName().equals(name)) ||
            relativeFmi.getItemMappings().isAny(q -> q.getName() == null))
          return false;
    }
    if (parentTmi != null) {
      if (parentTmi.getItemIgnores().getElements().isAny(q -> q.getElementName().equals(name)))
        return true;
    }
    return false;
  }

  public Tuple<IValueParser, IElementParser> getCustomParsersByType(Class originalType) {
    IValueParser vp = null;
    IElementParser ep = null;

    Class type = originalType;
    while (type != null) {
      TypeMetaInfo tmi = this.getTypeMetaInfo(type);
      if (vp == null && tmi.getCustomValueParser() != null) vp = tmi.getCustomValueParser();
      if (ep == null && tmi.getCustomElementParser() != null) ep = tmi.getCustomElementParser();
      if (vp != null && ep != null) break;
      type = type.getSuperclass();
    }
    return new Tuple<>(vp,ep);
  }

  private Applicator getComplexApplicator2(XElement element, IReadOnlyList<Mapping> fmiMappings, IReadOnlyList<Mapping> tmiMappings,
                                           String defaultElementName, String defaultAttributeName, Class expectedType, boolean isMap) {
    if (expectedType == null) {
      throw new IllegalArgumentException("Value of {expectedType} cannot not be null.");
    }

    IList<String> attNames = element.getAttributes().getKeys().toList();
    IList<String> elmNames;
    if (isMap)
      elmNames = element.getChildren().select(q -> q.getName());
    else
      elmNames = new EList<>(new String[]{element.getName()});

    Applicator app;

    String name;
    IParser parser;
    Class type;
    boolean isAttribute;
    {
      Mapping mapping = null;
      if (fmiMappings != null)
        mapping = Mapping.tryGetBestByName(fmiMappings, attNames, elmNames);
      if (mapping == null && tmiMappings != null)
        mapping = Mapping.tryGetBestByName(tmiMappings, attNames, elmNames);
      if (mapping != null) {
        name = coalesce(mapping.getName(), defaultElementName);
        type = coalesce(mapping.getType(), expectedType);
        isAttribute = mapping.isAttribute();
        parser = mapping.getCustomParser();
      } else {
        type = expectedType;
        parser = null;
        if (attNames.contains(defaultAttributeName)) {
          name = defaultAttributeName;
          isAttribute = true;
        } else {
          name = defaultElementName;
          isAttribute = false;
        }
      }
    }

    if (isAttribute == false) {
      XElement tmp;
      if (isMap)
        tmp = element.getChild(name);
      else
        tmp = element;
      Class realType = TypeMappingManager.tryGetCustomTypeByElement(tmp);
      if (realType != null)
        type = realType;
    }

    Tuple<IValueParser, IElementParser> customParser = this.getCustomParsersByType(type);
    if (parser == null)
      if (isAttribute)
        parser = customParser.getA();
      else
        parser = customParser.getB();

    app = new Applicator(name, type, parser, isAttribute);
    return app;
  }

  private Applicator getComplexApplicator(
      Object object, String defaultName, Class declaredObjectType,
      IReadOnlyList<Mapping> fmiRelatedMappings, IReadOnlyList<Mapping> tmiRelatedMappings) {
    Mapping itemMapping = null;
    Class realItemType = object == null ? Object.class : object.getClass();

    String name;
    IParser parser;
    Class type;
    boolean isAttribute;

    if (fmiRelatedMappings != null)
      itemMapping = Mapping.tryGetBestByType(fmiRelatedMappings, realItemType);
    if (tmiRelatedMappings != null)
      itemMapping = Mapping.tryGetBestByType(tmiRelatedMappings, realItemType);

    TypeMetaInfo itemTmi = this.getTypeMetaInfo(realItemType);


    if (itemMapping == null) {
      name = defaultName;
      type = declaredObjectType;
      isAttribute = declaredObjectType.equals(realItemType) &&
          (TypeMappingManager.isSimpleTypeOrEnum(realItemType) || itemTmi.getCustomValueParser() != null);
      Tuple<IValueParser, IElementParser> customParser = this.getCustomParsersByType(itemTmi.getType());
      if (isAttribute)
        parser = customParser.getA();
      else
        parser = customParser.getB();
    } else {
      name = coalesce(itemMapping.getName(), defaultName);
      type = coalesce(declaredObjectType, itemMapping.getType());
      parser = itemMapping.getCustomParser();
      isAttribute = itemMapping.isAttribute();
    }

    Applicator ret = new Applicator(name, type, parser, isAttribute);
    return ret;
  }

  private void decodeTypeMetaInfo(Class type) {

    TypeMetaInfo tmi = TypeMetaInfo.decode(type);

    map.set(type, tmi);
  }


}
