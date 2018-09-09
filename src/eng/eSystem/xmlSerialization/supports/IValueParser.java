package eng.eSystem.xmlSerialization.supports;

public interface IValueParser<T> extends IParser {
  T parse(String value) ;

  String format(T value);
}
