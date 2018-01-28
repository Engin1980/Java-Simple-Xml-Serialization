package eng.eSystem.xmlSerialization;

public class XmlSerializationException extends RuntimeException {
  public XmlSerializationException(String message) {
    super(message);
  }

  public XmlSerializationException(String message, Throwable cause) {
    super(message, cause);
  }

  public XmlSerializationException(String format, String ... args){
    super(String.format(format, args));
  }

  public XmlSerializationException(Throwable cause, String format, String ... args){
    super(String.format(format, args), cause);
  }
}
