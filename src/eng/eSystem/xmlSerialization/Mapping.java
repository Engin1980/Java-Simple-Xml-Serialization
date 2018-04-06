package eng.eSystem.xmlSerialization;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Marek
 */
class Mapping {
  private final static List<Class> simpleTypes = new ArrayList();

  static {
    simpleTypes.add(Integer.class);
    simpleTypes.add(int.class);
    simpleTypes.add(Long.class);
    simpleTypes.add(long.class);
    simpleTypes.add(Double.class);
    simpleTypes.add(double.class);
    simpleTypes.add(Boolean.class);
    simpleTypes.add(boolean.class);
    simpleTypes.add(String.class);
    simpleTypes.add(char.class);
    simpleTypes.add(Character.class);
    simpleTypes.add(Number.class);
    simpleTypes.add(byte.class);
    simpleTypes.add(Byte.class);
    simpleTypes.add(short.class);
    simpleTypes.add(Short.class);
    simpleTypes.add(float.class);
    simpleTypes.add(Float.class);
  }

  static boolean isSimpleTypeOrEnum(Class c) {
    return  simpleTypes.contains(c) || c.isEnum();
  }

}
