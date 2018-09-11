package eng.eSystem.xmlSerialization.annotations.deserialize.objectSingle;

import eng.eSystem.eXml.XElement;
import eng.eSystem.xmlSerialization.XmlSerializer;
import org.junit.Test;

import static org.junit.Assert.*;

public class TesterC {

  @Test
  public void testAsElements(){
    XElement root = new XElement("root");
    XElement elm;

    elm = new XElement("name", "John");
    root.addElement(elm);
    elm = new XElement("age", "11");
    root.addElement(elm);

    XmlSerializer ser = new XmlSerializer();
    Person p = ser.deserialize(root, Person.class);

    assertNotNull(p);
    assertEquals("John", p.name);
    assertEquals(11, p.age);
  }

  @Test
  public void testAsAttributes(){
    XElement root = new XElement("root");
    XElement elm;

    root.setAttribute("name", "John");
    root.setAttribute("age", "11");

    XmlSerializer ser = new XmlSerializer();
    Person p = ser.deserialize(root, Person.class);

    assertNotNull(p);
    assertEquals("John", p.name);
    assertEquals(11, p.age);
  }
}

class Person {
  public String name;
  public int age;
}
