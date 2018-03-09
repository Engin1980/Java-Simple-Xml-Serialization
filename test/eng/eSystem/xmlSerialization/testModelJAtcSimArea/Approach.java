package eng.eSystem.xmlSerialization.testModelJAtcSimArea;

import eng.eSystem.xmlSerialization.XmlOptional;

public class Approach implements KeyItem<Approach.eType> {

  public enum eType {

    ILS_I,
    ILS_II,
    ILS_III,
    VORDME,
    NDB,
    GNSS,
    Visual
  }

  private eType type;
  private int da;
  private int radial;
  @XmlOptional
  private double glidePathPercentage = 3;
  private Coordinate point;
  private String gaRoute;
  //  private SpeechList<IAtcCommand> _gaCommands;
  private RunwayThreshold parent;

  @Override
  public eType getKey() {
    return type;
  }
}
