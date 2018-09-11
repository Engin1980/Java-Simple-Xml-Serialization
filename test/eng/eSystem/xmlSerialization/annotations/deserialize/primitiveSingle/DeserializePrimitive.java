package eng.eSystem.xmlSerialization.annotations.deserialize.primitiveSingle;

import eng.eSystem.eXml.XElement;
import eng.eSystem.xmlSerialization.XmlSerializer;
import org.junit.Test;

import static org.junit.Assert.*;

public class DeserializePrimitive {
  @Test
  public void test(){
    XElement root = new XElement("a", "8");

    XmlSerializer ser = new XmlSerializer();

    int ret = (int) ser.deserialize(root, Integer.class);
    assertEquals(8, ret);
  }
}
