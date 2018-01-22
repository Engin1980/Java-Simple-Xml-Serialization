package eng.EXmlSerialization.common.parsers;

import eng.EXmlSerialization.IValueParser;
import eng.EXmlSerialization.XmlSerializationException;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses HEX color value to AWT color.
 */
public class HexToAwtColorValueParser implements IValueParser<java.awt.Color> {
  @Override
  public String getTypeName() {
    return java.awt.Color.class.getName();
  }

  @Override
  public java.awt.Color parse(String value) {
    Color ret = null;
    String ps = "(..)(..)(..)";
    Pattern p = Pattern.compile(ps);

    Matcher m = p.matcher(value);
    if (m.find()) {
      String r = m.group(1);
      String g = m.group(2);
      String b = m.group(3);
      try {
        int ri = Integer.parseInt(r, 16);
        int gi = Integer.parseInt(g, 16);
        int bi = Integer.parseInt(b, 16);
        ret = new Color(ri, gi, bi);
      } finally {
      }
    }
    if (ret == null) {
      throw new XmlSerializationException("Unable to parse \"" + value + "\" into color.");
    }

    return ret;
  }
}
