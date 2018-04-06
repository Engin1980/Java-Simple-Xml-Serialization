/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eng.eSystem.xmlSerialization;

import eng.eSystem.collections.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static eng.eSystem.xmlSerialization.Shared.getDeclaredFields;
import static eng.eSystem.xmlSerialization.Shared.isRegexMatch;

/**
 * @author Marek
 */
class Parser {

  private static final Object UNSET = new Object();
  private static final String SEPARATOR = "\t";
  private final Settings settings;
  private Map<String, String> typeMap = new HashMap<>();
  private int logIndent = 0;

  Parser(Settings settings) {
    this.settings = settings;
  }

  public synchronized Object deserialize(Element root, Class objectType) throws XmlDeserializationException {
    if (root == null) {
      throw new IllegalArgumentException("Value of {el} cannot not be null.");
    }
    if (objectType == null) {
      throw new IllegalArgumentException("Value of {objectType} cannot not be null.");
    }

    loadTypeMaps(root);

    Class c = objectType;
    Object ret;
    ret = parseIt(root, c);

    return ret;
  }

  public Object parseArray(Element el, Class c) throws XmlDeserializationException {

    Object ret;
    List<Element> children = getElements(el);
    removeTypeMapElementIfExist(children);
    int cnt = children.size();
    Class itemType = c.getComponentType();
    ret = createArrayInstance(itemType, cnt);

    for (int i = 0; i < children.size(); i++) {
      Element e = children.get(i);
      Object itemValue = parseIt(e, itemType);
      Array.set(ret, i, itemValue);

      // mappings, now ingored::
//      Class itemType = getItemType(classFieldKey, e, false);
//      if (itemType == null) itemType = itemType;
//      if (Mapping.isSimpleTypeOrEnum(itemType)) {
//        String value = e.getTextContent();
//        Object val = convertToType(value, itemType);
//        Array.set(arr, i, val);
//      } else {
//        // list item is complex type
//        Object inn = createObjectInstance(itemType);
//        Array.set(arr, i, inn);
//
//        fillObject(e, inn);
//      }
    }
    return ret;
  }

  private void logVerbose(String format, Object... params) {
    if (settings.isVerbose())
      Shared.log(Shared.eLogType.info, format, params);
  }

  private void loadTypeMaps(Element el) {
    typeMap.clear();

    Element elm;
    try {
      elm = getElement(el, Shared.TYPE_MAP_ELEMENT_NAME, false);
    } catch (Exception ex) {
      elm = null;
    }
    if (elm != null) {
      List<Element> types = getElements(elm);
      for (Element type : types) {
        String key = type.getAttribute(Shared.TYPE_MAP_KEY_ATTRIBUTE_NAME);
        String fullName = type.getAttribute(Shared.TYPE_MAP_FULL_ATTRIBUTE_NAME);
        typeMap.put(key, fullName);
      }
    }
  }

  private Object parseIt(Element el, Class type) throws XmlDeserializationException {
    logIndent++;
    logVerbose("deserialize <%s> --> %s", el.getNodeName(), type.getName());

    Object ret;
    try {
      if (isNullValuedElement(el))
        ret = null;
      else {
        IElementParser customElementParser = Shared.tryGetCustomElementParser(type, settings);
        if (customElementParser != null) {
          ret = convertElementByElementParser(el, customElementParser);
        } else if (Mapping.isSimpleTypeOrEnum(type)) {
          // jednoduchý typ
          ret = parsePrimitiveFromElement(el, type);
        } else if (List.class.isAssignableFrom(type)) {
          ret = parseList(el, type);
        } else if (IList.class.isAssignableFrom(type)) {
          ret = parseIList(el, type);
        } else if (Set.class.isAssignableFrom(type)) {
          ret = parseSet(el, type);
        } else if (ISet.class.isAssignableFrom(type)) {
          ret = parseISet(el, type);
        } else if (Map.class.isAssignableFrom(type)) {
          ret = parseMap(el, type);
        } else if (IMap.class.isAssignableFrom(type)) {
          ret = parseIMap(el, type);
        } else if (type.isArray()) {
          ret = parseArray(el, type);
        } else {
          ret = parseObject(el, type);
        }
      }
    } catch (XmlDeserializationException ex) {
      throw new XmlDeserializationException(ex,
          "Failed to parse class '%s' from element %s",
          type.getName(), Shared.getElementInfoString(el));
    }

    logVerbose("... result = " + ret);
    logIndent--;
    return ret;
  }

  private Object parseISet(Element setElement, Class c) throws XmlDeserializationException {
    Object ret;
    List<String> elementsWithObjectWarningLogged = new ArrayList<>();

    ISet set = (ISet) createObjectInstanceByElement(setElement, c);

    List<Element> children = getElements(setElement);
    removeTypeMapElementIfExist(children);

    Class expectedClass = tryGetArrayItemTypeByElement(setElement);
    if (expectedClass == null) expectedClass = Object.class;


    for (Element e : children) {

      Class itemExpectedClass;

      XmlListItemMapping map = tryGetListElementMapping(e);
      if (map != null) {
        itemExpectedClass = map.itemType;
      } else {
        itemExpectedClass = expectedClass;
        if (itemExpectedClass.equals(Object.class) && elementsWithObjectWarningLogged.contains(e.getNodeName()) == false) {
          elementsWithObjectWarningLogged.add(e.getNodeName());
          Shared.log(
              Shared.eLogType.warning,
              "Set item from element <%s> for set '%s' is deserialized as 'Object' class. Probably missing custom collection mapping. Full node info: %s",
              e.getNodeName(), set.getClass().getName(), Shared.getElementInfoString(e));
        }
      }


      Object itemValue = parseIt(e, itemExpectedClass);
      set.add(itemValue);
    }

    ret = set;
    return ret;
  }

  private Object parseSet(Element setElement, Class c) throws XmlDeserializationException {
    Object ret;
    List<String> elementsWithObjectWarningLogged = new ArrayList<>();

    Set set = (Set) createObjectInstanceByElement(setElement, c);

    List<Element> children = getElements(setElement);
    removeTypeMapElementIfExist(children);

    Class expectedClass = tryGetArrayItemTypeByElement(setElement);
    if (expectedClass == null) expectedClass = Object.class;


    for (Element e : children) {

      Class itemExpectedClass;

      XmlListItemMapping map = tryGetListElementMapping(e);
      if (map != null) {
        itemExpectedClass = map.itemType;
      } else {
        itemExpectedClass = expectedClass;
        if (itemExpectedClass.equals(Object.class) && elementsWithObjectWarningLogged.contains(e.getNodeName()) == false) {
          elementsWithObjectWarningLogged.add(e.getNodeName());
          Shared.log(
              Shared.eLogType.warning,
              "Set item from element <%s> for set '%s' is deserialized as 'Object' class. Probably missing custom collection mapping. Full node info: %s",
              e.getNodeName(), set.getClass().getName(), Shared.getElementInfoString(e));
        }
      }


      Object itemValue = parseIt(e, itemExpectedClass);
      set.add(itemValue);
    }

    ret = set;
    return ret;
  }

  private Object parseIList(Element listElement, Class c) throws XmlDeserializationException {
    Object ret;
    List<String> elementsWithObjectWarningLogged = new ArrayList<>();

    IList lst = (IList) createObjectInstanceByElement(listElement, c);

    List<Element> children = getElements(listElement);
    removeTypeMapElementIfExist(children);

    Class expectedClass = tryGetArrayItemTypeByElement(listElement);
    if (expectedClass == null) expectedClass = Object.class;


    for (Element e : children) {

      Class itemExpectedClass;

      XmlListItemMapping map = tryGetListElementMapping(e);
      if (map != null) {
        itemExpectedClass = map.itemType;
      } else {
        itemExpectedClass = expectedClass;
        if (itemExpectedClass.equals(Object.class) && elementsWithObjectWarningLogged.contains(e.getNodeName()) == false) {
          elementsWithObjectWarningLogged.add(e.getNodeName());
          Shared.log(
              Shared.eLogType.warning,
              "List item from element <%s> for list '%s' is deserialized as 'Object' class. Probably missing custom list mapping. Full node info: %s",
              e.getNodeName(), lst.getClass().getName(), Shared.getElementInfoString(e));
        }
      }


      Object itemValue = parseIt(e, itemExpectedClass);
      lst.add(itemValue);
    }

    ret = lst;
    return ret;
  }

  private Object convertElementByElementParser(Element el, IElementParser customElementParser) throws XmlDeserializationException {
    Object ret;
    try {
      ret = customElementParser.parse(el);
    } catch (Exception ex) {
      throw new XmlDeserializationException(
          ex, "Failed to parse instance of class %s from %s using parser %s.",
          customElementParser.getType().getName(),
          Shared.getElementInfoString(el),
          customElementParser.getClass().getName());
    }
    return ret;
  }

  private Object parseList(Element listElement, Class c) throws XmlDeserializationException {
    Object ret;
    List<String> elementsWithObjectWarningLogged = new ArrayList<>();

    List lst = (List) createObjectInstanceByElement(listElement, c);

    List<Element> children = getElements(listElement);
    removeTypeMapElementIfExist(children);

    Class expectedClass = tryGetArrayItemTypeByElement(listElement);
    if (expectedClass == null) expectedClass = Object.class;


    for (Element e : children) {

      Class itemExpectedClass;

      XmlListItemMapping map = tryGetListElementMapping(e);
      if (map != null) {
        itemExpectedClass = map.itemType;
      } else {
        itemExpectedClass = expectedClass;
        if (itemExpectedClass.equals(Object.class) && elementsWithObjectWarningLogged.contains(e.getNodeName()) == false) {
          elementsWithObjectWarningLogged.add(e.getNodeName());
          Shared.log(
              Shared.eLogType.warning,
              "List item from element <%s> for list '%s' is deserialized as 'Object' class. Probably missing custom list mapping. Full node info: %s",
              e.getNodeName(), lst.getClass().getName(), Shared.getElementInfoString(e));
        }
      }


      Object itemValue = parseIt(e, itemExpectedClass);
      lst.add(itemValue);
    }

    ret = lst;
    return ret;
  }

  private Object parseMap(Element mapElement, Class c) throws XmlDeserializationException {
    Object ret;
//    List<String> elementsWithObjectWarningLogged = new ArrayList<>();

    Map map = (Map) createObjectInstanceByElement(mapElement, c);

    List<Element> children = getElements(mapElement);
    removeTypeMapElementIfExist(children);

    Class keyAttExpectedClass = tryGetKeyItemTypeByElement(mapElement);
    Class valueAttExpectedClass = tryGetValueItemTypeByElement(mapElement);

    if (keyAttExpectedClass == null) keyAttExpectedClass = Object.class;
    if (valueAttExpectedClass == null) valueAttExpectedClass = Object.class;

    for (Element e : children) {

      Class keyExpectedClass;
      Class valueExpectedClass;

      keyExpectedClass = keyAttExpectedClass;
      valueExpectedClass = valueAttExpectedClass;

//      XmlListItemMapping map = tryGetListElementMapping(e);
//      if (map != null) {
//        itemExpectedClass = map.itemType;
//      } else {
//        itemExpectedClass = expectedClass;
//        if (itemExpectedClass.equals(Object.class) && elementsWithObjectWarningLogged.contains(e.getNodeName()) == false) {
//          elementsWithObjectWarningLogged.add(e.getNodeName());
//          Shared.log(
//              Shared.eLogType.warning,
//              "List item from element <%s> for list '%s' is deserialized as 'Object' class. Probably missing custom list mapping. Full node info: %s",
//              e.getNodeName(), lst.getClass().getName(), Shared.getElementInfoString(e));
//        }
//      }


      Element keyElement = (Element) e.getElementsByTagName("key").item(0);
      Element valueElement = (Element) e.getElementsByTagName("value").item(0);

      Object key = parseIt(keyElement, keyExpectedClass);
      Object value = parseIt(valueElement, valueExpectedClass);

      map.put(key, value);
    }

    ret = map;
    return ret;
  }

  private Object parseIMap(Element mapElement, Class c) throws XmlDeserializationException {
    Object ret;
//    List<String> elementsWithObjectWarningLogged = new ArrayList<>();

    IMap map = (IMap) createObjectInstanceByElement(mapElement, c);

    List<Element> children = getElements(mapElement);
    removeTypeMapElementIfExist(children);

    Class keyAttExpectedClass = tryGetKeyItemTypeByElement(mapElement);
    Class valueAttExpectedClass = tryGetValueItemTypeByElement(mapElement);

    if (keyAttExpectedClass == null) keyAttExpectedClass = Object.class;
    if (valueAttExpectedClass == null) valueAttExpectedClass = Object.class;

    for (Element e : children) {

      Class keyExpectedClass;
      Class valueExpectedClass;

      keyExpectedClass = keyAttExpectedClass;
      valueExpectedClass = valueAttExpectedClass;

//      XmlListItemMapping map = tryGetListElementMapping(e);
//      if (map != null) {
//        itemExpectedClass = map.itemType;
//      } else {
//        itemExpectedClass = expectedClass;
//        if (itemExpectedClass.equals(Object.class) && elementsWithObjectWarningLogged.contains(e.getNodeName()) == false) {
//          elementsWithObjectWarningLogged.add(e.getNodeName());
//          Shared.log(
//              Shared.eLogType.warning,
//              "List item from element <%s> for list '%s' is deserialized as 'Object' class. Probably missing custom list mapping. Full node info: %s",
//              e.getNodeName(), lst.getClass().getName(), Shared.getElementInfoString(e));
//        }
//      }


      Element keyElement = (Element) e.getElementsByTagName("key").item(0);
      Element valueElement = (Element) e.getElementsByTagName("value").item(0);

      Object key = parseIt(keyElement, keyExpectedClass);
      Object value = parseIt(valueElement, valueExpectedClass);

      map.set(key, value);
    }

    ret = map;
    return ret;
  }

  private Object createObjectInstanceByElement(Element el, Class c) throws XmlDeserializationException {

    Class customType = tryGetCustomTypeByElement(el);
    if (customType != null)
      c = customType;

    Object ret = createObjectInstance(c);

    return ret;
  }

  private Object parseObject(Element el, Class c) throws XmlDeserializationException {

    Object ret = createObjectInstanceByElement(el, c);

    Field[] fields = getDeclaredFields(ret.getClass());

    for (Field f : fields) {
      if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
        continue; // static are skipped
      } else if (f.getAnnotation(XmlIgnore.class) != null) {
        logVerbose("%s.%s field skipped due to @XmlIgnored annotation.", el.getNodeName(), f.getName());
        continue; // skipped due to annotation
      } else if (Shared.isSkippedBySettings(f, settings)) {
        logVerbose("%s.%s field skipped due to ignore field setting.", el.getNodeName(), f.getName());
        continue;
      }
      Object tmp;
      try {
        tmp = parseField(el, f);
      } catch (Exception ex) {
        throw new XmlDeserializationException(ex,
            "Failed to fill field '%s.%s' ('%s') from xml-element %s.",
            c.getName(), f.getName(), f.getType().getName(),
            Shared.getElementInfoString(el));
      }
      try {
        if (tmp != UNSET) {
          f.setAccessible(true);
          f.set(ret, tmp);
        }
      } catch (Exception ex) {
        String tmpType = tmp == null ? "null" : tmp.getClass().getName();
        throw new XmlDeserializationException(ex,
            "Failed to fill field '%s.%s' ('%s') with value '%s' ('%s') from xml-element %s.",
            c.getName(), f.getName(), f.getType().getName(), tmp, tmpType,
            Shared.getElementInfoString(el));
      }
    }

    return ret;
  }

  private Class tryGetCustomTypeByElement(Element el) throws XmlDeserializationException {
    Class ret = null;
    String tmp;
    if (el.hasAttribute(Shared.TYPE_MAP_OF_ATTRIBUTE_NAME)) {
      tmp = el.getAttribute(Shared.TYPE_MAP_OF_ATTRIBUTE_NAME);
      try {
        ret = loadClass(tmp);
      } catch (Exception ex) {
        throw new XmlDeserializationException(ex,
            "Failed to load class for element class defined in %s.", Shared.getElementInfoString(el));
      }
    }

    return ret;
  }

  private Class tryGetArrayItemTypeByElement(Element el) throws XmlDeserializationException {
    Class ret = tryExtractTypeFromAttribute(el, Shared.TYPE_MAP_ITEM_OF_ATTRIBUTE_NAME);
    return ret;
  }

  private Class tryGetKeyItemTypeByElement(Element el) throws XmlDeserializationException {
    Class ret = tryExtractTypeFromAttribute(el, Shared.TYPE_MAP_KEY_OF_ATTRIBUTE_NAME);
    return ret;
  }

  private Class tryGetValueItemTypeByElement(Element el) throws XmlDeserializationException {
    Class ret = tryExtractTypeFromAttribute(el, Shared.TYPE_MAP_VALUE_OF_ATTRIBUTE_NAME);
    return ret;
  }

  private Class tryExtractTypeFromAttribute(Element el, String attributeName) throws XmlDeserializationException {
    Class ret;
    String tmp;
    if (el.hasAttribute(attributeName)) {
      tmp = el.getAttribute(attributeName);
      try {
        ret = loadClass(tmp);
      } catch (Exception ex) {
        throw new XmlDeserializationException(ex,
            "Failed to load class for element item-class defined in %s.", Shared.getElementInfoString(el));
      }
    } else
      ret = null;

    return ret;
  }

  private Class loadClass(String className) throws XmlDeserializationException {
    Class ret;

    String tmp = tryGetMappedType(className);
    if (tmp != null)
      className = tmp;

    try {
      ret = Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new XmlDeserializationException(e, "Failed to load class '%s' from JVM.", className);
    }
    return ret;
  }

  private String tryGetMappedType(String className) {
    String ret;
    if (this.typeMap.containsKey(className))
      ret = this.typeMap.get(className);
    else
      ret = null;
    return ret;
  }

  private Object parsePrimitiveFromElement(Element el, Class c) throws XmlDeserializationException {
    String txt = el.getTextContent();
    Object ret;
    Class tmp = tryGetCustomTypeByElement(el);
    if (tmp != null) c = tmp;
    ret = convertToType(txt, c);
    return ret;
  }

  private Object parseField(Element parentElement, Field f) throws XmlDeserializationException {
    Object ret;
    logIndent++;
    logVerbose("deserialize field %s.%s from %s",
        f.getDeclaringClass().getName(),
        f.getName(),
        Shared.getElementInfoString(parentElement)
    );

    try {
      Class c = f.getType();

      IValueParser customValueParser = Shared.tryGetCustomValueParser(c, settings);
      boolean storedInAttribute =
          Mapping.isSimpleTypeOrEnum(c) || customValueParser != null;

      boolean required = f.getAnnotation(XmlOptional.class) == null;
      if (storedInAttribute) {
        String attributeValue = readAttributeValue(parentElement, f.getName(), required);
        if (attributeValue == null) {
          ret = UNSET;
        } else if (attributeValue.equals(settings.getNullString())) {
          ret = null;
        } else {
          if (customValueParser != null) {
            logVerbose("... applied custom value parser %s", customValueParser.getClass().getName());
            ret = convertValueByCustomParser(attributeValue, customValueParser);
          } else
            ret = convertToType(attributeValue, c);
        }
      } else {
        String fieldElementName = f.getName();
        XmlCustomFieldMapping map = tryGetCustomFieldMapping(f, parentElement);
        if (map != null) {
          logVerbose("... applied custom field mapping %s", map);
          fieldElementName = map.getXmlElementName();
          c = map.getTargetFieldClass();
        }

        Element el = getElement(parentElement, fieldElementName, required);
        if (el == null)
          ret = UNSET;
        else {
          ret = parseIt(el, c);
        }
      }
    } catch (Exception ex) {
      throw new XmlDeserializationException(ex,
          "Failed to parse field '%s.%s' ('%s') from element %s.",
          f.getDeclaringClass().getName(), f.getName(), f.getType().getName(), Shared.getElementInfoString(parentElement));
    }

    logVerbose("... result = " + ret);
    logIndent--;
    return ret;
  }

  private XmlCustomFieldMapping tryGetCustomFieldMapping(Field f, Element parentElement) {
    XmlCustomFieldMapping ret = null;

    for (XmlCustomFieldMapping mapping : settings.getCustomFieldMappings()) {
      boolean tmp = isMappingFitting(mapping, f);
      if (tmp) {
        tmp = containsElementWithName(parentElement, mapping.getXmlElementName());
        if (tmp) {
          ret = mapping;
          break;
        }
      }
    }
    return ret;
  }

  private boolean containsElementWithName(Element parentElement, String xmlElementName) {
    Element elm;
    try {
      elm = getElement(parentElement, xmlElementName, false);
    } catch (XmlDeserializationException e) {
      elm = null;
    }
    return (elm != null);
  }

  private boolean isMappingFitting(XmlCustomFieldMapping mapping, Field f) {
    if (mapping.getFieldName().equals(f.getName()) == false)
      return false;

    if (mapping.getClassDeclaringField() != null && !(mapping.getClassDeclaringField().equals(f.getDeclaringClass())))
      return false;

    if (mapping.getDeclaredFieldClass() != null && !(mapping.getDeclaredFieldClass().equals(f.getType())))
      return false;

    return true;
  }

  private Element getElement(Element parentElement, String name, boolean required) throws XmlDeserializationException {
    Element ret = null;
    List<Element> elms = getElements(parentElement);
    for (Element elm : elms) {
      if (elm.getNodeName().equals(name)) {
        ret = elm;
        break;
      }
    }
    if (ret == null && required) {
      throw new XmlDeserializationException("Unable to find sub-element '%s' in element %s.",
          name,
          Shared.getElementInfoString(parentElement));
    }
    return ret;
  }

  private void removeTypeMapElementIfExist(List<Element> lst) {
    for (int i = 0; i < lst.size(); i++) {
      if (lst.get(i).getNodeName().equals(Shared.TYPE_MAP_ELEMENT_NAME)) {
        lst.remove(lst.get(i));
        i--;
      }
    }
  }

  private Object convertValueByCustomParser(String value, IValueParser parser) throws XmlDeserializationException {
    Object ret;
    try {
      ret = parser.parse(value);
    } catch (Exception ex) {
      throw new XmlDeserializationException(ex,
          "Failed to convert '%s' to type '%s' using '%s' custom IValueParser.",
          value, parser.getType().getName(), parser.getClass().getName());
    }
    return ret;
  }

  private String readAttributeValue(Element el, String key, boolean isRequired) throws XmlDeserializationException {
    String ret = null;
    if (el.hasAttribute(key)) {
      ret = el.getAttribute(key);
    } else {
      NodeList tmp = el.getElementsByTagName(key);
      if (tmp.getLength() > 0) {
        ret = tmp.item(0).getTextContent().trim();
      }
    }

    if (ret == null && isRequired) {
      throw new XmlDeserializationException(
          "Unable to find key '%s' in element %s.",
          key, Shared.getElementInfoString(el));
    }

    return ret;
  }

  private Object convertToType(String value, Class<?> type) throws XmlDeserializationException {
    Object ret;
    try {
      if (type.isEnum()) {
        ret = Enum.valueOf((Class<Enum>) type, value);
      } else {
        switch (type.getName()) {
          case "byte":
          case "java.lang.Byte":
            ret = Byte.parseByte(value);
            break;
          case "short":
          case "java.lang.Short":
            ret = Short.parseShort(value);
            break;
          case "int":
          case "java.lang.Integer":
            ret = Integer.parseInt(value);
            break;
          case "long":
          case "java.lang.Long":
            ret = Long.parseLong(value);
            break;
          case "float":
          case "java.lang.Float":
            ret = Float.parseFloat(value);
            break;
          case "double":
          case "java.lang.Double":
            ret = Double.parseDouble(value);
            break;
          case "char":
          case "java.lang.Character":
            ret = value.charAt(0);
            break;
          case "boolean":
          case "java.lang.Boolean":
            ret = Boolean.parseBoolean(value);
            break;
          case "java.lang.String":
            ret = value;
            break;
          default:
            throw new XmlDeserializationException("Type '%s' does not have primitive conversion defined. Use custom IValueParser.", type.getName());
        }
      }
    } catch (Exception ex) {
      throw new XmlDeserializationException(ex,
          "Failed to convert value '%s' into type '%s'.",
          value,
          type.getName());
    }

    return ret;
  }

  private boolean isNullValuedElement(Element el) {
    if (el.getTextContent().equals(settings.getNullString()))
      return true;
    else
      return false;
  }

  private Object createObjectInstance(Class<?> type) throws XmlDeserializationException {
    Object ret;

    if (type.equals(List.class) || type.equals(AbstractList.class))
      type = settings.getDefaultListTypeImplementation();
    else if (type.equals(IList.class) || type.equals(IReadOnlyList.class) || type.equals(ICollection.class))
      type = EList.class;
    else if (type.equals(ISet.class) || type.equals(IReadOnlySet.class))
      type = ESet.class;
    else if (type.equals(IMap.class))
      type = EMap.class;

    // check if there is not an custom instance creator
    IInstanceCreator creator = tryGetInstanceCreator(type);

    if (creator == null)
      try {
        Constructor constructor;
        constructor = type.getDeclaredConstructor(null);
        constructor.setAccessible(true);
        ret = constructor.newInstance(null);
        // ret = type.newInstance(); // old solution
      } catch (InstantiationException | IllegalAccessException ex) {
        throw new XmlDeserializationException(
            ex,
            "Failed to create new instance of '%s'. Probably missing public parameter-less constructor.",
            type.getName());
      } catch (NoSuchMethodException ex) {
        throw new XmlDeserializationException(
            ex,
            "Failed to create new instance of '%s'. Probably missing any parameter-less constructor.",
            type.getName());
      } catch (InvocationTargetException ex) {
        throw new XmlDeserializationException(
            ex,
            "Failed to create new instance of '%s'.",
            type.getName());
      }
    else {
      try {
        ret = creator.createInstance();
      } catch (Exception ex) {
        throw new XmlDeserializationException(
            ex,
            "Failed to create a new instance of '%s' using custom creator '%s'.",
            type.getName(), creator.getClass().getName());
      }
    }
    return ret;
  }

  private Object createArrayInstance(Class elementType, int length) throws XmlDeserializationException {
    Object ret;
    try {
      ret = Array.newInstance(elementType, length);
    } catch (Exception ex) {
      throw new XmlDeserializationException(
          ex,
          "Failed to create a new instance of '%s[]'.",
          elementType.getName());
    }
    return ret;
  }

  private IInstanceCreator tryGetInstanceCreator(Class<?> type) {
    IInstanceCreator ret = null;

    for (IInstanceCreator iInstanceCreator : settings.getInstanceCreators()) {
      if (iInstanceCreator.getType().equals(type)) {
        ret = iInstanceCreator;
        break;
      }
    }

    return ret;
  }

  private List<Element> getElements(Element el) {
    List<Element> ret = new ArrayList();
    NodeList c = el.getChildNodes();
    for (int i = 0; i < c.getLength(); i++) {
      Node n = c.item(i);
      if (n.getNodeType() != Node.ELEMENT_NODE)
        continue;

      Element eel = (Element) c.item(i);
      ret.add(eel);
    }
    return ret;
  }

  private XmlListItemMapping tryGetListElementMapping(Element itemElement) {
    Element parentElement = (Element) itemElement.getParentNode();
    String parentXPath = Shared.getElementXPath(parentElement);

    XmlListItemMapping ret = null;
    for (XmlListItemMapping mi : settings.getListItemMappings()) {
      if (isRegexMatch(mi.listElementXPathRegex, parentXPath) && (mi.itemElementName == null || mi.itemElementName.equals(itemElement.getNodeName()))) {
        ret = mi;
        break;
      }
    }

    return ret;
  }
}
