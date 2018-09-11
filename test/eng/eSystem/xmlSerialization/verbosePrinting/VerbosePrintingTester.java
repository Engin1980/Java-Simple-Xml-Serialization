package eng.eSystem.xmlSerialization.verbosePrinting;

import eng.eSystem.collections.EList;
import eng.eSystem.collections.IList;
import eng.eSystem.eXml.XDocument;
import eng.eSystem.eXml.XElement;
import eng.eSystem.xmlSerialization.Log;
import eng.eSystem.xmlSerialization.XmlSerializer;
import eng.eSystem.xmlSerialization.XmlSettings;
import org.junit.Test;

public class VerbosePrintingTester {

  @Test
  public void test() {
    Person p = new Person();

    p.name = "John Doe";
    p.birthDate = java.time.LocalDate.of(2019, 11, 12);
    p.children = new EList<>();

    Person a = new Person();
    a.name = "Jane Child Doe";
    a.birthDate = java.time.LocalDate.of(2018, 11, 23);
    p.children.add(a);

    a = new Person();
    a.name = "John Child Doe";
    a.birthDate = java.time.LocalDate.of(2017, 11, 10);
    p.children.add(a);

    XDocument doc = new XDocument(new XElement("root"));
    Person q;

    {
      XmlSettings sett = new XmlSettings();
      sett.setLogLevel(Log.LogLevel.warning);
      XmlSerializer ser = new XmlSerializer(sett);
      ser.serialize(doc, p);
    }

    System.out.println(" * * * * * *  d e s e  * * * * * * ");

    {
      XmlSettings sett = new XmlSettings();
      sett.setLogLevel(Log.LogLevel.warning);
      XmlSerializer ser = new XmlSerializer(sett);

      q = ser.deserialize(doc, Person.class);
    }
  }
}

class Person {
  String name;
  java.time.LocalDate birthDate;
  IList<Person> children;

  @Override
  public String toString() {
    return "Person{" +
        "name='" + name + '\'' +
        '}';
  }
}
