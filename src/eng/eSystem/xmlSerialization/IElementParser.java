package eng.eSystem.xmlSerialization;

public interface IElementParser<T> {
  Class getType();

  T parse(org.w3c.dom.Element element) throws XmlDeserializationException;

  void format(T value, org.w3c.dom.Element element) throws XmlSerializationException;
}
