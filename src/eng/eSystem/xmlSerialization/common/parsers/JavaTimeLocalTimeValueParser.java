package eng.eSystem.xmlSerialization.common.parsers;

import eng.eSystem.xmlSerialization.exceptions.XmlSerializationException;
import eng.eSystem.xmlSerialization.supports.IValueParser;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class JavaTimeLocalTimeValueParser implements IValueParser<java.time.LocalTime> {

  private static final String HOUR_MINUTE_FORMAT = "H:mm";
  private static final String HOUR_MINUTE_SECOND_FORMAT = "H:mm:ss";
  private final String customFormat;

  public JavaTimeLocalTimeValueParser(String customFormat) {
    this.customFormat = customFormat;
  }

  public JavaTimeLocalTimeValueParser() {
    this.customFormat = null;
  }

  @Override
  public LocalTime parse(String value) {
    LocalTime ret;
    DateTimeFormatter dtf;
    if (customFormat != null)
      dtf = DateTimeFormatter.ofPattern(customFormat);
    else {
      if (value.length() < 6) // HH:MM
        dtf = DateTimeFormatter.ofPattern(HOUR_MINUTE_FORMAT);
      else
        dtf = DateTimeFormatter.ofPattern(HOUR_MINUTE_SECOND_FORMAT);
    }
    try{
      ret = LocalTime.parse(value, dtf);
    }catch (Exception ex){
      throw new XmlSerializationException("Unable to deserialize value of local time from " + value + ".", ex);
    }
    return ret;
  }

  @Override
  public String format(LocalTime value) {
    String ret;
    DateTimeFormatter dtf;
    if (customFormat != null)
      dtf = DateTimeFormatter.ofPattern(customFormat);
    else {
      if (value.getSecond() == 0)
        dtf = DateTimeFormatter.ofPattern(HOUR_MINUTE_FORMAT);
      else
        dtf = DateTimeFormatter.ofPattern(HOUR_MINUTE_SECOND_FORMAT);
    }
    ret = value.format(dtf);
    return ret;
  }
}
