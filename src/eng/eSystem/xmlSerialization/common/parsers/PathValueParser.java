package eng.eSystem.xmlSerialization.common.parsers;

import eng.eSystem.xmlSerialization.supports.IValueParser;

import java.nio.file.Path;
import java.nio.file.Paths;

public class PathValueParser implements IValueParser<java.nio.file.Path> {
  @Override
  public Path parse(String value) {
    Path ret = Paths.get(value);
    return ret;
  }

  @Override
  public String format(Path value) {
    return value.toString();
  }
}