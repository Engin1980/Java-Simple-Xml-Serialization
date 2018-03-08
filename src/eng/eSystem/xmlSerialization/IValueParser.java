package eng.eSystem.xmlSerialization;

public interface IValueParser<T> {
  Class getType();

  T parse(String value);

  String format(T value);
}
