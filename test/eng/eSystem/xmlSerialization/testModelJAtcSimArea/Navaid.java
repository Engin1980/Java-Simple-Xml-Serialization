package eng.eSystem.xmlSerialization.testModelJAtcSimArea;

public class Navaid implements KeyItem<String> {

  @Override
  public String getKey() {
    return name;
  }
  public enum eType{
    VOR,
    NDB,
    Fix,
    FixMinor,
    Airport
  }

  private Coordinate coordinate;
  private String name;
  private eType type;
}
