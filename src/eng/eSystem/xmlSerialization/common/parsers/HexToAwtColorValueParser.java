package eng.eSystem.xmlSerialization.common.parsers;

import eng.eSystem.xmlSerialization.IValueParser;
import eng.eSystem.xmlSerialization.XmlDeserializationException;
import eng.eSystem.xmlSerialization.XmlSerializationException;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses HEX color value to AWT color. Only R-G-B channels, ignores alpha channel.
 * @see java.awt.Color
 * @see IValueParser
 */
public class HexToAwtColorValueParser implements IValueParser<java.awt.Color> {
  @Override
  public Class getType() {
    return java.awt.Color.class;
  }

  @Override
  public java.awt.Color parse(String value) throws XmlDeserializationException {
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
      throw new XmlDeserializationException("Unable to parse \"" + value + "\" into color.");
    }

    return ret;
  }

  @Override
  public String format(Color value) {
    String ret = String.format("#%02X%02X%02X", value.getRed(), value.getGreen(), value.getBlue());
    return ret;
  }
}
