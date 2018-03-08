package eng.eSystem.xmlSerialization.testModelRealA;

import eng.eSystem.xmlSerialization.Settings;
import eng.eSystem.xmlSerialization.XmlListItemMapping;
import eng.eSystem.xmlSerialization.XmlSerializer;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class Tests {

  @Test
  public void testPersonRealData() {

    Person src = new Person();
    src.setName("John");
    src.surname = "Doe";

    Address a = new Address();
    a.setHouseNumber(1576);
    a.setStreet("Nejasna");
    a.setUsed(true);

    src.setAddress(a);

    List<Phone> lst = new ArrayList();
    src.setPhones(lst);

    Phone h;

    h = new Phone();
    h.number = "123";
    lst.add(h);

    h = new Phone();
    h.number = "456";
    lst.add(h);

    h = new Phone();
    h.number = "789";
    lst.add(h);

    lst.add(null);

    src.setFriends(new Friend[3]);
    Friend f;

    f = new Friend();
    f.setName("Amy");
    f.setSurname("Doe");
    src.getFriends()[0] = f;

    f = new Friend();
    f.setName("Melany");
    f.setSurname("Doe");
    src.getFriends()[1] = f;

    XmlSerializer ser = new XmlSerializer();

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    ser.serialize(bos, src);

    // System.out.println(new String(bos.toByteArray()));

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

    Person trg = (Person) ser.deserialize(bis, Person.class);


    assertEquals(src.getName(), trg.getName());
    assertEquals(src.surname, trg.surname);
    assertEquals(src.getAddress().getHouseNumber(), trg.getAddress().getHouseNumber());
    assertEquals(src.getAddress().getStreet(), trg.getAddress().getStreet());
    assertEquals(src.getAddress().isUsed(), trg.getAddress().isUsed());

    assertEquals(src.getPhones().size(), trg.getPhones().size());
    assertEquals(src.getPhones().get(0).number, trg.getPhones().get(0).number);
    assertEquals(src.getPhones().get(1).number, trg.getPhones().get(1).number);
    assertEquals(src.getPhones().get(2).number, trg.getPhones().get(2).number);
    assertEquals(src.getPhones().get(3), trg.getPhones().get(3));

    assertEquals(src.getFriends().length, trg.getFriends().length);
    assertEquals(src.getFriends()[0].getName(), trg.getFriends()[0].getName());
    assertEquals(src.getFriends()[0].getSurname(), trg.getFriends()[0].getSurname());
  }

  @Test
  public void testMultiList(){
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<data>\n" +
        "    <cList>true</cList>\n" +
        "    <aList>\n" +
        "        <a>5</a>\n" +
        "        <a>6</a>\n" +
        "        <a>7</a>\n" +
        "    </aList>\n" +
        "    <bList>\n" +
        "        <a>xuxu</a>\n" +
        "        <a>yuyu</a>\n" +
        "        <a>zuzu</a>\n" +
        "    </bList>\n" +
        "</data>";

    Settings settings = new Settings();

    settings.getListItemMappings().add(
        new XmlListItemMapping("aL.+", Integer.class)
    );
    settings.getListItemMappings().add(
        new XmlListItemMapping("bL.+", String.class)
    );

    XmlSerializer ser = new XmlSerializer(settings);

    ByteArrayInputStream bis = new ByteArrayInputStream(xml.getBytes());

    MultiList trg = (MultiList) ser.deserialize(bis, MultiList.class);

    assertFalse(trg.getaList().isEmpty());
    assertFalse(trg.getbList().isEmpty());
  }

}
