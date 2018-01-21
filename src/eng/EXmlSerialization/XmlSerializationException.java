package eng.EXmlSerialization;

public class XmlSerializationException extends RuntimeException {
  public XmlSerializationException(String message) {
    super(message);
  }

  public XmlSerializationException(String message, Throwable cause) {
    super(message, cause);
  }
}
