package eng.eSystem.xmlSerialization.common.parsers;

import eng.eSystem.xmlSerialization.IValueParser;
import eng.eSystem.xmlSerialization.XmlDeserializationException;
import eng.eSystem.xmlSerialization.XmlSerializationException;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathValueParser implements IValueParser<Path> {
  @Override
  public Class getType() {
    return java.nio.file.Path.class;
  }

  @Override
  public Path parse(String value) throws XmlDeserializationException {
    Path ret = Paths.get(value);
    return ret;
  }

  @Override
  public String format(Path value) throws XmlSerializationException {
    return value.toString();
  }
}
