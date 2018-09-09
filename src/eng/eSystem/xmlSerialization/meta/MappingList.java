package eng.eSystem.xmlSerialization.meta;

import eng.eSystem.collections.EList;

public class MappingList extends EList<Mapping> {
  public void removeOverlying(boolean isAttribute, String name, Class type) {
    if (name != null)
      this.remove(q -> q.isAttribute() == isAttribute && q.getName().equals(name));
    if (type != null)
      this.remove(q -> q.isAttribute() == isAttribute && q.getType().equals(type));
  }

  public MappingList(Iterable<? extends Mapping> elements) {
    super(elements);
  }
}
