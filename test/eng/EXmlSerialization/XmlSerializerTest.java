package eng.EXmlSerialization;

import eng.EXmlSerialization.model.Address;
import eng.EXmlSerialization.model.Person;
import eng.EXmlSerialization.model.Phone;
import org.junit.Test;

import static org.junit.Assert.*;

public class XmlSerializerTest {

  private String PERSON_FILE_NAME = "C:\\Users\\Marek Vajgl\\Documents\\IdeaProjects\\Java-Simple-Xml-Serialization\\res\\test.xml";

  @Test
  public void fillObjectSimple() {

    Settings settings = new Settings();
    settings.getIgnoredFieldsRegex().add("phone.*");
    XmlSerializer ser = new XmlSerializer(settings);

    Person p = new Person();
    ser.fillObject(PERSON_FILE_NAME, p);

    assertEquals("Michal", p.getName());
    assertEquals("Volny", p.surname);
  }

  @Test
  public void fillObjectWithAddress() {

    Settings settings = new Settings();
    settings.getIgnoredFieldsRegex().add("phone.*");
    XmlSerializer ser = new XmlSerializer(settings);

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
    settings.getIgnoredFieldsRegex().add("addr");
    settings.getIgnoredFieldsRegex().add("phone.*");
    XmlSerializer ser = new XmlSerializer(settings);

    Person p = new Person();
    ser.fillObject(PERSON_FILE_NAME, p);

    assertNull(p.getAddress());
  }

  @Test
  public void fillObjectWithPhoneList(){
    Settings settings = new Settings();
    settings.getListItemMapping().add(
        new XmlListItemMapping("phones", Phone.class));
    settings.getIgnoredFieldsRegex().add("phoneNumbers");
    XmlSerializer ser = new XmlSerializer(settings);

    Person p = new Person();
    ser.fillObject(PERSON_FILE_NAME, p);

    assertEquals(3,p.getPhones().size());
  }

  @Test
  public void fillObjectWithPhoneNumbersList(){
    Settings settings = new Settings();
    settings.getListItemMapping().add(
        new XmlListItemMapping("phones", Phone.class));
    settings.getListItemMapping().add(
        new XmlListItemMapping("phoneNumbers", String.class));
    XmlSerializer ser = new XmlSerializer(settings);

    Person p = new Person();
    ser.fillObject(PERSON_FILE_NAME, p);

    assertEquals(3,p.getPhones().size());
  }


  @Test
  public void fillList() {
  }
}