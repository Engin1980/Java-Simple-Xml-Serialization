/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eng.eSystem.xmlSerialization;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static eng.eSystem.xmlSerialization.Shared.*;

/**
 * @author Marek
 */
class Parser {

  private final Settings settings;

  Parser(Settings settings) {
    this.settings = settings;
  }

  public Object deserialize(Element el, Class objectType) {
    if (el == null) {
        throw new IllegalArgumentException("Value of {el} cannot not be null.");
    }
    if (objectType == null) {
        throw new IllegalArgumentException("Value of {objectType} cannot not be null.");
    }

    if (settings.isVerbose()) {
      System.out.println("  deserialize( <" + el.getNodeName() + "...>, " + objectType.getSimpleName());
    }

    Class c = objectType;
    Object ret;
    IValueParser customValueParser = Shared.tryGetCustomValueParser(c, settings);
    IElementParser customElementParser = Shared.tryGetCustomElementParser(c, settings);
    if (customValueParser != null) {
      ret = convertAndSetFieldSimpleByCustomParser2(el, customValueParser);
    } else if (customElementParser != null) {
      ret = convertAndSetFieldComplexByCustomParser2(el, customElementParser);
    } else if (Mapping.isSimpleTypeOrEnum(c)) {
      // jednoduchý typ
      ret = convertAndSetFieldValue2(el, c);
    } else if (List.class.isAssignableFrom(c)) {
      ret = setFieldList2(el, c);
    } else if (c.isArray()) {
      ret = setFieldArray2(el,c );
    } else {
      ret = setFieldComplex2(el, objectType);
    }
    return ret;
  }

  private Object convertAndSetFieldComplexByCustomParser2(Element el, IElementParser customElementParser) {
    Object ret;
    try {
      if (isNullValued(el))
        ret = null;
      else
        ret = customElementParser.parse(el);
    } catch (Exception ex) {
      throw new XmlSerializationException(
          "Failed to parse instance for " + customElementParser.getTypeName() +
              " using custom-element-parser " + customElementParser.getClass().getName() + ".",
          ex);
    }
    return ret;
  }

  private Object convertAndSetFieldSimpleByCustomParser2(Element el, IValueParser parser) {
    String tmpS = extractSimpleValueFromElement(el, "N/A", true);
    if (tmpS == null) {
      return null;
    }
    Object tmpO;
    if (tmpS.equals(settings.getNullString()))
      tmpO = null;
    else
      tmpO = parser.parse(tmpS);
    return tmpO;
  }

  private Object setFieldList2(Element el, Class c) {
    Object ret;

    if (el.getTextContent().equals(settings.getNullString())) {
      // list, but null, no instance
      ret = null;
    } else {
      List lst = (List) createInstance(c);
      String key = c.getSimpleName();
      fillFieldList(el, lst, key);
      ret = lst;
    }
    return ret;
  }

  public Object setFieldArray2(Element el, Class c) {

    Object ret;
    if (el.getTextContent().equals(settings.getNullString())) {
      // array, but null, no instance
      ret = null;
    } else {
      int cnt = getElements(el).size();
      Class itemClass = c.getComponentType();
      ret = Array.newInstance(itemClass, cnt);
      String key = c.getSimpleName();

      fillFieldArray(el, ret, key, itemClass);
    }
    return ret;
  }

  private Object setFieldComplex2(Element el, Class c) {

    Object ret;
    try {
      if (isNullValued(el)) {
        ret = null;
      } else {
        ret = createInstance(c);
      }
    } catch (Exception ex) {
      throw new XmlSerializationException(
          "Failed to create instance for " + c.getSimpleName()+ ".",
          ex);
    }

    if (ret != null)
      fillObject(el, ret);

    return ret;
  }

  private Object convertAndSetFieldValue2(Element el, Class c) {
    String tmpS = extractSimpleValueFromElement(el, "N/A", true);
    if (tmpS == null) {
      return null;
    }
    Object tmpO;
    if (tmpS.equals(settings.getNullString())) {
      tmpO = null;
    } else {
      tmpO = convertToType(tmpS, c);
    }
    return tmpO;
  }

  <T> void fillObject(Element el, T targetObject) {
    if (settings.isVerbose()) {
      System.out.println("fillObject( <" + el.getNodeName() + "...>, " + targetObject.getClass().getSimpleName());
    }
    Class c = targetObject.getClass();
    Field[] fields = getDeclaredFields(c);

    for (Field f : fields) {
      if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
        continue; // statické přeskakujem
      } else if (Shared.isSkippedBySettings(f, settings)) {
        if (settings.isVerbose()) {
          System.out.println("  " + el.getNodeName() + "." + f.getName() + " field skipped due to settings-ignoredFieldsRegex list.");
        }
        continue; // parent neplníme, ty jsou reference na nadřazené objekty a plní se sami
      }
      try {
        fillField(el, f, targetObject);
      } catch (Exception ex) {
        throw new XmlSerializationException(ex,
            "Failed to fill field '%s' ('%s') of object of type '%s' using element '%s'.",
            f.getName(), f.getType().getName(), c.getName(),
            getElementXPath(el, true, true));
      }
    }
  }

  void fillList(Element el, List lst) {
    fillFieldList(el, lst, el.getNodeName()); //lst.getClass().getSimpleName());
  }

  Object fillArray(Element el, Class arrayItemType) {
    Object ret;

    if (el.getTextContent().equals(settings.getNullString())) {
      // list, but null, no instance
      ret = null;
    } else {
      int cnt = getElements(el).size();
      Class itemClass = arrayItemType;
      Object arr = Array.newInstance(itemClass, cnt);
      String key = el.getNodeName();

      fillFieldArray(el, arr, key, itemClass);

      ret = arr;
    }
    return ret;
  }

  private <T> void fillField(Element el, Field f, T targetObject) {
    if (settings.isVerbose()) {
      System.out.println("  fillField( <" + el.getNodeName() + "...>, " + targetObject.getClass().getSimpleName() + "." + f.getName());
    }

    Class c = f.getType();
    IValueParser customValueParser = Shared.tryGetCustomValueParser(c, settings);
    IElementParser customElementParser = Shared.tryGetCustomElementParser(c, settings);
    if (customValueParser != null) {
      convertAndSetFieldSimpleByCustomParser(el, f, customValueParser, targetObject);
    } else if (customElementParser != null) {
      convertAndSetFieldComplexByCustomParser(el, f, customElementParser, targetObject);
    } else if (Mapping.isSimpleTypeOrEnum(c)) {
      // jednoduchý typ
      convertAndSetFieldValue(el, f, targetObject);
    } else if (List.class.isAssignableFrom(c)) {
      setFieldList(el, f, targetObject);
    } else if (c.isArray()) {
      setFieldArray(el, f, targetObject);
    } else {
      setFieldComplex(el, f, targetObject);
    }
  }

  private <T> void convertAndSetFieldComplexByCustomParser(Element el, Field f, IElementParser customElementParser, T ref) {

    // first check if I have something to fill the object with
    boolean required = f.getAnnotation(XmlOptional.class) == null;

    Element subEl;
    try {
      subEl = getElements(el, f.getName()).get(0);
    } catch (Exception e) {
      if (required)
        throw XmlInvalidDataException.createNoSuchElement(el, f.getName(), ref.getClass());
      else
        subEl = null;
    }

    // if is optional and element-data has not been found, skip
    if (subEl == null)
      return;

    // then create instance and fill it
    Object newInstance;
    try {
      if (isNullValued(subEl))
        newInstance = null;
      else
        newInstance = customElementParser.parse(subEl);
    } catch (Exception ex) {
      throw new XmlSerializationException(
          "Failed to parse instance for " + ref.getClass().getSimpleName() + "." + f.getName() +
              " using custom-element-parser " + customElementParser.getClass().getName() + ".",
          ex);
    }

    setFieldValue(f, ref, newInstance);
    // following comment can be deleted after previous line is verified
//    try {
//      f.setAccessible(true);
//      f.set(ref, newInstance);
//      f.setAccessible(false);
//    } catch (IllegalArgumentException | IllegalAccessException ex) {
//      throw new XmlSerializationException(
//          "Failed to set value to field " + ref.getClass().getName() + "." + f.getName(), ex);
//    }
  }

  private <T> void convertAndSetFieldSimpleByCustomParser(Element el, Field f, IValueParser parser, T targetObject) {
    boolean required = f.getAnnotation(XmlOptional.class) == null;
    String tmpS = extractSimpleValueFromElement(el, f.getName(), required);
    if (tmpS == null) {
      return;
    }
    Object tmpO;
    if (tmpS.equals(settings.getNullString()))
      tmpO = null;
    else
      tmpO = parser.parse(tmpS);
    setFieldValue(f, targetObject, tmpO);
  }

  private <T> void convertAndSetFieldValue(Element el, Field f, T targetObject) {
    boolean required = f.getAnnotation(XmlOptional.class) == null;
    String tmpS = extractSimpleValueFromElement(el, f.getName(), required);
    if (tmpS == null) {
      return;
    }
    Object tmpO;
    if (tmpS.equals(settings.getNullString())) {
      tmpO = null;
    } else {
      tmpO = convertToType(tmpS, f.getType());
    }
    setFieldValue(f, targetObject, tmpO);
  }

  private String extractSimpleValueFromElement(Element el, String key, boolean isRequired) {
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
      throw new XmlSerializationException("Unable to find key \"" + key + "\" in element \"" +
          Shared.getElementXPath(el, true, true) + "\"");
    }

    return ret;
  }

  private <T> void setFieldValue(Field f, T targetObject, Object value) throws SecurityException, XmlSerializationException {
    try {
      f.setAccessible(true);
      f.set(targetObject, value);
      f.setAccessible(false);
    } catch (IllegalArgumentException | IllegalAccessException ex) {
      throw new XmlSerializationException(
          "Failed to set into " + f.getDeclaringClass().getName() + "." + f.getName() + " value " + value);
    }
  }

  private Object convertToType(String value, Class<?> type) {
    Object ret;
    if (type.isEnum()) {
      ret = Enum.valueOf((Class<Enum>) type, value);
    } else {
      switch (type.getName()) {
        case "int":
        case "java.lang.Integer":
          ret = Integer.parseInt(value);
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
          throw new XmlSerializationException("Type " + type.getName() + " is not supported.");
      }
    }

    return ret;
  }

  private <T> void setFieldComplex(Element el, Field f, T ref) {

    // first check if I have something to fill the object with
    boolean required = f.getAnnotation(XmlOptional.class) == null;

    Element subEl;
    try {
      subEl = getElements(el, f.getName()).get(0);
    } catch (Exception e) {
      if (el.getAttribute(f.getName()).isEmpty() == false) {
        throw XmlInvalidDataException.createAttributeInsteadOfElementFound(el, f.getName(), ref.getClass());
      }
      if (required) {
        throw XmlInvalidDataException.createNoSuchElement(el, f.getName(), ref.getClass());
      } else
        subEl = null;
    }

    // if is optional and element-data has not been found, skip
    if (subEl == null)
      return;

    // then create instance and fill it
    Object newInstance;
    try {
      if (isNullValued(subEl)) {
        newInstance = null;
      } else {
        newInstance = createInstance(f.getType());
      }
    } catch (Exception ex) {
      throw new XmlSerializationException(
          "Failed to create instance for " + ref.getClass().getSimpleName() + "." + f.getName() + ".",
          ex);
    }

    setFieldValue(f, ref, newInstance);
// previous line replaces following code, however not tested yet.
// this comment can be deleted after testing
//    try {
//      f.setAccessible(true);
//      f.set(ref, newInstance);
//      f.setAccessible(false);
//    } catch (IllegalArgumentException | IllegalAccessException ex) {
//      throw new XmlSerializationException(
//          "Failed to set value to field " + ref.getClass().getName() + "." + f.getName(), ex);
//    }

    if (newInstance != null)
      fillObject(subEl, newInstance);
  }

  private boolean isNullValued(Element el) {
    if (el.getTextContent().equals(settings.getNullString()))
      return true;
    else
      return false;
  }

  private Object createInstance(Class<?> type) {
    Object ret;

    // programuje se proti List, tak sem přijde požadavek na "List"
    // ale to je rozhraní, takže ho nahradím ArrayListem
    if (type.equals(List.class)) {
      type = settings.getDefaultListTypeImplementation();
    }

    // check if there is not an custom instance creator
    IInstanceCreator creator = tryGetInstanceCreator(type);

    if (creator == null)
      try {
        ret = type.newInstance();
      } catch (InstantiationException | IllegalAccessException ex) {
        throw new XmlSerializationException("Failed to create new instance of " + type.getName() + ". Check if public parameter-less constructor exists.", ex);
      }
    else {
      try {
        ret = creator.createInstance();
      } catch (Exception ex) {
        throw new XmlSerializationException("Failed to create a new instance of " + type.getName() + " using creator " + creator.getClass().getName() + ".", ex);
      }
    }
    return ret;
  }

  private IInstanceCreator tryGetInstanceCreator(Class<?> type) {
    IInstanceCreator ret = null;

    for (IInstanceCreator iInstanceCreator : settings.getInstanceCreators()) {
      if (iInstanceCreator.getTypeName().equals(type.getName())) {
        ret = iInstanceCreator;
        break;
      }
    }

    return ret;
  }

  private void setFieldList(Element el, Field f, Object targetObject) {
    // first check if I have something to fill the object with
    boolean required = f.getAnnotation(XmlOptional.class) == null;

    List<Element> tmp = getElements(el, f.getName());
    if (tmp.isEmpty()) {
      if (required)
        throw XmlInvalidDataException.createNoSuchElement(el, f.getName(), targetObject.getClass());
      else
        return;
    }

    el = tmp.get(0);

    if (el.getTextContent().equals(settings.getNullString())) {
      // list, but null, no instance
      setFieldValue(f, targetObject, null);
    } else {
      List lst = (List) createInstance(f.getType());
      setFieldValue(f, targetObject, lst);

      String key = targetObject.getClass().getSimpleName() + "." + f.getName();
      fillFieldList(el, lst, key);
    }
  }

  private void fillFieldList(Element el, List lst, String classFieldKey) {
    List<Element> children = getElements(el);
    for (Element e : children) {
      if (isNullValued(e)) {
        lst.add(null);
        continue;
      }

      Class itemType = getItemType(classFieldKey, e, true);
      if (Mapping.isSimpleTypeOrEnum(itemType)) {
        // list item is a primitive type
        // in this case it should be some like <xxx>value</xxx>
        String value = e.getTextContent();
        Object val = convertToType(value, itemType);
        lst.add(val);
      } else {
        // list item is complex type
        Object inn = createInstance(itemType);
        lst.add(inn);

        fillObject(e, inn);
      }
    }
  }

  private void setFieldArray(Element el, Field f, Object targetObject) {
    // first check if I have something to fill the object with
    boolean required = f.getAnnotation(XmlOptional.class) == null;

    List<Element> tmp = getElements(el, f.getName());
    if (tmp.isEmpty()) {
      if (required)
        throw XmlInvalidDataException.createNoSuchElement(el, f.getName(), targetObject.getClass());
      else
        return;
    }

    el = tmp.get(0);

    if (el.getTextContent().equals(settings.getNullString())) {
      // list, but null, no instance
      setFieldValue(f, targetObject, null);
    } else {
      int cnt = getElements(el).size();
      Class itemClass = f.getType().getComponentType();
      Object arr = Array.newInstance(itemClass, cnt);
      String key = targetObject.getClass().getSimpleName() + "." + f.getName();

      setFieldValue(f, targetObject, arr);

      fillFieldArray(el, arr, key, itemClass);
    }
  }

  private void fillFieldArray(Element el, Object arr, String classFieldKey, Class itemClass) {
    List<Element> children = getElements(el);
    for (int i = 0; i < children.size(); i++) {
      Element e = children.get(i);
      if (isNullValued(e)) {
        Array.set(arr, i, null);
        continue;
      }

      Class itemType = getItemType(classFieldKey, e, false);
      if (itemType == null) itemType = itemClass;
      if (Mapping.isSimpleTypeOrEnum(itemType)) {
        String value = e.getTextContent();
        Object val = convertToType(value, itemType);
        Array.set(arr, i, val);
      } else {
        // list item is complex type
        Object inn = createInstance(itemType);
        Array.set(arr, i, inn);

        fillObject(e, inn);
      }
    }
  }

  private List<Element> getElements(Element el) {
    return getElements(el, null);
  }

  private List<Element> getElements(Element el, String subElementName) {
    List<Element> ret = new ArrayList();
    NodeList c = el.getChildNodes();
    for (int i = 0; i < c.getLength(); i++) {
      Node n = c.item(i);
      if (n.getNodeType() != Node.ELEMENT_NODE) {
        continue;
      }
      if (subElementName != null && n.getNodeName().equals(subElementName) == false) {
        continue;
      }
      Element eel = (Element) c.item(i);
      ret.add(eel);
    }
    return ret;
  }

  private Class getItemType(String classFieldKey, Element elementOrNull, boolean mustExist) {
    Class ret;

    String elementName;
    if (elementOrNull != null)
      elementName = elementOrNull.getNodeName();
    else
      elementName = null;

    ret = getMappedType(classFieldKey, elementName);

    if (ret == null && mustExist) {
      throw new XmlSerializationException("No list-mapping found for list-typed field '%s' using xml-element '%s'. Check settings.getListItemMapping().",
          classFieldKey, getElementXPath(elementOrNull, true, true));
    }

    return ret;
  }

  private Class getMappedType(String xpath, String elementName) {
    Class ret = null;
    // TODO this regex mapping takes not full element path, but only element name !!!
    for (XmlListItemMapping mi : settings.getListItemMapping()) {
      if (isRegexMatch(mi.listPathRegex, xpath))
        if (mi.itemPathRegexOrNull == null) {
          ret = mi.itemType;
          break;
        } else if (
            isRegexMatch(mi.itemPathRegexOrNull, elementName)) {
          ret = mi.itemType;
          break;
        }
    }

    return ret;
  }

}
