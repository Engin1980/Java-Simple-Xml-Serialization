package eng.eSystem.xmlSerialization;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Formatter {


  private final Settings settings;
  private Map<Class, String> typeMap = new HashMap<>();

  public Formatter(Settings settings) {
    this.settings = settings;
  }

  public synchronized Document saveObject(Object source) {
    Document doc;
    try {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      doc = dBuilder.newDocument();

      Element el = doc.createElement("root");
      doc.appendChild(el);

      if (settings.isUseSimpleTypeNamesInReferences()) {
        this.typeMap.clear();
      }

      //TODO I cannot store "null"
      if (source == null)
        storeIt(el, null, Object.class);
      else
        storeIt(el, source, source.getClass());

      if (settings.isUseSimpleTypeNamesInReferences()) {
        appendTypeMap(el);
      }

    } catch (ParserConfigurationException ex) {
      throw new XmlSerializationException("Failed to create w3c Document. Internal application error.", ex);
    }

    return doc;
  }

  public void storeField(Element parentElement, Object source, Field f) {

    if (isIgnoredFieldName(f.getName()))
      return;

    Class c = f.getType();
    Object value = getFieldValue(source, f);

    IValueParser customValueParser = Shared.tryGetCustomValueParser(c, settings);

    boolean storedInAttribute =
        Mapping.isSimpleTypeOrEnum(c) || customValueParser != null;

    if (storedInAttribute) {
      if (customValueParser != null)
        value = convertUsingCustomValueParser(value, customValueParser);
      storePrimitive(parentElement, f.getName(), value);
    } else {
      Element el = createElementForObject(parentElement, f.getName());
      storeIt(el, value, c);
    }
  }

  private boolean isIgnoredFieldName(String fieldName) {
    for (String s : settings.getIgnoredFieldsRegex()) {
      if (Shared.isRegexMatch(s, fieldName))
        return true;
    }
    return false;
  }

  public void storeIt(Element el, Object value, Class declaredType) {
    if (settings.isVerbose()) {
      //System.out.println("  storeIt( <" + el.getNodeName() + "<..., " + sourceObject.getClass().getSimpleName() + "." + f.getName());
    }

    if (value == null) {
      storeNullElement(el);
    } else {
      Class realType = value.getClass();
      addClassAttributeIfRequired(el, value.getClass(), declaredType);

      IElementParser customElementParser = Shared.tryGetCustomElementParser(realType, settings);
      if (customElementParser != null) {
        convertAndStoreFieldComplexByCustomParser(el, value, customElementParser);
      } else if (Mapping.isSimpleTypeOrEnum(realType)) {
        // jednoduchý typ
        storePrimitive(el, value);
      } else if (List.class.isAssignableFrom(realType)) {
        storeList(el, (List) value);
      } else if (realType.isArray()) {
        storeArray(el, value);
      } else {
        storeObject(el, value);
      }
    }
  }

  private void appendTypeMap(Element el) {
    el = createElementForObject(el, Shared.TYPE_MAP_ELEMENT_NAME);
    for (Class key : typeMap.keySet()) {
      Element sel = createElementForObject(el, Shared.TYPE_MAP_ITEM_ELEMENT_NAME);
      sel.setAttribute(Shared.TYPE_MAP_KEY_ATTRIBUTE_NAME, typeMap.get(key));
      sel.setAttribute(Shared.TYPE_MAP_FULL_ATTRIBUTE_NAME, key.getName());
    }
  }

  private Element createElementForObject(Element parent, String elementName) {
    Element ret = parent.getOwnerDocument().createElement(elementName);
    parent.appendChild(ret);
    return ret;
  }

  private void convertAndStoreFieldComplexByCustomParser(Element el, Object value, IElementParser parser) {
    try {
      parser.format(value, el);
    } catch (Exception ex) {
      throw new XmlSerializationException(ex,
          "Failed to format value.");
    }
  }

  private Object convertUsingCustomValueParser(Object value, IValueParser parser) {
    String s;
    if (value == null)
      s = settings.getNullString();
    else
      try {
        s = parser.format(value);
      } catch (Exception ex) {
        throw new XmlSerializationException(ex,
            "Failed to format .");
      }

    return s;
  }

  private void storeList(Element el, List list) {

    Class listItemType = deriveListItemType(list);
    addItemTypeAttribute(el, listItemType);

    try {
      for (Object item : list) {

        String tagName;
        if (item == null)
          tagName = "item";
        else {
          XmlListItemMapping map = tryGetListItemMapping(el, item.getClass());
          tagName = (map == null || map.itemElementName == null) ?
              item.getClass().getSimpleName() :
              map.itemElementName;
        }

        Element itemElement = createElementForObject(el, tagName);
        storeIt(itemElement, item, listItemType);
      }

    } catch (Exception ex) {
      throw new XmlSerializationException(ex,
          "Failed to store list.");
    }
  }

  private XmlListItemMapping tryGetListItemMapping(Element listElement, Class itemClass) {
    XmlListItemMapping ret = null;
    String listElementXPath = Shared.getElementXPath(listElement);

    for (XmlListItemMapping map : settings.getListItemMappings()) {
      if (Shared.isRegexMatch(map.listElementXPathRegex, listElementXPath)
          &&
          itemClass.equals(map.itemType)
          ){
        ret = map;
        break;
      }
    }
    return ret;
  }

  private Class deriveListItemType(List value) {
    Class ret;
    List<Class> topTypes = new ArrayList<>(1);

    for (Object o : value) {
      if (o == null) continue;
      Class c = o.getClass();

      if (topTypes.contains(c) == false)
        topTypes.add(c);
    }

    if (topTypes.isEmpty())
      ret = Object.class;
    else {
      ret = topTypes.get(0);
      for (Class topType : topTypes) {
        ret = getBestParent(ret, topType);
      }
    }
    return ret;
  }

  private Class getBestParent(Class a, Class b) {
    Class ret;
    if (a.equals(b))
      ret = a;
    else{
      if (a.isAssignableFrom(b))
        ret = a;
      else if (b.isAssignableFrom(a))
        ret = b;
      else {
        ret = getBestSuperClass(a,b);
        if (ret.equals(Object.class)){
          Class tmp = tryGetSomeSuperInterface(a,b);
          if (tmp != null) ret = tmp;
        }
      }
    }
    return ret;
  }

  private Class tryGetSomeSuperInterface(Class a, Class b) {
    return null;
  }

  private Class getBestSuperClass(Class a, Class b) {
    Class ret;
    while (true){
      a = a.getSuperclass();
      if (a.isAssignableFrom(b)) {
        ret = a;
        break;
      }
    }
    return ret;
  }

  private void storeArray(Element el, Object value) {
    try {
      int cnt = Array.getLength(value);
      Class itemType = value.getClass().getComponentType();
      for (int i = 0; i < cnt; i++) {
        Object item = Array.get(value, i);
        String itemElementName = itemType.getSimpleName();
        while (itemElementName.endsWith("[]")) {
          itemElementName = itemElementName.substring(0, itemElementName.length() - 2) + "_arr";
        }
        Element itemElement = createElementForObject(el, itemElementName);
        storeIt(itemElement, item, itemType);
      }
    } catch (Exception ex) {
      throw new XmlSerializationException(ex,
          "Fail store array.");
    } // try-catch
  }

  private void storeNullElement(Element element) {
    element.setTextContent(settings.getNullString());
  }

  private void storeNullAttribute(Element element, String attributeName) {
    element.setAttribute(attributeName, settings.getNullString());
  }

  private void storeObject(Element el, Object value) {
    Class c = value.getClass();
    Field[] fields = Shared.getDeclaredFields(c);

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
        storeField(el, value, f);
      } catch (Exception ex) {
        throw new XmlSerializationException(ex,
            "Failed to store field '%s' of object of type '%s' into the element '%s'.",
            f.getName(), c.getName(),
            Shared.getElementInfoString(el));
      }
    }
  }

  private void storePrimitive(Element element, Object value) {
    if (value == null)
      storeNullElement(element);
    else {
      element.setTextContent(value.toString());
    }
  }

  private void addClassAttributeIfRequired(Element el, Class realType, Class declaredType) {
    if (declaredType != null && realType.equals(declaredType) == false) {
      addClassAttribute(el, realType);
    }
  }

  private void addClassAttribute(Element element, Class<?> realType) {
    String attributeTypeName;

    if (settings.isUseSimpleTypeNamesInReferences()) {

      if (typeMap.containsKey(realType) == false) {
        String simple = generateTypeSimpleName(realType);
        typeMap.put(realType, simple);
      }
      attributeTypeName = typeMap.get(realType);
    } else
      attributeTypeName = realType.getName();

    element.setAttribute(Shared.TYPE_MAP_OF_ATTRIBUTE_NAME, attributeTypeName);
  }

  private void addItemTypeAttribute(Element element, Class<?> realType) {
    String attributeTypeName;

    if (settings.isUseSimpleTypeNamesInReferences()) {

      if (typeMap.containsKey(realType) == false) {
        String simple = generateTypeSimpleName(realType);
        typeMap.put(realType, simple);
      }
      attributeTypeName = typeMap.get(realType);
    } else
      attributeTypeName = realType.getName();

    element.setAttribute(Shared.TYPE_MAP_ITEM_OF_ATTRIBUTE_NAME, attributeTypeName);
  }

  private String generateTypeSimpleName(Class<?> realType) {
    String orig = realType.getSimpleName();
    String simple = orig;
    if (typeMap.containsValue(simple)) {
      int index = 'A';
      simple = orig + (char) index;
      while (typeMap.containsValue(simple)) {
        index++;
        simple = orig + (char) index;
      }
    }
    return simple;
  }

  private void storePrimitive(Element element, String attributeName, Object value) {
    if (value == null)
      storeNullAttribute(element, attributeName);
    else
      element.setAttribute(attributeName, value.toString());
  }

  @Nullable
  private Object getFieldValue(@NotNull Object sourceObject, @NotNull Field f) {
    Object value;
    try {
      f.setAccessible(true);
      value = f.get(sourceObject);
      f.setAccessible(false);
    } catch (IllegalAccessException ex) {
      throw new XmlSerializationException(ex,
          "Failed to get value of field '%s' from object type '%s'.",
          f.getName(),
          sourceObject.getClass().getName());
    }
    return value;
  }

}
