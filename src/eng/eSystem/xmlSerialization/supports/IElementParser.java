package eng.eSystem.xmlSerialization.supports;

import eng.eSystem.eXml.XElement;
import eng.eSystem.xmlSerialization.XmlSerializer;

public interface IElementParser<T> extends IParser {

  T parse(XElement element, XmlSerializer.Deserializer source);

  void format(T value, XElement element, XmlSerializer.Serializer source);
}
