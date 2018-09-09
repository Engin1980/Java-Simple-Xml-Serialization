package eng.esystem.xmlSerialization.annotations.both.iterable;

import org.junit.Assert;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Shared {
  public static <T> void assertListEquals(List<T> a, List<T> b) {
    if (a == null || b == null){
      Assert.assertNull(a);
      Assert.assertNull(b);
    } else {
      Object[] arrA = a.toArray();
      Object[] arrB = b.toArray();

      Assert.assertArrayEquals(arrA, arrB);
    }
  }

  public static <T> void assertSetEquals(Set<T> a, Set<T> b) {
    Object[] arrA = a.toArray();
    Object[] arrB = b.toArray();

    Assert.assertArrayEquals(arrA, arrB);
  }

  public static <T,V> void assertMapEquals(Map<T,V> a, Map<T,V> b) {
    Assert.assertEquals(a.size(), b.size());
    for (T aKey : a.keySet()) {
      Assert.assertTrue(b.containsKey(aKey));

      V av = a.get(aKey);
      V bv = b.get(aKey);

      Assert.assertEquals(av, bv);
    }
  }
}
