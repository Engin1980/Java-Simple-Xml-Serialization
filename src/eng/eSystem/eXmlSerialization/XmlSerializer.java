/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eng.eSystem.eXmlSerialization;

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
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Instance used to serialize and deserialize objects from/to xml.
 * <p>
 *   For parsing object from XML, use:
 *   <ul>
 *     <li>
 *       For Arrays use method {@linkplain #fillArray(String, Class)}
 *     </li>
 *     <li>
 *       For implementation of java.util.List use method {@linkplain #fillList(String, List)}
 *     </li>
 *     <li>
 *       For common class use method {@linkplain #fillObject(String, Object)}
 *     </li>
 *   </ul>
 * </p>
 * <p>
 *   For formatting object to XML, use {@linkplain #serialize(String, Object)}.
 * </p>
 * @author Marek
 */
public class XmlSerializer {

  private final Settings settings;
  private final Parser parser;
  private final Formatter formatter;

  /**
   * Creates default instance with default settings.
   * @see #XmlSerializer(Settings)
   */
  public XmlSerializer() {
    settings = new Settings();
    parser = new Parser(settings);
    formatter = new Formatter(settings);
  }

  /**
   * Creates instance with custom {@linkplain Settings}.
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

  /**
   * Fills the specified object with the data from XMl file. Don't use this method for Lists and Arrays !!!
   * @param xmlFileName XML file to be used as a source. Must exist and be accessible.
   * @param targetObject Object which will be filled. Cannot be null.
   * @see #fillList(String, List)
   * @see #fillArray(String, Class)
   */
  public void fillObject (@NotNull String xmlFileName, @NotNull Object targetObject){

    Element el = loadXmlAndGetRootElement(xmlFileName);
    this.parser.fillObject(el, targetObject);
  }

  /**
   * Deserializes an instance of the objectType from xmlFileName.
   * <p>
   *   If stored object in xml file is not null, then there must be way how a new instance
   *   is created from objectType - either public parameter-less constructor,
   *   or custom {@linkplain IInstanceCreator} defined in {@linkplain #getSettings()}.
   * </p>
   * @param xmlFileName XML file name to be read.
   * @param objectType A type of a returned class.
   * @return Object deserialized from the class.
   */
  public Object deserialize(@NotNull String xmlFileName, @NotNull Class objectType){
    Element el = loadXmlAndGetRootElement(xmlFileName);
    Object ret = this.parser.deserialize(el, objectType);
    return ret;
  }

  /**
   * Saves an object into specified XMl file.
   * <p>
   *   Object should not be null. If XMl file exists, it will be overwritten.
   * </p>
   * @param xmlFileName Target XML file name. If exists, will be overwritten.
   * @param sourceObject Object to be stored.
   */
  public void serialize(@NotNull String xmlFileName, @NotNull Object sourceObject){
    Document doc;

    doc = this.formatter.saveObject(sourceObject);
    saveXmlDocument(xmlFileName, doc);
  }

  private void saveXmlDocument(String fileName, Document doc) {
    try {
      File fXmlFile = new File(fileName);
      TransformerFactory tFactory =
          TransformerFactory.newInstance();
      Transformer transformer =
          tFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

      DOMSource source = new DOMSource(doc);
      StreamResult result = new StreamResult(fXmlFile);
      transformer.transform(source, result);

    } catch (javax.xml.transform.TransformerException ex) {
      throw new XmlSerializationException(ex, "Failed to load XML file '%s'.", fileName);
    }
  }

  private Element loadXmlAndGetRootElement(String xmlFileName) {
    Document doc = readXmlDocument(xmlFileName);
    Element el = doc.getDocumentElement();
    return el;
  }

  /**
   * Fills the specified list with the data from XMl file. Don't use this method for common classes and Arrays !!!
   * @param xmlFileName XML file to be used as a source. Must exist and be accessible.
   * @param targetObject List which will be filled. Cannot be null.
   * @see #fillObject(String, Object)
   * @see #fillArray(String, Class)
   */
  public void fillList (@NotNull String xmlFileName, @NotNull List targetObject){
    Element el = loadXmlAndGetRootElement(xmlFileName);
    this.parser.fillList(el, targetObject);
  }

  /**
<<<<<<< HEAD
   * Returns an array of specified type filled with the data from XMl file. Don't use this method for common classes and lists!!!
=======
   * Returns an array of specified type filled with the data from XMl file. Don't use this method for common classes and arrays!!!
>>>>>>> b9fb0b4a110256528250f7e781cc618b9e650d73
   * @param xmlFileName XML file to be used as a source. Must exist and be accessible.
   * @param arrayItemType A data type to which the array element should be typed to.
   * @return An instance of array of specified type with the size according to the data read from xml file.
   * @see #fillObject(String, Object)
   * @see #fillList(String, List)
   */
  @NotNull
  public Object fillArray(@NotNull String xmlFileName, @NotNull Class arrayItemType){
    Element el = loadXmlAndGetRootElement(xmlFileName);
    Object ret = this.parser.fillArray(el, arrayItemType);
    return ret;
  }

  private Document readXmlDocument(String fileName){
    Document doc = null;
    try {
      File fXmlFile = new File(fileName);
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      doc = dBuilder.parse(fXmlFile);

      //optional, but recommended
      //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
      doc.getDocumentElement().normalize();
    } catch (ParserConfigurationException | SAXException | IOException ex) {
      throw new XmlSerializationException(ex, "Failed to load XML file '%s'.", fileName);
    }

    return doc;
  }

  /**
   * Returns an instance of current {@linkplain Settings}. Setting's properties can be adjusted,
   * setting object is read-only.
   * @return Instance of settings.
   * @see Settings
   */
  @NotNull
  public Settings getSettings() {
    return settings;
  }
}
