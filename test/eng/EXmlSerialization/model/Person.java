package eng.EXmlSerialization.model;

import java.util.List;

public class Person {
  private String name;
  public String surname;
  private Address address;
  private List<Phone> phones;
  private List<String> phoneNumbers;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public List<Phone> getPhones() {
    return phones;
  }

  public void setPhones(List<Phone> phones) {
    this.phones = phones;
  }

  public List<String> getPhoneNumbers() {
    return phoneNumbers;
  }
}
