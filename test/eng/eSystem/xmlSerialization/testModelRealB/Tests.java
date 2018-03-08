package eng.eSystem.xmlSerialization.testModelRealB;

import eng.eSystem.xmlSerialization.Settings;
import eng.eSystem.xmlSerialization.XmlListItemMapping;
import eng.eSystem.xmlSerialization.XmlSerializer;
import eng.eSystem.xmlSerialization.common.parsers.JavaTimeLocalDateValueParser;
import org.junit.Test;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class Tests {
  @Test
  public void testGpsDeserialization() {
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<database>\n" +
        "    <persons>\n" +
        "        <person name=\"John\" surname=\"Doe\" birthDate=\"2018-01-20\">\n" +
        "            <address city=\"Ostrava\" street=\"Stodolni\" houseNumber=\"23/1284\" />\n" +
        "            <phones>\n" +
        "                <homePhone prefix=\"+420\" number=\"597 493 829\" />\n" +
        "                <mobilePhone prefix=\"+420\" number=\"594 892 384\" />\n" +
        "            </phones>\n" +
        "        </person>\n" +
        "        <person>\n" +
        "            <name>Jane</name>\n" +
        "            <surname>Doe</surname>\n" +
        "            <birthDate>2000-12-24</birthDate>\n" +
        "            <address>\n" +
        "                <city>Bruntál</city>\n" +
        "                <street>Školní</street>\n" +
        "                <houseNumber>2</houseNumber>\n" +
        "                <!-- GPS is optional -->\n" +
        "                <gps latitude=\"49.9891146\" longitude=\"17.4568346\" />\n" +
        "            </address>\n" +
        "            <phones>\n" +
        "            </phones>\n" +
        "        </person>\n" +
        "    </persons>\n" +
        "</database>";


    // settings intialization
    Settings settings = new Settings();

    // defines list mapping for any elements in <database>.<persons>.
    // into database.persons list field.
    settings.getListItemMappings().add(
        new XmlListItemMapping("/persons$", Person.class)
    );

    // defines list mapping for 'home-phone' and 'mobile-phone' in <database>.<persons>.<person>.<phones>
    // into person.phones list field.
    settings.getListItemMappings().add(
        new XmlListItemMapping("/phones$", "homePhone", HomePhone.class)
    );
    settings.getListItemMappings().add(
        new XmlListItemMapping("/phones$", "mobilePhone", MobilePhone.class)
    );


    // defines custom parsing of birthDate to LocalDate
    settings.getValueParsers().add(
        new JavaTimeLocalDateValueParser("yyyy-MM-dd")
    );


    // serializer initialization
    XmlSerializer ser = new XmlSerializer(settings);
    ByteArrayInputStream bis = new ByteArrayInputStream(xml.getBytes());
    Database trg = (Database) ser.deserialize(bis, Database.class);

    // asserts
    assertEquals(2, trg.getPersons().size());
    assertEquals("+420", trg.getPersons().get(0).getPhones().get(0).getPrefix());
    assertNull(trg.getPersons().get(0).getAddress().getGps());
    assertEquals(49.9891146, trg.getPersons().get(1).getAddress().getGps().getLatitude(), 0.0);
  }

}
