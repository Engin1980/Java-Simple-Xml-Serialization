/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eng.eSystem.xmlSerialization;

import org.w3c.dom.Element;

import java.util.List;

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
            "Looking for '%s' xml-attribute or xml-element in '%s' to be put in field '%s' of object of '%s' failed.",
            fieldName,
            Shared.getElementInfoString(parentElement),
            targetClass.getName(),
            fieldName));

    return new XmlInvalidDataException(sb.toString());
  }

  public static XmlInvalidDataException createNoSuchElementInMappings(Element parentElement, String fieldName, Class<? extends Object> targetClass,
                                                                      List<XmlCustomFieldMapping>usedCustomMappings) {
    StringBuilder sb = new StringBuilder();

    StringBuilder txt = new StringBuilder();
    txt.append(usedCustomMappings.get(0).toString());
    for (XmlCustomFieldMapping usedCustomMapping : usedCustomMappings) {
      txt.append("// ");
      txt.append(usedCustomMapping.toString());
    }

    sb.append("Value for property not found in XML data. ");
    sb.append(
        String.format(
            "Looking for mapping-based xml-attribute or xml-element in '%s' to be put in field '%s' of object of '%s' failed. Used mapings: %s",
            Shared.getElementInfoString(parentElement),
            targetClass.getName(),
            fieldName,
            txt.toString()
            ));

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
            Shared.getElementInfoString(parentElement),
            targetClass.getName(),
            fieldName));

    return new XmlInvalidDataException(sb.toString());
  }
}
