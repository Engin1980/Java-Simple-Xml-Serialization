/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eng.eSystem.xmlSerialization;

import eng.eSystem.collections.*;
import eng.eSystem.eXml.XElement;

import java.lang.reflect.*;
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
  private final XmlSerializer parent;
  private int logIndent = 0;

  Parser(XmlSerializer parent) {
    this.parent = parent;
    this.settings = parent.getSettings();
  }

  public synchronized Object deserialize(XElement root, Class objectType) throws XmlDeserializationException {
    if (root == null) {
      throw new IllegalArgumentException("Value of {el} cannot not be null.");
    }
    if (objectType == null) {
      throw new IllegalArgumentException("Value of {objectType} cannot not be null.");
    }


    Class c = objectType;
    Object ret;
    ret = parseIt(root, c);

    return ret;
  }

  public synchronized void deserializeContent(XElement root, Object target) throws XmlDeserializationException {
    if (target != null)
      fillObjectInstanceByElement(root, target, target.getClass());
  }

  private Object parseArray(XElement el, Class c) throws XmlDeserializationException {

    Object ret;
    IList<XElement> children = new EList<>(el.getChildren());
    removeTypeMapElementIfExist(children);
    int cnt = children.size();
    Class itemType = c.getComponentType();
    ret = createArrayInstance(itemType, cnt);


    for (int i = 0; i < children.size(); i++) {
      XElement e = children.get(i);
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

  private Object parseIt(XElement el, Class type) throws XmlDeserializationException {
    logIndent++;
    logVerbose("deserialize <%s> --> %s", el.getName(), type.getName());

    Object ret;
    try {
      if (isNullValuedElement(el))
        ret = null;
      else {
        // custom type defined in element
        Class customType = tryGetCustomTypeByElement(el);
        if (customType != null)
          type = customType;

        IElementParser customElementParser = Shared.tryGetCustomElementParser(type, settings);
        if (customElementParser != null) {
          ret = convertElementByElementParser(el, customElementParser);
        } else if (Mapping.isSimpleTypeOrEnum(type)) {
          // jednoduchý typ
          ret = parsePrimitiveFromElement(el, type);
        } else if (isInnerInstanceClass(type)) {
          throw new XmlDeserializationException((Throwable) null,
              "Deserialization of inner instance class (%s) is not supported.", type.getName());
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

  private boolean isInnerInstanceClass(Class type) {
    boolean ret = type.getEnclosingClass() != null && !Modifier.isStatic(type.getModifiers());
    return ret;
  }

  private Object parseISet(XElement setElement, Class c) throws XmlDeserializationException {
    Object ret;
    List<String> elementsWithObjectWarningLogged = new ArrayList<>();

    ISet set = (ISet) createObjectInstanceByElement(setElement, c);

    IList<XElement> children = new EList<>(setElement.getChildren());
    removeTypeMapElementIfExist(children);

    Class expectedClass = tryGetArrayItemTypeByElement(setElement);
    if (expectedClass == null) expectedClass = Object.class;


    for (XElement e : children) {

      Class itemExpectedClass;

      XmlListItemMapping map = tryGetListElementMapping(e);
      if (map != null) {
        itemExpectedClass = map.itemType;
      } else {
        itemExpectedClass = expectedClass;
        if (itemExpectedClass.equals(Object.class) && elementsWithObjectWarningLogged.contains(e.getName()) == false) {
          elementsWithObjectWarningLogged.add(e.getName());
          Shared.log(
              Shared.eLogType.warning,
              "Set item from element <%s> for set '%s' is deserialized as 'Object' class. Probably missing custom collection mapping. Full node info: %s",
              e.getName(), set.getClass().getName(), Shared.getElementInfoString(e));
        }
      }


      Object itemValue = parseIt(e, itemExpectedClass);
      set.add(itemValue);
    }

    ret = set;
    return ret;
  }

  private Object parseSet(XElement setElement, Class c) throws XmlDeserializationException {
    Object ret;
    List<String> elementsWithObjectWarningLogged = new ArrayList<>();

    Set set = (Set) createObjectInstanceByElement(setElement, c);

    IList<XElement> children = new EList<>(setElement.getChildren());
    removeTypeMapElementIfExist(children);

    Class expectedClass = tryGetArrayItemTypeByElement(setElement);
    if (expectedClass == null) expectedClass = Object.class;


    for (XElement e : children) {

      Class itemExpectedClass;

      XmlListItemMapping map = tryGetListElementMapping(e);
      if (map != null) {
        itemExpectedClass = map.itemType;
      } else {
        itemExpectedClass = expectedClass;
        if (itemExpectedClass.equals(Object.class) && elementsWithObjectWarningLogged.contains(e.getName()) == false) {
          elementsWithObjectWarningLogged.add(e.getName());
          Shared.log(
              Shared.eLogType.warning,
              "Set item from element <%s> for set '%s' is deserialized as 'Object' class. Probably missing custom collection mapping. Full node info: %s",
              e.getName(), set.getClass().getName(), Shared.getElementInfoString(e));
        }
      }


      Object itemValue = parseIt(e, itemExpectedClass);
      set.add(itemValue);
    }

    ret = set;
    return ret;
  }

  private Object parseIList(XElement listElement, Class c) throws XmlDeserializationException {
    Object ret;
    List<String> elementsWithObjectWarningLogged = new ArrayList<>();

    IList lst = (IList) createObjectInstanceByElement(listElement, c);

    IList<XElement> children = new EList<>(listElement.getChildren());
    removeTypeMapElementIfExist(children);

    Class expectedClass = tryGetArrayItemTypeByElement(listElement);
    if (expectedClass == null) expectedClass = Object.class;


    for (XElement e : children) {

      Class itemExpectedClass;

      XmlListItemMapping map = tryGetListElementMapping(e);
      if (map != null) {
        itemExpectedClass = map.itemType;
      } else {
        itemExpectedClass = expectedClass;
        if (itemExpectedClass.equals(Object.class) && elementsWithObjectWarningLogged.contains(e.getName()) == false) {
          elementsWithObjectWarningLogged.add(e.getName());
          Shared.log(
              Shared.eLogType.warning,
              "List item from element <%s> for list '%s' is deserialized as 'Object' class. Probably missing custom list mapping. Full node info: %s",
              e.getName(), lst.getClass().getName(), Shared.getElementInfoString(e));
        }
      }


      Object itemValue = parseIt(e, itemExpectedClass);
      lst.add(itemValue);
    }

    ret = lst;
    return ret;
  }

  private Object convertElementByElementParser(XElement el, IElementParser customElementParser) throws XmlDeserializationException {
    Object ret;
    try {
      ret = customElementParser.parse(el, this.parent.new Deserializer());
    } catch (Throwable ex) {
      throw new XmlDeserializationException(
          ex, "Failed to parse instance of class %s from %s using parser %s.",
          customElementParser.getType().getName(),
          Shared.getElementInfoString(el),
          customElementParser.getClass().getName());
    }
    return ret;
  }

  private Object parseList(XElement listElement, Class c) throws XmlDeserializationException {
    Object ret;
    List<String> elementsWithObjectWarningLogged = new ArrayList<>();

    List lst = (List) createObjectInstanceByElement(listElement, c);

    IList<XElement> children = new EList<>(listElement.getChildren());
    removeTypeMapElementIfExist(children);

    Class expectedClass = tryGetArrayItemTypeByElement(listElement);
    if (expectedClass == null) expectedClass = Object.class;


    for (XElement e : children) {

      Class itemExpectedClass;

      XmlListItemMapping map = tryGetListElementMapping(e);
      if (map != null) {
        itemExpectedClass = map.itemType;
      } else {
        itemExpectedClass = expectedClass;
        if (itemExpectedClass.equals(Object.class) && elementsWithObjectWarningLogged.contains(e.getName()) == false) {
          elementsWithObjectWarningLogged.add(e.getName());
          Shared.log(
              Shared.eLogType.warning,
              "List item from element <%s> for list '%s' is deserialized as 'Object' class. Probably missing custom list mapping. Full node info: %s",
              e.getName(), lst.getClass().getName(), Shared.getElementInfoString(e));
        }
      }


      Object itemValue = parseIt(e, itemExpectedClass);
      lst.add(itemValue);
    }

    ret = lst;
    return ret;
  }

  private Object parseMap(XElement mapElement, Class c) throws XmlDeserializationException {
    Object ret;
    List<String> elementsWithObjectWarningLogged = new ArrayList<>();

    Map map = (Map) createObjectInstanceByElement(mapElement, c);

    IList<XElement> children = new EList<>(mapElement.getChildren());
    removeTypeMapElementIfExist(children);

    Class keyAttExpectedClass = tryGetKeyItemTypeByElement(mapElement);
    Class valueAttExpectedClass = tryGetValueItemTypeByElement(mapElement);

    if (keyAttExpectedClass == null) keyAttExpectedClass = Object.class;
    if (valueAttExpectedClass == null) valueAttExpectedClass = Object.class;

    for (XElement e : children) {

      Class keyExpectedClass;
      Class valueExpectedClass;

      keyExpectedClass = keyAttExpectedClass;
      valueExpectedClass = valueAttExpectedClass;

      XmlMapItemMapping mem = tryGetMapElementMapping(e);
      if (mem != null) {
        keyExpectedClass = mem.keyType;
        valueExpectedClass = mem.valueType;
      } else {
        if (keyExpectedClass.equals(Object.class) && valueExpectedClass.equals(Object.class) && elementsWithObjectWarningLogged.contains(e.getName()) == false) {
          elementsWithObjectWarningLogged.add(e.getName());
          Shared.log(
              Shared.eLogType.warning,
              "Map item from element <%s> for map '%s' is deserialized as 'Object' class for key and value too. Probably missing custom map mapping. Full node info: %s",
              e.getName(), map.getClass().getName(), Shared.getElementInfoString(e));
        }
      }


      XElement keyElement = getElement(e, "key", true); // (XElement) e.getElementsByTagName("key").item(0);
      XElement valueElement = getElement(e, "value", true); //XElement) e.getElementsByTagName("value").item(0);

      Object key = parseIt(keyElement, keyExpectedClass);
      Object value = parseIt(valueElement, valueExpectedClass);

      map.put(key, value);
    }

    ret = map;
    return ret;
  }

  private Object parseIMap(XElement mapElement, Class c) throws XmlDeserializationException {
    Object ret;
    List<String> elementsWithObjectWarningLogged = new ArrayList<>();

    IMap map = (IMap) createObjectInstanceByElement(mapElement, c);

    IList<XElement> children = new EList<>(mapElement.getChildren());
    removeTypeMapElementIfExist(children);

    Class keyAttExpectedClass = tryGetKeyItemTypeByElement(mapElement);
    Class valueAttExpectedClass = tryGetValueItemTypeByElement(mapElement);

    if (keyAttExpectedClass == null) keyAttExpectedClass = Object.class;
    if (valueAttExpectedClass == null) valueAttExpectedClass = Object.class;

    for (XElement e : children) {

      Class keyExpectedClass;
      Class valueExpectedClass;

      keyExpectedClass = keyAttExpectedClass;
      valueExpectedClass = valueAttExpectedClass;

      XmlMapItemMapping mem = tryGetMapElementMapping(e);
      if (mem != null) {
        keyExpectedClass = mem.keyType;
        valueExpectedClass = mem.valueType;
      } else {
        if (keyExpectedClass.equals(Object.class) && valueExpectedClass.equals(Object.class) && elementsWithObjectWarningLogged.contains(e.getName()) == false) {
          elementsWithObjectWarningLogged.add(e.getName());
          Shared.log(
              Shared.eLogType.warning,
              "Map item from element <%s> for map '%s' is deserialized as 'Object' class for key and value too. Probably missing custom map mapping. Full node info: %s",
              e.getName(), map.getClass().getName(), Shared.getElementInfoString(e));
        }
      }


      XElement keyElement = getElement(e, "key", true); // (XElement) e.getElementsByTagName("key").item(0);
      XElement valueElement = getElement(e, "value", true); //XElement) e.getElementsByTagName("value").item(0);

      Object key = parseIt(keyElement, keyExpectedClass);
      Object value = parseIt(valueElement, valueExpectedClass);

      map.set(key, value);
    }

    ret = map;
    return ret;
  }

  private Object createObjectInstanceByElement(XElement el, Class c) throws XmlDeserializationException {

    Object ret = createObjectInstance(c);

    return ret;
  }

  private Object parseObject(XElement el, Class c) throws XmlDeserializationException {

    Object ret = createObjectInstanceByElement(el, c);

    fillObjectInstanceByElement(el, ret, c);

    return ret;
  }

  private void fillObjectInstanceByElement(XElement el, Object trg, Class c) throws XmlDeserializationException {
    Field[] fields = getDeclaredFields(trg.getClass());

    for (Field f : fields) {
      if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
        continue; // static are skipped
      } else if (f.getAnnotation(XmlIgnore.class) != null) {
        logVerbose("%s.%s field skipped due to @XmlIgnored annotation.", el.getName(), f.getName());
        continue; // skipped due to annotation
      } else if (Shared.isSkippedBySettings(f, settings)) {
        logVerbose("%s.%s field skipped due to ignore field setting.", el.getName(), f.getName());
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
          f.set(trg, tmp);
        }
      } catch (Exception ex) {
        String tmpType = tmp == null ? "null" : tmp.getClass().getName();
        throw new XmlDeserializationException(ex,
            "Failed to fill field '%s.%s' ('%s') with value '%s' ('%s') from xml-element %s.",
            c.getName(), f.getName(), f.getType().getName(), tmp, tmpType,
            Shared.getElementInfoString(el));
      }
    }
  }

  private Class tryGetCustomTypeByElement(XElement el) throws XmlDeserializationException {
    Class ret = null;
    String tmp = el.getAttributes().tryGet(Shared.TYPE_MAP_OF_ATTRIBUTE_NAME);
    if (tmp != null) {
      try {
        ret = loadClass(tmp);
      } catch (Exception ex) {
        throw new XmlDeserializationException(ex,
            "Failed to load class for element class defined in %s.", Shared.getElementInfoString(el));
      }
    }

    return ret;
  }

  private Class tryGetArrayItemTypeByElement(XElement el) throws XmlDeserializationException {
    Class ret = tryExtractTypeFromAttribute(el, Shared.TYPE_MAP_ITEM_OF_ATTRIBUTE_NAME);
    return ret;
  }

  private Class tryGetKeyItemTypeByElement(XElement el) throws XmlDeserializationException {
    Class ret = tryExtractTypeFromAttribute(el, Shared.TYPE_MAP_KEY_OF_ATTRIBUTE_NAME);
    return ret;
  }

  private Class tryGetValueItemTypeByElement(XElement el) throws XmlDeserializationException {
    Class ret = tryExtractTypeFromAttribute(el, Shared.TYPE_MAP_VALUE_OF_ATTRIBUTE_NAME);
    return ret;
  }

  private Class tryExtractTypeFromAttribute(XElement el, String attributeName) throws XmlDeserializationException {
    Class ret;
    String tmp = el.getAttributes().tryGet(attributeName);
    if (tmp != null) {
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

    try {
      ret = Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new XmlDeserializationException(e, "Failed to load class '%s' from JVM.", className);
    }
    return ret;
  }

  private Object parsePrimitiveFromElement(XElement el, Class c) throws XmlDeserializationException {
    String txt = el.getContent();
    Object ret;
    Class tmp = tryGetCustomTypeByElement(el);
    if (tmp != null) c = tmp;
    ret = convertToType(txt, c);
    return ret;
  }

  private Object parseField(XElement parentElement, Field f) throws XmlDeserializationException {
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

        XElement el = getElement(parentElement, fieldElementName, required);
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

  private XmlCustomFieldMapping tryGetCustomFieldMapping(Field f, XElement parentElement) {
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

  private boolean containsElementWithName(XElement parentElement, String xmlElementName) {
    boolean ret = parentElement.getChildren().isAny(q -> q.getName().equals(xmlElementName));
    return ret;
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

  private XElement getElement(XElement parentElement, String name, boolean required) throws XmlDeserializationException {

    XElement ret = parentElement.getChildren().tryGetFirst(q -> q.getName().equals(name));
    if (ret == null && required) {
      throw new XmlDeserializationException("Unable to find sub-element '%s' in element %s.",
          name,
          Shared.getElementInfoString(parentElement));
    }
    return ret;
  }

  private void removeTypeMapElementIfExist(IList<XElement> lst) {
    lst.remove(q -> q.getName().equals(Shared.TYPE_MAP_DEFINITION_ELEMENT_NAME));
//    for (int i = 0; i < lst.size(); i++) {
//      if (lst.get(i).getName().equals(Shared.TYPE_MAP_DEFINITION_ELEMENT_NAME)) {
//        lst.remove(lst.get(i));
//        i--;
//      }
//    }
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

  private String readAttributeValue(XElement el, String key, boolean isRequired) throws XmlDeserializationException {
    String ret = el.getAttributes().tryGet(key);
    if (ret == null) {
      XElement tmp = el.getChildren().tryGetFirst(q -> q.getName().equals(key));
      if (tmp != null) ret = tmp.getContent();
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

  private boolean isNullValuedElement(XElement el) {
    if (el.getContent().equals(settings.getNullString()))
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
        ret = createInstanceByConstructor(type);
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

  private Object createInstanceByConstructor(Class<?> type) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
    IList<Constructor> constructors = new EList<>(type.getDeclaredConstructors());

    if (constructors.isAny(q->q.getAnnotation(XmlConstructor.class) != null))
      constructors = constructors.where(q->q.getAnnotation(XmlConstructor.class) != null);

    int minParams = constructors.min(q -> q.getParameterCount(), Integer.MAX_VALUE);
    constructors = constructors.where(q -> q.getParameterCount() == minParams);
    if (constructors.isEmpty()) {
      throw new NoSuchMethodException();
    }
    Constructor constructor = constructors.getRandom();
    if (constructor.getParameterCount() == 0 && Modifier.isPrivate(constructor.getModifiers())){
      System.out.println("REP:: Private parameter-less constructor used for " + type.getName());
    }
    Object[] params = new Object[constructor.getParameterCount()];
    for (int i = 0; i < constructor.getParameterCount(); i++) {
      Parameter par = constructor.getParameters()[i];
      Type parType = par.getType();
      if (parType.equals(int.class) ||
          parType.equals(Integer.class) ||
          parType.equals(byte.class) ||
          parType.equals(Byte.class) ||
          parType.equals(short.class) ||
          parType.equals(Short.class) ||
          parType.equals(long.class) ||
          parType.equals(Long.class) ||
          parType.equals(float.class) ||
          parType.equals(Float.class) ||
          parType.equals(double.class) ||
          parType.equals(Double.class))
        params[i] = 0;
      else if (parType.equals(boolean.class) ||
          parType.equals(Boolean.class))
        params[i] = false;
      else if (parType.equals(char.class) ||
          parType.equals(Character.class))
        params[i] = 'a';
      else
        params[i] = null;
    }

    Object ret;
    constructor.setAccessible(true);
    ret = constructor.newInstance(params);
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

  private XmlListItemMapping tryGetListElementMapping(XElement itemElement) {
    XElement parentElement = itemElement.getParent();
    String parentXPath = Shared.getElementXPath(parentElement);

    XmlListItemMapping ret = null;
    for (XmlListItemMapping mi : settings.getListItemMappings()) {
      if (isRegexMatch(mi.collectionElementXPathRegex, parentXPath) && (mi.itemElementName == null || mi.itemElementName.equals(itemElement.getName()))) {
        ret = mi;
        break;
      }
    }

    return ret;
  }

  private XmlMapItemMapping tryGetMapElementMapping(XElement itemElement) {
    XElement parentElement = itemElement.getParent();
    String parentXPath = Shared.getElementXPath(parentElement);

    XmlMapItemMapping ret = null;
    for (XmlMapItemMapping mi : settings.getMapItemMappings()) {
      if (isRegexMatch(mi.collectionElementXPathRegex, parentXPath) && (mi.itemElementName == null || mi.itemElementName.equals(itemElement.getName()))) {
        ret = mi;
        break;
      }
    }

    return ret;
  }
}
