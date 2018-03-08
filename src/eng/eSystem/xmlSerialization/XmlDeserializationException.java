package eng.eSystem.xmlSerialization;

import sun.rmi.transport.ObjectTable;

public class XmlDeserializationException extends RuntimeException {
  public XmlDeserializationException(String message, Object ... params) {
    super(String.format(message, params));
  }

  public XmlDeserializationException(Throwable cause, String message, Object ... params) {
    super(String.format(message, params), cause);
  }
}
