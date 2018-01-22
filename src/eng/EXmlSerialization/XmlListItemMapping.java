/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eng.EXmlSerialization;

/**
 *
 * @author Marek
 */
class XmlListItemMapping {
  /**
   * Represents an XML path to list.
   * Note that this is a regex, xml sub-elements are divided by a dot ".".
   * This should contain either full name to element name containing list, like "^person.addresses" or
   * just regex like "person.addresses".
   */
  public final String listPathRegex;
  /**
   * Represents element name inside list if requested.
   * This is used when there is a need to map different items in the list to different classes
   * according to the element names. If empty, all elements in the list
   * specified by {@linkplain listPathRegex} will be mapped to type {@linkplain itemType}.
   * If specified, only elements inside list {@linkplain listPathRegex} with this tag will be mapped to
   * {@linkplain itemType}.
   */
  public final String itemPathRegexOrNull;
  /**
   * Target class to which the element should be mapped into.
   */
  public final Class itemType;

  public XmlListItemMapping(String listPathRegex, String itemPathRegexOrNull, Class itemType) {
    this.listPathRegex = listPathRegex;
    this.itemPathRegexOrNull = itemPathRegexOrNull;
    this.itemType = itemType;
  }

  public XmlListItemMapping(String listPathRegex, Class itemType) {
    this.listPathRegex = listPathRegex;
    this.itemPathRegexOrNull = null;
    this.itemType = itemType;
  }

  @Override
  public String toString() {
    StringBuilder ret = new StringBuilder();
    ret.append("XmlListItemMapping{");
    ret.append(listPathRegex).append("\\");
    if (itemPathRegexOrNull == null)
      ret.append("*");
    else
      ret.append(itemPathRegexOrNull);
    ret.append("-->");
    ret.append(itemType.getName());
    return ret.toString();
  }
}
