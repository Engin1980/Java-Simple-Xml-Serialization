package eng.eSystem.xmlSerialization.testModelArray;

import eng.eSystem.xmlSerialization.XmlSerializer;
import org.junit.Ignore;
import org.junit.Test;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertArrayEquals;

public class Tests {
  @Test
  public void testArrayInObject() {
    DataItem src = new DataItem();
    src.dimensions = new Dimension[]{
        null,
        new Dimension(200, 200),
        new Dimension(300, 300)
    };
    src.numbers = new int[]{1, 2, 3};

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(bos, src);

    //System.out.println(new String(bos.toByteArray()));

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

    DataItem trg = (DataItem) ser.deserialize(bis, DataItem.class);

    assertArrayEquals(src.numbers, trg.numbers);
    assertArrayEquals(src.dimensions, trg.dimensions);
  }

  @Test
  public void testArrayDirectly() {
    String[] src = new String[]{"A", "B", null, "D"};

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(bos, src);

    System.out.println(new String(bos.toByteArray()));

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

    String[] tmp = new String[0];

    String[] trg = (String[]) ser.deserialize(bis, tmp.getClass());

    assertArrayEquals(src, trg);
  }

  @Test
  public void testArray2D() {
    DataItemArrays src = new DataItemArrays();
    src.data = new String[][]{
        new String[]{"A", "B", "C"},
        new String[]{"E", "F", "G"},
        new String[]{"K", "L", "M"}
    };

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(bos, src);

    // System.out.println(new String(bos.toByteArray()));

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

    DataItemArrays trg = (DataItemArrays) ser.deserialize(bis, DataItemArrays.class);

    assertArrayEquals(src.data[0], trg.data[0]);
    assertArrayEquals(src.data[1], trg.data[1]);
    assertArrayEquals(src.data[2], trg.data[2]);
  }

  @Test
  public void testHeterogenousArray(){
    Number[] src = new Number[]{(byte) 8, 12, 13.5d};

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(bos, src);

    //System.out.println(new String(bos.toByteArray()));

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

    Number[] tmp = new Number[0];

    Number[] trg = (Number[]) ser.deserialize(bis, tmp.getClass());

    assertArrayEquals(src, trg);
  }
}
