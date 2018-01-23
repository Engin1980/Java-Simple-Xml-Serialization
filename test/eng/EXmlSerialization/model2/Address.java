package eng.EXmlSerialization.model2;

import eng.EXmlSerialization.XmlOptional;

public class Address {
  private String city;
  private String street;
  private String houseNumber;
  @XmlOptional
  private Gps gps = null;

  public String getCity() {
    return city;
  }

  public String getStreet() {
    return street;
  }

  public String getHouseNumber() {
    return houseNumber;
  }

  public Gps getGps() {
    return gps;
  }
}
