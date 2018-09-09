package eng.esystem.xmlSerialization.annotations.both.objectAggregation;

import eng.eSystem.eXml.XDocument;
import eng.eSystem.eXml.XElement;
import eng.eSystem.xmlSerialization.XmlSerializer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TesterG {
  @Test
  public void test(){
    Person a = new Person();
    a.name = "John Doe";
    a.age = 10;
    a.address = new Address("Bruntal");

    XmlSerializer ser = new XmlSerializer();
    XDocument doc = new XDocument(new XElement("root"));

    ser.serialize(doc,a );
//    System.out.println(doc.getRoot().toFullString());

    Person b = ser.deserialize(doc, Person.class);

    assertEquals(a.name, b.name);
    assertEquals(a.age, b.age);
    assertNotNull(b.address);
    assertEquals(a.address.getCity(), b.address.getCity());
  }

}

class Person {
  public String name;
  public int age;
  public Address address;
}

class Address{
  private String city;

  public Address(String city) {
    this.city = city;
  }

  public String getCity() {
    return city;
  }
}
