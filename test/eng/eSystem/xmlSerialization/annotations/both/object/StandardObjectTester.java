package eng.esystem.xmlSerialization.annotations.both.object;

import eng.eSystem.eXml.XDocument;
import eng.eSystem.eXml.XElement;
import eng.eSystem.xmlSerialization.XmlSerializer;
import org.junit.Test;

import static org.junit.Assert.*;

public class StandardObjectTester {
  @Test
  public void testSimple() {
    PersonSimple a = new PersonSimple();
    a.name = "John Doe";
    a.age = 10;

    XmlSerializer ser = new XmlSerializer();
    XDocument doc = new XDocument(new XElement("root"));

    ser.serialize(doc, a);
//    System.out.println(doc.getRoot().toFullString());

    PersonSimple b = ser.deserialize(doc, PersonSimple.class);

    assertEquals(a.name, b.name);
    assertEquals(a.age, b.age);
  }

  @Test
  public void testAsObject() {
    PersonSimple a = new PersonSimple();
    a.name = "John Doe";
    a.age = 10;

    XmlSerializer ser = new XmlSerializer();
    XDocument doc = new XDocument(new XElement("root"));

    ser.serialize(doc, a);

//    System.out.println(doc.getRoot().toFullString());

    PersonSimple b = (PersonSimple) ser.deserialize(doc, Object.class);

    assertEquals(a.name, b.name);
    assertEquals(a.age, b.age);
  }

  @Test
  public void testPersonWithaddress() {
    PersonWithAddress a = new PersonWithAddress();
    a.name = "John Doe";
    a.age = 10;
    a.address = new Address("Bruntal");

    XmlSerializer ser = new XmlSerializer();
    XDocument doc = new XDocument(new XElement("root"));

    ser.serialize(doc, a);
//    System.out.println(doc.getRoot().toFullString());

    PersonWithAddress b = ser.deserialize(doc, PersonWithAddress.class);

    assertEquals(a.name, b.name);
    assertEquals(a.age, b.age);
    assertNotNull(b.address);
    assertEquals(a.address.getCity(), b.address.getCity());
  }
}

class PersonSimple {
  public String name;
  public int age;
}

class PersonWithAddress {
  public String name;
  public int age;
  public Address address;
}

class Address {
  private String city;

  public Address(String city) {
    this.city = city;
  }

  public String getCity() {
    return city;
  }
}

