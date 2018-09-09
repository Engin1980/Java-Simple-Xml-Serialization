package eng.eSystem.xmlSerialization;

import eng.eSystem.eXml.XElement;

class Shared {
  public static String getElementInfoString(XElement el) {
    String ret = getElementInfoText(el, true, true);
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
}
