package eng.eSystem.xmlSerialization;

public abstract class InternalXmlException extends Exception {

  public InternalXmlException(String message) {
    super(message);
  }

  public InternalXmlException(String message, Throwable cause) {
    super(message, cause);
  }
}
