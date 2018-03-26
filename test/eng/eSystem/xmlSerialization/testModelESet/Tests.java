package eng.eSystem.xmlSerialization.testModelESet;

import eng.eSystem.collections.ESet;
import eng.eSystem.collections.ISet;
import eng.eSystem.xmlSerialization.XmlSerializer;
import org.junit.Test;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;

import static org.junit.Assert.*;

public class Tests {

  @Test
  public void testSetsInObject() {
    DataItem src = new DataItem();
    src.dimensions = new ESet<>();
    src.dimensions.add(
        new Dimension(200, 200));
    src.dimensions.add(
        new Dimension(300, 300));

    src.numbers = new ESet<>();
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

    assertSetsEqual(src.dimensions, trg.dimensions);
    assertSetsEqual(src.numbers, trg.numbers);
    assertSetsEqual(src.emptyList, trg.emptyList);
  }

  private <T> void assertSetsEqual(ISet<T> src, ISet<T> trg) {
    if (src == null){
      assertNull(trg);
    }else {
      assertNotNull(trg);

      assertEquals(
          String.format("Lists size differ. Expected. %d, actual %d.",src.size(), trg.size()),
          src.size(), trg.size());
      for (T t : src) {
        if (trg.contains(t) == false){
          fail("Set source contains element " + t.toString() + " which is not in the other set.");
          break;
        }
      }
    }
  }

  @Test
  public void testSetsDirectly() {
    ISet<String> src = new ESet<>();
    src.add("a");
    src.add("b<bubla>");

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(bos, src);

    //System.out.println(new String(bos.toByteArray()));

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

    ISet<String> trg = (ISet<String>) ser.deserialize(bis, ESet.class);

    assertSetsEqual(src, trg);
  }

  @Test
  public void testSets2D() {
    DataItemLists src = new DataItemLists();
    src.texts = new ESet<>();

    ISet<String> tmp;

    tmp = new ESet<>();
    tmp.add("a");
    tmp.add("b");
    src.texts.add(tmp);

    tmp = new ESet<>();
    tmp.add("e");
    tmp.add("f");
    src.texts.add(tmp);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(bos, src);

    //System.out.println(new String(bos.toByteArray()));

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

    DataItemLists trg = (DataItemLists) ser.deserialize(bis, DataItemLists.class);

    assertEquals(src.texts.size() ,trg.texts.size());

    // REALLY HARD TO CHECK
  }

  @Test
  public void testHeterogenousSets(){
    ISet<Number> src = new ESet<>();
    src.add((byte) 8);
    src.add(12);
    src.add(13.5d);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(bos, src);

    //System.out.println(new String(bos.toByteArray()));

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

    ISet<Number> trg = (ISet<Number>) ser.deserialize(bis, ESet.class);

    assertSetsEqual(src, trg);
  }
}
