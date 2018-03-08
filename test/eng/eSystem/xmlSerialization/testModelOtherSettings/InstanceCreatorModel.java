package eng.eSystem.xmlSerialization.testModelOtherSettings;

import eng.eSystem.xmlSerialization.IInstanceCreator;

public class InstanceCreatorModel {
  public int number;

  public static class InstanceCreator implements IInstanceCreator<InstanceCreatorModel>{

    @Override
    public Class getType() {
      return InstanceCreatorModel.class;
    }

    @Override
    public InstanceCreatorModel createInstance() {
      return new InstanceCreatorModel(-1);
    }
  }

  public InstanceCreatorModel(int number) {
    this.number = number;
  }
}
