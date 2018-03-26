package eng.eSystem.xmlSerialization.testModelEList;

import com.sun.javafx.collections.ElementObservableListDecorator;
import eng.eSystem.collections.EList;
import eng.eSystem.collections.IList;
import eng.eSystem.xmlSerialization.XmlSerializer;
import org.junit.Test;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class Tests {

  @Test
  public void testListInObject() {
    DataItem src = new DataItem();
    src.dimensions = new EList<>();
    src.dimensions.add(
        new Dimension(200, 200));
    src.dimensions.add(
        new Dimension(300, 300));
    src.dimensions.add((Dimension)null);

    src.numbers = new EList<>();
    src.numbers.add(1);
    src.numbers.add(2);
    src.numbers.add(3);

    src.emptyList = null;

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(bos, src);

    // System.out.println(new String(bos.toByteArray()));

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

    DataItem trg = (DataItem) ser.deserialize(bis, DataItem.class);

    assertListEquals(src.dimensions, trg.dimensions);
    assertListEquals(src.numbers, trg.numbers);
    assertListEquals(src.emptyList, trg.emptyList);
  }

  private <T> void assertListEquals(IList<T> src, IList<T> trg) {
    if (src == null){
      assertNull(trg);
    }else {
      assertNotNull(trg);

      assertEquals(
          String.format("Lists size differ. Expected. %d, actual %d.",src.size(), trg.size()),
          src.size(), trg.size());
      for (int i = 0; i < src.size(); i++) {
        assertEquals("Elements at index " + i + " differs.", src.get(i), trg.get(i));
      }
    }
  }

  @Test
  public void testListDirectly() {
    IList<String> src = new EList<>();
    src.add("a");
    src.add("b<bubla>");

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(bos, src);

    //System.out.println(new String(bos.toByteArray()));

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

    IList<String> trg = (IList<String>) ser.deserialize(bis, EList.class);

    assertListEquals(src, trg);
  }

  @Test
  public void testLists2D() {
    DataItemLists src = new DataItemLists();
    src.texts = new EList<>();

    IList<String> tmp;

    tmp = new EList<>();
    tmp.add("a");
    tmp.add((String)null);
    tmp.add("b");
    src.texts.add(tmp);

    tmp = new EList<>();
    tmp.add("e");
    tmp.add("f");
    src.texts.add(tmp);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(bos, src);

    //System.out.println(new String(bos.toByteArray()));

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

    DataItemLists trg = (DataItemLists) ser.deserialize(bis, DataItemLists.class);

    assertEquals(src.texts.size() ,trg.texts.size() );
    for (int i = 0; i < src.texts.size(); i++) {
      assertListEquals(src.texts.get(i), trg.texts.get(i));
    }
  }

  @Test
  public void testHeterogenousLists(){
    IList<Number> src = new EList<>();
    src.add((byte) 8);
    src.add(12);
    src.add(13.5d);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(bos, src);

    //System.out.println(new String(bos.toByteArray()));

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

    IList<Number> trg = (IList<Number>) ser.deserialize(bis, EList.class);

    assertListEquals(src, trg);
  }
}
