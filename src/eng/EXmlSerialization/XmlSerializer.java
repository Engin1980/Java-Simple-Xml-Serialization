/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eng.EXmlSerialization;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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

  public XmlSerializer() {
    settings = new Settings();
    reflecter = new Reflecter(settings);
  }

  public XmlSerializer(Settings settings) {
    if (settings == null) {
        throw new IllegalArgumentException("Value of {settings} cannot not be null.");
    }

    this.settings = settings;
    this.reflecter = new Reflecter(settings);
  }

  public void fillObject (String xmlFileName, Object targetObject){

    Element el = loadXmlAndGetRootElement(xmlFileName);
    this.reflecter.fillObject(el, targetObject);
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
      throw new XmlSerializationException("Failed to load XML file " + fileName, ex);
    }

    return doc;
  }
}
