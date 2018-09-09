//package eng.eSystem.xmlSerialization.meta.newe;
//
//import eng.eSystem.collections.EList;
//import eng.eSystem.collections.IList;
//import eng.eSystem.xmlSerialization.annotations.*;
//import eng.eSystem.xmlSerialization.exceptions.XmlSerializationException;
//import eng.eSystem.xmlSerialization.supports.IElementParser;
//import eng.eSystem.xmlSerialization.supports.IFactory;
//import eng.eSystem.xmlSerialization.supports.IValueParser;
//
//import java.lang.annotation.Annotation;
//
//public class MetaFactory {
//
//  public static void decodeType(Class type, MetaManager mm) {
//    IList<MetaItem> ret = new EList<>();
//
//    _decodeTypeAnnotations(type, mm);
//    _decodeTypeFieldAnnotations(type);
//  }
//
//  private static void _decodeTypeAnnotations(Class type, MetaManager mm) {
//    TypeParsers.decodeCustomValueParser(type, mm);
//    TypeParsers.decodeCustomElementParser(type, mm);
//    Factories.decodeCustomFactory(type, mm);
//
//    Mappings.decodeItemAttributeMappings(type, mm);
//    Mappings.decodeItemElementMappings(type, mm);
//
//    Mappings.decodeKeyAttributeMappings(type, mm);
//    Mappings.decodeKeyElementMappings(type, mm);
//
//    Mappings.decodeValueAttributeMappings(type, mm);
//    Mappings.decodeValueElementMappings(type, mm);
//  }
//}
//
//class TypeParsers {
//  static void decodeCustomValueParser(Class type, MetaManager mm) {
//    XmlValueParser ann = (XmlValueParser) type.getDeclaredAnnotation(XmlValueParser.class);
//    if (ann != null) {
//      IValueParser ret;
//      try {
//        ret = Shared.createInstance(ann.value());
//      } catch (Exception ex) {
//        throw new XmlSerializationException("Failed to create custom value parser from " + ann.value().getName() + " for type " + type.getName(), ex);
//      }
//      if (ret != null)
//        mm.valueParsers.add(new CustomValueParser(type, null, ret));
//    }
//  }
//
//  static void decodeCustomElementParser(Class type, MetaManager mm) {
//    XmlElementParser ann = (XmlElementParser) type.getDeclaredAnnotation(XmlElementParser.class);
//    if (ann != null) {
//      IElementParser ret;
//      try {
//        ret = Shared.createInstance(ann.value());
//      } catch (Exception ex) {
//        throw new XmlSerializationException("Failed to create custom element parser from " + ann.value().getName() + " for type " + type.getName(), ex);
//      }
//      if (ret != null)
//        mm.elementParsers.add(new CustomElementParser(type, null, ret));
//    }
//  }
//}
//
//class Factories {
//  static void decodeCustomFactory(Class type, MetaManager mm) {
//    XmlFactory anFac = (XmlFactory) type.getDeclaredAnnotation(XmlFactory.class);
//    Class<? extends IFactory> customFactoryType = anFac == null ? null : anFac.value();
//    if (customFactoryType != null) {
//      IFactory factoryBase;
//      try {
//        factoryBase = Shared.createInstance(customFactoryType);
//      } catch (Exception ex) {
//        throw new XmlSerializationException("Failed to create custom factory from " + customFactoryType.getName(), ex);
//      }
//      mm.factories.add(new Factory(type, factoryBase));
//    }
//  }
//}
//
//class Mappings {
//  static void decodeItemAttributeMappings(Class clz, MetaManager mm) {
//    for (Annotation ann : clz.getDeclaredAnnotationsByType(XmlItemAttribute.class)) {
//      XmlItemAttribute item = (XmlItemAttribute) ann;
//      String elementName = item.attributeName();
//      Class type = item.type();
//
//      if (!Empty.isEmpty(item.parser())) {
//        IValueParser customParser = Shared.createInstance(item.parser());
//        CustomValueParser p = new CustomValueParser(type, clz, customParser);
//        mm.valueParsers.add(p);
//      }
//
//      boolean subs = item.subclassesIncluded();
//
//      // todo last parameter should be editable
//      TypeMapping tm = new TypeMapping(elementName, type, true, subs, clz, false);
//      mm.typeItemMappings.add(tm);
//
//    }
//  }
//
//  static void decodeItemElementMappings(Class clz, MetaManager mm) {
//    for (Annotation ann : clz.getDeclaredAnnotationsByType(XmlItemElement.class)) {
//      XmlItemElement item = (XmlItemElement) ann;
//      String elementName = item.elementName();
//      Class type = item.type();
//
//      if (!Empty.isEmpty(item.parser())) {
//        IElementParser customParser = Shared.createInstance(item.parser());
//        CustomElementParser p = new CustomElementParser(type, clz, customParser);
//        mm.elementParsers.add(p);
//      }
//
//      boolean subs = item.subclassesIncluded();
//
//      // todo last parameter should be editable
//      TypeMapping tm = new TypeMapping(elementName, type, false, subs, clz, false);
//      mm.typeItemMappings.add(tm);
//
//    }
//  }
//
//  static void decodeKeyAttributeMappings(Class clz, MetaManager mm) {
//    for (Annotation ann : clz.getDeclaredAnnotationsByType(XmlMapKeyAttribute.class)) {
//      XmlMapKeyAttribute item = (XmlMapKeyAttribute) ann;
//      String elementName = item.attributeName();
//      Class type = item.type();
//
//      if (!Empty.isEmpty(item.parser())) {
//        IValueParser customParser = Shared.createInstance(item.parser());
//        CustomValueParser p = new CustomValueParser(type, clz, customParser);
//        mm.valueParsers.add(p);
//      }
//
//      boolean subs = item.subclassesIncluded();
//
//      // todo last parameter should be editable
//      TypeMapping tm = new TypeMapping(elementName, type, true, subs, clz, false);
//      mm.typeKeyMappings.add(tm);
//
//    }
//  }
//
//  static void decodeKeyElementMappings(Class clz, MetaManager mm) {
//    for (Annotation ann : clz.getDeclaredAnnotationsByType(XmlMapKeyElement.class)) {
//      XmlMapKeyElement item = (XmlMapKeyElement) ann;
//      String elementName = item.elementName();
//      Class type = item.type();
//
//      if (!Empty.isEmpty(item.parser())) {
//        IElementParser customParser = Shared.createInstance(item.parser());
//        CustomElementParser p = new CustomElementParser(type, clz, customParser);
//        mm.elementParsers.add(p);
//      }
//
//      boolean subs = item.subclassesIncluded();
//
//      // todo last parameter should be editable
//      TypeMapping tm = new TypeMapping(elementName, type, false, subs, clz, false);
//      mm.typeKeyMappings.add(tm);
//    }
//  }
//
//  static void decodeValueAttributeMappings(Class clz, MetaManager mm) {
//    for (Annotation ann : clz.getDeclaredAnnotationsByType(XmlMapValueAttribute.class)) {
//      XmlMapValueAttribute item = (XmlMapValueAttribute) ann;
//      String elementName = item.attributeName();
//      Class type = item.type();
//
//      if (!Empty.isEmpty(item.parser())) {
//        IValueParser customParser = Shared.createInstance(item.parser());
//        CustomValueParser p = new CustomValueParser(type, clz, customParser);
//        mm.valueParsers.add(p);
//      }
//
//      boolean subs = item.subclassesIncluded();
//
//      // todo last parameter should be editable
//      TypeMapping tm = new TypeMapping(elementName, type, true, subs, clz, false);
//      mm.typeValueMappings.add(tm);
//
//    }
//  }
//
//  static void decodeValueElementMappings(Class clz, MetaManager mm) {
//    for (Annotation ann : clz.getDeclaredAnnotationsByType(XmlMapValueElement.class)) {
//      XmlMapValueElement item = (XmlMapValueElement) ann;
//      String elementName = item.elementName();
//      Class type = item.type();
//
//      if (!Empty.isEmpty(item.parser())) {
//        IElementParser customParser = Shared.createInstance(item.parser());
//        CustomElementParser p = new CustomElementParser(type, clz, customParser);
//        mm.elementParsers.add(p);
//      }
//
//      boolean subs = item.subclassesIncluded();
//
//      // todo last parameter should be editable
//      TypeMapping tm = new TypeMapping(elementName, type, false, subs, clz, false);
//      mm.typeValueMappings.add(tm);
//    }
//  }
//}
