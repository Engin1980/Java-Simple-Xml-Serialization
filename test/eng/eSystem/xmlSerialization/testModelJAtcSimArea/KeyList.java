package eng.eSystem.xmlSerialization.testModelJAtcSimArea;

public class KeyList<TValue extends KeyItem<TKey>, TKey> extends java.util.ArrayList<TValue> {
  public TValue tryGet(TKey key){
    for (TValue item : this){
      if (item.getKey().equals(key)){
        return item;
      }
    }
    return null;
  }

  public TValue get(TKey key){
    for (TValue item : this){
      if (item.getKey().equals(key)){
        return item;
      }
    }
    throw new RuntimeException("No such element in KeyList - key: " + key.toString());
  }
}

