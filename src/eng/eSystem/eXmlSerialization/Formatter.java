package eng.eSystem.eXmlSerialization;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.text.StyledEditorKit;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.lang.reflect.Field;

class Formatter {
  private final Settings settings;

  public Formatter(Settings settings) {
    this.settings = settings;
  }

  public Document saveObject(Object source){
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

  private void storeObject(Object source, Element el) {
    if (source == null){
      el.setNodeValue(settings.getNullString());
      return;
    }

    Class c = source.getClass();
    Field[] fields = Shared.getDeclaredFields(c);

    for (Field field : fields) {
      el.setAttribute(field.getName(), "xxx");
    }
  }
}
