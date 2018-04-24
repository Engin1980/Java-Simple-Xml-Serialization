package eng.eSystem.xmlSerialization;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import eng.eSystem.collections.IList;
import eng.eSystem.collections.IMap;
import eng.eSystem.collections.ISet;
import eng.eSystem.eXml.XDocument;
import eng.eSystem.eXml.XElement;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

public class Formatter {

  private static final String SEPARATOR = "\t";
  private final Settings settings;
  private int logIndent = 0;

  public Formatter(Settings settings) {
    this.settings = settings;
  }

  public synchronized XDocument saveObject(Object source) throws XmlSerializationException {
    XElement root = new XElement("root");
    XDocument doc = new XDocument(root);


    if (source == null)
      storeIt(root, null, Object.class);
    else
      storeIt(root, source, source.getClass());

    if (settings.isUseSimpleTypeNamesInReferences()) {
      Shared.applyTypeMappingShortening(root);
    }

    return doc;
  }

  private void storeField(XElement parentElement, Object source, Field f) throws XmlSerializationException {
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
        XElement el = createElementForObject(parentElement, elementName);
        storeIt(el, value, c);
      }
    } catch (XmlSerializationException ex) {
      throw new XmlSerializationException(ex,
          "Failed to store field '%s.%s' ('%s') into %s.",
          f.getDeclaringClass().getName(), f.getName(), f.getType().getName(), Shared.getElementInfoString(parentElement));
    }
    logIndent--;
  }

  private void storeIt(XElement el, Object value, Class declaredType) throws XmlSerializationException {
    logIndent++;
    logVerbose("serialize %s (%s) -> <%s>", value, declaredType.getClass().getName(), el.getName());

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

  private void storeISet(XElement el, ISet iset) throws XmlSerializationException {
    Set set = iset.toSet();
    storeSet(el, set);
  }

  private void storeSet(XElement el, Set set) throws XmlSerializationException {
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

      XElement itemElement = createElementForObject(el, tagName);
      storeIt(itemElement, item, listItemType);
    }
  }

  private void storeIList(XElement el, IList list) throws XmlSerializationException {
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

  private XElement createElementForObject(XElement parent, String elementName) {
    XElement ret = new XElement(elementName);
    parent.addElement(ret);
    return ret;
  }

  private void convertAndStoreFieldComplexByCustomParser(XElement el, Object value, IElementParser parser) throws XmlSerializationException {
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

  private void storeList(XElement el, List list) throws XmlSerializationException {

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

      XElement itemElement = createElementForObject(el, tagName);
      storeIt(itemElement, item, listItemType);
    }
  }

  private void storeMap(XElement el, Map map) throws XmlSerializationException {

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

      XElement itemElement = createElementForObject(el, tagName);

      XElement keyElement = createElementForObject(itemElement, "key");
      storeIt(keyElement, key, keyItemType);

      XElement valueElement = createElementForObject(itemElement, "value");
      storeIt(valueElement, value, valueItemType);
    }
  }

  private void storeIMap(XElement el, IMap map) throws XmlSerializationException {

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

      XElement itemElement = createElementForObject(el, tagName);

      XElement keyElement = createElementForObject(itemElement, "key");
      storeIt(keyElement, key, keyItemType);

      XElement valueElement = createElementForObject(itemElement, "value");
      storeIt(valueElement, value, valueItemType);
    }
  }

  private XmlListItemMapping tryGetListItemMapping(XElement listElement, Class itemClass) {
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

  private XmlMapItemMapping tryGetMapItemMapping(XElement listElement, Object key, Object value) {
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

  private void storeArray(XElement el, Object value) throws XmlSerializationException {
    int cnt = Array.getLength(value);
    Class itemType = value.getClass().getComponentType();
    for (int i = 0; i < cnt; i++) {
      Object item = Array.get(value, i);
      String itemElementName = itemType.getSimpleName();
      while (itemElementName.endsWith("[]")) {
        itemElementName = itemElementName.substring(0, itemElementName.length() - 2) + "_arr";
      }
      XElement itemElement = createElementForObject(el, itemElementName);
      storeIt(itemElement, item, itemType);
    }
  }

  private void storeNullElement(XElement element) {
    element.setContent(settings.getNullString());
  }

  private void storeNullAttribute(XElement element, String attributeName) {
    element.setAttribute(attributeName, settings.getNullString());
  }

  private void storeObject(XElement el, Object value) throws XmlSerializationException {
    Class c = value.getClass();
    Field[] fields = Shared.getDeclaredFields(c);

    for (Field f : fields) {
      if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
        continue; // statické přeskakujem
      } else if (f.getAnnotation(XmlIgnore.class) != null) {
        if (settings.isVerbose()) {
          System.out.println("  " + el.getName() + "." + f.getName() + " field skipped due to @XmlIgnored annotation.");
        }
        continue; // skipped due to annotation
      } else if (Shared.isSkippedBySettings(f, settings)) {
        if (settings.isVerbose()) {
          System.out.println("  " + el.getName() + "." + f.getName() + " field skipped due to settings-ignoredFieldsRegex list.");
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

  private void storePrimitive(XElement element, Object value) {
    if (value == null)
      storeNullElement(element);
    else {
      element.setContent(value.toString());
    }
  }

  private void addClassAttributeIfRequired(XElement el, Class realType, Class declaredType) {
    if (declaredType != null && realType.equals(declaredType) == false) {
      addClassAttribute(el, realType);
    }
  }

  private void addClassAttribute(XElement element, Class<?> realType) {
    element.setAttribute(Shared.TYPE_MAP_OF_ATTRIBUTE_NAME, realType.getName());
  }

  private void addItemTypeAttribute(XElement element, Class<?> realType) {
    element.setAttribute(Shared.TYPE_MAP_ITEM_OF_ATTRIBUTE_NAME, realType.getName());
  }

  private void addKeyTypeAttribute(XElement element, Class<?> realType) {
    element.setAttribute(Shared.TYPE_MAP_KEY_OF_ATTRIBUTE_NAME, realType.getName());
  }

  private void addValueTypeAttribute(XElement element, Class<?> realType) {
    element.setAttribute(Shared.TYPE_MAP_VALUE_OF_ATTRIBUTE_NAME, realType.getName());
  }


  private void storePrimitive(XElement element, String attributeName, Object value) {
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
