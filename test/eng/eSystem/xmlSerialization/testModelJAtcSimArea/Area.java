package eng.eSystem.xmlSerialization.testModelJAtcSimArea;

import eng.eSystem.xmlSerialization.XmlIgnore;

import java.util.ArrayList;
import java.util.List;

public class Area {

  private String icao;
  private final KeyList<Airport, String> airports = new KeyList();
  @XmlIgnore
  private final KeyList<Navaid, String> navaids = new KeyList();
  @XmlIgnore
  private final List<Border> borders = new ArrayList();

  public KeyList<Airport, String> getAirports() {
    return airports;
  }

  public String getIcao() {
    return icao;
  }

  public KeyList<Navaid, String> getNavaids() {
    return navaids;
  }

  public List<Border> getBorders() {
    return borders;
  }

}
