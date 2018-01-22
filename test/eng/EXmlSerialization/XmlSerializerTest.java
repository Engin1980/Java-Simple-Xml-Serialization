package eng.EXmlSerialization;

import eng.EXmlSerialization.common.parsers.HexToAwtColorValueParser;
import eng.EXmlSerialization.model.Address;
import eng.EXmlSerialization.model.NamedColor;
import eng.EXmlSerialization.model.Person;
import eng.EXmlSerialization.model.Phone;
import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class XmlSerializerTest {

  private String PERSON_FILE_NAME = "C:\\Users\\Marek Vajgl\\Documents\\IdeaProjects\\Java-Simple-Xml-Serialization\\res\\test.xml";
  private String COLOR_FILE_NAME = "C:\\Users\\Marek Vajgl\\Documents\\IdeaProjects\\Java-Simple-Xml-Serialization\\res\\colorTest.xml";

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
  public void valueParser(){
    Settings settings = new Settings();

    settings.getInstanceCreators().add(
        new eng.EXmlSerialization.common.instanceCreators.AwtColorCreator()
    );

    settings.getListItemMapping().add(
        new XmlListItemMapping("colors", NamedColor.class)
    );

    settings.getValueParsers().add(
        new HexToAwtColorValueParser()
    );


    XmlSerializer ser = new XmlSerializer(settings);

    List<NamedColor> namedColors = new ArrayList();
    ser.fillList(COLOR_FILE_NAME, namedColors);
  }


  @Test
  public void fillList() {
  }
}