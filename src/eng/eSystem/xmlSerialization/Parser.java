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
import java.util.*;

import static eng.eSystem.xmlSerialization.Shared.*;

/**
 * @author Marek
 */
class Parser {

  private static final Object UNSET = new Object();
  private final Settings settings;
  private Map<String, String> typeMap = new HashMap<>();

  Parser(Settings settings) {
    this.settings = settings;
  }

  public synchronized Object deserialize(Element root, Class objectType) {
    if (root == null) {
      throw new IllegalArgumentException("Value of {el} cannot not be null.");
    }
    if (objectType == null) {
      throw new IllegalArgumentException("Value of {objectType} cannot not be null.");
    }

    if (settings.isVerbose()) {
      System.out.println("  deserialize( <" + root.getNodeName() + "...>, " + objectType.getSimpleName());
    }

    loadTypeMaps(root);

    Class c = objectType;
    Object ret = parseIt(root, c);

    return ret;
  }

  private void loadTypeMaps(Element el) {
    typeMap.clear();

    Element elm = getElement(el, Shared.TYPE_MAP_ELEMENT_NAME, false);
    if (elm != null){
        List<Element> types = getElements(elm);
      for (Element type : types) {
        String key = type.getAttribute(Shared.TYPE_MAP_KEY_ATTRIBUTE_NAME);
        String fullName = type.getAttribute(Shared.TYPE_MAP_FULL_ATTRIBUTE_NAME);
        typeMap.put(key, fullName);
      }
    }
  }

  public Object parseArray(Element el, Class c) {

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

  private Object parseIt(Element el, Class type) {
    Object ret;
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
      } else if (type.isArray()) {
        ret = parseArray(el, type);
      } else {
        ret = parseObject(el, type);
      }
    }
    return ret;
  }

  private Object convertElementByElementParser(Element el, IElementParser customElementParser) {
    Object ret;
    try {
      ret = customElementParser.parse(el);
    } catch (Exception ex) {
      throw new XmlSerializationException(
          "Failed to parse instance for " + customElementParser.getType() +
              " using custom-element-parser " + customElementParser.getClass().getName() + ".",
          ex);
    }
    return ret;
  }

  private Object parseList(Element el, Class c) {
    Object ret;

    List lst = (List) createObjectInstanceByElement(el, c);

    List<Element> children = getElements(el);
    removeTypeMapElementIfExist(children);

    Class expectedClass = tryGetArrayItemTypeByElement(el);
    if (expectedClass == null) expectedClass = Object.class;

    for (Element e : children) {

      Class itemExpectedClass;

      XmlListItemMapping map = tryGetListElementMapping(e);
      if (map != null)
        itemExpectedClass = map.itemType;
      else
        itemExpectedClass = expectedClass;

      Object itemValue = parseIt(e, itemExpectedClass);
      lst.add(itemValue);
    }

    ret = lst;
    return ret;
  }

  private Object createObjectInstanceByElement(Element el, Class c){

    Class customType = tryGetCustomTypeByElement(el);
    if (customType != null)
      c = customType;

    Object ret = createObjectInstance(c);

    return ret;
  }

  private Object parseObject(Element el, Class c) {

    Object ret = createObjectInstanceByElement(el, c);

    Field[] fields = getDeclaredFields(ret.getClass());

    for (Field f : fields) {
      if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
        continue; // statické přeskakujem
      } else if (f.getAnnotation(XmlIgnore.class) != null) {
        if (settings.isVerbose()) {
          System.out.println("  " + el.getNodeName() + "." + f.getName() + " field skipped due to @XmlIgnored annotation.");
        }
        continue; // skipped due to annotation
      } else if (Shared.isSkippedBySettings(f, settings)) {
        if (settings.isVerbose()) {
          System.out.println("  " + el.getNodeName() + "." + f.getName() + " field skipped due to settings-ignoredFieldsRegex list.");
        }
        continue;
      }
      try {
        Object tmp = parseField(el, f);
        if (tmp != UNSET) {
          f.setAccessible(true);
          f.set(ret, tmp);
        }
      } catch (Exception ex) {
        throw new XmlSerializationException(ex,
            "Failed to fill field '%s' ('%s') of object of type '%s' using element '%s'.",
            f.getName(), f.getType().getName(), c.getName(),
            Shared.getElementInfoString(el));
      }
    }

    return ret;
  }

  private Class tryGetCustomTypeByElement(Element el) {
    Class ret = null;
    String tmp;
    if (el.hasAttribute(Shared.TYPE_MAP_OF_ATTRIBUTE_NAME)) {
      tmp = el.getAttribute(Shared.TYPE_MAP_OF_ATTRIBUTE_NAME);
      ret = loadClass(tmp);
    }

    return ret;
  }

  private Class tryGetArrayItemTypeByElement(Element el) {
    Class ret = null;
    String tmp;
    if (el.hasAttribute(Shared.TYPE_MAP_ITEM_OF_ATTRIBUTE_NAME)) {
      tmp = el.getAttribute(Shared.TYPE_MAP_ITEM_OF_ATTRIBUTE_NAME);
      ret = loadClass(tmp);
    } else
      ret = null;

    return ret;
  }

  private Class loadClass(String className) {
    Class ret;

    String tmp = tryGetMappedType(className);
    if (tmp != null)
      className = tmp;

    try {
      ret = Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new XmlDeserializationException(e, "Failed to load class %s.", className);
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

  private Object parsePrimitiveFromElement(Element el, Class c) {
    String txt = el.getTextContent();
    Class tmp = tryGetCustomTypeByElement(el);
    if (tmp != null) c = tmp;
    Object ret = convertToType(txt, c);
    return ret;
  }

  private Object parseField(Element parentElement, Field f) {
    Object ret;
    if (settings.isVerbose()) {
      //System.out.println("  fillField( <" + parentElement.getNodeName() + "...>, " + targetObject.getClass().getSimpleName() + "." + f.getName());
    }

    Class c = f.getType();

    IValueParser customValueParser = Shared.tryGetCustomValueParser(c, settings);
    boolean storedInAttribute =
        Mapping.isSimpleTypeOrEnum(c) || customValueParser != null;

    boolean required = f.getAnnotation(XmlOptional.class) == null;
    if (storedInAttribute) {
      String attributeValue = readAttributeValue(parentElement, f.getName(), required);
      if (attributeValue == null) {
        ret = UNSET;
      } else if (attributeValue.equals(settings.getNullString())){
        ret = null;
      } else {
        if (customValueParser != null)
          ret = convertValueByCustomParser(attributeValue, customValueParser);
        else
          ret = convertToType(attributeValue, c);
      }
    } else {
      Element el = getElement(parentElement,f.getName(), required);
      if (el == null)
        ret = UNSET;
      else {
        ret = parseIt(el, c);
      }
    }

    return ret;
  }

  private Element getElement(Element parentElement, String name, boolean required) {
    Element ret = null;
    List<Element> elms = getElements(parentElement);
    for (Element elm : elms) {
      if (elm.getNodeName().equals(name)){
        ret = elm;
        break;
      }
    }
    if (ret == null && required){
      throw new XmlSerializationException("Unable to find sub-element \"" + name + "\" in element \"" +
          Shared.getElementInfoString(parentElement) + "\"");
    }
    return ret;
  }

  private void removeTypeMapElementIfExist(List<Element> lst){
    for (int i = 0; i < lst.size(); i++) {
      if (lst.get(i).getNodeName().equals(Shared.TYPE_MAP_ELEMENT_NAME)){
        lst.remove(lst.get(i));
        i--;
      }
    }
  }

  private Object convertValueByCustomParser(String value, IValueParser parser) {
    Object ret;
    ret = parser.parse(value);
    return ret;
  }

  private String readAttributeValue(Element el, String key, boolean isRequired) {
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
          Shared.getElementInfoString(el) + "\"");
    }

    return ret;
  }

  private Object convertToType(String value, Class<?> type) {
    Object ret;
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
          throw new XmlSerializationException("Type " + type.getName() + " does not have primitive conversion. Use custom IValueParser.");
      }
    }

    return ret;
  }

  private boolean isNullValuedElement(Element el) {
    if (el.getTextContent().equals(settings.getNullString()))
      return true;
    else
      return false;
  }

  private Object createObjectInstance(Class<?> type) {
    Object ret;

    if (type.equals(List.class) ||type.equals(AbstractList.class)) {
      type = settings.getDefaultListTypeImplementation();
    }

    // check if there is not an custom instance creator
    IInstanceCreator creator = tryGetInstanceCreator(type);

    if (creator == null)
      try {
        ret = type.newInstance();
      } catch (InstantiationException | IllegalAccessException ex) {
        throw new XmlDeserializationException(
            ex,
            "Failed to create new instance of %s. Probably missing public parameter-less constructor.",
            type.getName());
      }
    else {
      try {
        ret = creator.createInstance();
      } catch (Exception ex) {
        throw new XmlDeserializationException(
            ex,
            "Failed to create a new instance of {%s} using custom creator %s.",
            type.getName() , creator.getClass().getName());
      }
    }
    return ret;
  }

  private Object createArrayInstance(Class<?> elementType, int length) {
    Object ret;
    ret = Array.newInstance(elementType, length);
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
      if (isRegexMatch(mi.listElementXPathRegex, parentXPath) && (mi.itemElementName == null || mi.itemElementName.equals(itemElement.getNodeName()))){
        ret = mi;
        break;
      }
    }

    return ret;
  }
}
