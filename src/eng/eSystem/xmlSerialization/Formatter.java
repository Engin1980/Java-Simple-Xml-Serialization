package eng.eSystem.xmlSerialization;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.List;

import static eng.eSystem.xmlSerialization.Shared.getElementXPath;

class Formatter {
  private final Settings settings;

  public Formatter(Settings settings) {
    this.settings = settings;
  }

  public Document saveObject(Object source) {
    Document doc;
    try {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      doc = dBuilder.newDocument();

      Element el = doc.createElement("root");
      doc.appendChild(el);
      storeObject(source, el);

    } catch (ParserConfigurationException ex) {
      throw new XmlSerializationException("Failed to create w3c Document. Internal application error.", ex);
    }

    return doc;
  }

  private void storeObject(Object sourceObject, Element el) {
    if (sourceObject == null) {
      el.setTextContent(settings.getNullString());
      return;
    }

    Class c = sourceObject.getClass();
    Field[] fields = Shared.getDeclaredFields(c);

    for (Field f : fields) {
      if (java.lang.reflect.Modifier.isStatic(f.getModifiers())) {
        continue; // statické přeskakujem
      } else if (f.getAnnotation(XmlIgnore.class) != null) {
        if (settings.isVerbose()) {
          System.out.println("  " + el.getNodeName() + "." + f.getName() + " field skipped due to @XmlIgnored annotation.");
        }
        continue; // skipped due to annotation
      } else if (Shared.isSkippedBySettings(f, settings)) {
        if (settings.isVerbose()) {
          System.out.println("  " + el.getNodeName() + "." + f.getName() + " field skipped due to settings-ignoredFieldsRegex list.");
        }
        continue; 
      }
      try {
        storeField(el, f, sourceObject);
      } catch (Exception ex) {
        throw new XmlSerializationException(ex,
            "Failed to store field '%s' of object of type '%s' into the element '%s'.",
            f.getName(), c.getName(),
            getElementXPath(el, true, true));
      }
    }
  }

  private void storeField(Element el, Field f, Object sourceObject) {
    if (settings.isVerbose()) {
      System.out.println("  storeField( <" + el.getNodeName() + "<..., " + sourceObject.getClass().getSimpleName() + "." + f.getName());
    }

    Class c = f.getType();
    IValueParser customValueParser = Shared.tryGetCustomValueParser(c, settings);
    IElementParser customElementParser = Shared.tryGetCustomElementParser(c, settings);
    if (customValueParser != null) {
      convertAndStoreFieldSimpleByCustomParser(el, f, customValueParser, sourceObject);
    } else if (customElementParser != null) {
      convertAndStoreFieldComplexByCustomParser(el, f, customElementParser, sourceObject);
    } else if (Mapping.isSimpleTypeOrEnum(c)) {
      // jednoduchý typ
      convertAndStoreFieldValue(el, f, sourceObject);
    } else if (List.class.isAssignableFrom(c)) {
      storeFieldList(el, f, sourceObject);
    } else if (c.isArray()) {
      storeFieldArray(el, f, sourceObject);
    } else {
      storeFieldComplex(el, f, sourceObject);
    }
  }

  @Nullable
  private Object getFieldValue(@NotNull Object sourceObject, @NotNull Field f) {
    Object value;
    try {
      f.setAccessible(true);
      value = f.get(sourceObject);
      f.setAccessible(false);
    } catch (IllegalAccessException ex) {
      throw new XmlSerializationException(ex,
          "Failed to get value of field '%s' from object type '%s'.",
          f.getName(),
          sourceObject.getClass().getName());
    }
    return value;
  }

  private <T> void convertAndStoreFieldComplexByCustomParser(Element el, Field f, IElementParser parser, T sourceObject) {

    Object value = getFieldValue(sourceObject, f);

    if (value == null) {
      el.setTextContent(settings.getNullString());
    } else {
      try {
        parser.format(value, el);
      } catch (Exception ex) {
        throw new XmlSerializationException(ex,
            "Failed to format value '%s' obtained from field '%s' of object type '%s' using parser '%s'.",
            value.toString(),
            f.getName(),
            sourceObject.getClass().getName(),
            parser.getClass().getName());
      }
    }
  }

  private <T> void convertAndStoreFieldSimpleByCustomParser(Element el, Field f, IValueParser parser, T sourceObject) {

    Object value = getFieldValue(sourceObject, f);

    String s;
    if (value == null)
      s = settings.getNullString();
    else
      try {
        s = parser.format(value);
      } catch (Exception ex) {
        throw new XmlSerializationException(ex,
            "Failed to format value '%s' obtained from field '%s' of object type '%s' using parser '%s'.",
            value.toString(),
            f.getName(),
            sourceObject.getClass().getName(),
            parser.getClass().getName());
      }

    el.setAttribute(f.getName(), s);
  }

  private <T> void convertAndStoreFieldValue(Element el, Field f, T sourceObject) {

    Object value = getFieldValue(sourceObject, f);

    String s;
    if (value == null)
      s = settings.getNullString();
    else
      try {
        s = value.toString();
      } catch (Exception ex) {
        throw new XmlSerializationException(ex,
            "Failed to format value '%s' obtained from field '%s' of object type '%s' using 'toString()' method.",
            value.toString(),
            f.getName(),
            sourceObject.getClass().getName());
      }

    el.setAttribute(f.getName(), s);
  }

  private <T> void storeFieldComplex(Element el, Field f, T sourceObject) {

    Object value = getFieldValue(sourceObject, f);

    if (value == null) {
      Element subEl = el.getOwnerDocument().createElement(f.getName());
      el.appendChild(subEl);
      subEl.setTextContent(settings.getNullString());
    } else {
      try {
        Element subEl = el.getOwnerDocument().createElement(f.getName());
        el.appendChild(subEl);
        storeObject(value, subEl);
      } catch (Exception ex) {
        throw new XmlSerializationException(ex,
            "Failed to store value '%s' obtained from field '%s' of object type '%s'.",
            value.toString(),
            f.getName(),
            sourceObject.getClass().getName());
      }
    }
  }

  private void storeFieldList(Element el, Field f, Object sourceObject) {

    Object value = getFieldValue(sourceObject, f);

    if (value == null) {
      Element subEl = el.getOwnerDocument().createElement(f.getName());
      el.appendChild(subEl);
      subEl.setTextContent(settings.getNullString());
    } else {
      try {
        List lst = (List) value;
        Element listEl = el.getOwnerDocument().createElement(f.getName());
        el.appendChild(listEl);

        for (Object item : lst) {
          // TODO update element name
          String tagName;
          if (item == null)
            tagName = "item";
          else
            tagName = item.getClass().getSimpleName();
          Element itemElement = listEl.getOwnerDocument().createElement(tagName);
          listEl.appendChild(itemElement);
          storeObject(item, itemElement);
        }

      } catch (Exception ex) {
        throw new XmlSerializationException(ex,
            "Failed to store value '%s' obtained from field '%s' of object type '%s'.",
            value.toString(),
            f.getName(),
            sourceObject.getClass().getName());
      }
    }
  }

  private void storeFieldArray(Element el, Field f, Object sourceObject) {

    Object value = getFieldValue(sourceObject, f);

    if (value == null) {
      Element subEl = el.getOwnerDocument().createElement(f.getName());
      el.appendChild(subEl);
      subEl.setTextContent(settings.getNullString());
    } else {
      try {
        Object arr = value;

        Element listEl = el.getOwnerDocument().createElement(f.getName());
        el.appendChild(listEl);

        int cnt = Array.getLength(arr);
        for (int i = 0; i < cnt; i++) {
          Object item = Array.get(arr, i);

          // TODO update element name
          String tagName;
          if (item == null)
            tagName = f.getType().getComponentType().getSimpleName();
          else
            tagName = item.getClass().getSimpleName();
          Element itemElement = listEl.getOwnerDocument().createElement(tagName);
          listEl.appendChild(itemElement);
          storeObject(item, itemElement);
        }

      } catch (Exception ex) {
        throw new XmlSerializationException(ex,
            "Failed to store value '%s' obtained from field '%s' of object type '%s'.",
            value.toString(),
            f.getName(),
            sourceObject.getClass().getName());
      }
    }
  }
}
