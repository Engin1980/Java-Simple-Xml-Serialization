package eng.eSystem.xmlSerialization;

import eng.eSystem.eXml.XDocument;
import eng.eSystem.eXml.XElement;
import eng.eSystem.exceptions.EXmlException;
import eng.eSystem.xmlSerialization.exceptions.XmlSerializationException;

import java.io.*;

import static eng.eSystem.utilites.FunctionShortcuts.sf;

public class XmlSerializer {

  public class Serializer {
    public void serialize(Object obj, XElement elm) {
      XmlSerializer.this.formatter.saveObject(obj, elm, false);
    }

  }

  public class Deserializer {
    public <T> T deserialize(XElement src, Class<? extends T> targetType) {
      throw new UnsupportedOperationException();
    }
  }

  private final XmlSettings settings;
  private final Parser parser;
  private final Formatter formatter;


  public XmlSerializer() {
    this(new XmlSettings());
  }

  public XmlSerializer(XmlSettings settings) {
    if (settings == null) {
        throw new IllegalArgumentException("Value of {settings} cannot not be null.");
    }

    this.settings = settings;
    this.parser = new Parser(this);
    this.formatter = new Formatter(this);
  }

  public <T> T deserialize(String xmlFileName, Class<? extends T> objectType) {
    InputStream is = openFileForReading(xmlFileName);
    T ret = deserialize(is, objectType);
    closeFile(is);
    return ret;
  }

  public XmlSettings getSettings() {
    return settings;
  }

  public <T> T deserialize(InputStream xmlFileStream, Class<? extends T> objectType) {
    XDocument doc;
    T ret;
    try {
      doc = loadXmlDocument(xmlFileStream);
    } catch (Exception ex) {
      throw new XmlSerializationException("Failed to load input stream into XLM document.", ex);
    }
    try {
      ret = this.deserialize(doc, objectType);
    } catch (Exception ex) {
      throw new XmlSerializationException("Failed to loadObject input stream.", ex);
    }
    return ret;
  }

  public <T> T deserialize(XDocument document, Class<? extends T> objectType) {
    T ret;
    XElement el = document.getRoot();
    try {
      TypeMappingManager.applyTypeMappingExpansion(el);

      ret = this.deserialize(el, objectType);
    } catch (Exception ex) {
      throw new XmlSerializationException("Failed to loadObject from XML document.", ex);
    }
    return ret;
  }

  public <T> T deserialize(XElement sourceElement, Class<? extends T> objectType) {
    T ret;
    try {
      ret = this.parser.loadObject(sourceElement, objectType);
    } catch (XmlSerializationException ex) {
      throw new XmlSerializationException(
          "Failed to loadObject type " + objectType.getName() +
              " from root element " + sourceElement.getName() + ".",
          ex);
    }
    return ret;
  }

  public void deserializeContent(XElement sourceElement, Object targetObject){
    try {
      this.parser.deserializeContent(sourceElement, targetObject);
    } catch (XmlSerializationException e) {
      throw new XmlSerializationException(
          sf("Failed to deserialize the content of '%s' from element %s.", targetObject.toString(), Shared.getElementInfoString(sourceElement)),
          e);
    }
  }

  public void serialize(String xmlFileName, Object sourceObject) {
    OutputStream os = openFileForWriting(xmlFileName);
    this.serialize(os, sourceObject);
    closeFile(os);
  }

  public void serialize(OutputStream outputStream, Object sourceObject) {
    XDocument doc = new XDocument(new XElement("root"));
    try {
      this.serialize(doc, sourceObject);
    } catch (Exception ex) {
      throw new XmlSerializationException("Failed to serialize object into newe xml document.", ex);
    }
    try {
      doc.save(outputStream);
    } catch (Exception ex) {
      throw new XmlSerializationException("Failed to save the xml document into the output stream.", ex);
    }
  }

  public void serialize(XDocument document, Object sourceObject) {
    XElement root = document.getRoot();
    this.serialize(root, sourceObject);
    if (settings.isUseSimpleTypeNamesInReferences()) {
      TypeMappingManager.applyTypeMappingShortening(root);
    }
  }

  public void serialize(XElement targetElement, Object sourceObject) throws XmlSerializationException {
    this.formatter.saveObject(sourceObject, targetElement, false);
  }

  private static InputStream openFileForReading(String fileName) {
    InputStream is;
    try {
      is = new FileInputStream(fileName);
    } catch (FileNotFoundException ex) {
      throw new XmlSerializationException("Failed to open file " + fileName + ". " + ex.getMessage(), ex);
    }
    return is;
  }

  private static void closeFile(Closeable os) {
    try {
      os.close();
    } catch (IOException ex) {
      throw new RuntimeException("Failed to close source file.", ex);
    }
  }

  private XDocument loadXmlDocument(InputStream inputStream) {
    XDocument ret;
    try {
      ret = XDocument.load(inputStream);
    } catch (EXmlException ex) {
      throw new XmlSerializationException("Failed to load XML file from stream.", ex);
    }
    return ret;
  }

  private OutputStream openFileForWriting(String fileName) {
    OutputStream is;
    try {
      is = new FileOutputStream(fileName);
    } catch (FileNotFoundException ex) {
      throw new XmlSerializationException("Failed to open file " + fileName + ". " + ex.getMessage(), ex);
    }
    return is;
  }
}
