/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eng.EXmlSerialization;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.text.StyledEditorKit;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static eng.EXmlSerialization.Shared.*;

/**
 * @author Marek
 */
class Reflecter {

  private final Settings settings;

  Reflecter(Settings settings) {
    this.settings = settings;
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
      } else if (isSkippedBySettings(f)) {
        if (settings.isVerbose()) {
          System.out.println("  " + el.getNodeName() + "." + f.getName() + " field skipped due to settings-ignoredFieldsRegex list.");
        }
        continue; // parent neplníme, ty jsou reference na nadřazené objekty a plní se sami
      }
      try {
        fillField(el, f, targetObject);
      } catch (Exception ex){
        throw new XmlSerializationException(ex,
            "Failed to fill field '%s' of object of type '%s' using element '%s'.",
            f.getName(), c.getName(),
            getElementXPath(el,true));
      }
    }
  }

  private Field[] getDeclaredFields(Class c){
    List<Field> lst = new ArrayList<>();
    Field[] fs;

    while (c != null){
      fs = c.getDeclaredFields();
      for (Field f : fs) {
        lst.add(f);
      }
      c = c.getSuperclass();
    }

    Field[] ret = lst.toArray(new Field[0]);
    return ret;
  }

  /**
   * Returns true if field should be skipped according to regex ignore settings
   *
   * @param f
   * @return
   */
  private boolean isSkippedBySettings(Field f) {
    boolean ret = false;

    for (String regex : this.settings.getIgnoredFieldsRegex()) {
      Pattern p = Pattern.compile(regex);
      if (p.matcher(f.getName()).find()) {
        ret = true;
        break;
      }
    }

    return ret;
  }

  void fillList(Element el, List lst) {
    fillFieldList(el, lst, el.getNodeName()); //lst.getClass().getSimpleName());
  }

  private <T> void fillField(Element el, Field f, T targetObject) {
    if (settings.isVerbose()) {
      System.out.println("  fillField( <" + el.getNodeName() + "...>, " + targetObject.getClass().getSimpleName() + "." + f.getName());
    }

    Class c = f.getType();
    IValueParser customValueParser = tryGetCustomValueParser(c);
    IElementParser customElementParser = tryGetCustomElementParser(c);
    if (customValueParser != null) {
      convertAndSetFieldSimpleByCustomParser(el, f, customValueParser, targetObject);
    } else if (customElementParser != null) {
      convertAndSetFieldComplexByCustomParser(el, f, customElementParser, targetObject);
    } else if (Mapping.isSimpleTypeOrEnum(c)) {
      // jednoduchý typ
      convertAndSetFieldValue(el, f, targetObject);
    } else if (List.class.isAssignableFrom(c)) {
      setFieldList(el, f, targetObject);
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
      newInstance = customElementParser.parse(subEl);
    } catch (Exception ex) {
      throw new XmlSerializationException(
          "Failed to parse instance for " + ref.getClass().getSimpleName() + "." + f.getName() +
              " using custom-element-parser " + customElementParser.getClass().getName() + ".",
          ex);
    }
    try {
      f.setAccessible(true);
      f.set(ref, newInstance);
      f.setAccessible(false);
    } catch (IllegalArgumentException | IllegalAccessException ex) {
      throw new XmlSerializationException(
          "Failed to set value to field " + ref.getClass().getName() + "." + f.getName(), ex);
    }
  }

  private IElementParser tryGetCustomElementParser(Class c) {
    IElementParser ret = null;

    for (IElementParser iElementParser : settings.getElementParsers()) {
      if (iElementParser.getTypeName().equals(c.getName())) {
        ret = iElementParser;
        break;
      }
    }

    return ret;
  }

  private IValueParser tryGetCustomValueParser(Class c) {
    IValueParser ret = null;

    for (IValueParser iValueParser : settings.getValueParsers()) {
      if (iValueParser.getTypeName().equals(c.getName())) {
        ret = iValueParser;
        break;
      }
    }

    return ret;
  }

  private <T> void convertAndSetFieldSimpleByCustomParser(Element el, Field f, IValueParser parser, T targetObject) {
    boolean required = f.getAnnotation(XmlOptional.class) == null;
    String tmpS = extractSimpleValueFromElement(el, f.getName(), required);
    if (tmpS == null) {
      return;
    }
    Object tmpO = parser.parse(tmpS);
    setFieldValue(f, targetObject, tmpO);
  }

  private <T> void convertAndSetFieldValue(Element el, Field f, T targetObject) {
    boolean required = f.getAnnotation(XmlOptional.class) == null;
    String tmpS = extractSimpleValueFromElement(el, f.getName(), required);
    if (tmpS == null) {
      return;
    }
    Object tmpO = convertToType(tmpS, f.getType());
    setFieldValue(f, targetObject, tmpO);
  }

  private String extractSimpleValueFromElement(Element el, String key, boolean isRequired) {
    String ret = null;
    if (el.hasAttribute(key)) {
      ret = el.getAttribute(key);
    } else {
      NodeList tmp = el.getElementsByTagName(key);
      if (tmp.getLength() > 0) {
        ret = tmp.item(0).getTextContent();
      }
    }

    if (ret == null && isRequired) {
      throw new XmlSerializationException("Unable to find key \"" + key + "\" in element \"" + el.getNodeName() + "\"");
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
        case "Integer":
          ret = Integer.parseInt(value);
          break;
        case "double":
        case "Double":
          ret = Double.parseDouble(value);
          break;
        case "char":
        case "Character":
          ret = value.charAt(0);
          break;
        case "boolean":
        case "Boolean":
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
      if (el.getAttribute(f.getName()).isEmpty() == false){
        throw XmlInvalidDataException.createAttributeInsteadOfElementFound(el, f.getName(), ref.getClass());
      }
      if (required) {
        throw XmlInvalidDataException.createNoSuchElement(el, f.getName(), ref.getClass());
      }
      else
        subEl = null;
    }

    // if is optional and element-data has not been found, skip
    if (subEl == null)
      return;

    // then create instance and fill it
    Object newInstance;
    try {
      newInstance = createInstance(f.getType());
    } catch (Exception ex) {
      throw new XmlSerializationException(
          "Failed to create instance for " + ref.getClass().getSimpleName() + "." + f.getName() + ".",
          ex);
    }
    try {
      f.setAccessible(true);
      f.set(ref, newInstance);
      f.setAccessible(false);
    } catch (IllegalArgumentException | IllegalAccessException ex) {
      throw new XmlSerializationException(
          "Failed to set value to field " + ref.getClass().getName() + "." + f.getName(), ex);
    }

    fillObject(subEl, newInstance);
  }

  private Object createInstance(Class<?> type) {
    Object ret;

    // programuje se proti List, tak sem přijde požadavek na "List"
    // ale to je rozhraní, takže ho nahradím ArrayListem
    if (type.equals(List.class)) {
      type = settings.getDefaultListTypeImplementation();
    }

    // check if there is not an custom instance creator
    IInstanceCreator creator = tryGetInstnaceCreator(type);

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

  private IInstanceCreator tryGetInstnaceCreator(Class<?> type) {
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
    List lst = (List) createInstance(f.getType());
    setFieldValue(f, targetObject, lst);

    // zanoření
    List<Element> tmp = getElements(el, f.getName());
    if (tmp.isEmpty()) {
      return;
    }
    el = tmp.get(0);

    String key = targetObject.getClass().getSimpleName() + "." + f.getName();
    fillFieldList(el, lst, key);
  }

  private void fillFieldList(Element el, List lst, String classFieldKey) {
    List<Element> children = getElements(el);
    for (Element e : children) {
      Class itemType = getItemType(classFieldKey, e);
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

  private Class getItemType(String classFieldKey, Element elementOrNull) {
    @SuppressWarnings("UnusedAssignment")
    Class ret = null;

    String elementName;
    if (elementOrNull != null)
      elementName = elementOrNull.getNodeName();
    else
      elementName = null;

    ret = getMappedType(classFieldKey, elementName);

    if (ret == null) {
      throw new XmlSerializationException("No list-mapping found for list-typed field '%s' using xml-element '%s'. Check settings.getListItemMapping().",
          classFieldKey, getElementXPath(elementOrNull,true));
    }

    return ret;
  }

  Class getMappedType(String key, String elementName) {
    Class ret = null;
    // TODO this regex mapping takes not full element path, but only element name !!!
    for (XmlListItemMapping mi : settings.getListItemMapping()) {
      if (isRegexMatch(mi.listPathRegex, key))
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
