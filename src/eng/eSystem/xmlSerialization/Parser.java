package eng.eSystem.xmlSerialization;

import eng.eSystem.Tuple;
import eng.eSystem.collections.*;
import eng.eSystem.eXml.XElement;
import eng.eSystem.xmlSerialization.annotations.XmlConstructor;
import eng.eSystem.xmlSerialization.exceptions.XmlSerializationException;
import eng.eSystem.xmlSerialization.meta.Applicator;
import eng.eSystem.xmlSerialization.meta.FieldMetaInfo;
import eng.eSystem.xmlSerialization.meta.MetaManager;
import eng.eSystem.xmlSerialization.meta.TypeMetaInfo;
import eng.eSystem.xmlSerialization.supports.IElementParser;
import eng.eSystem.xmlSerialization.supports.IFactory;
import eng.eSystem.xmlSerialization.supports.IValueParser;

import java.lang.reflect.*;
import java.util.*;

import static eng.eSystem.utilites.FunctionShortcuts.coalesce;
import static eng.eSystem.utilites.FunctionShortcuts.sf;

class Parser {

  private static final Object UNSET = new Object();
  private final XmlSettings settings;
  private final XmlSerializer parent;
  private final MetaManager metaManager;
  private final Log log;

  Parser(XmlSerializer parent) {
    this.parent = parent;
    this.settings = parent.getSettings();
    //TODO the following line must be the clone
    this.metaManager = this.settings.getMetaManager();
    this.log = new Log(this.settings.getLogLevel());
  }

  public synchronized <T> T loadObject(XElement root, Class<? extends T> objectType) {
    if (root == null) {
      throw new IllegalArgumentException("Value of {el} cannot not be null.");
    }
    if (objectType == null) {
      throw new IllegalArgumentException("Value of {objectType} cannot not be null.");
    }

    Applicator app = metaManager.getEmptyElementApplicator(root.getName(), objectType);

    Class cls = objectType;
    T ret;
    ret = (T) yReadInstanceFromElement(root, app, null);

    return ret;
  }

  private Object yReadInstanceFromAttribute(XElement element, Applicator app) {
    log.increaseIndent();
    log.log(Log.LogLevel.verbose, "%s <= ... %s=\"...\" from {%s}", app.getNormalizedType().getName(), app.getName(),
        Shared.getElementInfoString(element));

    Object ret;
    String attributeName = app.getName();
    String attributeValue = element.getAttribute(attributeName);

    if (settings.getNullString().equals(attributeValue))
      ret = null;
    else {
      IValueParser customParser = app.getCustomParser(IValueParser.class);
      if (customParser != null)
        ret = this.convertValueByCustomParser(attributeValue, customParser);
      else
        ret = this.convertToType(attributeValue, app.getNormalizedType());
    }
    log.log(Log.LogLevel.verbose, "{%s}", ret);

    log.decreaseIndent();
    return ret;
  }

  private Object yReadInstanceFromElement(XElement element, Applicator app, FieldMetaInfo relativeFmi) {
    log.increaseIndent();
    log.log(Log.LogLevel.verbose, "%s <= <%s> from {%s}", app.getNormalizedType().getName(), element.getName(),
        Shared.getElementInfoString(element));

    Object ret;
    try {
      ret = yReadInstanceFromElementInner(element, app, relativeFmi);
    } catch (Exception ex) {
      throw new XmlSerializationException(
          sf("Failed to parse class '%s' from element %s",
              app.getNormalizedType().getName(), Shared.getElementInfoString(element)), ex);
    }

    log.decreaseIndent();
    return ret;
  }

  private Object yReadInstanceFromElementInner(XElement element, Applicator app, FieldMetaInfo relativeFmi) {
    Object ret;
    IValueParser locallyStoredValueParserForSpecialCases = null;

    if (settings.getNullString().equals(element.getContent()))
      ret = null;
    else {
      {
        Class realType = TypeMappingManager.tryGetCustomTypeByElement(element);
        if (realType != null)
          app.updateType(realType);
        Tuple<IValueParser, IElementParser> derivedParsers = metaManager.getCustomParsersByType(app.getOriginalType());
        app.updateParserIfRequired(derivedParsers.getA(), derivedParsers.getB());
        locallyStoredValueParserForSpecialCases = derivedParsers.getA();
      }
      IElementParser customParser = app.getCustomParser(IElementParser.class);

      if (customParser != null)
        ret = this.yReadElementUsingCustomParser(element, customParser);
      else if (TypeMappingManager.isSimpleTypeOrEnum(app.getNormalizedType()))
        ret = yReadElementToPrimitive(element, app, locallyStoredValueParserForSpecialCases);
      else if (TypeMappingManager.isInnerInstanceClass(app.getNormalizedType()))
        throw new XmlSerializationException(sf(
            "Deserialization of inner instance class (%s) is not supported.", app.getNormalizedType().getName()));
      else if (TypeMappingManager.isMap(app.getNormalizedType()))
        ret = yReadMap(element, app, relativeFmi);
      else if (TypeMappingManager.isIterable(app.getNormalizedType()))
        ret = yReadIterable(element, app, relativeFmi);
      else if (app.getNormalizedType().isArray())
        ret = yReadArray(element, app, relativeFmi);
      else
        ret = yReadClass(element, app);
    }
    return ret;
  }

  private Object yReadIterable(XElement element, Applicator app, FieldMetaInfo relativeFmi) {
    IList<XElement> children = new EList<>(element.getChildren());
    TypeMappingManager.tryRemoveTypeMapElement(children);

    Class expectedClass = TypeMappingManager.tryGetCustomTypeByElement(element);
    Class expectedItemClass = coalesce(TypeMappingManager.tryGetItemTypeByElement(element), Object.class);
    if (expectedClass == null) expectedClass = app.getNormalizedType();
    TypeMetaInfo tmi = metaManager.getTypeMetaInfo(expectedClass);

    IList lst = yReadItems(children, expectedItemClass, app, relativeFmi);

    Class retType = app.getNormalizedType();
    Object ret = yCreateObjectInstance(tmi);
    if (Set.class.isAssignableFrom(retType)) {
      Set tmp = (Set) ret;
      tmp.addAll(lst.toList());
    } else if (List.class.isAssignableFrom(retType)) {
      List tmp = (List) ret;
      tmp.addAll(lst.toList());
    } else if (ISet.class.isAssignableFrom(retType)) {
      ISet tmp = (ISet) ret;
      tmp.add(lst);
    } else if (IList.class.isAssignableFrom(retType)) {
      IList tmp = (IList) ret;
      tmp.add(lst);
    } else
      throw new XmlSerializationException("Deserialization from the 'iterable' of type " + retType.getClass().getName() + " is not supported. Try using custom parser.");

    return ret;
  }

  private Object yReadMap(XElement element, Applicator app, FieldMetaInfo relativeFmi) {
    IList<XElement> children = new EList<>(element.getChildren());
    TypeMappingManager.tryRemoveTypeMapElement(children);

    Class keyExpectedClass = TypeMappingManager.tryGetKeyItemTypeByElement(element);
    if (keyExpectedClass == null) keyExpectedClass = Object.class;

    Class valueExpectedClass = TypeMappingManager.tryGetValueItemTypeByElement(element);
    if (valueExpectedClass == null) valueExpectedClass = Object.class;

    IList<MapEntry> lst = yReadMapItems(children, keyExpectedClass, valueExpectedClass, app, relativeFmi);

    TypeMetaInfo tmi = metaManager.getTypeMetaInfo(app.getNormalizedType());
    Object ret = yCreateObjectInstance(tmi);
    if (Map.class.isAssignableFrom(tmi.getType())) {
      Map tmp = (Map) ret;
      for (MapEntry mapEntry : lst) {
        tmp.put(mapEntry.key, mapEntry.value);
      }
    } else if (IMap.class.isAssignableFrom(tmi.getType())) {
      IMap tmp = (IMap) ret;
      for (MapEntry mapEntry : lst) {
        tmp.set(mapEntry.key, mapEntry.value);
      }
    } else
      throw new XmlSerializationException("Deserialization from the map of type " + tmi.getType().getName() + " is not supported. Try using custom parser.");

    return ret;
  }

  private IList<MapEntry> yReadMapItems(IList<XElement> children, Class keyExpectedType, Class valueExpectedType,
                                        Applicator parentApp, FieldMetaInfo relativeFmi) {

    IList<MapEntry> ret = new EList();
    IList<String> elementsWithObjectWarningLogged = new EList<>();
    TypeMetaInfo parentTmi = metaManager.getTypeMetaInfo(parentApp.getNormalizedType());


    for (XElement entryElement : children) {
      if (metaManager.isIgnoredItemElement(entryElement, relativeFmi, parentTmi, true)){
        log.log(Log.LogLevel.verbose  ,"Element <%s> skipped as ignored item-element.", entryElement.getName());
        continue;
      }

      Object key;
      Object value;

      // key stuff
      {
        Applicator app = metaManager.getMapKeyApplicator2(entryElement, keyExpectedType, relativeFmi, parentTmi);

        if (app.isAttribute() == false && settings.getNullString().equals(entryElement.getChild(app.getName()).getContent()) == false && app.getNormalizedType().equals(Object.class) && elementsWithObjectWarningLogged.contains(entryElement.getName()) == false) {
          elementsWithObjectWarningLogged.add(entryElement.getName());
          log.log(
              Log.LogLevel.warning,
              "Map entry from element <%s> for key/value '%s' is deserialized as 'Object' class. Probably missing custom mapping. Full node info: %s",
              entryElement.getName(), ret.getClass().getName(), Shared.getElementInfoString(entryElement));
        }

        Object val;
        if (app.isAttribute()) {
          val = yReadInstanceFromAttribute(entryElement, app);
        } else {
          XElement element = entryElement.getChild(app.getName());
          val = yReadInstanceFromElement(element, app, relativeFmi);
        }


        key = val;
      }

      // value stuff
      {
        Applicator app = metaManager.getMapValueApplicator2(entryElement, valueExpectedType, relativeFmi, parentTmi);

        if (app.isAttribute() == false && settings.getNullString().equals(entryElement.getChild(app.getName()).getContent()) == false && app.getNormalizedType().equals(Object.class) && elementsWithObjectWarningLogged.contains(entryElement.getName()) == false) {
          elementsWithObjectWarningLogged.add(entryElement.getName());
          log.log(
              Log.LogLevel.warning,
              "Map entry from element <%s> for key/value '%s' is deserialized as 'Object' class. Probably missing custom mapping. Full node info: %s",
              entryElement.getName(), ret.getClass().getName(), Shared.getElementInfoString(entryElement));
        }

        Object val;
        if (app.isAttribute()) {
          val = yReadInstanceFromAttribute(entryElement, app);
        } else {
          XElement element = entryElement.getChild(app.getName());
          val = yReadInstanceFromElement(element, app, relativeFmi);
        }


        value = val;
      }
      MapEntry me = new MapEntry(key, value);
      ret.add(me);
    }

    return ret;
  }

  private IList yReadItems(IReadOnlyList<XElement> children, Class expectedItemType, Applicator parentApp, FieldMetaInfo relativeFmi) {
    IList ret = new EList();
    IList<String> elementsWithObjectWarningLogged = new EList<>();
    TypeMetaInfo parentTmi = metaManager.getTypeMetaInfo(parentApp.getNormalizedType());
    for (XElement itemElement : children) {
      if (metaManager.isIgnoredItemElement(itemElement, relativeFmi, parentTmi, false)){
        log.log(Log.LogLevel.verbose  ,"Element <%s> skipped as ignored item-element.", itemElement.getName());
        continue;
      }

      IList<String> elementNames = new EList();
      elementNames.add(itemElement.getName());

      Applicator app = metaManager.getItemApplicator2(itemElement, expectedItemType, relativeFmi, parentTmi);

      if (app.isAttribute() == false && settings.getNullString().equals(itemElement.getContent()) == false && app.getNormalizedType().equals(Object.class) && elementsWithObjectWarningLogged.contains(itemElement.getName()) == false) {
        elementsWithObjectWarningLogged.add(itemElement.getName());
        log.log(
            Log.LogLevel.warning,
            "List item from element <%s> for iterable '%s' is deserialized as 'Object' class. Probably missing custom iterable mapping. Full node info: %s",
            itemElement.getName(), ret.getClass().getName(), Shared.getElementInfoString(itemElement));
      }

      Object val;
      if (app.isAttribute()) {
        val = yReadInstanceFromAttribute(itemElement, app);
      } else
        val = yReadInstanceFromElement(itemElement, app, relativeFmi);

      ret.add(val);
    }
    return ret;
  }

  private Object yReadArray(XElement element, Applicator app, FieldMetaInfo relativeFmi) {

    IList<XElement> children = new EList<>(element.getChildren());
    TypeMappingManager.tryRemoveTypeMapElement(children);
    Class expectedItemType = app.getNormalizedType().getComponentType();

    IList lst = yReadItems(children, expectedItemType, app, relativeFmi);

    Object ret;
    int cnt = lst.size();

    ret = xeCreateArrayInstance(app.getNormalizedType().getComponentType(), cnt);
    for (int i = 0; i < children.size(); i++) {
      Array.set(ret, i, lst.get(i));
    }
    return ret;
  }

  private Object xeCreateArrayInstance(Class elementType, int length) {
    Object ret;
    try {
      ret = Array.newInstance(elementType, length);
    } catch (Exception ex) {
      throw new XmlSerializationException(sf(
          "Failed to create a newe instance of '%s[]'.",
          elementType.getName()), ex);
    }
    return ret;
  }

  private Object yReadClass(XElement element, Applicator app) {
    log.increaseIndent();

    TypeMetaInfo tmi = metaManager.getTypeMetaInfo(app.getNormalizedType());

    log.log(Log.LogLevel.info, sf("%s <= <%s>", tmi.getType().getName(), element.getName()));

    Object ret = yCreateObjectInstance(tmi);

    yReadClassField(element, ret, tmi);

    log.decreaseIndent();

    return ret;
  }

  private void yReadClassField(XElement el, Object trg, TypeMetaInfo tmi) {

    for (FieldMetaInfo fmi : tmi.getFields()) {
      if (fmi.getNecessity() == FieldMetaInfo.eNecessity.ignore) {
        log.log(Log.LogLevel.info, "'%s' field skipped due to @XmlIgnore annotation.", fmi.getLocation(false));
        continue;
      }
      Object tmp;
      try {
        tmp = yReadObjectFieldInstance(el, fmi);
      } catch (Exception ex) {
        throw new XmlSerializationException(sf(
            "Failed to fill field '%s' ('%s') from xml-element %s.",
            fmi.getLocation(false), fmi.getField().getType().getName(), Shared.getElementInfoString(el)), ex);
      }
      try {
        if (tmp != UNSET) {
          fmi.getField().setAccessible(true);
          fmi.getField().set(trg, tmp);
        }
      } catch (Exception ex) {
        String tmpType = tmp == null ? "null" : tmp.getClass().getName();
        throw new XmlSerializationException(sf(
            "Failed to fill field '%s' ('%s') with value '%s' ('%s') from xml-element %s.",
            fmi.getLocation(false),
            fmi.getField().getType().getName(),
            tmp, tmpType,
            Shared.getElementInfoString(el)), ex);
      }
    }
  }

  private Object yReadObjectFieldInstance(XElement element, FieldMetaInfo fmi) {
    Object ret;
    log.increaseIndent();


    try {
      Applicator app = this.metaManager.getFieldApplicator(element, fmi);

      if (app != null) {
        log.log(Log.LogLevel.info, ".%s <- %s (%s)",
            fmi.getField().getName(), app.getName(), app.isAttribute() ? "att" : "elm"
        );
        if (app.isAttribute())
          ret = yReadInstanceFromAttribute(element, app);
        else
          ret = yReadInstanceFromElement(element.getChildren(app.getName()).getFirst(), app, fmi);
      } else {
        if (fmi.getNecessity() == FieldMetaInfo.eNecessity.mandatory) {
          throw new XmlSerializationException(sf("Source for mandatory object '%s' not found. No required xml-element or attribute found.",
              fmi.getLocation(false)));
        } else {
          log.log(Log.LogLevel.info, ".%s skipped, no source found",
              fmi.getField().getName());
          ret = UNSET;
        }
      }

    } catch (Exception ex) {
      throw new XmlSerializationException(sf(
          "Failed to parse field '%s' ('%s') from element %s.",
          fmi.getLocation(false), fmi.getField().getType().getName(), Shared.getElementInfoString(element)),
          ex);
    }

    log.decreaseIndent();
    return ret;
  }

  private Object convertValueByCustomParser(String value, IValueParser parser) {
    Object ret;
    try {
      ret = parser.parse(value);
    } catch (Exception ex) {
      throw new XmlSerializationException(sf(
          "Failed to convert '%s' using '%s' custom IValueParser.",
          value, parser.getClass().getName()), ex);
    }
    return ret;
  }

  private Object yCreateObjectInstance(TypeMetaInfo tmi) {
    Object ret;
    Class type = tmi.getType();
    if (type.equals(List.class) || type.equals(AbstractList.class))
      type = ArrayList.class;
    else if (type.equals(IList.class) || type.equals(IReadOnlyList.class) || type.equals(ICollection.class))
      type = EList.class;
    else if (type.equals(ISet.class) || type.equals(IReadOnlySet.class))
      type = ESet.class;
    else if (type.equals(IMap.class))
      type = EMap.class;

    // check if there is not an custom instance creator

    IFactory customFactory;
    customFactory = settings.getFactories().tryGetFirst(q -> q.getType().equals(tmi.getType()));
    if (customFactory == null)
      customFactory = tmi.getCustomFactory();


    if (customFactory == null)
      try {
        ret = createInstanceByConstructor(type);
      } catch (Exception ex) {
        throw new XmlSerializationException(
            "Failed to create newe instance of " + type.getName() + " using constructor(s).", ex);
      }
    else {
      try {
        ret = customFactory.createInstance();
      } catch (Exception ex) {
        throw new XmlSerializationException(sf(
            "Failed to create a newe instance of '%s' using custom creator '%s'.",
            type.getName(), customFactory.getClass().getName()), ex);
      }
    }
    return ret;
  }

  private Object createInstanceByConstructor(Class<?> type) throws IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchMethodException {
    IList<Constructor> constructors = new EList<>(type.getDeclaredConstructors());

    if (constructors.isAny(q -> q.getAnnotation(XmlConstructor.class) != null))
      constructors = constructors.where(q -> q.getAnnotation(XmlConstructor.class) != null);

    int minParams = constructors.min(q -> q.getParameterCount(), Integer.MAX_VALUE);
    constructors = constructors.where(q -> q.getParameterCount() == minParams);
    if (constructors.isEmpty()) {
      throw new NoSuchMethodException();
    }
    Constructor constructor = constructors.getRandom();
    if (constructor.getParameterCount() == 0 && Modifier.isPrivate(constructor.getModifiers())) {
      System.out.println("REP:: Private parameter-less constructor used for " + type.getName());
    }
    Object[] params = new Object[constructor.getParameterCount()];
    for (int i = 0; i < constructor.getParameterCount(); i++) {
      Parameter par = constructor.getParameters()[i];
      Type parType = par.getType();
      if (parType.equals(int.class) ||
          parType.equals(Integer.class) ||
          parType.equals(byte.class) ||
          parType.equals(Byte.class) ||
          parType.equals(short.class) ||
          parType.equals(Short.class) ||
          parType.equals(long.class) ||
          parType.equals(Long.class) ||
          parType.equals(float.class) ||
          parType.equals(Float.class) ||
          parType.equals(double.class) ||
          parType.equals(Double.class))
        params[i] = 0;
      else if (parType.equals(boolean.class) ||
          parType.equals(Boolean.class))
        params[i] = false;
      else if (parType.equals(char.class) ||
          parType.equals(Character.class))
        params[i] = 'a';
      else
        params[i] = null;
    }

    Object ret;
    constructor.setAccessible(true);
    ret = constructor.newInstance(params);
    return ret;
  }

  private Object yReadElementToPrimitive(XElement el, Applicator app, IValueParser customValueParser) {
    Object ret;
    String value = el.getContent().trim();
    if (customValueParser != null)
      ret = convertValueByCustomParser(value, customValueParser);
    else
      ret = this.convertToType(value, app.getNormalizedType());
    return ret;
  }

  private Object convertToType(String value, Class<?> type) {
    Object ret;
    try {
      if (type.isEnum()) {
        ret = Enum.valueOf((Class<Enum>) type, value);
      } else {
        switch (type.getName()) {
          case "byte":
          case "java.lang.Byte":
            ret = Byte.parseByte(value);
            break;
          case "short":
          case "java.lang.Short":
            ret = Short.parseShort(value);
            break;
          case "int":
          case "java.lang.Integer":
            ret = Integer.parseInt(value);
            break;
          case "long":
          case "java.lang.Long":
            ret = Long.parseLong(value);
            break;
          case "float":
          case "java.lang.Float":
            ret = Float.parseFloat(value);
            break;
          case "double":
          case "java.lang.Double":
            ret = Double.parseDouble(value);
            break;
          case "char":
          case "java.lang.Character":
            ret = value.charAt(0);
            break;
          case "boolean":
          case "java.lang.Boolean":
            ret = Boolean.parseBoolean(value);
            break;
          case "java.lang.String":
            ret = value;
            break;
          default:
            throw new XmlSerializationException(
                sf("Type '%s' does not have primitive conversion defined. Use custom IValueParser.",
                    type.getName()));
        }
      }
    } catch (Exception ex) {
      throw new XmlSerializationException(sf("Failed to convert value '%s' into type '%s'.",
          value,
          type.getName()), ex);
    }

    return ret;
  }

  private Object yReadElementUsingCustomParser(XElement el, IElementParser customElementParser) {
    Object ret;
    try {
      ret = customElementParser.parse(el, this.parent.new Deserializer());
    } catch (Throwable ex) {
      throw new XmlSerializationException(
          sf("Failed to parse instance from %s using parser %s.",
              Shared.getElementInfoString(el),
              customElementParser.getClass().getName()), ex);
    }
    return ret;
  }
}
