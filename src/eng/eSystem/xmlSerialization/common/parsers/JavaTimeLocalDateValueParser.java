package eng.eSystem.xmlSerialization.common.parsers;

import eng.eSystem.xmlSerialization.exceptions.XmlSerializationException;
import eng.eSystem.xmlSerialization.supports.IValueParser;

import java.time.LocalDate;

import static eng.eSystem.utilites.FunctionShortcuts.sf;

public class JavaTimeLocalDateValueParser implements IValueParser<java.time.LocalDate> {
  private final java.time.format.DateTimeFormatter formatter;

  /**
   * Creates default instance with format 'yyyy-MM-dd'.
   */
  public JavaTimeLocalDateValueParser() {
    this.formatter =
        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
  }

  @Override
  public LocalDate parse(String value) {
    LocalDate ret;

    try {
      ret =
          LocalDate.parse(value, formatter);
    } catch (Exception ex){
      throw new XmlSerializationException(sf("Failed to parse value '%s' using formatter type '%s' ('%s').",
          value,
          this.formatter.getClass().getName(),
          this.formatter.toString()
      ), ex);
    }

    return ret;

  }

  @Override
  public String format(LocalDate value) {
    String ret;
    try {
      ret = value.format(this.formatter);
    } catch (Exception ex){
      throw new XmlSerializationException(sf( "Failed to format value '%s' using formatter type '%s' ('%s').",
          value.toString(),
          this.formatter.getClass().getName(),
          this.formatter.toString()
      ), ex);
    }
    return ret;
  }
}