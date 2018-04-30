package eng.eSystem.xmlSerialization.testModelOtherSettings;

import eng.eSystem.eXml.XElement;
import eng.eSystem.xmlSerialization.IElementParser;
import eng.eSystem.xmlSerialization.XmlSerializer;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.awt.*;

public class IElementParserModel implements IElementParser<java.awt.Dimension> {
  @Override
  public Class getType() {
    return java.awt.Dimension.class;
  }

  @Override
  public Dimension parse(XElement element, XmlSerializer.Deserializer parent) {

    int w = getElementIntValue(element, "w");
    int h = getElementIntValue(element, "h");
    Dimension ret = new Dimension(w,h);
    return ret;
  }

  private int getElementIntValue(XElement element, String name) {

    XElement tmp = element.getChildren().getFirst(q->q.getName().equals(name));
    String s = tmp.getContent();
    int ret = Integer.parseInt(s);
    return ret;
  }

  @Override
  public void format(Dimension value, XElement element, XmlSerializer.Serializer parent) {
    XElement s;
    s = new XElement("w");
    s.setContent(Integer.toString(value.width));
    element.addElement(s);
    s = new XElement("h");
    s.setContent(Integer.toString(value.height));
    element.addElement(s);
  }

  @Override
  public boolean isApplicableOnDescendants() {
    return false;
  }
}
