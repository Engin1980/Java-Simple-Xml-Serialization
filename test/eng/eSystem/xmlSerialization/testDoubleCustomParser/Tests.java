package eng.eSystem.xmlSerialization.testDoubleCustomParser;

import eng.eSystem.xmlSerialization.*;
import org.junit.Assert;
import org.junit.Test;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class Tests {
  @Test
  public void test() {
    DataItem src = new DataItem();
    src.a = 10;
    src.b = 20d;
    src.c = null;

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    Settings sett = new Settings();
    sett.getValueParsers().add(new DoubleParser());
    XmlSerializer ser = new XmlSerializer(sett);

    ser.serialize(bos, src);
    System.out.println(new String(bos.toByteArray()));

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

    DataItem trg = (DataItem) ser.deserialize(bis, DataItem.class);

    Assert.assertEquals(src.a, trg.a, 0);
    Assert.assertEquals(src.b, trg.b, 0);
    Assert.assertNull(trg.c);
  }
}

class DoubleParser implements IValueParser<Double>{

  @Override
  public Class getType() {
    return double.class;
  }

  @Override
  public Double parse(String value) throws XmlDeserializationException {
    return Double.parseDouble(value);
  }

  @Override
  public String format(Double value) throws XmlSerializationException {
    return value.toString();
  }
}