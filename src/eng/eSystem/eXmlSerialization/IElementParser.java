package eng.eSystem.eXmlSerialization;

public interface IElementParser<T> {
  String getTypeName();

  T parse(org.w3c.dom.Element element);
}
