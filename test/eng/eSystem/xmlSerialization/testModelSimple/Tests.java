package eng.eSystem.xmlSerialization.testModelSimple;

import eng.eSystem.xmlSerialization.XmlSerializer;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class Tests {

  @Test
  public void simpleTest() {
    DataItem src = new DataItem();
    src.booleanValue = true;
    src.booleanWrappedValue = false;
    src.characterValue = 'a';
    src.charValue = 'a';
    src.dimensionValue = new java.awt.Dimension(200, 300);
    src.customDimensionValue = new Dimension(300, 500, 800);
    src.doubleValue = 10;
    src.doubleWrappedValue = 300d;
    src.intValue = 50;
    src.intWrappedValue = -100;
    src.stringValue = "John";
    src.intNullWrappedValue = null;
    src.doubleNullWrappedValue = null;
    src.booleanNullWrappedValue = null;
    src.dimensionNullValue = null;
    src.intIgnoredValue = Integer.MAX_VALUE;

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(bos, src);

    //System.out.println(new String(bos.toByteArray()));

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

    DataItem trg = (DataItem) ser.deserialize(bis, DataItem.class);

    assertEquals(src.characterValue, trg.characterValue);
    assertEquals(src.charValue, trg.charValue);
    assertEquals(src.dimensionValue.height, trg.dimensionValue.height);
    assertEquals(src.dimensionValue.width, trg.dimensionValue.width);

    Dimension srcD = (Dimension) src.customDimensionValue;
    Dimension trgD = (Dimension) trg.customDimensionValue;
    assertEquals(srcD.height, trgD.height);
    assertEquals(srcD.width, trgD.width);
    assertEquals(srcD.depth, trgD.depth);

    assertEquals(src.doubleValue, trg.doubleValue, 0);
    assertEquals(src.doubleWrappedValue, trg.doubleWrappedValue);
    assertEquals(src.intValue, trg.intValue);
    assertEquals(src.intWrappedValue, trg.intWrappedValue);
    assertEquals(src.stringValue, trg.stringValue);
    assertNull(trg.intNullWrappedValue);
    assertNull(trg.doubleNullWrappedValue);
    assertNull(trg.booleanNullWrappedValue);
    assertNull(trg.dimensionNullValue);
    assertEquals(0, trg.intIgnoredValue);
  }

}
