package eng.eSystem.xmlSerialization;

import eng.eSystem.Tuple;
import eng.eSystem.collections.*;
import eng.eSystem.eXml.XElement;
import eng.eSystem.exceptions.EXmlRuntimeException;
import eng.eSystem.xmlSerialization.exceptions.XmlSerializationException;
import eng.eSystem.xmlSerialization.meta.Applicator;
import eng.eSystem.xmlSerialization.meta.FieldMetaInfo;
import eng.eSystem.xmlSerialization.meta.MetaManager;
import eng.eSystem.xmlSerialization.meta.TypeMetaInfo;
import eng.eSystem.xmlSerialization.supports.IElementParser;
import eng.eSystem.xmlSerialization.supports.IValueParser;

import java.lang.reflect.Array;
import java.util.Map;

import static eng.eSystem.utilites.FunctionShortcuts.sf;

class Formatter {

  private final XmlSettings settings;
  private XmlSerializer parent;
  private RecursionDetector recursionDetector;
  private MetaManager metaManager;
  private Log log;

  public Formatter(XmlSerializer parent) {
    this.parent = parent;
    this.settings = parent.getSettings();
    //TODO the following line must be the clone
    this.metaManager = this.settings.getMetaManager();
    this.log = new Log(this.settings.getLogLevel());
  }

  public synchronized void saveObject(Object source, XElement root, boolean ignoreCustomParser) {

    this.recursionDetector = new RecursionDetector();

    Applicator app = metaManager.getEmptyElementApplicator("root", Object.class);
    if (source != null) {
      Tuple<IValueParser, IElementParser> parser = metaManager.getCustomParsersByType(source.getClass());
      app.updateParserIfRequired(parser.getA(), parser.getB());
    }

    yStoreInstanceToElement(root, source, app, null);
  }

  private void yStoreInstanceToAttribute(XElement element, Object value, Applicator app) {
    log.increaseIndent();
    String valueTypeName = value == null ? "null" : value.getClass().getName();
    log.log(Log.LogLevel.verbose, "%s (%s) => %s=\"..\" (using %s)", value, valueTypeName, element.getName(),
        app.getCustomParser() != null ? app.getCustomParser().getClass() : "default");

    String attributeName = app.getName();
    String attributeValue;
    if (value == null)
      attributeValue = settings.getNullString();
    else {
      assert value.getClass().equals(app.getNormalizedType()) || app.getCustomParser() != null;

      IValueParser customParser = app.getCustomParser(IValueParser.class);
      if (customParser != null)
        attributeValue = yGetValueFromIValueParser(value, customParser);
      else
        attributeValue = value.toString();
    }

    element.setAttribute(attributeName, attributeValue);

    log.decreaseIndent();
  }

  private void yStoreInstanceToElement(XElement element, Object value, Applicator app, FieldMetaInfo relativeFmi) {
    log.increaseIndent();
    String valueTypeName = value == null ? "null" : value.getClass().getName();
    log.log(Log.LogLevel.verbose, "%s (%s) => <%s> (using %s)", value, valueTypeName, element.getName(),
        app.getCustomParser() != null ? app.getCustomParser().getClass() : "default");
    recursionDetector.check(value);

    TypeMappingManager.addClassTypeAttribute(element,
        value == null ? Object.class : value.getClass(),
        app.getNormalizedType());

    try {
      this.yStoreInstanceToElementInner(element, value, app, relativeFmi);
    } catch (Exception ex) {
      throw new XmlSerializationException(sf("Failed to store '%s' (%s) into element %s.",
          value, valueTypeName, Shared.getElementInfoString(element)), ex);
    }

    recursionDetector.uncheck(value);
    log.decreaseIndent();
  }

  private void yStoreInstanceToElementInner(XElement element, Object value, Applicator app, FieldMetaInfo relativeFmi) {
    if (value == null) {
      element.setContent(settings.getNullString());
    } else {
      IElementParser customParser = app.getCustomParser(IElementParser.class);
      final boolean ignoreCustomParser = false;
      final Class realType = value.getClass();

      if (!ignoreCustomParser && customParser != null)
        this.yStoreInstanceToElementUsingCustomElementParser(element, value, customParser);
      else if (TypeMappingManager.isSimpleTypeOrEnum(realType))
        this.yStorePrimitiveToElement(element, value);
      else if (TypeMappingManager.isMap(realType))
        yStoreMap(element, value, relativeFmi);
      else if (TypeMappingManager.isIterable(realType))
        yStoreIterable(element, value, relativeFmi);
      else if (realType.isArray())
        yStoreArray(element, value, relativeFmi);
      else
        yStoreClass(element, value);
    }
  }

  private XElement yCreateInstanceWithElement(Object value, Applicator app, FieldMetaInfo relativeFmi) {
    XElement ret = new XElement(app.getName());
    yStoreInstanceToElement(ret, value, app, relativeFmi);
    return ret;
  }

  private void yStoreClass(XElement element, Object value) throws XmlSerializationException {
    log.increaseIndent();
    log.log(Log.LogLevel.info, sf("%s => <%s>", value.getClass().getName(), element.getName()));

    Class realValueType = value.getClass();
    TypeMetaInfo tmi = metaManager.getTypeMetaInfo(realValueType);

    for (FieldMetaInfo fmi : tmi.getFields()) {
      if (fmi.getField().getName().equals("this$0")){
        log.increaseIndent();
        log.log(Log.LogLevel.info, ".%s skipped, its inner class to outer class reference", fmi.getField().getName());
        log.decreaseIndent();
        continue; // skipped due to annotation
      }
      if (fmi.getNecessity() == FieldMetaInfo.eNecessity.ignore) {
        log.increaseIndent();
        log.log(Log.LogLevel.info, ".%s skipped due to @XmlIgnore", fmi.getField().getName());
        log.decreaseIndent();
        continue; // skipped due to annotation
      }
      try {
        yStoreClassField(element, value, fmi);
      } catch (Exception ex) {
        throw new XmlSerializationException(sf(
            "Failed to store field '%s' into the element '%s'.",
            fmi.getLocation(false),
            Shared.getElementInfoString(element)), ex);
      }
    }

    log.decreaseIndent();
  }

  private void yStoreClassField(XElement element, Object source, FieldMetaInfo fmi) {
    log.increaseIndent();

    try {
      Object value = getFieldValue(source, fmi);
      Applicator app = metaManager.getFieldApplicator(fmi, value);

      log.log(Log.LogLevel.info, ".%s -> %s (%s)",
          fmi.getField().getName(), app.getName(), app.isAttribute() ? "att" : "elm");


      if (app.isAttribute()) {
        yStoreInstanceToAttribute(element, value, app);
      } else {
        XElement newElement = yCreateInstanceWithElement(value, app, fmi);
        element.addElement(newElement);
      }

    } catch (XmlSerializationException ex) {
      throw new XmlSerializationException(sf(
          "Failed to store field '%s' ('%s') into %s.",
          fmi.getLocation(true), fmi.getField().getType().getName(), Shared.getElementInfoString(element)), ex);
    }
    log.decreaseIndent();
  }

  private void yStoreMap(XElement element, Object value, FieldMetaInfo relativeFmi) {
    TypeMetaInfo parentMapTmi = metaManager.getTypeMetaInfo(value.getClass());

    IList<MapEntry> entries = getMapEntries(value);
    Class declaredKeyType = yDeriveSuperTypeOfElements(entries.select(q -> q.key));
    Class declaredValueType = yDeriveSuperTypeOfElements(entries.select(q -> q.value));

    TypeMappingManager.addKeyTypeAttribute(element, declaredKeyType);
    TypeMappingManager.addValueTypeAttribute(element, declaredValueType);

    for (MapEntry entry : entries) {
      XElement entryElement = new XElement("entry");

      // key stuff
      {
        Object item = entry.key;
        Class declaredItemType = declaredKeyType;
        Applicator app = metaManager.getMapKeyApplicator(item, declaredItemType, parentMapTmi, relativeFmi);

        if (app.isAttribute()) {
          yStoreInstanceToAttribute(entryElement, item, app);
        } else {
          XElement itemElement = new XElement(app.getName());
          yStoreInstanceToElement(itemElement, item, app, relativeFmi);
          entryElement.addElement(itemElement);
        }
      }

      // value stuff
      {
        Object item = entry.value;
        Class declaredItemType = declaredValueType;
        Applicator app = metaManager.getMapValueApplicator(item, declaredItemType, parentMapTmi, relativeFmi);

        if (app.isAttribute()) {
          yStoreInstanceToAttribute(entryElement, item, app);
        } else {
          XElement itemElement = new XElement(app.getName());
          yStoreInstanceToElement(itemElement, item, app, relativeFmi);
          entryElement.addElement(itemElement);
        }
      }

      element.addElement(entryElement);
    }

  }

  private IList<MapEntry> getMapEntries(Object value) {
    IList<MapEntry> entries = new EList<>();

    if (value instanceof Map) {
      Map map = (Map) value;
      for (Object key : map.keySet()) {
        eng.eSystem.xmlSerialization.MapEntry me = new eng.eSystem.xmlSerialization.MapEntry(key, map.get(key));
        entries.add(me);
      }
    } else if (value instanceof IMap) {
      IMap map = (IMap) value;
      for (Object key : map.getKeys()) {
        eng.eSystem.xmlSerialization.MapEntry me = new eng.eSystem.xmlSerialization.MapEntry(key, map.get(key));
        entries.add(me);
      }
    }
    return entries;
  }

  private void yStoreIterable(XElement element, Object value, FieldMetaInfo relativeFmi) {
    IList items = new EList();
    Iterable iterable = (Iterable) value;
    items.add(iterable);

    TypeMetaInfo iterableTmi = metaManager.getTypeMetaInfo(value.getClass());

    Class declaredItemType = yDeriveSuperTypeOfElements(items);
    TypeMappingManager.addItemTypeAttribute(element, declaredItemType);

    yStoreIterableItems(element, items, declaredItemType, iterableTmi, relativeFmi);
  }

  private void yStoreIterableItems(XElement element, Iterable items, Class declaredItemType, TypeMetaInfo parentIterableTmi, FieldMetaInfo relativeFmi) {
    for (Object item : items) {

      Applicator app = metaManager.getItemApplicator(item, declaredItemType, parentIterableTmi, relativeFmi);

      XElement itemElement = new XElement(app.getName());
      if (app.isAttribute()) {
        yStoreInstanceToAttribute(itemElement, item, app);
      } else {
        yStoreInstanceToElement(itemElement, item, app, relativeFmi);
      }
      element.addElement(itemElement);
    }
  }

  private Class yDeriveSuperTypeOfElements(IList value) {
    Class ret;
    ISet<Class> topTypes = new ESet<>();
    topTypes.add(value
        .where(q -> q != null)
        .select(q -> q.getClass()));

    if (topTypes.isEmpty())
      ret = Object.class;
    else {
      ret = topTypes.getFirst();
      //TODO This can really be done in some better way
      for (Class topType : topTypes) {
        ret = yGetBestParent(ret, topType);
      }
    }
    return ret;
  }

  private Class yGetBestParent(Class a, Class b) {
    Class ret;
    if (a.equals(b))
      ret = a;
    else {
      if (a.isAssignableFrom(b))
        ret = a;
      else if (b.isAssignableFrom(a))
        ret = b;
      else {
        ret = getBestSuperClass(a, b);
        if (ret.equals(Object.class)) {
          Class tmp = tryGetSomeSuperInterface(a, b);
          if (tmp != null) ret = tmp;
        }
      }
    }
    return ret;
  }

  private Class tryGetSomeSuperInterface(Class a, Class b) {
    log.log(Log.LogLevel.info, "Formatter.tryGetSomeSuperInterface(...) not supported.");
    return null;
  }

  private Class getBestSuperClass(Class a, Class b) {
    Class ret;
    while (true) {
      a = a.getSuperclass();
      if (a.isAssignableFrom(b)) {
        ret = a;
        break;
      }
    }
    return ret;
  }

  private String yGetValueFromIValueParser(Object value, IValueParser parser) throws XmlSerializationException {
    String s;
    try {
      s = parser.format(value);
    } catch (Exception ex) {
      throw new XmlSerializationException(sf(
          "Failed to format value '%s' (%s) by custom value parser '%s'.",
          value.toString(), value.getClass().getName(), parser.getClass().getName()), ex);
    }

    return s;
  }

  private Object getFieldValue(Object sourceObject, FieldMetaInfo fmi) throws XmlSerializationException {
    Object value;
    try {
      fmi.getField().setAccessible(true);
      value = fmi.getField().get(sourceObject);
      fmi.getField().setAccessible(false);
    } catch (IllegalAccessException ex) {
      throw new XmlSerializationException(sf(
          "Failed to read value of field '%s'.",
          fmi.getLocation(false)), ex);
    }
    return value;
  }

  private void yStoreArray(XElement element, Object value, FieldMetaInfo relativeFmi) throws XmlSerializationException {
    int cnt = Array.getLength(value);
    Class itemType = value.getClass().getComponentType();
    TypeMetaInfo parentIterableTmi = metaManager.getTypeMetaInfo(value.getClass());

    IList items = new EList();
    for (int i = 0; i < cnt; i++) {
      Object item = Array.get(value, i);
      items.add(item);
    }

    yStoreIterableItems(element, items, itemType, parentIterableTmi, relativeFmi);
  }

  private void yStorePrimitiveToElement(XElement element, Object value) {
    element.setContent(value.toString());
  }

  private void yStoreInstanceToElementUsingCustomElementParser(XElement el, Object value, IElementParser parser) {
    try {
      parser.format(value, el, parent.new Serializer());
    } catch (Exception ex) {
      throw new XmlSerializationException(sf(
          "Failed to format value '%s' (%s) by custom element parser '%s'.",
          value.toString(), value.getClass().getName(), parser.getClass().getName()), ex);
    }
  }

}

class RecursionDetector {

  private static final int MAX_CHECK_COUNT = 5;
  private IMap<Object, Integer> map = new EMap<>();

  public void check(Object item) {
    if (item == null) {
      return;
    } else {
      if (map.containsKey(item)) {
        Integer val = map.get(item) + 1;
        map.set(item, val);
        if (val >= MAX_CHECK_COUNT)
          throw new EXmlRuntimeException("Infinite recursive call over object {" + item.getClass().getName() + "}: " + item.toString());
      } else
        map.set(item, 1);
    }
  }

  public void uncheck(Object item) {
    if (item == null)
      return;
    else {
      Integer val = map.get(item) - 1;
      map.set(item, val);
    }
  }
}

class MapEntry {
  Object key;
  Object value;

  public MapEntry(Object key, Object value) {
    this.key = key;
    this.value = value;
  }
}
