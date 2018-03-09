package eng.eSystem.xmlSerialization;

public interface IValueParser<T> {
  Class getType();

  T parse(String value) throws XmlDeserializationException;

  String format(T value) throws XmlSerializationException;
}
