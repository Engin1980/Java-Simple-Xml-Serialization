package eng.eSystem.xmlSerialization.annotations.both.primitive;

import eng.eSystem.eXml.XDocument;
import eng.eSystem.eXml.XElement;
import eng.eSystem.xmlSerialization.XmlSerializer;
import org.junit.Test;
import static org.junit.Assert.*;

public class TesterE {

  @Test
  public void test(){
    int a = 8;

    XmlSerializer ser = new XmlSerializer();

    XDocument doc = new XDocument(new XElement("root"));

    ser.serialize(doc, a);

//    System.out.println(doc.getRoot().toFullString());

    int b = ser.deserialize(doc, Integer.class);

    assertEquals(a,b);
  }

}
