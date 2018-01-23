package eng.EXmlSerialization;

import eng.EXmlSerialization.common.parsers.JavaTimeLocalDateValueParser;
import eng.EXmlSerialization.model2.Database;
import eng.EXmlSerialization.model2.HomePhone;
import eng.EXmlSerialization.model2.MobilePhone;
import eng.EXmlSerialization.model2.Person;
import org.junit.Test;
import static org.junit.Assert.*;

public class Model2Test {

  @Test
  public void model2Test(){

    // input file name
    String FILE_NAME = "C:\\Users\\Marek Vajgl\\Documents\\IdeaProjects\\Java-Simple-Xml-Serialization\\res\\model2.xml";

    // settings intialization
    Settings settings = new Settings();

    // defines list mapping for any elements in <database>.<persons>.
    // into database.persons list field.
    settings.getListItemMapping().add(
      new XmlListItemMapping("persons", Person.class)
    );

    // defines list mapping for 'home-phone' and 'mobile-phone' in <database>.<persons>.<person>.<phones>
    // into person.phones list field.
    settings.getListItemMapping().add(
        new XmlListItemMapping("phones", "homePhone", HomePhone.class)
    );
    settings.getListItemMapping().add(
        new XmlListItemMapping("phones", "mobilePhone", MobilePhone.class)
    );



    // defines custom parsing of birthDate to LocalDate
    settings.getValueParsers().add(
      new JavaTimeLocalDateValueParser("yyyy-MM-dd")
    );


    // serializer initialization
    XmlSerializer ser = new XmlSerializer(settings);


    // deserialization
    Database db = new Database();
    ser.fillObject(FILE_NAME, db);

    // asserts
    assertEquals(2, db.getPersons().size());
    assertEquals("+420", db.getPersons().get(0).getPhones().get(0).getPrefix());
    assertNull(db.getPersons().get(0).getAddress().getGps());
    assertEquals(49.9891146, db.getPersons().get(1).getAddress().getGps().getLatitude(), 0.0);
  }
}
