package eng.eSystem.xmlSerialization.testModelSimple;

public class Dimension extends java.awt.Dimension {
  public int depth;

  public Dimension(int width, int height, int depth) {
    super(width,height);
    this.depth = depth;
  }

  public Dimension() {
  }
}
