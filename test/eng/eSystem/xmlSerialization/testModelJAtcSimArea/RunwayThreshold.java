package eng.eSystem.xmlSerialization.testModelJAtcSimArea;

import eng.eSystem.xmlSerialization.XmlOptional;

public class RunwayThreshold implements KeyItem<String> {

  @XmlOptional
  private final KeyList<Approach, Approach.eType> approaches = new KeyList();
  @XmlOptional
  private final KeyList<Route, String> routes = new KeyList();
  private String name;
  private Coordinate coordinate;
  private Runway parent;
  private double _course;
  private int initialDepartureAltitude;
  private RunwayThreshold _other;
  @XmlOptional
  private boolean preferred = false;
  @XmlOptional
  private Coordinate fafCross;

  public String getName() {
    return name;
  }

  @Override
  public String getKey() {
    return getName();
  }
}
