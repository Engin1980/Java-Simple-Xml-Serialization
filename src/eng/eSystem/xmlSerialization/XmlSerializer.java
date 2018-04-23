/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eng.eSystem.xmlSerialization;

import com.sun.istack.internal.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.List;

/**
 * Instance used to serialize and deserialize objects from/to xml.
 * <p>
 * For parsing object from XML, use:
 * <ul>
 * <li>
 * For Arrays use method {@linkplain #fillArray(String, Class)}
 * </li>
 * <li>
 * For implementation of java.util.List use method {@linkplain #fillList(String, List)}
 * </li>
 * <li>
 * For common class use method {@linkplain #fillObject(String, Object)}
 * </li>
 * </ul>
 * </p>
 * <p>
 * For formatting object to XML, use {@linkplain #serialize(String, Object)}.
 * </p>
 *
 * @author Marek
 */
public class XmlSerializer {

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
    this.parser = new Parser(settings);
    this.formatter = new Formatter(settings);
  }

//  /**
//   * Fills the specified object with the data from XMl file. Don't use this method for Lists and Arrays !!!
//   *
//   * @param xmlFileName  XML file to be used as a source. Must exist and be accessible.
//   * @param targetObject Object which will be filled. Cannot be null.
//   * @see #fillList(String, List)
//   * @see #fillArray(String, Class)
//   */
//  public void fillObject(@NotNull String xmlFileName, @NotNull Object targetObject) {
//    InputStream is = openFileForReading(xmlFileName);
//    this.fillObject(is, targetObject);
//  }
//
//  /**
//   * Fills the specified object with the data from XMl file. Don't use this method for Lists and Arrays !!!
//   *
//   * @param xmlFileName  XML file to be used as a source. Must exist and be accessible.
//   * @param targetObject Object which will be filled. Cannot be null.
//   * @see #fillList(String, List)
//   * @see #fillArray(String, Class)
//   */
//  public void fillObject(@NotNull InputStream xmlFileName, @NotNull Object targetObject) {
//    Element el = loadXmlAndGetRootElement(xmlFileName);
//    this.parser.fillObject(el, targetObject);
//  }


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
    Element el;
    Object ret;
    try {
      el = loadXmlAndGetRootElement(xmlFileName);
      ret = this.parser.deserialize(el, objectType);
    } catch (XmlDeserializationException ex){
      throw new XmlException(ex);
    }
    return ret;
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

  private void closeFile(Closeable os) {
    try {
      os.close();
    } catch (IOException ex) {
      throw new RuntimeException("Failed to close source file.", ex);
    }
  }

  /**
   * Saves an object into specified XMl file.
   * <p>
   * Object should not be null. If XMl file exists, it will be overwritten.
   * </p>
   *
   * @param outputStream  Target stream
   * @param sourceObject Object to be stored.
   */
  public void serialize(@NotNull OutputStream outputStream, @NotNull Object sourceObject) {
    Document doc;
    try {
      doc = this.formatter.saveObject(sourceObject);
      saveXmlDocument(outputStream, doc);
    } catch (XmlSerializationException ex){
      throw new XmlException(ex);
    }
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

  private Element loadXmlAndGetRootElement(InputStream xmlFileName) throws XmlDeserializationException {
    Document doc = readXmlDocument(xmlFileName);
    Element el = doc.getDocumentElement();
    return el;
  }

  private Document readXmlDocument(InputStream inputStream) throws XmlDeserializationException {
    Document doc = null;
    try {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      doc = dBuilder.parse(inputStream);

      //optional, but recommended
      //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
      doc.getDocumentElement().normalize();
    } catch (ParserConfigurationException | SAXException | IOException ex) {
      throw new XmlDeserializationException(ex, "Failed to load XML file from stream.");
    }

    return doc;
  }

  public InputStream openFileForReading(String fileName){
    InputStream is;
    try {
      is = new FileInputStream(fileName);
    } catch (FileNotFoundException ex) {
      throw new RuntimeException("Failed to open file " + fileName + ". " + ex.getMessage(), ex);
    }
    return is;
  }

  public OutputStream openFileForWriting(String fileName){
    OutputStream is;
    try {
      is = new FileOutputStream(fileName);
    } catch (FileNotFoundException ex) {
      throw new RuntimeException("Failed to open file " + fileName + ". " + ex.getMessage(), ex);
    }
    return is;
  }
}
