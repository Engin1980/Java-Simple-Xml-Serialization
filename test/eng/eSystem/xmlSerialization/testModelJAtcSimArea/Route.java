package eng.eSystem.xmlSerialization.testModelJAtcSimArea;

import eng.eSystem.xmlSerialization.XmlOptional;

import java.util.List;

public class Route implements KeyItem<String> {
  //  private eType type;
  private String name;
  private String route;
  private RunwayThreshold parent;
  @XmlOptional
  private String category = null;
  //  private SpeechList<IAtcCommand> _routeCommands = null;
  private List<Navaid> _routeNavaids = null;
  private double _routeLength = -1;
  private Navaid _mainFix = null;

  @Override
  public String getKey() {
    return name;
  }
}
