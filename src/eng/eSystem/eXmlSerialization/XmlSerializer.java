/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eng.eSystem.eXmlSerialization;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author Marek
 */
public class XmlSerializer {

  private final Settings settings;
  private final Reflecter reflecter;
  private final Formatter formatter;

  public XmlSerializer() {
    settings = new Settings();
    reflecter = new Reflecter(settings);
    formatter = new Formatter(settings);
  }

  public XmlSerializer(Settings settings) {
    if (settings == null) {
        throw new IllegalArgumentException("Value of {settings} cannot not be null.");
    }

    this.settings = settings;
    this.reflecter = new Reflecter(settings);
    this.formatter = new Formatter(settings);
  }

  public void fillObject (String xmlFileName, Object targetObject){

    Element el = loadXmlAndGetRootElement(xmlFileName);
    this.reflecter.fillObject(el, targetObject);
  }

  public void saveObject(String xmlFileName, Object sourceObject){
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

  public void fillList (String xmlFileName, List targetObject){
    Element el = loadXmlAndGetRootElement(xmlFileName);
    this.reflecter.fillList(el, targetObject);
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
}
