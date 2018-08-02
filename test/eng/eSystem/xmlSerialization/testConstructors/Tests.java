package eng.eSystem.xmlSerialization.testConstructors;

import eng.eSystem.xmlSerialization.Settings;
import eng.eSystem.xmlSerialization.XmlSerializer;
import eng.eSystem.xmlSerialization.testDoubleCustomParser.DataItem;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class Tests {
  @Test
  public void testA() {
    DemoA a = new DemoA(1, 5);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    Settings sett = new Settings();
    XmlSerializer ser = new XmlSerializer(sett);

    ser.serialize(bos, a);
    System.out.println(new String(bos.toByteArray()));

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

    a = (DemoA) ser.deserialize(bis, DemoA.class);
  }

  @Test
  public void testB() {
    DemoB a = new DemoB(false, "false", new ArrayList());

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    Settings sett = new Settings();
    XmlSerializer ser = new XmlSerializer(sett);

    ser.serialize(bos, a);
    System.out.println(new String(bos.toByteArray()));

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

    a = (DemoB) ser.deserialize(bis, DemoB.class);
  }

  @Test
  public void testC() {
    DemoC a = new DemoC(1,1,1);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    Settings sett = new Settings();
    XmlSerializer ser = new XmlSerializer(sett);

    ser.serialize(bos, a);
    System.out.println(new String(bos.toByteArray()));

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

    a = (DemoC) ser.deserialize(bis, DemoC.class);
  }
}
