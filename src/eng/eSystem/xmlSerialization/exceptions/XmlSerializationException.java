package eng.eSystem.xmlSerialization.exceptions;

public class XmlSerializationException extends RuntimeException {

  public XmlSerializationException(String message) {
    super(message);
  }

  public XmlSerializationException(String message, Throwable cause) {
    super(message, cause);
  }

  public XmlSerializationException(Throwable cause) {
    super(cause);
  }
}
