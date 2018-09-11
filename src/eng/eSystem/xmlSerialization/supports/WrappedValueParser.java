package eng.eSystem.xmlSerialization.supports;

import eng.eSystem.eXml.XElement;
import eng.eSystem.xmlSerialization.XmlSerializer;

public class WrappedValueParser<T> implements IElementParser<T> {
  private final IValueParser<T> valueParser;

  public WrappedValueParser(IValueParser<T> valueParser) {
    if (valueParser == null) {
      throw new IllegalArgumentException("Value of {valueParser} cannot not be null.");
    }

    this.valueParser = valueParser;
  }

  @Override
  public T parse(XElement element, XmlSerializer.Deserializer source) {
    String s = element.getContent();
    T ret = valueParser.parse(s);
    return ret;
  }

  @Override
  public void format(T value, XElement element, XmlSerializer.Serializer source) {
    String s = valueParser.format(value);
    element.setContent(s);
  }
}
