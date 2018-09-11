package eng.eSystem.xmlSerialization.annotations.both.objectCustomParsersGlobal;

import eng.eSystem.eXml.XDocument;
import eng.eSystem.eXml.XElement;
import eng.eSystem.xmlSerialization.XmlSerializer;
import eng.eSystem.xmlSerialization.annotations.XmlAttribute;
import eng.eSystem.xmlSerialization.supports.IValueParser;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class CustomParserTester {

  @Test
  public void test() {
    Person a = new Person("John Doe", new GregorianCalendar());

    XmlSerializer ser = new XmlSerializer();
    XDocument doc = new XDocument(new XElement("root"));

    ser.serialize(doc, a);

//    System.out.println(doc.getRoot().toFullString());

    Person b = ser.deserialize(doc, Person.class);

    org.junit.Assert.assertEquals(a.name, b.name);
    org.junit.Assert.assertEquals(a.birthDate.getTimeInMillis(), b.birthDate.getTimeInMillis());
  }
}

class Person {
  public String name;
  @XmlAttribute(parser = CalendarValueParser.class, subclassesIncluded = true)
  public java.util.Calendar birthDate;

  public Person(String name, Calendar birthDate) {
    this.name = name;
    this.birthDate = birthDate;
  }
}

class CalendarValueParser implements IValueParser<Calendar> {

  @Override
  public Calendar parse(String value) {
    long milis = Long.parseLong(value);
    Calendar ret = Calendar.getInstance();
    ret.setTimeInMillis(milis);
    return ret;
  }

  @Override
  public String format(Calendar value) {
    long milis = value.getTimeInMillis();
    String ret = Long.toString(milis);
    return ret;
  }
}