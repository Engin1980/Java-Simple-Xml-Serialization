package eng.eSystem.xmlSerialization.testModelOtherSettings;

import eng.eSystem.xmlSerialization.IValueParser;

import java.awt.*;

public class IValueParserModel {

  public static class DimensionValueParser implements IValueParser<Dimension>{

    @Override
    public Class getType() {
      return Dimension.class;
    }

    @Override
    public Dimension parse(String value) {
      String [] val = value.split(";");
      Dimension ret = new Dimension(
        Integer.parseInt(val[0]),
        Integer.parseInt(val[1])
      );
      return ret;
    }

    @Override
    public String format(Dimension value) {
      return String.format("%s;%s", value.width, value.height);
    }
  }

  public Dimension dimension;
}
