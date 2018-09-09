package eng.eSystem.xmlSerialization.meta.newe;

import eng.eSystem.xmlSerialization.exceptions.XmlSerializationException;

import java.lang.reflect.Constructor;

class Shared {
  static <T> T createInstance(Class<? extends T> type){
    T ret;
    try {
      Constructor c = type.getDeclaredConstructor();
      c.setAccessible(true);
      ret = (T) c.newInstance(null);
    } catch (Exception ex) {
      throw new XmlSerializationException("Failed to create newe instance of '" + type.getName() + "' using public parameter-less constructor.", ex);
    }
    return ret;
  }
}
