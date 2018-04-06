package eng.eSystem.xmlSerialization.testModelMap;

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
    src.dimensions = new HashMap<>();
    src.dimensions.put("jedna",
        new Dimension(200, 200));
    src.dimensions.put("dva",
        new Dimension(300, 300));
    src.dimensions.put("tři", null);

    src.numbers = new TreeMap<>();
    src.numbers.put(1, "one");
    src.numbers.put(2, "two");
    src.numbers.put(3, "three");

    src.empty = new TreeMap<>();

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

  private <T,K> void assertMapEquals(Map<T, K> src, Map<T, K> trg) {
    if (src == null){
      assertNull(trg);
    }else {
      assertNotNull(trg);

      assertEquals(
          String.format("Maps size differ. Expected. %d, actual %d.",src.size(), trg.size()),
          src.size(), trg.size());

      Set<T> srcKeys = src.keySet();
      Set<T> trgKeys = trg.keySet();

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
    Map<String, Integer> src = new HashMap<>();
    src.put("jedna", 1 );
    src.put("dva", 2);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(bos, src);

    // System.out.println(new String(bos.toByteArray()));

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

    Map<String, Integer> trg = (Map<String, Integer>) ser.deserialize(bis, HashMap.class);

    assertMapEquals(src, trg);
  }

  @Test
  public void testListInMap() {
    DataItemMaps src = new DataItemMaps();
    src.texts = new HashMap<>();

    List<String> lst;

    lst = new ArrayList<>();
    src.texts.put(0, lst);

    lst = new ArrayList<>();
    lst.add("one");
    src.texts.put(1, lst );

    lst = new ArrayList<>();
    lst.add("one");
    lst.add("two");
    src.texts.put(2, lst );

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
    Map<Number, Number> src = new HashMap<>();
    src.put((byte) 8, 8d);
    src.put(8, 8L);
    src.put(8f, 8d);

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(bos, src);

    // System.out.println(new String(bos.toByteArray()));

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

    Map<Number, Number> trg = (Map<Number, Number>) ser.deserialize(bis, HashMap.class);

    assertMapEquals(src, trg);
  }
}
