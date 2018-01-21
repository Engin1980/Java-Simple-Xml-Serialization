package eng.EXmlSerialization;

import eng.EXmlSerialization.model.Address;
import eng.EXmlSerialization.model.Person;
import org.junit.Test;

import static org.junit.Assert.*;

public class XmlSerializerTest {

  private String PERSON_FILE_NAME = "C:\\Users\\Marek Vajgl\\Documents\\IdeaProjects\\Java-Simple-Xml-Serialization\\res\\test.xml";

  @Test
  public void fillObjectSimple() {

    XmlSerializer ser = new XmlSerializer();

    Person p = new Person();
    ser.fillObject(PERSON_FILE_NAME, p);

    assertEquals("Michal", p.getName());
    assertEquals("Volny", p.surname);
  }

  @Test
  public void fillObjectWithAddress() {

    XmlSerializer ser = new XmlSerializer();

    Person p = new Person();
    ser.fillObject(PERSON_FILE_NAME, p);

    Address act = p.getAddress();
    assertEquals("Novicka", act.getStreet());
    assertEquals(58, act.getHouseNumber());
    assertEquals(true, act.isUsed());

  }

  @Test
  public void fillObjectIgnoreAddress() {

    Settings settings = new Settings();
    settings.setVerbose(true);
    settings.getIgnoredFieldsRegex().add("addr");
    XmlSerializer ser = new XmlSerializer(settings);

    Person p = new Person();
    ser.fillObject(PERSON_FILE_NAME, p);

    assertNull(p.getAddress());
  }


  @Test
  public void fillList() {
  }
}