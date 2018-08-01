package eng.eSystem.xmlSerialization.testConstructors;

import eng.eSystem.exceptions.ERuntimeException;
import eng.eSystem.xmlSerialization.XmlConstructor;

public class DemoC {
  public boolean ok;

  private DemoC(int a, int b){
    throw new ERuntimeException("Dont use this.");
  }
  public DemoC(){
    throw new ERuntimeException("Dont use this.");
  }

  @XmlConstructor
  public DemoC(int a, int b, int c){
    ok = true;
  }
}
