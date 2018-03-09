package eng.eSystem.xmlSerialization.testModelJAtcSimArea;

public class Runway implements KeyItem<String> {
  private final KeyList<RunwayThreshold, String> thresholds = new KeyList();
  private boolean active;

  private Airport parent;

  @Override
  public String getKey() {
    return getName();
  }

  public String getName(){
    return getThresholdA().getName() + "-" + getThresholdB().getName();
  }

  public RunwayThreshold getThresholdA(){
    return thresholds.get(0);
  }

  public RunwayThreshold getThresholdB(){
    return thresholds.get(1);
  }
}
