package eng.eSystem.xmlSerialization;

import eng.eSystem.collections.EList;
import eng.eSystem.collections.EMap;
import eng.eSystem.collections.IList;
import eng.eSystem.collections.IMap;
import eng.eSystem.eXml.XElement;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Shared {

  public enum eLogType {
    info,
    warning,
    error
  }

  public static final String TYPE_MAP_DEFINITION_ELEMENT_NAME = "__typeMap";
  public static final String TYPE_MAP_DEFINITION_ITEM_ELEMENT_NAME = "item";
  public static final String TYPE_MAP_DEFINITION_FULL_ATTRIBUTE_NAME = "class";
  public static final String TYPE_MAP_DEFINITION_KEY_ATTRIBUTE_NAME = "key";
  public static final String TYPE_MAP_OF_ATTRIBUTE_NAME = "__class";
  public static final String TYPE_MAP_ITEM_OF_ATTRIBUTE_NAME = "__itemClass";
  public static final String TYPE_MAP_KEY_OF_ATTRIBUTE_NAME = "__keyClass";
  public static final String TYPE_MAP_VALUE_OF_ATTRIBUTE_NAME = "__valueClass";
  private static final String LOG_TEXT_PREFIX = "E-XmlSerialization";

  public static void log(eLogType type, String format, Object... params) {
    String s = String.format(format, params);
    s = String.format("%s - %s: %s", LOG_TEXT_PREFIX, type.toString(), s);
    System.out.println(s);
  }

  public static boolean isRegexMatch(String regex, String text) {
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(text);
    boolean ret = m.find();
    return ret;
  }

  public static String getElementXPath(XElement el) {
    String ret = getElementInfoText(el, false, false);
    return ret;
  }

  public static String getElementInfoString(XElement el) {
    String ret = getElementInfoText(el, true, true);
    return ret;
  }

  public static Field[] getDeclaredFields(Class c) {
    List<Field> lst = new ArrayList<>();
    Field[] fs;

    while (c != null) {
      fs = c.getDeclaredFields();
      for (Field f : fs) {
        lst.add(f);
      }
      c = c.getSuperclass();
    }

    Field[] ret = lst.toArray(new Field[0]);
    return ret;
  }

  public static boolean isSkippedBySettings(Field f, Settings settings) {
    boolean ret = false;

    for (String regex : settings.getIgnoredFieldsRegex()) {
      Pattern p = Pattern.compile(regex);
      if (p.matcher(f.getName()).find()) {
        ret = true;
        break;
      }
    }

    return ret;
  }

  public static IElementParser tryGetCustomElementParser(Class c, Settings settings) {
    IElementParser ret = null;

    for (IElementParser iElementParser : settings.getElementParsers()) {
      if (iElementParser.getType().equals(c)) {
        ret = iElementParser;
        break;
      }
    }

    return ret;
  }

  public static IValueParser tryGetCustomValueParser(Class c, Settings settings) {
    IValueParser ret = null;

    for (IValueParser iValueParser : settings.getValueParsers()) {
      if (iValueParser.getType().equals(c)) {
        ret = iValueParser;
        break;
      }
    }

    return ret;
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

  private static String getElementInfoText(XElement el, boolean appendAttributes, boolean addBrackets) {

    String ret;

    if (el == null)
      ret = "null";
    else {
      if (addBrackets)
        ret = el.toXmlPath(appendAttributes);
      else
        ret = el.toXPath();
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

}