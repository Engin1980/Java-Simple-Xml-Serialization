package eng.eSystem.xmlSerialization;

import eng.eSystem.eXml.XElement;

public interface IElementParser<T> {
  Class getType();

  T parse(XElement element) throws XmlDeserializationException;

  void format(T value, XElement element) throws XmlSerializationException;
}
