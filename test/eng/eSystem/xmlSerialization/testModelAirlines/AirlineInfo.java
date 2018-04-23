package eng.eSystem.xmlSerialization.testModelAirlines;

import eng.eSystem.collections.EList;
import eng.eSystem.collections.EMap;
import eng.eSystem.collections.IList;
import eng.eSystem.collections.IMap;
import eng.eSystem.xmlSerialization.XmlOptional;

public class AirlineInfo {
  private final String name;
  private final String url;
  private final boolean active;
  @XmlOptional
  private IList<String> codes =new EList<>();
  @XmlOptional
  private IMap<String, Integer> fleet = new EMap<>();

  private AirlineInfo() {
    name = null;
    url = null;
    active = false;
  }

  public IMap<String, Integer> getFleet() {
    return fleet;
  }

  public IList<String> getCodes() {
    return codes;
  }

  public AirlineInfo(String name, String url, boolean active) {
    this.name = name;
    this.url = url;
    this.active = active;
  }

  public boolean isActive() {
    return active;
  }

  public String getName() {
    return name;
  }

  public String getUrl() {
    return url;
  }

  @Override
  public String toString() {
    return "AirlineInfo{" +
        "name='" + name + '\'' +
        ", url='" + url + '\'' +
        ", active=" + active +
        '}';
  }
}
