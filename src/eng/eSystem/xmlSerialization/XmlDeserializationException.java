package eng.eSystem.xmlSerialization;

public class XmlDeserializationException extends InternalXmlException {
  public XmlDeserializationException(String message, Object ... params) {
    super(String.format(message, params));
  }

  public XmlDeserializationException(Throwable cause, String message, Object ... params) {
    super(String.format(message, params), cause);
  }
}
