package eng.eSystem.xmlSerialization.meta;

import eng.eSystem.collections.EList;
import eng.eSystem.collections.IList;
import eng.eSystem.collections.IReadOnlyList;
import eng.eSystem.xmlSerialization.annotations.*;
import eng.eSystem.xmlSerialization.supports.IElementParser;
import eng.eSystem.xmlSerialization.supports.IValueParser;

import java.lang.reflect.Field;

public class FieldMetaInfo {

  public enum eNecessity {
    mandatory,
    optional,
    ignore
  }

  private Field field;
  private eNecessity necessity;
  private MappingList mappings;
  private MappingList itemMappings;
  private MappingList mapKeyMappings;
  private MappingList mapValueMappings;

  public IReadOnlyList<Mapping> getMapKeyMappings() {
    return mapKeyMappings;
  }

  public IReadOnlyList<Mapping> getMapValueMappings() {
    return mapValueMappings;
  }

  public Field getField() {
    return field;
  }

  public eNecessity getNecessity() {
    return necessity;
  }

  public void updateNecessity(eNecessity necessity) {
    this.necessity = necessity;
  }

  public FieldMetaInfo(Field field, eNecessity necessity, IList<Mapping> mappings, IList<Mapping> itemMappings, IList<Mapping> mapKeyMappings, IList<Mapping> mapValueMappings) {
    this.field = field;
    this.necessity = necessity;
    this.mappings = new MappingList(mappings);
    this.itemMappings = new MappingList(itemMappings);
    this.mapKeyMappings = new MappingList(mapKeyMappings);
    this.mapValueMappings = new MappingList(mapValueMappings);
  }

  public IReadOnlyList<Mapping> getMappings() {
    return mappings;
  }

  public IReadOnlyList<Mapping> getItemMappings() {
    return itemMappings;
  }

  public String getLocation(boolean useLong){
    return
        (useLong ? field.getDeclaringClass().getName() : field.getDeclaringClass().getSimpleName()) +
            "." + field.getName();
  }

  public static FieldMetaInfo decode(Field field) {

    eNecessity usability = geteUsability(field);

    IList<Mapping> maps = new EList<>();
    maps.add(getAttributeMappings(field));
    maps.add(getMappings(field));

    IList<Mapping> itemMaps = new EList<>();
    itemMaps.add(getItemAttributeMappings(field));
    itemMaps.add(getItemElementMappings(field));


    IList<Mapping> keyMaps = new EList<>();
    keyMaps.add(getKeyAttributeMappings(field));
    keyMaps.add(getKeyElementMappings(field));

    IList<Mapping> valMaps = new EList<>();
    valMaps.add(getValueAttributeMappings(field));
    valMaps.add(getValueElementMappings(field));


    FieldMetaInfo ret = new FieldMetaInfo(field, usability, maps, itemMaps, keyMaps, valMaps);
    return ret;
  }

  private static IList<Mapping> getMappings(Field field) {
    IList<Mapping> mappings = new EList<>();
    for (XmlElement item : field.getDeclaredAnnotationsByType(XmlElement.class)) {
      Mapping m = Mapping.create(item);
      mappings.add(m);
    }
    return mappings;
  }
  private static IList<Mapping> getAttributeMappings(Field field) {
    IList<Mapping> mappings = new EList<>();
    for (XmlAttribute item : field.getDeclaredAnnotationsByType(XmlAttribute.class)) {
      Mapping m = Mapping.create(item);
      mappings.add(m);
    }
    return mappings;
  }

  private static IList<Mapping> getItemElementMappings(Field field) {
    IList<Mapping> mappings = new EList<>();
    for (XmlItemElement item : field.getDeclaredAnnotationsByType(XmlItemElement.class)) {
      Mapping m = Mapping.create(item);
      mappings.add(m);
    }
    return mappings;
  }
  private static IList<Mapping> getItemAttributeMappings(Field field) {
    IList<Mapping> mappings = new EList<>();
    for (XmlItemAttribute item : field.getDeclaredAnnotationsByType(XmlItemAttribute.class)) {
      Mapping m = Mapping.create(item);
      mappings.add(m);
    }
    return mappings;
  }

  private static IList<Mapping> getKeyElementMappings(Field field) {
    IList<Mapping> mappings = new EList<>();
    for (XmlMapKeyElement item : field.getDeclaredAnnotationsByType(XmlMapKeyElement.class)) {
      Mapping m = Mapping.create(item);
      mappings.add(m);
    }
    return mappings;
  }
  private static IList<Mapping> getKeyAttributeMappings(Field field) {
    IList<Mapping> mappings = new EList<>();
    for (XmlMapKeyAttribute item : field.getDeclaredAnnotationsByType(XmlMapKeyAttribute.class)) {
      Mapping m = Mapping.create(item);
      mappings.add(m);
    }
    return mappings;
  }

  private static IList<Mapping> getValueElementMappings(Field field) {
    IList<Mapping> mappings = new EList<>();
    for (XmlMapValueElement item : field.getDeclaredAnnotationsByType(XmlMapValueElement.class)) {
      Mapping m = Mapping.create(item);
      mappings.add(m);
    }
    return mappings;
  }
  private static IList<Mapping> getValueAttributeMappings(Field field) {
    IList<Mapping> mappings = new EList<>();
    for (XmlMapValueAttribute item : field.getDeclaredAnnotationsByType(XmlMapValueAttribute.class)) {
      Mapping m = Mapping.create(item);
      mappings.add(m);
    }
    return mappings;
  }

  private static eNecessity geteUsability(Field field) {
    eNecessity usability;
    if (field.getDeclaredAnnotation(XmlIgnore.class) != null)
      usability = eNecessity.ignore;
    else if (field.getDeclaredAnnotation(XmlOptional.class) != null)
      usability = eNecessity.optional;
    else
      usability = eNecessity.mandatory;
    return usability;
  }

  @Override
  public String toString() {
    return this.getLocation(false) + " {fmi}";
  }

  public void updateXmlElementMapping(String name, Class type, boolean isTypeSubtypeIncluded, IElementParser parser) {
    mappings.removeOverlying(false, name, type);
    Mapping m = Mapping.createCustom(name, type, isTypeSubtypeIncluded, false, parser);
    mappings.add(m);
  }

  public void updateXmlAttributeMapping(String name, Class type, boolean isTypeSubtypeIncluded, IValueParser parser) {
    mappings.removeOverlying(true, name, type);
    Mapping m = Mapping.createCustom(name, type, isTypeSubtypeIncluded, true, parser);
    mappings.add(m);
  }


  public void updateXmlItemElementMapping(String name, Class type, boolean isTypeSubtypeIncluded, IElementParser parser) {
    itemMappings.removeOverlying(false, name, type);
    Mapping m = Mapping.createCustom(name, type, isTypeSubtypeIncluded, false, parser);
    itemMappings.add(m);
  }

  public void updateXmlItemAttributeMapping(String name, Class type, boolean isTypeSubtypeIncluded, IValueParser parser) {
    itemMappings.removeOverlying(true, name, type);
    Mapping m = Mapping.createCustom(name, type, isTypeSubtypeIncluded, true, parser);
    itemMappings.add(m);
  }






  public void updateXmlMapKeyElementMapping(String name, Class type, boolean isTypeSubtypeIncluded, IElementParser parser) {
    mapKeyMappings.removeOverlying(false, name, type);
    Mapping m = Mapping.createCustom(name, type, isTypeSubtypeIncluded, false, parser);
    mapKeyMappings.add(m);
  }

  public void updateXmlMapKeyAttributeMapping(String name, Class type, boolean isTypeSubtypeIncluded, IValueParser parser) {
    mapKeyMappings.removeOverlying(true, name, type);
    Mapping m = Mapping.createCustom(name, type, isTypeSubtypeIncluded, true, parser);
    mapKeyMappings.add(m);
  }






  public void updateXmlMapValueElementMapping(String name, Class type, boolean isTypeSubtypeIncluded, IElementParser parser) {
    mapValueMappings.removeOverlying(false, name, type);
    Mapping m = Mapping.createCustom(name, type, isTypeSubtypeIncluded, false, parser);
    mapValueMappings.add(m);
  }

  public void updateXmlMapValueAttributeMapping(String name, Class type, boolean isTypeSubtypeIncluded, IValueParser parser) {
    mapValueMappings.removeOverlying(true, name, type);
    Mapping m = Mapping.createCustom(name, type, isTypeSubtypeIncluded, true, parser);
    mapValueMappings.add(m);
  }
}
