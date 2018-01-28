package eng.eSystem.xmlSerialization.common.parsers;

import eng.eSystem.xmlSerialization.IValueParser;
import eng.eSystem.xmlSerialization.XmlSerializationException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class JavaTimeLocalDateValueParser implements IValueParser<java.time.LocalDate> {
  
  private final java.time.format.DateTimeFormatter formatter;

  /**
   * Creates default instance with format 'yyyy-MM-dd'.
   */
  public JavaTimeLocalDateValueParser() {
    this.formatter =
      java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
  }

  /**
   * Creates instance with custom formatter.
   * @param formatter
   */
  public JavaTimeLocalDateValueParser(DateTimeFormatter formatter) {
    if (formatter == null) {
        throw new IllegalArgumentException("Value of {formatter} cannot not be null.");
    }
    
    this.formatter = formatter;
  }

  /**
   * Creates instance with custom date-time format pattern.
   * @param format
   */
  public JavaTimeLocalDateValueParser(String format) {
    if (format == null) {
        throw new IllegalArgumentException("Value of {format} cannot not be null.");
    }
    
    this.formatter = DateTimeFormatter.ofPattern(format);
  }

  @Override
  public String getTypeName() {
    return java.time.LocalDate.class.getName();
  }

  @Override
  public LocalDate parse(String value) {
    
    LocalDate ret;

    try {
      ret =
          LocalDate.parse(value, formatter);
    } catch (Exception ex){
      throw new XmlSerializationException(ex, "Failed to parse value '%s' using formatter type '%s' ('%s').",
          value,
          this.formatter.getClass().getName(),
          this.formatter.toString()
          );
    }
    
    return ret;
    
  }

  @Override
  public String format(LocalDate value) {
    String ret;
    try {
      ret = value.format(this.formatter);
    } catch (Exception ex){
      throw new XmlSerializationException(ex, "Failed to format value '%s' using formatter type '%s' ('%s').",
          value.toString(),
          this.formatter.getClass().getName(),
          this.formatter.toString()
      );
    }
    return ret;
  }
}
