package eng.eSystem.xmlSerialization.annotations.both.customParsers;

import eng.eSystem.eXml.XDocument;
import eng.eSystem.eXml.XElement;
import eng.eSystem.xmlSerialization.XmlSerializer;
import eng.eSystem.xmlSerialization.supports.IElementParser;
import eng.eSystem.xmlSerialization.supports.IValueParser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FieldCustomParsersTester {
  @Test
  public void test(){
   RunwayA a = new RunwayA();
   a.name = "23";
   a.altitude = 23000;

    XmlSerializer ser = new XmlSerializer();
    XDocument doc = new XDocument(new XElement("root"));

    ser.serialize(doc,a );
//    System.out.println(doc.getRoot().toFullString());

    RunwayA b = ser.deserialize(doc, RunwayA.class);

    assertEquals(a.name, b.name);
    assertEquals(a.altitude, b.altitude);
  }

  @Test
  public void testB(){
    RunwayB a = new RunwayB();
    a.name = "23";
    a.altitude = 23000;

    XmlSerializer ser = new XmlSerializer();
    XDocument doc = new XDocument(new XElement("root"));

    ser.serialize(doc,a );
//    System.out.println(doc.getRoot().toFullString());

    RunwayB b = ser.deserialize(doc, RunwayB.class);

    assertEquals(a.name, b.name);
    assertEquals(a.altitude, b.altitude);
  }

}

class RunwayA {
  public String name;
  public int altitude;
}

class RunwayB {
  public String name;
  public int altitude;
}

class AltitudeElementParser implements IElementParser<Integer> {

  @Override
  public Integer parse(XElement element, XmlSerializer.Deserializer source) {
    String tmp = element.getContent().substring(2);
    int ret = Integer.parseInt(tmp);
    ret = ret * 100;
    return ret;
  }

  @Override
  public void format(Integer value, XElement element, XmlSerializer.Serializer source) {
    int tmp = value;
    tmp = tmp / 100;
    String ret = "FL" + Integer.toString(tmp);
    element.setContent(ret);
  }

}

class AltitudeValueParser implements IValueParser<Integer>{

  @Override
  public Integer parse(String value) {
    String tmp = value.substring(2);
    int ret = Integer.parseInt(tmp);
    ret = ret * 100;
    return ret;
  }

  @Override
  public String format(Integer value) {
    int tmp = value;
    tmp = tmp / 100;
    String ret = "FL" + Integer.toString(tmp);
    return ret;
  }

}


