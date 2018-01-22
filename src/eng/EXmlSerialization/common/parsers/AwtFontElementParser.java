package eng.EXmlSerialization.common.parsers;

import eng.EXmlSerialization.IElementParser;
import eng.EXmlSerialization.XmlSerializationException;
import org.w3c.dom.Element;

import java.awt.*;

/**
 * Converts element with font data into java.awt.Font instance. Expected attributes are "family" for font-family-name,
 * "style" for magic constant style definition (see {@linkplain java.awt.Font} documentation) and "size".
 * Example:
 * &lt;font family="Courier" style="0" size="12" /&gt;
 */
public class AwtFontElementParser implements IElementParser<java.awt.Font> {

  public final static String ATTR_FAMILY = "family";
  public final static String ATTR_STYLE = "style";
  public final static String ATTR_SIZE = "size";

  @Override
  public String getTypeName() {
    return java.awt.Font.class.getName();
  }

  @Override
  public Font parse(Element element) {
    String familyName = getAttributeValue(element, ATTR_FAMILY);
    String styleS = getAttributeValue(element, ATTR_STYLE);
    String sizeS = getAttributeValue(element, ATTR_SIZE);

    int style = toInt(styleS, ATTR_STYLE);
    int size = toInt(sizeS, ATTR_SIZE);

    Font ret = new Font(familyName, style, size);
    return ret;
  }

  private int toInt(String value, String key) {
    int ret;
    try{
      ret = Integer.parseInt(value);
     } catch (Exception ex){
      throw new XmlSerializationException("Failed to convert attribute " + key + " value " + value + " to {int} in " + this.getClass().getName() + " parsing.");
    }
    return ret;
  }

  private String getAttributeValue(Element el, String key){
    if (el.hasAttribute(key) == false)
      throw new XmlSerializationException("Failed to find required attribute " + key + " for " + this.getClass().getName() + " parsing.");
    String ret = el.getAttribute(key);
    return ret;
  }
}
