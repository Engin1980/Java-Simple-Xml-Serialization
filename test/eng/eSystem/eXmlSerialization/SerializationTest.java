package eng.eSystem.eXmlSerialization;

import eng.eSystem.eXmlSerialization.model.Address;
import eng.eSystem.eXmlSerialization.model.Person;
import eng.eSystem.eXmlSerialization.model.Phone;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SerializationTest {

  @Test
  @Ignore
  public void EmptyTest(){

    XmlSerializer ser = new XmlSerializer();

    Object o = new Object();
    ser.saveObject("R:\\testEmpty.xml", o);

  }

  @Test
  public void PersonTest(){

    XmlSerializer ser = new XmlSerializer();

    Person p = new Person();
    p.setName("John");
    p.surname = "Doe";

    Address a = new        Address();
    a.setHouseNumber(1576);
    a.setStreet("Nejasna");
    a.setUsed(true);

    p.setAddress(a);

    List<Phone> lst = new ArrayList();
    p.setPhones(lst);

    Phone h;

    h = new Phone();
    h.number="123";
    lst.add(h);

    h = new Phone();
    h.number="456";
    lst.add(h);

    h = new Phone();
    h.number="789";
    lst.add(h);

    ser.saveObject("R:\\testPerson.xml", p);

  }
}
