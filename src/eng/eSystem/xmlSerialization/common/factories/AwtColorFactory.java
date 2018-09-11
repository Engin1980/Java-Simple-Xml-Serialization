package eng.eSystem.xmlSerialization.common.factories;

import eng.eSystem.xmlSerialization.supports.IFactory;

import java.awt.*;

public class AwtColorFactory implements IFactory<java.awt.Color> {
  @Override
  public Color createInstance() {
    return new java.awt.Color(0);
  }
}