package eng.eSystem.xmlSerialization.annotations.both.object;

import eng.eSystem.eXml.XDocument;
import eng.eSystem.eXml.XElement;
import eng.eSystem.xmlSerialization.XmlSerializer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ObjectInheritanceTester {
  @Test
  public void test(){
   Runway a = new Runway();
   a.name = "23";
   a.approach = new IlsApproach("ILS_III");

    XmlSerializer ser = new XmlSerializer();
    XDocument doc = new XDocument(new XElement("root"));

    ser.serialize(doc,a );
//    System.out.println(doc.getRoot().toFullString());

    Runway b = ser.deserialize(doc, Runway.class);

    assertEquals(a.name, b.name);
    assertTrue(a.approach instanceof IlsApproach);
    assertEquals(((IlsApproach)a.approach).type, ((IlsApproach)b.approach).type);
  }

  @Test
  public void test2(){
    Runway a = new Runway();
    a.name = "23";
    a.approach = new VorApproach(23, VorApproach.eType.vorDme);

    XmlSerializer ser = new XmlSerializer();
    XDocument doc = new XDocument(new XElement("root"));

    ser.serialize(doc,a );
//    System.out.println(doc.getRoot().toFullString());

    Runway b = ser.deserialize(doc, Runway.class);

    assertEquals(a.name, b.name);
    assertTrue(a.approach instanceof VorApproach);
    assertEquals(((VorApproach)a.approach).type, ((VorApproach)b.approach).type);
    assertEquals(((VorApproach)a.approach).course, ((VorApproach)b.approach).course);
  }

}

class Runway {
  public String name;
  public Approach approach;
}

abstract class Approach{
}

class IlsApproach extends Approach{
  public String type;

  public IlsApproach(String type) {
    this.type = type;
  }
}

class VorApproach extends Approach{
  public int course;
  public eType type;
  public enum eType{
    vor,
    vorDme
  }

  public VorApproach(int course, eType type) {
    this.course = course;
    this.type = type;
  }
}
