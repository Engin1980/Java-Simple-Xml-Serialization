package eng.eSystem.xmlSerialization.annotations;

import eng.eSystem.xmlSerialization.supports.IElementParser;
import eng.eSystem.xmlSerialization.supports.IValueParser;

public abstract class Empty implements IElementParser, IValueParser {
  public static final String EMPTY_STRING = "EMPTY._.EMPTY_STRING";

  public static boolean isEmpty(String elementName) {
    return EMPTY_STRING.equals(elementName);
  }

  public static boolean isEmpty(Object obj){
    return Empty.class.equals(obj);
  }
}
