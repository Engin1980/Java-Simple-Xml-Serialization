/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eng.eSystem.xmlSerialization;

import com.sun.istack.internal.NotNull;
import eng.eSystem.eXml.XDocument;
import eng.eSystem.eXml.XElement;
import eng.eSystem.exceptions.EXmlException;
import org.w3c.dom.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;

/**
 * Instance used to serialize and deserialize objects from/to xml.
 *
 * @author Marek
 */
public class XmlSerializer {

  public class Serializer {
    public void serialize(Object obj, XElement elm) throws XmlSerializationException {
      XmlSerializer.this.formatter.saveObject(obj, elm, false);
    }

  }

  public class Deserializer {
    public Object deserialize(XElement src, Class targetType) throws XmlDeserializationException {
      throw new UnsupportedOperationException();
    }
  }

  private final Settings settings;
  private final Parser parser;
  private final Formatter formatter;

  /**
   * Creates default instance with default settings.
   *
   * @see #XmlSerializer(Settings)
   */
  public XmlSerializer() {
    this(new Settings());
  }

  /**
   * Creates instance with custom {@linkplain Settings}.
   *
   * @param settings Settings to be used
   * @see Settings
   * @see #XmlSerializer()
   */
  public XmlSerializer(Settings settings) {
    if (settings == null) {
      throw new IllegalArgumentException("Value of {settings} cannot not be null.");
    }

    this.settings = settings;
    this.parser = new Parser(this);
    this.formatter = new Formatter(this);
  }

  /**
   * Deserializes an instance of the objectType from xmlFileName.
   * <p>
   * If stored object in xml file is not null, then there must be way how a new instance
   * is created from objectType - either public parameter-less constructor,
   * or custom {@linkplain IInstanceCreator} defined in {@linkplain #getSettings()}.
   * </p>
   *
   * @param xmlFileName XML file name to be read.
   * @param objectType  A type of a returned class.
   * @return Object deserialized from the class.
   */
  public Object deserialize(@NotNull String xmlFileName, @NotNull Class objectType) {
    InputStream is = openFileForReading(xmlFileName);
    Object ret = deserialize(is, objectType);
    closeFile(is);
    return ret;
  }

  /**
   * Deserializes an instance of the objectType from xmlFileName.
   * <p>
   * If stored object in xml file is not null, then there must be way how a new instance
   * is created from objectType - either public parameter-less constructor,
   * or custom {@linkplain IInstanceCreator} defined in {@linkplain #getSettings()}.
   * </p>
   *
   * @param xmlFileName XML file name to be read.
   * @param objectType  A type of a returned class.
   * @return Object deserialized from the class.
   */
  public Object deserialize(@NotNull InputStream xmlFileName, @NotNull Class objectType) {
    XElement el;
    Object ret;
    try {
      el = loadXmlAndGetRootElement(xmlFileName);

      Shared.applyTypeMappingExpansion(el);

      ret = this.deserialize(el, objectType);
    } catch (XmlDeserializationException ex) {
      throw new XmlException(ex);
    }
    return ret;
  }

  public Object deserialize(@NotNull XElement sourceElement, @NotNull Class objectType) {
    Object ret;
    try {
      ret = this.parser.deserialize(sourceElement, objectType);
    } catch (XmlDeserializationException ex) {
      throw new XmlException(ex);
    }
    return ret;
  }

  public void deserializeContent(@NotNull XElement sourceElement, @NotNull Object targetObject){
    try {
      this.parser.deserializeContent(sourceElement, targetObject);
    } catch (XmlDeserializationException e) {
      throw new XmlException(e);
    }
  }

  /**
   * Saves an object into specified XMl file.
   * <p>
   * Object should not be null. If XMl file exists, it will be overwritten.
   * </p>
   *
   * @param xmlFileName  Target XML file name. If exists, will be overwritten.
   * @param sourceObject Object to be stored.
   */
  public void serialize(@NotNull String xmlFileName, @NotNull Object sourceObject) {
    OutputStream os = openFileForWriting(xmlFileName);
    this.serialize(os, sourceObject);
    closeFile(os);
  }

  /**
   * Saves an object into specified XMl file.
   * <p>
   * Object should not be null. If XMl file exists, it will be overwritten.
   * </p>
   *
   * @param outputStream Target stream
   * @param sourceObject Object to be stored.
   */
  public void serialize(@NotNull OutputStream outputStream, @NotNull Object sourceObject) {
    XElement elm;
    try {
      elm = new XElement("root");
      serialize(elm, sourceObject);

      if (settings.isUseSimpleTypeNamesInReferences()) {
        Shared.applyTypeMappingShortening(elm);
      }

      XDocument doc = new XDocument(elm);
      doc.save(outputStream);
    } catch (XmlSerializationException ex) {
      throw new XmlException(ex);
    } catch (EXmlException ex) {
      throw new XmlException(new XmlSerializationException("Failed to save the document.", ex));
    }
  }

  public void serialize(@NotNull XElement targetElement, @NotNull Object sourceObject) throws XmlSerializationException {
    this.formatter.saveObject(sourceObject, targetElement, true);
  }

  /**
   * Returns an instance of current {@linkplain Settings}. Setting's properties can be adjusted,
   * setting object is read-only.
   *
   * @return Instance of settings.
   * @see Settings
   */
  @NotNull
  public Settings getSettings() {
    return settings;
  }

//  /**
//   * Fills the specified list with the data from XMl file. Don't use this method for common classes and Arrays !!!
//   *
//   * @param xmlFileName  XML file to be used as a source. Must exist and be accessible.
//   * @param targetObject List which will be filled. Cannot be null.
//   * @see #fillObject(String, Object)
//   * @see #fillArray(String, Class)
//   */
//  public void fillList(@NotNull String xmlFileName, @NotNull List targetObject) {
//    InputStream is = openFileForReading(xmlFileName);
//    fillObject(is,targetObject);
//  }
//
//  /**
//   * Fills the specified list with the data from XMl file. Don't use this method for common classes and Arrays !!!
//   *
//   * @param inputStream XML file to be used as a source. Must exist and be accessible.
//   * @param targetObject List which will be filled. Cannot be null.
//   * @see #fillObject(String, Object)
//   * @see #fillArray(String, Class)
//   */
//  public void fillList(@NotNull InputStream inputStream, @NotNull List targetObject) {
//    Element el = loadXmlAndGetRootElement(inputStream);
//    this.parser.fillList(el, targetObject);
//  }
//
//  /**
//   * <<<<<<< HEAD
//   * Returns an array of specified type filled with the data from XMl file. Don't use this method for common classes and lists!!!
//   * =======
//   * Returns an array of specified type filled with the data from XMl file. Don't use this method for common classes and arrays!!!
//   * >>>>>>> b9fb0b4a110256528250f7e781cc618b9e650d73
//   *
//   * @param xmlFileName   XML file to be used as a source. Must exist and be accessible.
//   * @param arrayItemType A data type to which the array element should be typed to.
//   * @return An instance of array of specified type with the size according to the data read from xml file.
//   * @see #fillObject(String, Object)
//   * @see #fillList(String, List)
//   */
//  @NotNull
//  public Object fillArray(@NotNull String xmlFileName, @NotNull Class arrayItemType) {
//    InputStream is = openFileForReading(xmlFileName);
//    Object ret = fillArray(is, arrayItemType);
//    return ret;
//  }

//  /**
//   * <<<<<<< HEAD
//   * Returns an array of specified type filled with the data from XMl file. Don't use this method for common classes and lists!!!
//   * =======
//   * Returns an array of specified type filled with the data from XMl file. Don't use this method for common classes and arrays!!!
//   * >>>>>>> b9fb0b4a110256528250f7e781cc618b9e650d73
//   *
//   * @param inputStream   XML stream source.
//   * @param arrayItemType A data type to which the array element should be typed to.
//   * @return An instance of array of specified type with the size according to the data read from xml file.
//   * @see #fillObject(String, Object)
//   * @see #fillList(String, List)
//   */
//  @NotNull
//  public Object fillArray(@NotNull InputStream inputStream, @NotNull Class arrayItemType) {
//    Element el = loadXmlAndGetRootElement(inputStream);
//    Object ret = this.parser.fillArray(el, arrayItemType);
//    return ret;
//  }

  public InputStream openFileForReading(String fileName) {
    InputStream is;
    try {
      is = new FileInputStream(fileName);
    } catch (FileNotFoundException ex) {
      throw new RuntimeException("Failed to open file " + fileName + ". " + ex.getMessage(), ex);
    }
    return is;
  }

  public OutputStream openFileForWriting(String fileName) {
    OutputStream is;
    try {
      is = new FileOutputStream(fileName);
    } catch (FileNotFoundException ex) {
      throw new RuntimeException("Failed to open file " + fileName + ". " + ex.getMessage(), ex);
    }
    return is;
  }

  private void closeFile(Closeable os) {
    try {
      os.close();
    } catch (IOException ex) {
      throw new RuntimeException("Failed to close source file.", ex);
    }
  }

  private void saveXmlDocument(OutputStream os, Document doc) throws XmlSerializationException {
    try {

      TransformerFactory tFactory =
          TransformerFactory.newInstance();
      Transformer transformer =
          tFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

      DOMSource source = new DOMSource(doc);
      StreamResult result = new StreamResult(os);
      transformer.transform(source, result);

    } catch (javax.xml.transform.TransformerException ex) {
      throw new XmlSerializationException(ex, "Failed to write XML content.");
    }
  }

  private XElement loadXmlAndGetRootElement(InputStream xmlFileName) throws XmlDeserializationException {
    XDocument doc = readXmlDocument(xmlFileName);
    XElement el = doc.getRoot();
    return el;
  }

  private XDocument readXmlDocument(InputStream inputStream) throws XmlDeserializationException {
    XDocument ret;
    try {
      ret = XDocument.load(inputStream);
    } catch (EXmlException ex) {
      throw new XmlDeserializationException(ex, "Failed to load XML file from stream.");
    }
    return ret;
  }
}
