/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eng.EXmlSerialization;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * @author Marek
 */
public class XmlInvalidDataException extends XmlSerializationException {

  private XmlInvalidDataException(String message) {
    super(message);
  }

  public static XmlInvalidDataException createNoSuchElement(Element parentElement, String fieldName, Class<? extends Object> targetClass) {
    StringBuilder sb = new StringBuilder();

    sb.append("Value for property not found in XML data. ");
    sb.append(
        String.format(
            "Looking for '%s' xml-attribute or xml-element in '%s' to be put in field '%s' of object of '%s'.",
            fieldName,
            Shared.getElementXPath(parentElement, true),
            targetClass.getName(),
            fieldName));

    return new XmlInvalidDataException(sb.toString());
  }


  public static XmlInvalidDataException createAttributeInsteadOfElementFound(
      Element parentElement, String fieldName, Class<? extends Object> targetClass) {
    StringBuilder sb = new StringBuilder();

    sb.append("Value of type found as attribute, however an sub-element was expected. Probably missing custom parser? ");
    sb.append(
        String.format(
            "Looking for '%s' xml-element in '%s' to be put in field '%s' of object of '%s', but only same-named attribute was found.",
            fieldName,
            Shared.getElementXPath(parentElement, true),
            targetClass.getName(),
            fieldName));

    return new XmlInvalidDataException(sb.toString());
  }
}
