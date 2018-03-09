package eng.eSystem.xmlSerialization;

public class XmlException extends RuntimeException {
  public XmlException(InternalXmlException cause) {
    super("XML de/serialization failed.", cause);
  }
}
