package eng.eSystem.xmlSerialization.testModelOtherSettings;

import eng.eSystem.xmlSerialization.IElementParser;
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
  public Dimension parse(Element element) {

    int w = getElementIntValue(element, "w");
    int h = getElementIntValue(element, "h");
    Dimension ret = new Dimension(w,h);
    return ret;
  }

  private int getElementIntValue(Element element, String name) {
    Element el = null;
    for (int i = 0; i < element.getChildNodes().getLength(); i++) {
      Node n = element.getChildNodes().item(i);
      if (n.getNodeType() != Node.ELEMENT_NODE) continue;
      if (n.getNodeName().equals(name) == false) continue;
      el = (Element)n;
      break;
    }

    String s = el.getTextContent();
    int ret = Integer.parseInt(s);
    return ret;
  }

  @Override
  public void format(Dimension value, Element element) {
    Element s;
    s = element.getOwnerDocument().createElement("w");
    s.setTextContent(Integer.toString(value.width));
    element.appendChild(s);
    s = element.getOwnerDocument().createElement("h");
    s.setTextContent(Integer.toString(value.height));
    element.appendChild(s);
  }
}
