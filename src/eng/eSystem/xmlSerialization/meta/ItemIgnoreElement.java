package eng.eSystem.xmlSerialization.meta;

public class ItemIgnoreElement extends ItemIgnore {
  private String elementName;

  public ItemIgnoreElement(String elementName) {
    this.elementName = elementName;
  }

  public String getElementName() {
    return elementName;
  }
}
