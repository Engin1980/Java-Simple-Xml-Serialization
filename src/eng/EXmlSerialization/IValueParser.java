package eng.EXmlSerialization;

public interface IValueParser<T> {
  String getTypeName();

  T parse(String value);
}
