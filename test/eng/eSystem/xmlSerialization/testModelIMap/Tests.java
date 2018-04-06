package eng.eSystem.xmlSerialization.testModelIMap;

import eng.eSystem.collections.EMap;
import eng.eSystem.collections.IMap;
import eng.eSystem.collections.ISet;
import eng.eSystem.xmlSerialization.XmlSerializer;
import org.junit.Test;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.List;

import static org.junit.Assert.*;

public class Tests {

  @Test
  public void testMapInObject() {
    DataItem src = new DataItem();
    src.dimensions = new EMap<>();
    src.dimensions.set("jedna",
        new Dimension(200, 200));
    src.dimensions.set("dva",
        new Dimension(300, 300));
    src.dimensions.set("tři", null);

    src.numbers = new EMap<>();
    src.numbers.set(1, "one");
    src.numbers.set(2, "two");
    src.numbers.set(3, "three");

    src.empty = new EMap<>();

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(bos, src);

    // System.out.println(new String(bos.toByteArray()));

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

    DataItem trg = (DataItem) ser.deserialize(bis, DataItem.class);

    assertMapEquals(src.dimensions, trg.dimensions);
    assertMapEquals(src.numbers, trg.numbers);
    assertMapEquals(src.empty, trg.empty);
  }

  private <T,K> void assertMapEquals(IMap<T, K> src, IMap<T, K> trg) {
    if (src == null){
      assertNull(trg);
    }else {
      assertNotNull(trg);

      assertEquals(
          String.format("Maps size differ. Expected. %d, actual %d.",src.size(), trg.size()),
          src.size(), trg.size());

      ISet<T> srcKeys = src.keySet();
      ISet<T> trgKeys = trg.keySet();

      for (T srcKey : srcKeys) {
        T trgKey = null;
        for (T key : trgKeys) {
          if (srcKey.equals(key)){
            trgKey = key;
            break;
          }
        }

        K srcVal = src.get(srcKey);
        K trgVal = trg.get(trgKey);

        assertEquals("Elements for key " + srcKey + " differs.", srcVal, trgVal);
      }
    }
  }

  @Test
  public void testMapDirectly() {
    IMap<String, Integer> src = new EMap<>();
    src.set("jedna", 1 );
    src.set("dva", 2);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(bos, src);

    // System.out.println(new String(bos.toByteArray()));

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

    IMap<String, Integer> trg = (IMap<String, Integer>) ser.deserialize(bis, EMap.class);

    assertMapEquals(src, trg);
  }

  @Test
  public void testListInMap() {
    DataItemMaps src = new DataItemMaps();
    src.texts = new EMap<>();

    List<String> lst;

    lst = new ArrayList<>();
    src.texts.set(0, lst);

    lst = new ArrayList<>();
    lst.add("one");
    src.texts.set(1, lst );

    lst = new ArrayList<>();
    lst.add("one");
    lst.add("two");
    src.texts.set(2, lst );

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(bos, src);

    // System.out.println(new String(bos.toByteArray()));

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

    DataItemMaps trg = (DataItemMaps) ser.deserialize(bis, DataItemMaps.class);

    assertMapEquals(src.texts, trg.texts );
  }

  @Test
  public void testHeterogenousMaps(){
    IMap<Number, Number> src = new EMap<>();
    src.set((byte) 8, 8d);
    src.set(8, 8L);
    src.set(8f, 8d);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(bos, src);

    // System.out.println(new String(bos.toByteArray()));

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

    IMap<Number, Number> trg = (IMap<Number, Number>) ser.deserialize(bis, EMap.class);

    assertMapEquals(src, trg);
  }
}
