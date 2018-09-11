package eng.eSystem.xmlSerialization.annotations.deserialize.objectFieldInheritance;

import eng.eSystem.eXml.XElement;
import eng.eSystem.xmlSerialization.XmlSerializer;
import eng.eSystem.xmlSerialization.annotations.XmlAttribute;
import eng.eSystem.xmlSerialization.annotations.XmlElement;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FieldInheritanceTester {
  @Test
  public void testInt(){
    XElement root = new XElement("root");
    root.addElement(new XElement("name", "John"));
    root.addElement(new XElement("intAge", "10"));

    XmlSerializer ser = new XmlSerializer();
    Person p = ser.deserialize(root, Person.class);

    assertNotNull(p);
    assertEquals("John", p.name);
    assertTrue(p.age instanceof Integer);
    assertEquals(10, p.age);
  }

  @Test
  public void testIntAttrib(){
    XElement root = new XElement("root");
    root.setAttribute("name", "John");
    root.setAttribute("intAge", "10");

    XmlSerializer ser = new XmlSerializer();
    PersonAttrib p = ser.deserialize(root, PersonAttrib.class);

    assertNotNull(p);
    assertEquals("John", p.name);
    assertTrue(p.age instanceof Integer);
    assertEquals(10, p.age);
  }


  @Test
  public void testDbl(){
    XElement root = new XElement("root");
    root.addElement(new XElement("name", "John"));
    root.addElement(new XElement("dblAge", "10"));

    XmlSerializer ser = new XmlSerializer();
    Person p = ser.deserialize(root, Person.class);

    assertNotNull(p);
    assertEquals("John", p.name);
    assertTrue(p.age instanceof Double);
    assertEquals(10.0, (double) p.age, 0);
  }
}

class Person {
  public String name;
  @XmlElement(elementName = "intAge", type = Integer.class)
  @XmlElement(elementName = "dblAge", type = Double.class)
  public Number age;
}

class PersonAttrib {
  public String name;
  @XmlAttribute(attributeName= "intAge", type = Integer.class)
  @XmlAttribute(attributeName= "dblAge", type = Double.class)
  public Number age;
}
