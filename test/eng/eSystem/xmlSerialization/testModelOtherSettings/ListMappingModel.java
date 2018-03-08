package eng.eSystem.xmlSerialization.testModelOtherSettings;

import java.util.List;

public class ListMappingModel {

  public static class A {
    public int number;

    public A(int number) {
      this.number = number;
    }

    public A() {
    }
  }

  public static class B{
    public String text;

    public B(String text) {
      this.text = text;
    }

    public B() {
    }
  }

  public List<A> numbers;
  public List<B> texts;
}
