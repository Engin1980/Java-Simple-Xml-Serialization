package eng.eSystem.xmlSerialization.testModelRealB;

import java.time.LocalDate;
import java.util.List;

public class Person {
  private String name;
  private String surname;
  private java.time.LocalDate birthDate;
  private Address address;
  private List<Phone> phones;

  public String getName() {
    return name;
  }

  public String getSurname() {
    return surname;
  }

  public LocalDate getBirthDate() {
    return birthDate;
  }

  public Address getAddress() {
    return address;
  }

  public List<Phone> getPhones() {
    return phones;
  }
}
