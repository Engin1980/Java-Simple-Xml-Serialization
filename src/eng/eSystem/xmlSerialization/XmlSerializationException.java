package eng.eSystem.xmlSerialization;

public class XmlSerializationException extends InternalXmlException {
  public XmlSerializationException(String message, Object ... params) {
    super(String.format(message, params));
  }

  public XmlSerializationException(Throwable cause, String message, Object ... params) {
    super(String.format(message, params), cause);
  }
}
