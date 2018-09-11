package eng.eSystem.xmlSerialization.annotations.deserialize.list;

import eng.eSystem.collections.EList;
import eng.eSystem.collections.IList;
import eng.eSystem.eXml.XDocument;
import eng.eSystem.eXml.XElement;
import eng.eSystem.xmlSerialization.XmlSerializer;
import eng.eSystem.xmlSerialization.annotations.XmlItemElement;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ListDeserializeTester {

  @Test
  public void testDeserialize() {

    XElement root;
    XElement e;


    root = new XElement("data");

    e = new XElement("myItem");
    e.setAttribute("id", "10");
    root.addElement(e);

    e = new XElement("myItem");
    e.setAttribute("id", "10");
    root.addElement(e);

    e = new XElement("myItem");
    e.setAttribute("id", "10");
    root.addElement(e);

//    System.out.println(root.toFullString());

    XmlSerializer ser = new XmlSerializer();

    MyList lst= ser.deserialize(root, MyList.class);

    assertTrue(lst instanceof EList);

    Object item = lst.get(0);
    assertTrue(item instanceof MyItem);
  }
}

@XmlItemElement(elementName = "myItem", type = MyItem.class)
class MyList extends EList<MyItem> {

}


class MyItem {
  public String id;
}