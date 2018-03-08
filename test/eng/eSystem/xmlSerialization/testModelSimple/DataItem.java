package eng.eSystem.xmlSerialization.testModelSimple;

import eng.eSystem.xmlSerialization.XmlIgnore;

import java.awt.*;

public class DataItem {
  public int intValue;
  public Integer intWrappedValue;
  public Integer intNullWrappedValue;
  public String stringValue;
  public double doubleValue;
  public Double doubleWrappedValue;
  public Double doubleNullWrappedValue;
  public boolean booleanValue;
  public Boolean booleanWrappedValue;
  public Boolean booleanNullWrappedValue;
  public char charValue;
  public Character characterValue;
  public Character characterNullValue;

  public java.awt.Dimension dimensionValue;
  public java.awt.Dimension dimensionNullValue;

  public java.awt.Dimension customDimensionValue;

  @XmlIgnore
  public int intIgnoredValue;
}
