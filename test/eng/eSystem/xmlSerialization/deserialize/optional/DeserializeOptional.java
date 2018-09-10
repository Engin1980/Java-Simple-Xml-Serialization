package eng.eSystem.xmlSerialization.deserialize.optional;

import eng.eSystem.eXml.XElement;
import eng.eSystem.xmlSerialization.XmlSerializer;
import eng.eSystem.xmlSerialization.annotations.XmlIgnore;
import eng.eSystem.xmlSerialization.annotations.XmlOptional;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DeserializeOptional {
  @Test
  public void testOptional(){
    XElement root = new XElement("root");
    root.setAttribute("mandatory", "1");
    root.setAttribute("optional", "1");
    root.setAttribute("ignore", "1");

    XmlSerializer ser = new XmlSerializer();

    Data d = ser.deserialize(root, Data.class);

    assertEquals(1, d.mandatory);
    assertEquals(1, d.optional);
    assertEquals(0, d.ignore);
  }

  @Test
  public void testOptionalMissing(){
    XElement root = new XElement("root");
    root.setAttribute("mandatory", "1");
    root.setAttribute("ignore", "1");

    XmlSerializer ser = new XmlSerializer();

    Data d = ser.deserialize(root, Data.class);

    assertEquals(1, d.mandatory);
    assertEquals(0, d.optional);
    assertEquals(0, d.ignore);
  }
}

class Data{
  int mandatory = 0;
  @XmlOptional
  int optional = 0;
  @XmlIgnore
  int ignore = 0;
}
