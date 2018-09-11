package eng.eSystem.xmlSerialization.meta;

import eng.eSystem.collections.EList;
import eng.eSystem.collections.IList;
import eng.eSystem.collections.IReadOnlyList;

public class ItemIgnoreList {
  private IList<ItemIgnoreElement> elements = new EList<>();
  private IList<ItemIgnoreType> types = new EList<>();

  public ItemIgnoreList(Iterable<? extends ItemIgnore> elements) {
    for (ItemIgnore element : elements) {
      this.add(element);
    }
  }

  public IReadOnlyList<ItemIgnoreElement> getElements(){
    return elements;
  }

  public IReadOnlyList<ItemIgnoreType> getTypes(){
    return types;
  }

  public void add(ItemIgnore element) {
    if (element instanceof ItemIgnoreElement)
      this.elements.add((ItemIgnoreElement) element);
    else if (element instanceof ItemIgnoreType)
      this.types.add((ItemIgnoreType) element);
  }
}
