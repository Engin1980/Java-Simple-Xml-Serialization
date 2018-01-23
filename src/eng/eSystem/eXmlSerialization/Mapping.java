package eng.eSystem.eXmlSerialization;

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
    simpleTypes.add(Double.class);
    simpleTypes.add(double.class);
    simpleTypes.add(Boolean.class);
    simpleTypes.add(boolean.class);
    simpleTypes.add(String.class);
    simpleTypes.add(char.class);
    simpleTypes.add(Character.class);
  }

  static boolean isSimpleTypeOrEnum(Class c) {
    return  simpleTypes.contains(c) || c.isEnum();
  }

}
