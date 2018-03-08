package eng.eSystem.xmlSerialization.testModelList;

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
    src.dimensions = new ArrayList();
    src.dimensions.add(
        new Dimension(200, 200));
    src.dimensions.add(
        new Dimension(300, 300));
    src.dimensions.add(null);

    src.numbers = new LinkedList<>();
    src.numbers.add(1);
    src.numbers.add(2);
    src.numbers.add(3);

    src.emptyList = null;

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(bos, src);

    //System.out.println(new String(bos.toByteArray()));

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

    DataItem trg = (DataItem) ser.deserialize(bis, DataItem.class);

    assertListEquals(src.dimensions, trg.dimensions);
    assertListEquals(src.numbers, trg.numbers);
    assertListEquals(src.emptyList, trg.emptyList);
  }

  private <T> void assertListEquals(List<T> src, List<T> trg) {
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
    List<String> src = new ArrayList<>();
    src.add("a");
    src.add("b<bubla>");

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(bos, src);

    //System.out.println(new String(bos.toByteArray()));

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

    List<String> trg = (List<String>) ser.deserialize(bis, ArrayList.class);

    assertListEquals(src, trg);
  }

  @Test
  public void testLists2D() {
    DataItemLists src = new DataItemLists();
    src.texts = new ArrayList<>();

    List<String> tmp;

    tmp = new ArrayList<>();
    tmp.add("a");
    tmp.add(null);
    tmp.add("b");
    src.texts.add(tmp);

    tmp = new LinkedList<>();
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
    List<Number> src = new ArrayList<>();
    src.add((byte) 8);
    src.add(12);
    src.add(13.5d);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(bos, src);

    //System.out.println(new String(bos.toByteArray()));

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

    List<Number> trg = (List<Number>) ser.deserialize(bis, ArrayList.class);

    assertListEquals(src, trg);
  }
}
