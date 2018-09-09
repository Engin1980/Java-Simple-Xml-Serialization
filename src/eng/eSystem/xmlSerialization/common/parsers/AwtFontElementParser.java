package eng.eSystem.xmlSerialization.common.parsers;

import eng.eSystem.eXml.XElement;
import eng.eSystem.xmlSerialization.XmlSerializer;
import eng.eSystem.xmlSerialization.exceptions.XmlSerializationException;
import eng.eSystem.xmlSerialization.supports.IElementParser;

import java.awt.*;

/**
 * Converts element with font data into java.awt.Font instance. Expected attributes are "family" for font-family-name,
 * "style" for magic constant style definition (see {@linkplain java.awt.Font} documentation) and "size".
 * Example:
 * &lt;font family="Courier" style="0" size="12" /&gt;
 */
public class AwtFontElementParser implements IElementParser<Font> {

  public final static String ATTR_FAMILY = "family";
  public final static String ATTR_STYLE = "style";
  public final static String ATTR_SIZE = "size";

  @Override
  public Font parse(XElement element, XmlSerializer.Deserializer source) {
    String familyName = element.getAttributes().get(ATTR_FAMILY);
    String styleS = element.getAttributes().get(ATTR_STYLE);
    String sizeS = element.getAttributes().get(ATTR_SIZE);

    int style = toInt(styleS, ATTR_STYLE);
    int size = toInt(sizeS, ATTR_SIZE);

    Font ret = new Font(familyName, style, size);
    return ret;
  }

  @Override
  public void format(Font value, XElement element, XmlSerializer.Serializer source) {
    element.setAttribute(ATTR_FAMILY, value.getName());
    element.setAttribute(ATTR_STYLE, Integer.toString(value.getStyle()));
    element.setAttribute(ATTR_SIZE, Integer.toString(value.getSize()));
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

  private String getAttributeValue(XElement el, String key) {
    String ret = el.tryGetAttribute(key);
    if (ret == null){
      throw new XmlSerializationException("Failed to find required attribute " + key + " for " + this.getClass().getName() + " parsing.");
    }
    return ret;
  }
}
