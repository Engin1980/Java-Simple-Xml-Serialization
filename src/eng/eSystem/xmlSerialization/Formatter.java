package eng.eSystem.xmlSerialization;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import eng.eSystem.collections.IList;
import eng.eSystem.collections.IMap;
import eng.eSystem.collections.ISet;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

public class Formatter {

  private static final String SEPARATOR = "\t";
  private final Settings settings;
  private Map<Class, String> typeMap = new HashMap<>();
  private int logIndent = 0;

  public Formatter(Settings settings) {
    this.settings = settings;
  }

  public synchronized Document saveObject(Object source) throws XmlSerializationException {
    Document doc;
    DocumentBuilderFactory dbFactory;
    DocumentBuilder dBuilder;
    Element el;
    try {

      dbFactory = DocumentBuilderFactory.newInstance();
      dBuilder = dbFactory.newDocumentBuilder();
      doc = dBuilder.newDocument();


      el = doc.createElement("root");
      doc.appendChild(el);
    } catch (ParserConfigurationException ex) {
      throw new XmlSerializationException("Failed to create w3c document. Internal error.", ex);
    }

    if (settings.isUseSimpleTypeNamesInReferences()) {
      this.typeMap.clear();
    }

    if (source == null)
      storeIt(el, null, Object.class);
    else
      storeIt(el, source, source.getClass());

    if (settings.isUseSimpleTypeNamesInReferences()) {
      appendTypeMap(el);
    }

    return doc;
  }

  public void storeField(Element parentElement, Object source, Field f) throws XmlSerializationException {
    logIndent++;
    logVerbose("serialize field %s.%s into %s",
        f.getDeclaringClass().getName(),
        f.getName(),
        Shared.getElementInfoString(parentElement)
    );

    if (isIgnoredFieldName(f.getName())) {
      logVerbose("... ignored, skipped");
      return;
    }

    try {
      Class c = f.getType();
      Object value = getFieldValue(source, f);

      IValueParser customValueParser = Shared.tryGetCustomValueParser(c, settings);

      boolean storedInAttribute =
          Mapping.isSimpleTypeOrEnum(c) || customValueParser != null;

      if (storedInAttribute) {
        if (customValueParser != null) {
          logVerbose("... applied custom value parser %s ", customValueParser.getClass().getName());
          value = convertUsingCustomValueParser(value, customValueParser);
        }
        storePrimitive(parentElement, f.getName(), value);
      } else {
        String elementName = f.getName();
        XmlCustomFieldMapping map = tryGetCustomFieldMapping(f, value);
        if (map != null) {
          logVerbose("... custom field mapping applied %s", map);
          elementName = map.getXmlElementName();
          c = map.getTargetFieldClass();
        }
        Element el = createElementForObject(parentElement, elementName);
        storeIt(el, value, c);
      }
    } catch (XmlSerializationException ex) {
      throw new XmlSerializationException(ex,
          "Failed to store field '%s.%s' ('%s') into %s.",
          f.getDeclaringClass().getName(), f.getName(), f.getType().getName(), Shared.getElementInfoString(parentElement));
    }
    logIndent--;
  }

  public void storeIt(Element el, Object value, Class declaredType) throws XmlSerializationException {
    logIndent++;
    logVerbose("serialize %s (%s) -> <%s>", value, declaredType.getClass().getName(), el.getNodeName());

    try {
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
        } else if (IList.class.isAssignableFrom(realType)) {
          storeIList(el, (IList) value);
        } else if (Set.class.isAssignableFrom(realType)) {
          storeSet(el, (Set) value);
        } else if (ISet.class.isAssignableFrom(realType)) {
          storeISet(el, (ISet) value);
        } else if (Map.class.isAssignableFrom(realType)) {
          storeMap(el, (Map) value);
        } else if (IMap.class.isAssignableFrom(realType)) {
          storeIMap(el, (IMap) value);
        } else if (realType.isArray()) {
          storeArray(el, value);
        } else {
          storeObject(el, value);
        }
      }
    } catch (XmlSerializationException ex) {
      String valueTypeName = value == null ? "null" : value.getClass().getName();
      throw new XmlSerializationException(ex,
          "Failed to store '%s' (%s) into element %s.",
          value, valueTypeName, Shared.getElementInfoString(el));
    }

    logIndent--;
  }

  private void storeISet(Element el, ISet iset) throws XmlSerializationException {
    Set set = iset.toSet();
    storeSet(el, set);
  }

  private void storeSet(Element el, Set set) throws XmlSerializationException {
    Class listItemType = deriveIterableItemType(set);
    addItemTypeAttribute(el, listItemType);

    for (Object item : set) {

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
  }

  private void storeIList(Element el, IList list) throws XmlSerializationException {
    List lst = list.toList();
    storeList(el, lst);
  }

  private void logVerbose(String format, Object... params) {
    if (settings.isVerbose())
      Shared.log(Shared.eLogType.info, format, params);
  }

  private XmlCustomFieldMapping tryGetCustomFieldMapping(Field f, Object value) {
    XmlCustomFieldMapping ret = null;
    if (value != null) {
      for (XmlCustomFieldMapping mapping : settings.getCustomFieldMappings()) {
        if (isMappingFitting(mapping, f, value)) {
          ret = mapping;
          break;
        }
      }
    }

    return ret;
  }

  private boolean isMappingFitting(XmlCustomFieldMapping mapping, Field f, Object value) {
    if (!mapping.getFieldName().equals(f.getName()))
      return false;

    if (!(mapping.getTargetFieldClass().equals(value.getClass())))
      return false;

    if (mapping.getClassDeclaringField() != null && !(mapping.getClassDeclaringField().equals(f.getDeclaringClass())))
      return false;

    if (mapping.getDeclaredFieldClass() != null && !(mapping.getDeclaredFieldClass().equals(f.getType())))
      return false;

    return true;
  }

  private boolean isIgnoredFieldName(String fieldName) {
    for (String s : settings.getIgnoredFieldsRegex()) {
      if (Shared.isRegexMatch(s, fieldName))
        return true;
    }
    return false;
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

  private void convertAndStoreFieldComplexByCustomParser(Element el, Object value, IElementParser parser) throws XmlSerializationException {
    try {
      parser.format(value, el);
    } catch (Exception ex) {
      throw new XmlSerializationException(ex,
          "Failed to format value '%s' (%s) by custom element parser '%s'.",
          value.toString(), value.getClass().getName(), parser.getClass().getName());
    }
  }

  private Object convertUsingCustomValueParser(Object value, IValueParser parser) throws XmlSerializationException {
    String s;
    try {
      s = parser.format(value);
    } catch (Exception ex) {
      throw new XmlSerializationException(ex,
          "Failed to format value '%s' (%s) by custom value parser '%s'.",
          value.toString(), value.getClass().getName(), parser.getClass().getName());
    }

    return s;
  }

  private void storeList(Element el, List list) throws XmlSerializationException {

    Class listItemType = deriveIterableItemType(list);
    addItemTypeAttribute(el, listItemType);

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
  }

  private void storeMap(Element el, Map map) throws XmlSerializationException {

    Class keyItemType = deriveIterableItemType(map.keySet());
    addKeyTypeAttribute(el, keyItemType);
    Class valueItemType = deriveIterableItemType(map.values());
    addValueTypeAttribute(el, valueItemType);

    for (Object key : map.keySet()) {
      Object value = map.get(key);

      XmlMapItemMapping mem = tryGetMapItemMapping(el, key, value);
      String tagName = (mem == null || mem.itemElementName == null) ?
          "item" :
          mem.itemElementName;

      Element itemElement = createElementForObject(el, tagName);

      Element keyElement = createElementForObject(itemElement, "key");
      storeIt(keyElement, key, keyItemType);

      Element valueElement = createElementForObject(itemElement, "value");
      storeIt(valueElement, value, valueItemType);
    }
  }

  private void storeIMap(Element el, IMap map) throws XmlSerializationException {

    Class keyItemType = deriveIterableItemType(map.getKeys());
    addKeyTypeAttribute(el, keyItemType);
    Class valueItemType = deriveIterableItemType(map.getValues());
    addValueTypeAttribute(el, valueItemType);

    for (Object key : map.getKeys()) {
      Object value = map.get(key);
      XmlMapItemMapping mem = tryGetMapItemMapping(el, key, value);
      String tagName = (mem == null || mem.itemElementName == null) ?
          "item" :
          mem.itemElementName;

      Element itemElement = createElementForObject(el, tagName);

      Element keyElement = createElementForObject(itemElement, "key");
      storeIt(keyElement, key, keyItemType);

      Element valueElement = createElementForObject(itemElement, "value");
      storeIt(valueElement, value, valueItemType);
    }
  }

  private XmlListItemMapping tryGetListItemMapping(Element listElement, Class itemClass) {
    XmlListItemMapping ret = null;
    String listElementXPath = Shared.getElementXPath(listElement);

    for (XmlListItemMapping map : settings.getListItemMappings()) {
      if (Shared.isRegexMatch(map.collectionElementXPathRegex, listElementXPath)
          &&
          itemClass.equals(map.itemType)
          ) {
        ret = map;
        break;
      }
    }
    return ret;
  }

  private XmlMapItemMapping tryGetMapItemMapping(Element listElement, Object key, Object value) {
    XmlMapItemMapping ret = null;
    String listElementXPath = Shared.getElementXPath(listElement);

    Class keyClass = key.getClass();
    Class valueClass;
    if (value == null)
      valueClass = null;
    else
      valueClass = value.getClass();

    for (XmlMapItemMapping map : settings.getMapItemMappings()) {
      if (Shared.isRegexMatch(map.collectionElementXPathRegex, listElementXPath)
          &&
          keyClass.equals(map.keyType)
          &&
          (valueClass == null || valueClass.equals(map.valueType))
          ) {
        ret = map;
        break;
      }
    }
    return ret;
  }

  private Class deriveIterableItemType(Iterable value) {
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
    else {
      if (a.isAssignableFrom(b))
        ret = a;
      else if (b.isAssignableFrom(a))
        ret = b;
      else {
        ret = getBestSuperClass(a, b);
        if (ret.equals(Object.class)) {
          Class tmp = tryGetSomeSuperInterface(a, b);
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
    while (true) {
      a = a.getSuperclass();
      if (a.isAssignableFrom(b)) {
        ret = a;
        break;
      }
    }
    return ret;
  }

  private void storeArray(Element el, Object value) throws XmlSerializationException {
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
  }

  private void storeNullElement(Element element) {
    element.setTextContent(settings.getNullString());
  }

  private void storeNullAttribute(Element element, String attributeName) {
    element.setAttribute(attributeName, settings.getNullString());
  }

  private void storeObject(Element el, Object value) throws XmlSerializationException {
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
            "Failed to store field '%s.%s' into the element '%s'.",
            c.getName(), f.getName(),
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
    String attributeTypeName = registerAndGetTypeForAttribute(realType);
    element.setAttribute(Shared.TYPE_MAP_OF_ATTRIBUTE_NAME, attributeTypeName);
  }

  private String registerAndGetTypeForAttribute(Class<?> type) {
    String ret;
    if (settings.isUseSimpleTypeNamesInReferences()) {

      if (typeMap.containsKey(type) == false) {
        String simple = generateTypeSimpleName(type);
        typeMap.put(type, simple);
      }
      ret = typeMap.get(type);
    } else
      ret = type.getName();
    return ret;
  }

  private void addItemTypeAttribute(Element element, Class<?> realType) {
    String attributeTypeName = registerAndGetTypeForAttribute(realType);
    element.setAttribute(Shared.TYPE_MAP_ITEM_OF_ATTRIBUTE_NAME, attributeTypeName);
  }

  private void addKeyTypeAttribute(Element element, Class<?> realType) {
    String attributeTypeName = registerAndGetTypeForAttribute(realType);
    element.setAttribute(Shared.TYPE_MAP_KEY_OF_ATTRIBUTE_NAME, attributeTypeName);
  }

  private void addValueTypeAttribute(Element element, Class<?> realType) {
    String attributeTypeName = registerAndGetTypeForAttribute(realType);
    element.setAttribute(Shared.TYPE_MAP_VALUE_OF_ATTRIBUTE_NAME, attributeTypeName);
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
  private Object getFieldValue(@NotNull Object sourceObject, @NotNull Field f) throws XmlSerializationException {
    Object value;
    try {
      f.setAccessible(true);
      value = f.get(sourceObject);
      f.setAccessible(false);
    } catch (IllegalAccessException ex) {
      throw new XmlSerializationException(ex,
          "Failed to get value of field '%s.%s'.",
          sourceObject.getClass().getName(), f.getName()
      );
    }
    return value;
  }

}
