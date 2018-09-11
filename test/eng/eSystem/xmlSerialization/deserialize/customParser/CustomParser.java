package eng.eSystem.xmlSerialization.deserialize.customParser;

import eng.eSystem.collections.EList;
import eng.eSystem.collections.IList;
import eng.eSystem.eXml.XElement;
import eng.eSystem.xmlSerialization.Log;
import eng.eSystem.xmlSerialization.XmlSerializer;
import eng.eSystem.xmlSerialization.XmlSettings;
import eng.eSystem.xmlSerialization.annotations.XmlAttribute;
import eng.eSystem.xmlSerialization.supports.IValueParser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CustomParser {
  @Test
  public void testParserGlobal() {
    XElement root = new XElement("root");
    root.setAttribute("altitude", "FL990");

    XmlSettings settings = new XmlSettings();
    settings.getMeta().registerCustomParser(int.class, new AltitudeValueParser());
    XmlSerializer serializer = new XmlSerializer(settings);

    Data d = serializer.deserialize(root, Data.class);

    assertEquals((int) 99000, (int) d.altitude);
  }

  @Test
  public void testParserLocal() {
    XElement root = new XElement("root");
    root.setAttribute("altitude", "FL990");

    XmlSettings settings = new XmlSettings();
    settings.getMeta().registerCustomParser(int.class, new AltitudeValueParser());
    XmlSerializer serializer = new XmlSerializer(settings);

    Dudu d = serializer.deserialize(root, Dudu.class);

    assertEquals((int) 99000, (int) d.altitude);
  }

  @Test
  public void testParserInArrayGlobal() {
    XElement root = new XElement("root");
    XElement elm;

    elm = new XElement("item");
    elm.setAttribute("altitude", "FL990");
    root.addElement(elm);

    elm = new XElement("item");
    elm.setAttribute("altitude", "FL990");
    root.addElement(elm);

    XmlSettings settings = new XmlSettings();
    settings.getMeta().registerCustomParser(int.class, new AltitudeValueParser());
    XmlSerializer serializer = new XmlSerializer(settings);

    Data[] d = serializer.deserialize(root, Data[].class);

    assertEquals(2, d.length);
    assertEquals((int) 99000, (int) d[0].altitude);
  }

  @Test
  public void testParserInArrayLocal() {
    XElement root = new XElement("root");
    XElement elm;

    elm = new XElement("item");
    elm.setAttribute("altitude", "FL990");
    root.addElement(elm);

    elm = new XElement("item");
    elm.setAttribute("altitude", "FL990");
    root.addElement(elm);

    XmlSettings settings = new XmlSettings();
    settings.getMeta().registerXmlItemElement(EList.class, "item", Dudu.class, false, null);
    settings.getMeta().registerCustomParser(int.class, new AltitudeValueParser());
    XmlSerializer serializer = new XmlSerializer(settings);

    IList<Dudu> d = serializer.deserialize(root, EList.class);

    assertEquals(2, d.size());
    assertEquals((int) 99000, (int) d.get(0).altitude);
  }

  @Test
  public void testParserGlobalAsElement() {
    XElement root = new XElement("root");
    XElement tmp = new XElement("altitude", "FL990");
    root.addElement(tmp);

    XmlSettings settings = new XmlSettings();
    settings.getMeta().registerCustomParser(int.class, new AltitudeValueParser());
    XmlSerializer serializer = new XmlSerializer(settings);

    Data d = serializer.deserialize(root, Data.class);

    assertEquals(99000, d.altitude);
  }

}

class Data {
  public int altitude;
}

class Dudu {
  @XmlAttribute(parser = AltitudeValueParser.class)
  public int altitude;
}


class AltitudeValueParser implements IValueParser<Integer> {
  @Override
  public Integer parse(String s) {
    int ret;
    if (s.startsWith("FL")) {
      s = s.substring(2);
      ret = Integer.parseInt(s) * 100;
    } else {
      ret = Integer.parseInt(s);
    }
    return ret;
  }

  @Override
  public String format(Integer value) {
    String ret;
    if (value <= 10000) {
      ret = Integer.toString(value);
    } else {
      ret = "FL" + (int) (Math.ceil(value / 100d));
    }
    return ret;
  }
}