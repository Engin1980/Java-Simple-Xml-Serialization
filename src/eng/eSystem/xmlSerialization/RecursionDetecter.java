package eng.eSystem.xmlSerialization;

import eng.eSystem.collections.EMap;
import eng.eSystem.collections.IMap;
import eng.eSystem.exceptions.EXmlRuntimeException;

public class RecursionDetecter {

  private static final int MAX_CHECK_COUNT = 5;
  private IMap<Object, Integer> map = new EMap<>();

  public void check(Object item) {
    if (item == null) {
      return;
    } else {
      if (map.containsKey(item)) {
        Integer val = map.get(item) + 1;
        map.set(item, val);
        if (val >= MAX_CHECK_COUNT)
          throw new EXmlRuntimeException("Infinite recursive call over object {" + item.getClass().getName() + "}: " + item.toString());
      } else
        map.set(item, 1);
    }
  }

  public void uncheck(Object item){
    if (item == null)
      return;
    else {
      Integer val = map.get(item) - 1;
      map.set(item, val);
    }
  }
}
