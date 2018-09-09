package eng.eSystem.xmlSerialization;

import eng.eSystem.collections.EList;
import eng.eSystem.collections.EMap;
import eng.eSystem.collections.IList;
import eng.eSystem.collections.IMap;
import eng.eSystem.eXml.XElement;
import eng.eSystem.xmlSerialization.exceptions.XmlSerializationException;

import java.lang.reflect.Modifier;
import java.util.Map;

import static eng.eSystem.utilites.FunctionShortcuts.sf;

public class TypeMappingManager {

  public static final String TYPE_MAP_DEFINITION_ELEMENT_NAME = "__typeMap";
  public static final String TYPE_MAP_DEFINITION_ITEM_ELEMENT_NAME = "item";
  public static final String TYPE_MAP_DEFINITION_FULL_ATTRIBUTE_NAME = "class";
  public static final String TYPE_MAP_DEFINITION_KEY_ATTRIBUTE_NAME = "key";
  public static final String TYPE_MAP_OF_ATTRIBUTE_NAME = "__class";
  public static final String TYPE_MAP_ITEM_OF_ATTRIBUTE_NAME = "__itemClass";
  public static final String TYPE_MAP_KEY_OF_ATTRIBUTE_NAME = "__keyClass";
  public static final String TYPE_MAP_VALUE_OF_ATTRIBUTE_NAME = "__valueClass";
  private final static IList<Class> simpleTypes = new EList();
  private static IMap<Class, Class> primitiveTypeMapping = new EMap<>();

  public static Class wrapPrimitiveType(Class primitiveType) {
    assert primitiveType.isPrimitive();

    Class ret = primitiveTypeMapping.get(primitiveType);
    return ret;
  }

  static {
    simpleTypes.add(Integer.class);
    simpleTypes.add(int.class);
    simpleTypes.add(Long.class);
    simpleTypes.add(long.class);
    simpleTypes.add(Double.class);
    simpleTypes.add(double.class);
    simpleTypes.add(Boolean.class);
    simpleTypes.add(boolean.class);
    simpleTypes.add(String.class);
    simpleTypes.add(char.class);
    simpleTypes.add(Character.class);
    simpleTypes.add(Number.class);
    simpleTypes.add(byte.class);
    simpleTypes.add(Byte.class);
    simpleTypes.add(short.class);
    simpleTypes.add(Short.class);
    simpleTypes.add(float.class);
    simpleTypes.add(Float.class);

    primitiveTypeMapping.set(int.class, Integer.class);
    primitiveTypeMapping.set(long.class, Long.class);
    primitiveTypeMapping.set(double.class, Double.class);
    primitiveTypeMapping.set(boolean.class, Boolean.class);
    primitiveTypeMapping.set(char.class, Character.class);
    primitiveTypeMapping.set(byte.class, Byte.class);
    primitiveTypeMapping.set(short.class, Short.class);
    primitiveTypeMapping.set(float.class, Float.class);
  }

  public static void tryRemoveTypeMapElement(IList<XElement> lst) {
    lst.remove(q -> q.getName().equals(TypeMappingManager.TYPE_MAP_DEFINITION_ELEMENT_NAME));
  }

  public static boolean isMap(Class realType) {
    return Map.class.isAssignableFrom(realType) || IMap.class.isAssignableFrom(realType);
  }

  public static boolean isIterable(Class type) {
    return Iterable.class.isAssignableFrom(type);
  }

  public static void applyTypeMappingShortening(XElement element) {
    IMap<String, Integer> tmp = new EMap<>();
    evalElementTypeMap(element, tmp);
    IList<Map.Entry<String, Integer>> lst = new EList<>(tmp.getEntries());
    lst.sort(q -> -q.getValue());

    IMap<String, String> map = createTypeMapping(lst);
    shortenTypeNamesByMapping(element, map);

    XElement ret = new XElement(TYPE_MAP_DEFINITION_ELEMENT_NAME);
    for (String key : map.getKeys()) {
      XElement itm = new XElement(TYPE_MAP_DEFINITION_ITEM_ELEMENT_NAME);
      itm.setAttribute(TYPE_MAP_DEFINITION_FULL_ATTRIBUTE_NAME, key);
      itm.setAttribute(TYPE_MAP_DEFINITION_KEY_ATTRIBUTE_NAME, map.get(key));
      ret.addElement(itm);
    }
    element.addElement(ret);
  }

  public static void applyTypeMappingExpansion(XElement element) {
    XElement elm = element.getChildren().tryGetFirst(q -> q.getName().equals(TYPE_MAP_DEFINITION_ELEMENT_NAME));
    if (elm == null) return;

    IMap<String, String> map = new EMap<>();
    for (XElement item : elm.getChildren()) {
      String key = item.getAttributes().get(TYPE_MAP_DEFINITION_KEY_ATTRIBUTE_NAME);
      String value = item.getAttributes().get(TYPE_MAP_DEFINITION_FULL_ATTRIBUTE_NAME);
      map.set(key, value);
    }

    expandTypeMapping(element, map);

  }

  public static Class tryGetCustomTypeByElement(XElement el) {
    Class ret = null;
    String tmp = el.getAttributes().tryGet(TYPE_MAP_OF_ATTRIBUTE_NAME);
    if (tmp != null) {
      try {
        ret = loadClass(tmp);
      } catch (Exception ex) {
        throw new XmlSerializationException(
            sf("Failed to load class for element class defined in %s.", Shared.getElementInfoString(el)), ex);
      }
    }

    return ret;
  }

  public static boolean isInnerInstanceClass(Class type) {
    boolean ret = type.getEnclosingClass() != null && !Modifier.isStatic(type.getModifiers());
    return ret;
  }

  public static Class tryGetItemTypeByElement(XElement el) {
    Class ret = tryExtractTypeFromAttribute(el, TypeMappingManager.TYPE_MAP_ITEM_OF_ATTRIBUTE_NAME);
    return ret;
  }

  public static Class tryGetKeyItemTypeByElement(XElement el) {
    Class ret = tryExtractTypeFromAttribute(el, TypeMappingManager.TYPE_MAP_KEY_OF_ATTRIBUTE_NAME);
    return ret;
  }

  public static Class tryGetValueItemTypeByElement(XElement el) {
    Class ret = tryExtractTypeFromAttribute(el, TypeMappingManager.TYPE_MAP_VALUE_OF_ATTRIBUTE_NAME);
    return ret;
  }

  public static void addClassTypeAttribute(XElement element, Class realType, Class declaredType) {
    if (realType == null || declaredType == null || isTypeEquality(realType, declaredType) == false) {
      element.setAttribute(TYPE_MAP_OF_ATTRIBUTE_NAME, realType.getName());
    }
  }

  public static void addItemTypeAttribute(XElement element, Class<?> realType) {
    element.setAttribute(TypeMappingManager.TYPE_MAP_ITEM_OF_ATTRIBUTE_NAME, realType.getName());
  }

  public static void addKeyTypeAttribute(XElement element, Class<?> realType) {
    element.setAttribute(TypeMappingManager.TYPE_MAP_KEY_OF_ATTRIBUTE_NAME, realType.getName());
  }

  public static void addValueTypeAttribute(XElement element, Class<?> realType) {
    element.setAttribute(TypeMappingManager.TYPE_MAP_VALUE_OF_ATTRIBUTE_NAME, realType.getName());
  }

  public static boolean isSimpleTypeOrEnum(Class c) {
    return simpleTypes.contains(c) || c.isEnum();
  }

  private static Class tryExtractTypeFromAttribute(XElement el, String attributeName) {
    Class ret;
    String tmp = el.getAttributes().tryGet(attributeName);
    if (tmp != null) {
      try {
        ret = loadClass(tmp);
      } catch (Exception ex) {
        throw new XmlSerializationException(sf(
            "Failed to load class for instance defined in %s.", Shared.getElementInfoString(el)), ex);
      }
    } else
      ret = null;

    return ret;
  }

  private static boolean isTypeEquality(Class realType, Class declaredType) {
    boolean ret;
    if (declaredType.isPrimitive()) {
      ret = primitiveTypeMapping.get(declaredType).equals(realType);
    } else
      ret = realType.equals(declaredType);
    return ret;
  }

  private static void shortenTypeNamesByMapping(XElement element, IMap<String, String> map) {
    shortenAttributeIfExists(element, TYPE_MAP_OF_ATTRIBUTE_NAME, map);
    shortenAttributeIfExists(element, TYPE_MAP_ITEM_OF_ATTRIBUTE_NAME, map);
    shortenAttributeIfExists(element, TYPE_MAP_KEY_OF_ATTRIBUTE_NAME, map);
    shortenAttributeIfExists(element, TYPE_MAP_VALUE_OF_ATTRIBUTE_NAME, map);

    element.getChildren().forEach(q -> shortenTypeNamesByMapping(q, map));
  }

  private static void shortenAttributeIfExists(XElement element, String attributeName, IMap<String, String> map) {
    String val = element.getAttributes().tryGet(attributeName);
    if (val != null && map.containsKey(val)) {
      val = map.get(val);
      element.setAttribute(attributeName, val);
    }
  }

  private static void expandTypeMapping(XElement element, IMap<String, String> map) {
    expandAttributeIfExists(element, TYPE_MAP_OF_ATTRIBUTE_NAME, map);
    expandAttributeIfExists(element, TYPE_MAP_ITEM_OF_ATTRIBUTE_NAME, map);
    expandAttributeIfExists(element, TYPE_MAP_KEY_OF_ATTRIBUTE_NAME, map);
    expandAttributeIfExists(element, TYPE_MAP_VALUE_OF_ATTRIBUTE_NAME, map);

    element.getChildren().forEach(q -> expandTypeMapping(q, map));
  }

  private static void expandAttributeIfExists(XElement element, String attributeName, IMap<String, String> map) {
    String val = element.getAttributes().tryGet(attributeName);
    if (val != null && map.containsKey(val)) {
      val = map.get(val);
      element.setAttribute(attributeName, val);
    }
  }

  private static IMap<String, String> createTypeMapping(IList<Map.Entry<String, Integer>> lst) {
    IMap<String, String> ret = new EMap<>();
    for (Map.Entry<String, Integer> entry : lst) {
      String s = entry.getKey();
      int lastDotIndex = s.lastIndexOf('.');
      if (lastDotIndex >= 0) {
        String key = s.substring(lastDotIndex + 1);
        String value = s;
        ret.set(value, key);
      }
    }
    return ret;
  }

  private static void evalElementTypeMap(XElement element, IMap<String, Integer> map) {

    String tmp;
    tmp = element.getAttributes().tryGet(TYPE_MAP_OF_ATTRIBUTE_NAME);
    if (tmp != null) setOrInc(map, tmp);
    tmp = element.getAttributes().tryGet(TYPE_MAP_ITEM_OF_ATTRIBUTE_NAME);
    if (tmp != null) setOrInc(map, tmp);
    tmp = element.getAttributes().tryGet(TYPE_MAP_KEY_OF_ATTRIBUTE_NAME);
    if (tmp != null) setOrInc(map, tmp);
    tmp = element.getAttributes().tryGet(TYPE_MAP_VALUE_OF_ATTRIBUTE_NAME);
    if (tmp != null) setOrInc(map, tmp);

    element.getChildren().forEach(q -> evalElementTypeMap(q, map));
  }

  private static void setOrInc(IMap<String, Integer> map, String key) {
    if (map.containsKey(key)) {
      int val = map.get(key);
      map.set(key, val + 1);
    } else {
      map.set(key, 1);
    }
  }

  private static Class loadClass(String className) {
    Class ret;

    try {
      ret = Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new XmlSerializationException(sf("Failed to load class '%s' from JVM.", className), e);
    }
    return ret;
  }

  private TypeMappingManager() {
  }
}
