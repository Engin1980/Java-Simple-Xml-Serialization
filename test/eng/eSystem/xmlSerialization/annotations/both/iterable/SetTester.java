package eng.eSystem.xmlSerialization.annotations.both.iterable;

import eng.eSystem.eXml.XDocument;
import eng.eSystem.eXml.XElement;
import eng.eSystem.xmlSerialization.XmlSerializer;
import org.junit.Test;

import java.util.*;

public class SetTester {

  @Test
  public void HashSetDirectly() {
    Set<String> a = new HashSet<>();
    a.add("a");
    a.add("b");
    a.add("d");

    XDocument doc = new XDocument(new XElement("root"));
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(doc, a);

//    System.out.println(doc.getRoot().toFullString());

    Set<String> b = ser.deserialize(doc, HashSet.class);

    Shared.assertSetEquals(a, b);
  }

  @Test
  public void SetInObject() {
    DataSetSet a = new DataSetSet();
    a.numbers.add((Number)3);
    a.numbers.add((Number)4);
    a.numbers.add((Number)5);
    a.numbers.add((Number)6);

    XDocument doc = new XDocument(new XElement("root"));
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(doc, a);

//    System.out.println(doc.getRoot().toFullString());

    DataSetSet b = ser.deserialize(doc, DataSetSet.class);


    Shared.assertSetEquals(a.numbers, b.numbers);
  }

}

class DataSetSet {
  public Set<Number> numbers = new TreeSet<>();
}
