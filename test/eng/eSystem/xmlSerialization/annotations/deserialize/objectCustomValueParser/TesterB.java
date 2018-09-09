package eng.esystem.xmlSerialization.annotations.deserialize.objectCustomValueParser;

import eng.eSystem.eXml.XElement;
import eng.eSystem.xmlSerialization.XmlSerializer;
import eng.eSystem.xmlSerialization.annotations.XmlElement;
import eng.eSystem.xmlSerialization.supports.IElementParser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TesterB {

  @Test
  public void testA(){
    XElement root = new XElement("root");
    XElement elm;

    elm = new XElement("name", "John");
    root.addElement(elm);
    elm = new XElement("age", "nothing");
    root.addElement(elm);

    XmlSerializer ser = new XmlSerializer();
    PersonA p = ser.deserialize(root, PersonA.class);

    assertNotNull(p);
    assertEquals("John", p.name);
    assertEquals(-1, p.age);
  }

  @Test
  public void testAa(){
    XElement root = new XElement("root");
    XElement elm;

    elm = new XElement("name", "John");
    root.addElement(elm);
    elm = new XElement("age", "55");
    root.addElement(elm);

    XmlSerializer ser = new XmlSerializer();
    PersonA p = (PersonA) ser.deserialize(root, PersonA.class);

    assertNotNull(p);
    assertEquals("John", p.name);
    assertEquals(55, p.age);
  }

  @Test
  public void testB(){
    XElement root = new XElement("root");
    XElement elm;

    elm = new XElement("name", "John");
    root.addElement(elm);
    elm = new XElement("age", "nothing");
    root.addElement(elm);

    XmlSerializer ser = new XmlSerializer();
    PersonA p = (PersonA) ser.deserialize(root, PersonA.class);

    assertNotNull(p);
    assertEquals("John", p.name);
    assertEquals(-1, p.age);
  }

  @Test
  public void testBb(){
    XElement root = new XElement("root");
    XElement elm;

    elm = new XElement("name", "John");
    root.addElement(elm);
    elm = new XElement("age", "55");
    root.addElement(elm);

    XmlSerializer ser = new XmlSerializer();
    PersonA p = (PersonA) ser.deserialize(root, PersonA.class);

    assertNotNull(p);
    assertEquals("John", p.name);
    assertEquals(55, p.age);
  }
}

class PersonA {
  public String name;
  @XmlElement(parser = IntegerParser.class)
  public int age;
}

class PersonB {
  public String name;
  @XmlElement(parser = IntegerParserBackCall.class)
  public int age;
}

class IntegerParser implements IElementParser<Integer>{

  @Override
  public Integer parse(XElement element, XmlSerializer.Deserializer source) {
    String s = element.getContent();
    if (s == "nothing")
      return -1;
    else
      return Integer.parseInt(s);
  }

  @Override
  public void format(Integer value, XElement element, XmlSerializer.Serializer source) {
    element.setContent(value.toString());
  }

}

class IntegerParserBackCall implements IElementParser<Integer>{

  @Override
  public Integer parse(XElement element, XmlSerializer.Deserializer source) {
    String s = element.getContent();
    if (s == "nothing")
      return -1;
    else
      return source.deserialize(element, int.class);
  }

  @Override
  public void format(Integer value, XElement element, XmlSerializer.Serializer source) {
    if (value == null)
      source.serialize(value, element);
    else
      element.setContent(value.toString());
  }

}
