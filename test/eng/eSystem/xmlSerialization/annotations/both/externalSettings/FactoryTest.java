package eng.esystem.xmlSerialization.annotations.both.externalSettings;

import eng.eSystem.eXml.XElement;
import eng.eSystem.xmlSerialization.XmlSettings;
import eng.eSystem.xmlSerialization.XmlSerializer;
import eng.eSystem.xmlSerialization.supports.IFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FactoryTest {

  @Test
  public void factoryTest(){
    Person p;

    XElement root = new XElement("root");
    root.setAttribute("name", "John Doe");

    XmlSettings sett = new XmlSettings();
    sett.getMeta().registerFactory(new PersonFactory());
    XmlSerializer ser = new XmlSerializer(sett);

    p = ser.deserialize(root, Person.class);

    assertNotNull(p);
    assertTrue(p instanceof Employee);
    assertEquals("John Doe", p.name);
  }

}

abstract class Person{
  String name;



}

class Employee extends Person{

}

class PersonFactory implements IFactory<Person>{

  @Override
  public Class<? extends Person> getType() {
    return Person.class;
  }

  @Override
  public Person createInstance() {
    return new Employee();
  }
}
