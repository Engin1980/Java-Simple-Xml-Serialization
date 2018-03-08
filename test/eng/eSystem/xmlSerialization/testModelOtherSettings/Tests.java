package eng.eSystem.xmlSerialization.testModelOtherSettings;

import eng.eSystem.xmlSerialization.Settings;
import eng.eSystem.xmlSerialization.XmlListItemMapping;
import eng.eSystem.xmlSerialization.XmlSerializer;
import org.junit.Test;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class Tests {

  @Test
  public void testInstanceCreator() {
    InstanceCreatorModel src = new InstanceCreatorModel(10);

    Settings sett = new Settings();
    sett.getInstanceCreators().add(new InstanceCreatorModel.InstanceCreator());

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    XmlSerializer ser = new XmlSerializer(sett);

    ser.serialize(bos, src);

    // System.out.println(new String(bos.toByteArray()));

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

    InstanceCreatorModel trg = (InstanceCreatorModel) ser.deserialize(bis, InstanceCreatorModel.class);

    assertEquals(src.number, trg.number);
  }

  @Test
  public void testIValueParser() {
    IValueParserModel src = new IValueParserModel();
    src.dimension = new Dimension(300,200);

    Settings sett = new Settings();
    sett.getValueParsers().add(new IValueParserModel.DimensionValueParser());

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    XmlSerializer ser = new XmlSerializer(sett);

    ser.serialize(bos, src);

    // System.out.println(new String(bos.toByteArray()));

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

    IValueParserModel trg = (IValueParserModel) ser.deserialize(bis, IValueParserModel.class);

    assertEquals(src.dimension, trg.dimension);
  }

  @Test
  public void testIElementParser() {
    Dimension src = new Dimension(300,200);

    Settings sett = new Settings();
    sett.getElementParsers().add(new IElementParserModel());

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    XmlSerializer ser = new XmlSerializer(sett);

    ser.serialize(bos, src);

    // System.out.println(new String(bos.toByteArray()));

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

    Dimension  trg = (Dimension ) ser.deserialize(bis, Dimension .class);

    assertEquals(src.width, trg.width);
    assertEquals(src.height, trg.height);
  }

  @Test
  public void testListElementMapping() {
    ListMappingModel src = new ListMappingModel();
    src.numbers = new ArrayList<>();
    src.numbers.add(new ListMappingModel.A(1));
    src.numbers.add(new ListMappingModel.A(2));

    src.texts = new LinkedList<>();
    src.texts.add(new ListMappingModel.B("Ahoj"));
    src.texts.add(new ListMappingModel.B("Nazdar"));


    Settings sett = new Settings();
    sett.getListItemMappings().add(
        new XmlListItemMapping("/numbers", "acka", ListMappingModel.A.class));
    sett.getListItemMappings().add(
        new XmlListItemMapping("/texts", "bcka", ListMappingModel.B.class));

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    XmlSerializer ser = new XmlSerializer(sett);

    ser.serialize(bos, src);

    // System.out.println(new String(bos.toByteArray()));

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

    ListMappingModel  trg = (ListMappingModel) ser.deserialize(bis, ListMappingModel.class);

    assertEquals(src.numbers.size(), trg.numbers.size());
    assertEquals(src.texts.size(), trg.texts.size());
    for (int i = 0; i < src.numbers.size(); i++) {
      assertEquals(src.numbers.get(i).number, trg.numbers.get(i).number);
    }
    for (int i = 0; i < src.texts.size(); i++) {
      assertEquals(src.texts.get(i).text, trg.texts.get(i).text);
    }
  }

  @Test
  public void testListElementMappingOwnXML() {
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
        "<root>\n" +
        "  <numbers>\n" +
        "    <acka number=\"1\"/>\n" +
        "    <acka number=\"2\"/>\n" +
        "  </numbers>\n" +
        "  <texts>\n" +
        "    <bcka text=\"Ahoj\"/>\n" +
        "    <bcka text=\"Nazdar\"/>\n" +
        "  </texts>\n" +
        "</root>";


    ListMappingModel src = new ListMappingModel();
    src.numbers = new ArrayList<>();
    src.numbers.add(new ListMappingModel.A(1));
    src.numbers.add(new ListMappingModel.A(2));

    src.texts = new LinkedList<>();
    src.texts.add(new ListMappingModel.B("Ahoj"));
    src.texts.add(new ListMappingModel.B("Nazdar"));


    Settings sett = new Settings();
    sett.getListItemMappings().add(
        new XmlListItemMapping("/numbers", "acka", ListMappingModel.A.class));
    sett.getListItemMappings().add(
        new XmlListItemMapping("/texts", "bcka", ListMappingModel.B.class));

    XmlSerializer ser = new XmlSerializer(sett);

    ByteArrayInputStream bis = new ByteArrayInputStream(xml.getBytes());

    ListMappingModel  trg = (ListMappingModel) ser.deserialize(bis, ListMappingModel.class);

    assertEquals(src.numbers.size(), trg.numbers.size());
    assertEquals(src.texts.size(), trg.texts.size());
    for (int i = 0; i < src.numbers.size(); i++) {
      assertEquals(src.numbers.get(i).number, trg.numbers.get(i).number);
    }
    for (int i = 0; i < src.texts.size(); i++) {
      assertEquals(src.texts.get(i).text, trg.texts.get(i).text);
    }
  }

  @Test
  public void textIgnoredFields() {
    IgnoredFieldsModel src = new IgnoredFieldsModel();
    src.ignoredText = "nothing";
    src.otherIgnoredText = "another nothing";
    src.text = "something";
    src.notOtherIgnoredText = "another something";


    Settings sett = new Settings();
    sett.getIgnoredFieldsRegex().add("^((ignored)|(.{5}Ignored))");

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    XmlSerializer ser = new XmlSerializer(sett);

    ser.serialize(bos, src);

    // System.out.println(new String(bos.toByteArray()));

    ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());

    IgnoredFieldsModel  trg = (IgnoredFieldsModel ) ser.deserialize(bis, IgnoredFieldsModel.class);

    assertEquals(src.text, trg.text);
    assertEquals(src.notOtherIgnoredText, trg.notOtherIgnoredText);
    assertNull(trg.ignoredText);
    assertNull(trg.otherIgnoredText);
  }


}
