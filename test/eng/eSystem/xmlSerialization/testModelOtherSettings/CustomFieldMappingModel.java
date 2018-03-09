package eng.eSystem.xmlSerialization.testModelOtherSettings;

import java.awt.*;

public class CustomFieldMappingModel {
  public static class MyParent{
    public int number;

  }

  public static class MyChild extends MyParent{
    public String text;
  }

  public MyParent a;
  public MyParent b;
}
