package eng.eSystem.xmlSerialization.testModelOther;

import eng.eSystem.xmlSerialization.Settings;
import eng.eSystem.xmlSerialization.XmlListItemMapping;
import eng.eSystem.xmlSerialization.XmlSerializer;
import eng.eSystem.xmlSerialization.common.instanceCreators.AwtColorCreator;
import eng.eSystem.xmlSerialization.common.parsers.AwtFontElementParser;
import eng.eSystem.xmlSerialization.common.parsers.HexToAwtColorValueParser;
import eng.eSystem.xmlSerialization.testModelRealA.NamedColor;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class Tests {

  private static String COLOR_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<colors>\n" +
      "  <item name=\"Red\" color=\"FF0000\"/>\n" +
      "  <item name=\"Light blue\" color=\"AAAAFF\"/>\n" +
      "  <item name=\"Black with font\" color=\"AAAAFF\">\n" +
      "    <font family=\"Verdana\" style=\"0\" size=\"12\"/>\n" +
      "  </item>\n" +
      "</colors>";

  @Test
  public void fillListWithCustomCreatorAndCustomValueParser() {
    Settings settings = new Settings();

    settings.getIgnoredFieldsRegex().add("^font$");

    settings.getInstanceCreators().add(
        new AwtColorCreator()
    );

    settings.getListItemMappings().add(
        new XmlListItemMapping("colors", NamedColor.class)
    );

    settings.getValueParsers().add(
        new HexToAwtColorValueParser()
    );


    XmlSerializer ser = new XmlSerializer(settings);

    ByteArrayInputStream bis = new ByteArrayInputStream(COLOR_XML.getBytes());

    List<NamedColor> ret = (List) ser.deserialize(bis, ArrayList.class);

    assertEquals(3, ret.size());
  }

  @Test
  public void fillListWithCustomElementParser() {
    Settings settings = new Settings();

    settings.getInstanceCreators().add(
        new AwtColorCreator()
    );

    settings.getListItemMappings().add(
        new XmlListItemMapping("colors", NamedColor.class)
    );

    settings.getValueParsers().add(
        new HexToAwtColorValueParser()
    );
    settings.getElementParsers().add(
        new AwtFontElementParser()
    );


    XmlSerializer ser = new XmlSerializer(settings);

    ByteArrayInputStream bis = new ByteArrayInputStream(COLOR_XML.getBytes());

    List<NamedColor> trg = (List) ser.deserialize(bis, ArrayList.class);

    assertEquals(3, trg.size());
    assertNotNull(trg.get(2).getFont());
    assertEquals("Verdana", trg.get(2).getFont().getName());
  }
}
