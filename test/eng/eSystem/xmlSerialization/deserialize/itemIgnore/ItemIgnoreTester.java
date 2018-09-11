package eng.eSystem.xmlSerialization.deserialize.itemIgnore;

import eng.eSystem.collections.EList;
import eng.eSystem.eXml.XElement;
import eng.eSystem.xmlSerialization.XmlSerializer;
import eng.eSystem.xmlSerialization.annotations.XmlItemElement;
import eng.eSystem.xmlSerialization.annotations.XmlItemIgnoreElement;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ItemIgnoreTester {

  @Test
  public void testIgnoreField(){
    XElement root = new XElement("root");
    XElement numbers = new XElement("numbers");
    root.addElement(numbers);
    XElement tmp;
    tmp = new XElement("item", "10");
    numbers.addElement(tmp);
    tmp = new XElement("bubla", "10");
    numbers.addElement(tmp);
    tmp = new XElement("item", "10");
    numbers.addElement(tmp);
    tmp = new XElement("item", "10");
    numbers.addElement(tmp);

    XmlSerializer ser = new XmlSerializer();

    Dede d = ser.deserialize(root, Dede.class);

    assertEquals(3, d.numbers.size());
  }

  @Test
  public void testIgnoreType(){
    XElement root = new XElement("root");
    XElement numbers = new XElement("numbers");
    root.addElement(numbers);
    XElement tmp;
    tmp = new XElement("item", "10");
    numbers.addElement(tmp);
    tmp = new XElement("bubla", "10");
    numbers.addElement(tmp);
    tmp = new XElement("item", "10");
    numbers.addElement(tmp);
    tmp = new XElement("item", "10");
    numbers.addElement(tmp);

    XmlSerializer ser = new XmlSerializer();

    Dudu d = ser.deserialize(root, Dudu.class);

    assertEquals(3, d.numbers.size());
  }
}

class Dede {
  @XmlItemIgnoreElement(elementName = "bubla")
  @XmlItemElement(elementName = "item", type = Integer.class)
  public EList<Integer> numbers;
}

class Dudu {

  public DuduList numbers;
}

@XmlItemIgnoreElement(elementName = "bubla")
@XmlItemElement(elementName = "item", type = Integer.class)
class DuduList extends EList<Integer> {
}
