package eng.eSystem.eXmlSerialization;

import eng.eSystem.eXmlSerialization.common.parsers.AwtFontElementParser;
import eng.eSystem.eXmlSerialization.common.parsers.HexToAwtColorValueParser;
import eng.eSystem.eXmlSerialization.model.*;
import eng.eSystem.eXmlSerialization.common.instanceCreators.AwtColorCreator;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DeserializaitonTest {

  private String PERSON_FILE_NAME = "C:\\Users\\Marek Vajgl\\Documents\\IdeaProjects\\Java-Simple-Xml-Serialization\\res\\test.xml";
  private String COLOR_FILE_NAME = "C:\\Users\\Marek Vajgl\\Documents\\IdeaProjects\\Java-Simple-Xml-Serialization\\res\\colorTest.xml";
  private String MULTILIST_FILE_NAME = "C:\\Users\\Marek Vajgl\\Documents\\IdeaProjects\\Java-Simple-Xml-Serialization\\res\\multiList.xml";

  @Test
  public void fillObjectSimple() {

    Settings settings = new Settings();
    settings.getIgnoredFieldsRegex().add("phone.*");
    XmlSerializer ser = new XmlSerializer(settings);

    Person p = new Person();
    ser.fillObject(PERSON_FILE_NAME, p);

    assertEquals("Michal", p.getName());
    assertEquals("Volny", p.surname);

    assertNull(p.getBackupAddress());
    assertNotNull(p.getPrivateIdA());
    assertNull(p.getPrivateIdB());
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
  public void fillListWithCustomCreatorAndCustomValueParser(){
    Settings settings = new Settings();

    settings.getIgnoredFieldsRegex().add("^font$");

    settings.getInstanceCreators().add(
        new AwtColorCreator()
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
  public void fillListWithCustomElementParser(){
    Settings settings = new Settings();

    settings.getInstanceCreators().add(
        new AwtColorCreator()
    );

    settings.getListItemMapping().add(
        new XmlListItemMapping("colors", NamedColor.class)
    );

    settings.getValueParsers().add(
        new HexToAwtColorValueParser()
    );

    settings.getElementParsers().add(
      new AwtFontElementParser()
    );


    XmlSerializer ser = new XmlSerializer(settings);

    List<NamedColor> namedColors = new ArrayList();
    ser.fillList(COLOR_FILE_NAME, namedColors);

    assertEquals(3, namedColors.size());
    assertNotNull(namedColors.get(2).getFont());
    assertEquals("Verdana", namedColors.get(2).getFont().getName());
  }

  @Test
  public void multiList(){
    Settings settings = new Settings();

    settings.getListItemMapping().add(
        new XmlListItemMapping("aL.+", Integer.class)
    );
    settings.getListItemMapping().add(
        new XmlListItemMapping("bL.+", String.class)
    );

    XmlSerializer ser = new XmlSerializer(settings);

    MultiList o = new MultiList();
    ser.fillObject(MULTILIST_FILE_NAME, o);

    assertFalse(o.getaList().isEmpty());
    assertFalse(o.getbList().isEmpty());
  }


  @Test
  public void fillList() {
  }
}