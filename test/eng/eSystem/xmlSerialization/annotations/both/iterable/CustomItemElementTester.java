package eng.esystem.xmlSerialization.annotations.both.iterable;

import eng.eSystem.collections.EList;
import eng.eSystem.eXml.XDocument;
import eng.eSystem.eXml.XElement;
import eng.eSystem.exceptions.ERuntimeException;
import eng.eSystem.xmlSerialization.XmlSerializer;
import eng.eSystem.xmlSerialization.annotations.XmlItemElement;
import eng.eSystem.xmlSerialization.supports.IElementParser;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

public class CustomItemElementTester {

  @Test
  public void GlobalItemElements() {

    Items a = new Items();
    a.add(new B());
    a.add(new C());

    XDocument doc = new XDocument(new XElement("root"));
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(doc, a);

//    System.out.println(doc.getRoot().toFullString());

    Items b = ser.deserialize(doc, Items.class);

    assertTrue(doc.getRoot().toFullString().contains("iterable.C"));
    Shared.assertListEquals(a.toList(), b.toList());
  }

  @Test
  public void GlobalItemElementsInherited() {

    Items a = new Items();
    a.add(new B());
    a.add(new D());

    XDocument doc = new XDocument(new XElement("root"));
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(doc, a);

//    System.out.println(doc.getRoot().toFullString());

    Items b = ser.deserialize(doc, Items.class);

    assertTrue(doc.getRoot().toFullString().contains("iterable.D"));
    Shared.assertListEquals(a.toList(), b.toList());
  }

  @Test
  public void GlobalItemElementsInherited2() {

    Items a = new Items();
    a.add(new B());
    a.add(new E());

    XDocument doc = new XDocument(new XElement("root"));
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(doc, a);

//    System.out.println(doc.getRoot().toFullString());

    Items b = ser.deserialize(doc, Items.class);

    assertTrue(doc.getRoot().toFullString().contains("cucflek"));
    Shared.assertListEquals(a.toList(), b.toList());
  }

  @Test
  public void GlobalItemElementsInheritedNotAllowedOnDescendants() {

    Items a = new Items();
    a.add(new B());
    a.add(new F());

    XDocument doc = new XDocument(new XElement("root"));
    XmlSerializer ser = new XmlSerializer();

    ser.serialize(doc, a);

//    System.out.println(doc.getRoot().toFullString());

    Items b = ser.deserialize(doc, Items.class);

    assertFalse(doc.getRoot().toFullString().contains("cucflek"));
    Shared.assertListEquals(a.toList(), b.toList());
  }

}

@XmlItemElement(type = B.class, elementName = "B")
@XmlItemElement(type = C.class, elementName = "C", subclassesIncluded = true, parser = CElementParser.class)
@XmlItemElement(type = E.class, elementName = "E", parser = EElementParser.class)
class Items extends EList<A> {

}

class A {

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return this.getClass().equals(obj.getClass());
  }
}

class B extends A {
}

class C extends A {
}

class D extends C {

}

class E extends A {

}

class F extends E {

}

class CElementParser implements IElementParser<C> {

  @Override
  public C parse(XElement element, XmlSerializer.Deserializer source) {
    String clsName = element.getContent();
    Class cls;
    C ret;
    try {
      cls = Class.forName(clsName);
      Constructor constructor = cls.getDeclaredConstructor(new Class[0]);
      ret = (C) constructor.newInstance(new Object[0]);
    } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new ERuntimeException("CustomParserFail:ů);");
    }
    return ret;
  }

  @Override
  public void format(C value, XElement element, XmlSerializer.Serializer source) {
    element.setContent(value.getClass().getName());
  }

}


class EElementParser implements IElementParser<E> {

  @Override
  public E parse(XElement element, XmlSerializer.Deserializer source) {
    return new E();
  }

  @Override
  public void format(E value, XElement element, XmlSerializer.Serializer source) {
    element.setContent("cucflek");
  }

}
