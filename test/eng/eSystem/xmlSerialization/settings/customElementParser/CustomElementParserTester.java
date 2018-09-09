package eng.esystem.xmlSerialization.settings.customElementParser;

import eng.eSystem.eXml.XDocument;
import eng.eSystem.eXml.XElement;
import eng.eSystem.xmlSerialization.XmlSettings;
import eng.eSystem.xmlSerialization.XmlSerializer;
import eng.eSystem.xmlSerialization.supports.IElementParser;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class CustomElementParserTester {

  @Test
  public void testA() {
    Calendar a = Calendar.getInstance();

    XDocument doc = new XDocument(new XElement("root"));

    XmlSettings sett = new XmlSettings();
    sett.getMeta().registerCustomParser(Calendar.class, true, new CalendarParser());
    XmlSerializer ser = new XmlSerializer(sett);

    ser.serialize(doc, a);

    System.out.println(doc.getRoot().toFullString());
  }
}

class CalendarParser implements IElementParser<Calendar> {

  @Override
  public Calendar parse(XElement element, XmlSerializer.Deserializer source) {
    String value = element.getContent();
    long milis = Long.parseLong(value);
    GregorianCalendar ret = new GregorianCalendar();
    ret.setTimeInMillis(milis);
    return ret;
  }

  @Override
  public void format(Calendar value, XElement element, XmlSerializer.Serializer source) {
    long milis = value.getTimeInMillis();
    String ret = Long.toString(milis);
    element.setContent(ret);
  }
}
