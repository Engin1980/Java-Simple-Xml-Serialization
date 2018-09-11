package eng.eSystem.xmlSerialization.annotations.both.iterable;

import eng.eSystem.eXml.XDocument;
import eng.eSystem.eXml.XElement;
import eng.eSystem.xmlSerialization.XmlSerializer;
import eng.eSystem.xmlSerialization.annotations.XmlItemElement;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ListTester {

  @Test
  public void ArrayListDirectly() {
    List<String> a = new ArrayList<>();
    a.add("a");
    a.add("b");
    a.add(null);
    a.add("d");

    XDocument doc = new XDocument(new XElement("root"));
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(doc, a);

//    System.out.println(doc.getRoot().toFullString());

    List<String> b = ser.deserialize(doc, List.class);

    Shared.assertListEquals(a, b);
  }

  @Test
  public void LinkedListInInstance() {
    DataSet a = new DataSet();
    a.numbers.add((byte) 1);
    a.numbers.add((short) 2);
    a.numbers.add(3);
    a.numbers.add(4L);
    a.numbers.add(5f);
    a.numbers.add(6d);
    a.numbers.add(null);

    XDocument doc = new XDocument(new XElement("root"));
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(doc, a);

//    System.out.println(doc.getRoot().toFullString());

    DataSet b = ser.deserialize(doc, DataSet.class);

    Shared.assertListEquals(a.numbers, b.numbers);
  }

  @Test
  public void LinkedListInInstanceAsElements() {
    DataSetAsElements a = new DataSetAsElements();
    a.numbers.add((byte) 1);
    a.numbers.add((short) 2);
    a.numbers.add(3);
    a.numbers.add(4L);
    a.numbers.add(5f);
    a.numbers.add(6d);
    a.numbers.add(null);

    XDocument doc = new XDocument(new XElement("root"));
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(doc, a);

//    System.out.println(doc.getRoot().toFullString());

    DataSetAsElements b = ser.deserialize(doc, DataSetAsElements.class);

    assertFalse(doc.getRoot().toFullString().contains("value=\""));
    Shared.assertListEquals(a.numbers, b.numbers);
  }

}

class DataSet {
  public List<Number> numbers = new LinkedList<>();
}

class DataSetAsElements{
  @XmlItemElement(type = Number.class, elementName = "item")
  public List<Number> numbers = new LinkedList<>();
}