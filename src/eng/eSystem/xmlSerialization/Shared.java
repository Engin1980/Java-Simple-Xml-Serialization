package eng.eSystem.xmlSerialization;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Shared {

  public enum eLogType {
    info,
    warning,
    error
  }

  public static final String TYPE_MAP_ITEM_ELEMENT_NAME = "item";
  public static final String TYPE_MAP_FULL_ATTRIBUTE_NAME = "class";
  public static final String TYPE_MAP_KEY_ATTRIBUTE_NAME = "key";
  public static final String TYPE_MAP_OF_ATTRIBUTE_NAME = "__class";
  public static final String TYPE_MAP_ITEM_OF_ATTRIBUTE_NAME = "__itemClass";
  public static final String TYPE_MAP_KEY_OF_ATTRIBUTE_NAME = "__keyClass";
  public static final String TYPE_MAP_VALUE_OF_ATTRIBUTE_NAME = "__valueClass";
  public static final String TYPE_MAP_ELEMENT_NAME = "__typeMap";
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

  public static String getElementXPath(Element el) {
    String ret = getElementInfoText(el, false, false);
    return ret;
  }

  public static String getElementInfoString(Element el) {
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

  private static String getElementInfoText(Element el, boolean appendAttributes, boolean addBrackets) {
    StringBuilder sb = new StringBuilder();

    if (el == null)
      return "null";

    if (addBrackets)
      sb.append("<");
    sb.append(el.getTagName());
    if (appendAttributes) {
      sb.append(" ");
      NamedNodeMap nnm = el.getAttributes();
      for (int i = 0; i < nnm.getLength(); i++) {
        Node n = nnm.item(i);
        sb.append(String.format("%s=\"%s\" ", n.getNodeName(), n.getNodeValue()));
      }
    }
    if (sb.charAt(sb.length() - 1) == ' ')
      sb.deleteCharAt(sb.length() - 1);
    if (addBrackets)
      sb.append(">");

    Node n = el.getParentNode();
    while (n != null && n.getNodeType() != Node.DOCUMENT_NODE) {
      sb.insert(0, "/");
      String tmp;
      if (addBrackets)
        tmp = "<" + n.getNodeName() + ">";
      else
        tmp = n.getNodeName();
      sb.insert(0, tmp);
      n = n.getParentNode();
    }

    return sb.toString();
  }

}
