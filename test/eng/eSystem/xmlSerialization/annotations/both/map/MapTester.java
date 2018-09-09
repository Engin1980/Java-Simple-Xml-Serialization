package eng.esystem.xmlSerialization.annotations.both.map;

import eng.eSystem.eXml.XDocument;
import eng.eSystem.eXml.XElement;
import eng.eSystem.xmlSerialization.XmlSerializer;
import eng.esystem.xmlSerialization.annotations.both.iterable.Shared;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class MapTester {
  @Test
  public void mapDirectly(){
    Map<String, Integer> a = new HashMap<>();
    a.put("one", 1);
    a.put("two", 2);
    a.put("nothing", null);
    a.put("three", 3);

    XmlSerializer ser = new XmlSerializer();
    XDocument doc = new XDocument(new XElement("root"));

    ser.serialize(doc, a);

//    System.out.println(doc.getRoot().toFullString());

    Map<String, Integer> b = ser.deserialize(doc, Map.class);

    Shared.assertMapEquals(a, b);
  }

  @Test
  public void mapDirectlyComplex(){
    Map<String, Number> a = new HashMap<>();
    a.put("one", 1);
    a.put("two", 2);
    a.put("nothing", null);
    a.put("three", 3);

    XmlSerializer ser = new XmlSerializer();
    XDocument doc = new XDocument(new XElement("root"));

    ser.serialize(doc, a);

//    System.out.println(doc.getRoot().toFullString());

    Map<String, Number> b = ser.deserialize(doc, Map.class);

    Shared.assertMapEquals(a, b);
  }
}
