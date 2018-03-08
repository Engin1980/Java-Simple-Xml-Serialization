package eng.eSystem.xmlSerialization.common.instanceCreators;

import eng.eSystem.xmlSerialization.IInstanceCreator;

import java.awt.*;

public class AwtColorCreator implements IInstanceCreator<Color> {
  @Override
  public Class getType() {
    return java.awt.Color.class;
  }

  @Override
  public Color createInstance() {
    return new java.awt.Color(0);
  }
}
