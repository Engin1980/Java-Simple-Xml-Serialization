package eng.eSystem.xmlSerialization;

import eng.eSystem.eXml.XElement;

public interface IElementParser<T> {
  Class getType();

  T parse(XElement element, XmlSerializer.Deserializer source) throws XmlDeserializationException;

  void format(T value, XElement element, XmlSerializer.Serializer source) throws XmlSerializationException;

  boolean isApplicableOnDescendants();
}
