package eng.eSystem.eXmlSerialization.model;

import eng.eSystem.eXmlSerialization.XmlOptional;

import java.util.List;

public class Person {

  public enum Gender{
    unset,
    male,
    female
  }

  private String name;
  public String surname;
  private String privateIdA;
  private String privateIdB;
  private Address address;
  private Address backupAddress;
  private List<Phone> phones;
  private List<String> phoneNumbers;
  private Gender gender = Gender.unset;
  @XmlOptional
  private Friend[] friends;

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

  public Friend[] getFriends() {
    return friends;
  }

  public void setFriends(Friend[] friends) {
    this.friends = friends;
  }

  public Gender getGender() {
    return gender;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }
}
