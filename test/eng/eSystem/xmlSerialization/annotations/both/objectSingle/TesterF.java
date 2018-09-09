package eng.esystem.xmlSerialization.annotations.both.objectSingle;

import eng.eSystem.eXml.XDocument;
import eng.eSystem.eXml.XElement;
import eng.eSystem.xmlSerialization.XmlSerializer;
import org.junit.Test;

import static org.junit.Assert.*;

public class TesterF {
  @Test
  public void test(){
    Person a = new Person();
    a.name = "John Doe";
    a.age = 10;

    XmlSerializer ser = new XmlSerializer();
    XDocument doc = new XDocument(new XElement("root"));

    ser.serialize(doc,a );
//    System.out.println(doc.getRoot().toFullString());

    Person b = ser.deserialize(doc, Person.class);

    assertEquals(a.name, b.name);
    assertEquals(a.age, b.age);
  }

  @Test
  public void testAsObject(){
    Person a = new Person();
    a.name = "John Doe";
    a.age = 10;

    XmlSerializer ser = new XmlSerializer();
    XDocument doc = new XDocument(new XElement("root"));

    ser.serialize(doc, a);
//    System.out.println(doc.getRoot().toFullString());

    Person b = (Person) ser.deserialize(doc, Object.class);

    assertEquals(a.name, b.name);
    assertEquals(a.age, b.age);
  }
}

class Person {
  public String name;
  public int age;
}
