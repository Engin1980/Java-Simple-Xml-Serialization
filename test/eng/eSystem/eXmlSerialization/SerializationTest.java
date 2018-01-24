package eng.eSystem.eXmlSerialization;

import eng.eSystem.eXmlSerialization.model.Person;
import org.junit.Test;

public class SerializationTest {

  @Test
  public void EmptyTest(){

    XmlSerializer ser = new XmlSerializer();

    Object o = new Object();
    ser.saveObject("R:\\testEmpty.xml", o);

  }

  @Test
  public void PersonTest(){

    XmlSerializer ser = new XmlSerializer();

    Person p = new Person();
    ser.saveObject("R:\\testPerson.xml", p);

  }
}
