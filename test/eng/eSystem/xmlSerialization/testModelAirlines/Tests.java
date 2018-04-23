package eng.eSystem.xmlSerialization.testModelAirlines;

import eng.eSystem.collections.EList;
import eng.eSystem.xmlSerialization.XmlSerializer;
import eng.eSystem.xmlSerialization.testModelArray.DataItem;
import org.junit.Test;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertArrayEquals;

public class Tests {

  @Test
  public void test() {

    AirlineInfo ai;
    EList<AirlineInfo> lst = new EList<>();

    ai = new AirlineInfo("Easyjet", "www.easyjet.cz", true);
    ai.getCodes().add(new String[]{"U2", "EZ", "EZY"});
    ai.getFleet().set("Bojing", 0);
    ai.getFleet().set("Arbus", 100);

    lst.add(ai);

    XmlSerializer ser = new XmlSerializer();

    ser.serialize("R:\\test.xml", lst);


//
//    DataItem src = new DataItem();
//    src.dimensions = new Dimension[]{
//        null,
//        new Dimension(200, 200),
//        new Dimension(300, 300)
//    };
//    src.numbers = new int[]{1, 2, 3};
//
//    ByteArrayOutputStream bos = new ByteArrayOutputStream();
//    XmlSerializer ser = new XmlSerializer();
//
//    ser.serialize(bos, src);
//
//    //System.out.println(new String(bos.toByteArray()));
//
//    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
//
//    DataItem trg = (DataItem) ser.deserialize(bis, DataItem.class);
//
//    assertArrayEquals(src.numbers, trg.numbers);
//    assertArrayEquals(src.dimensions, trg.dimensions);
  }

}
