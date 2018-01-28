package eng.eSystem.xmlSerialization;

public interface IValueParser<T> {
  String getTypeName();

  T parse(String value);

  String format(T value);
}
