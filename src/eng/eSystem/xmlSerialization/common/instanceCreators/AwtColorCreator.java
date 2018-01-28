package eng.eSystem.xmlSerialization.common.instanceCreators;

import eng.eSystem.xmlSerialization.IInstanceCreator;

import java.awt.*;

public class AwtColorCreator implements IInstanceCreator<Color> {
  @Override
  public String getTypeName() {
    return java.awt.Color.class.getName();
  }

  @Override
  public Color createInstance() {
    return new java.awt.Color(0);
  }
}
