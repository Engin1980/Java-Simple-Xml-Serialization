package eng.eSystem.xmlSerialization.testModelJAtcSimArea;

import java.util.ArrayList;
import java.util.List;

public class Border {
  public enum eType{
    Country,
    TMA,
    CTR,
    Restricted,
    Other
  }

  private String name;
  private eType type;
  private final List<BorderPoint> points = new ArrayList();
  private boolean enclosed;
}
