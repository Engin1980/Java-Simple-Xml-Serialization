package eng.eSystem.xmlSerialization.model;

import eng.eSystem.xmlSerialization.XmlOptional;

import java.awt.*;

public class NamedColor {
  private String name;
  private Color color;
  @XmlOptional
  private Font font;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Color getColor() {
    return color;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public Font getFont() {
    return font;
  }
}
