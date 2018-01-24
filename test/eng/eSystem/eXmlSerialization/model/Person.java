package eng.eSystem.eXmlSerialization.model;

import java.util.List;

public class Person {
  private String name;
  public String surname;
  private String privateIdA;
  private String privateIdB;
  private Address address;
  private Address backupAddress;
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

  public String getPrivateIdA() {
    return privateIdA;
  }

  public String getPrivateIdB() {
    return privateIdB;
  }

  public Address getBackupAddress() {
    return backupAddress;
  }
}
