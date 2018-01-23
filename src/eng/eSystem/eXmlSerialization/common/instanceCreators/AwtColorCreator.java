package eng.eSystem.eXmlSerialization.common.instanceCreators;

import eng.eSystem.eXmlSerialization.IInstanceCreator;

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
