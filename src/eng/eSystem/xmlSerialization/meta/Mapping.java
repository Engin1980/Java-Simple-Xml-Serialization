package eng.eSystem.xmlSerialization.meta;

import eng.eSystem.collections.IList;
import eng.eSystem.collections.IReadOnlyList;
import eng.eSystem.xmlSerialization.annotations.*;
import eng.eSystem.xmlSerialization.supports.IParser;

public final class Mapping {
  public static Mapping createCustom(String name, Class type, boolean isTypeSubtypeIncluded, boolean isAttribute, IParser parser) {
    Mapping ret = new Mapping();
    ret.name = name;
    ret.type = type;
    ret.attribute = isAttribute;
    ret.subtypeIncluded = isTypeSubtypeIncluded;
    ret.parser = parser;
    return ret;
  }

  private String name;
  private Class type;
  private boolean attribute;
  private boolean subtypeIncluded;
  private IParser parser;

  public static Mapping create(XmlElement item) {
    Mapping ret = new Mapping();
    ret.name = Empty.isEmpty(item.elementName()) ? null : item.elementName();
    ret.type = Empty.isEmpty(item.type()) ? null : item.type();
    ret.attribute = false;
    ret.parser = Empty.isEmpty(item.parser()) ? null : Shared.createInstance(item.parser());
    ret.subtypeIncluded = item.subclassesIncluded();
    return ret;
  }

  public static Mapping create(XmlAttribute item) {
    Mapping ret = new Mapping();
    ret.name = Empty.isEmpty(item.attributeName()) ? null : item.attributeName();
    ret.type = Empty.isEmpty(item.type()) ? null : item.type();
    ret.attribute = true;
    ret.parser = Empty.isEmpty(item.parser()) ? null : Shared.createInstance(item.parser());
    ret.subtypeIncluded = item.subclassesIncluded();
    return ret;
  }

  public static Mapping create(XmlItemElement item) {
    Mapping ret = new Mapping();
    ret.name = Empty.isEmpty(item.elementName()) ? null : item.elementName();
    ret.type = Empty.isEmpty(item.type()) ? null : item.type();
    ret.attribute = false;
    ret.parser = Empty.isEmpty(item.parser()) ? null : Shared.createInstance(item.parser());
    ret.subtypeIncluded = item.subclassesIncluded();
    return ret;
  }

  public static Mapping create(XmlItemAttribute item) {
    Mapping ret = new Mapping();
    ret.name = Empty.isEmpty(item.attributeName()) ? null : item.attributeName();
    ret.type = Empty.isEmpty(item.type()) ? null : item.type();
    ret.attribute = true;
    ret.parser = Empty.isEmpty(item.parser()) ? null : Shared.createInstance(item.parser());
    ret.subtypeIncluded = item.subclassesIncluded();
    return ret;
  }

  public static Mapping create(XmlMapKeyElement item) {
    Mapping ret = new Mapping();
    ret.name = Empty.isEmpty(item.elementName()) ? null : item.elementName();
    ret.type = Empty.isEmpty(item.type()) ? null : item.type();
    ret.attribute = false;
    ret.parser = Empty.isEmpty(item.parser()) ? null : Shared.createInstance(item.parser());
    ret.subtypeIncluded = item.subclassesIncluded();
    return ret;
  }

  public static Mapping create(XmlMapKeyAttribute item) {
    Mapping ret = new Mapping();
    ret.name = Empty.isEmpty(item.attributeName()) ? null : item.attributeName();
    ret.type = Empty.isEmpty(item.type()) ? null : item.type();
    ret.attribute = true;
    ret.parser = Empty.isEmpty(item.parser()) ? null : Shared.createInstance(item.parser());
    ret.subtypeIncluded = item.subclassesIncluded();
    return ret;
  }

  public static Mapping create(XmlMapValueElement item) {
    Mapping ret = new Mapping();
    ret.name = Empty.isEmpty(item.elementName()) ? null : item.elementName();
    ret.type = Empty.isEmpty(item.type()) ? null : item.type();
    ret.attribute = false;
    ret.parser = Empty.isEmpty(item.parser()) ? null : Shared.createInstance(item.parser());
    ret.subtypeIncluded = item.subclassesIncluded();
    return ret;
  }

  public static Mapping create(XmlMapValueAttribute item) {
    Mapping ret = new Mapping();
    ret.name = Empty.isEmpty(item.attributeName()) ? null : item.attributeName();
    ret.type = Empty.isEmpty(item.type()) ? null : item.type();
    ret.attribute = true;
    ret.parser = Empty.isEmpty(item.parser()) ? null : Shared.createInstance(item.parser());
    ret.subtypeIncluded = item.subclassesIncluded();
    return ret;
  }

  public static <T extends Mapping> T tryGetBestByType(IReadOnlyList<T> lst, Class type) {
    int dist = -1;
    T ret = null;
    T universalRet = null;
    for (T t : lst) {
      if (t.getType() == null)
        universalRet = t;
      else {
        int tmp = getDistanceToType(t.getType(), type);
        if (tmp != -1) {
          if (t.isSubtypeIncluded() == false && tmp > 0) continue;
          if (dist == -1 || tmp < dist) {
            dist = tmp;
            ret = t;
          }
        }
      }
    }
    if (ret == null) ret = universalRet;
    return ret;
  }

  public static Mapping tryGetBestByName(IReadOnlyList<Mapping> maps, IList<String> attributeNames, IList<String> elementNames) {
    Mapping ret;

    ret = maps.tryGetFirst(q->attributeNames.contains(q.getName()));
    if (ret == null)
      ret = maps.tryGetFirst(q->elementNames.contains(q.getName()));
    if (ret == null)
      ret = maps.tryGetFirst(q->q.getName() == null);
    return ret;
  }

  public static Mapping createDefault(String name, Class type, boolean isAttribute) {
    assert name != null;
    assert type != null;

    Mapping ret = new Mapping();
    ret.name = name;
    ret.type = type;
    ret.parser = null;
    ret.attribute = isAttribute;
    ret.subtypeIncluded = false;
    return ret;
  }

  private static int getDistanceToType(Class child, Class parent) {
    if (parent == null) {
      return 0;
    }

    int ret = 0;
    while (parent != null) {
      if (child.equals(parent)) {
        break;
      } else {
        parent = parent.getSuperclass();
        ret++;
      }
    }
    if (parent == null)
      ret = -1;
    return ret;
  }



  private Mapping() {
  }

  public Mapping(String name, Class type, boolean attribute, boolean subtypeIncluded, IParser parser) {
    this.name = name;
    this.type = type;
    this.attribute = attribute;
    this.subtypeIncluded = subtypeIncluded;
    this.parser = parser;
  }

  public boolean isAttribute() {
    return attribute;
  }

  public boolean isElement() {
    return !attribute;
  }

  public boolean isSubtypeIncluded() {
    return subtypeIncluded;
  }

  public IParser getCustomParser() {
    return parser;
  }

  public <T extends IParser> T getCustomParser(Class<? extends T> cls) {
    return (T) parser;
  }

  public String getName() {
    return name;
  }

  public Class getType() {
    return type;
  }
}
