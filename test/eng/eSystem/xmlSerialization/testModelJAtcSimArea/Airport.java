package eng.eSystem.xmlSerialization.testModelJAtcSimArea;

public class Airport implements KeyItem<String> {

  private final KeyList<Runway, String> runways = new KeyList();
  private String icao;
  private String name;
  private int altitude;
  private int transitionAltitude;
  private int vfrAltitude;
  private String mainAirportNavaidName;
  private Navaid _mainAirportNavaid;
//private final KeyList<AtcTemplate, Atc.eType> atcTemplates = new KeyList();
//private final KeyList<PublishedHold, Navaid> holds = new KeyList();
//private final KeyList<VfrPoint, String> vfrPoints = new KeyList();
//private Traffic traffic;
  private Area parent;

  @Override
  public String getKey() {
    return icao;
  }
}
