package eng.eSystem.eXmlSerialization;

public interface IValueParser<T> {
  String getTypeName();

  T parse(String value);

  String format(T value);
}
