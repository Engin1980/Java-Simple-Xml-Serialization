package eng.eSystem.xmlSerialization.annotations.both.array;

import eng.eSystem.eXml.XDocument;
import eng.eSystem.eXml.XElement;
import eng.eSystem.xmlSerialization.XmlSerializer;
import eng.eSystem.xmlSerialization.annotations.both.iterable.Shared;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ArrayTester {

  @Test
  public void ArrayDirectly() {
    String[] a = {"a", "b", null, "d"};

    XDocument doc = new XDocument(new XElement("root"));
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(doc, a);

   // System.out.println(doc.getRoot().toFullString());

    String[] b = ser.deserialize(doc, String[].class);

    assertArrayEquals(a, b);
  }

  @Test
  public void ArrayInObject() {
    Person a = new Person();
    a.name = "John Doe";
    a.marks = new int[]{1, 2, 3, 4, 5};

    XDocument doc = new XDocument(new XElement("root"));
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(doc, a);

//    System.out.println(doc.getRoot().toFullString());

    Person b = ser.deserialize(doc, Person.class);

    assertArrayEquals(a.marks, b.marks);
  }

  @Test
  public void ArrayMultiTypes(){
    Number [] a = new Number[]{ null, (short)1, (byte)2, 3, 4l, 5f, 6d};

    XDocument doc = new XDocument(new XElement("root"));
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(doc, a);

//    System.out.println(doc.getRoot().toFullString());

    Number[] b = ser.deserialize(doc, Number[].class);

    assertArrayEquals(a, b);
  }

  @Test
  public void ArrayInArray(){
    String [][] a = new String[][]{{"a", "b", "c"}, {"c", null}, null};

    XDocument doc = new XDocument(new XElement("root"));
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(doc, a);

//    System.out.println(doc.getRoot().toFullString());

    String[][] b = ser.deserialize(doc, String[][].class);

    assertEquals(a.length, b.length);
    for (int i = 0; i < a.length; i++) {
      assertArrayEquals(a[i], b[i]);
    }
  }

  @Test
  public void ListInArray(){
    List<String>[] a = new ArrayList[3];
    a[0] = new ArrayList<>();
    a[0].add("a");
    a[0].add("b");
    a[1] = new ArrayList<>();
    a[1].add("c");
    a[1].add(null);
    a[2] = null;

    XDocument doc = new XDocument(new XElement("root"));
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(doc, a);

//    System.out.println(doc.getRoot().toFullString());

    List<String>[] b = ser.deserialize(doc, List[].class);

    assertEquals(a.length, b.length);
    for (int i = 0; i < a.length; i++) {
      Shared.assertListEquals(a[i], b[i]);
    }
  }

  @Test
  public void ArrayInList(){
    List<String[]> a = new ArrayList<>();
    a.add(new String[]{"a","b","c"});
    a.add(new String[]{"d",null});
    a.add(null);

    XDocument doc = new XDocument(new XElement("root"));
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(doc, a);

//    System.out.println(doc.getRoot().toFullString());

    List<String[]> b = ser.deserialize(doc, List.class);

    assertEquals(a.size(), b.size());
    for (int i = 0; i < a.size(); i++) {
      assertArrayEquals(a.get(i), b.get(i) );
    }
  }
}

class Person {
  public String name;
  public int[] marks;
}