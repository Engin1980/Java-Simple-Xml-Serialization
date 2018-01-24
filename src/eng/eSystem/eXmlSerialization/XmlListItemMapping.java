/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eng.eSystem.eXmlSerialization;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

/**
 * Represents mapping from xml-element representing list to list instance.
 * <p>
 *   During deserialization, when a mechanism finds a field in the class which is List
 *   (or one of its descendants), it will create a new list (according to settings in {@linkplain Settings#getDefaultListTypeImplementation()}).
 *   Then, to fill the list, it will take all sub-elements of the element as an object in the list. As Java is using
 *   "generic type erasure", during runtime there is not information what the type in the list should be put.
 *   This mapping defines, that for specific element ({@linkplain #listPathRegex}) when used as list, (and optionally
 *   for specific sub-element in the list named to {@linkplain #itemPathRegexOrNull}) the instance of class
 *   {@linkplain #itemType} will be used.
 * </p>
 * @author Marek
 * @see Settings
 */
public class XmlListItemMapping {
  /**
   * Represents an XML path to list.
   * Note that this is a regex, xml sub-elements are divided by a dot ".".
   * This should contain either full name to element name containing list, like "^person.addresses" or
   * just regex like "person.addresses".
   */
  @NotNull
  public final String listPathRegex;
  /**
   * Represents element name inside list if requested.
   * This is used when there is a need to map different items in the list to different classes
   * according to the element names. If empty, all elements in the list
   * specified by {@linkplain listPathRegex} will be mapped to type {@linkplain itemType}.
   * If specified, only elements inside list {@linkplain listPathRegex} with this tag will be mapped to
   * {@linkplain itemType}.
   */
  @Nullable
  public final String itemPathRegexOrNull;
  /**
   * Target class to which the element should be mapped into.
   */
  @NotNull
  public final Class itemType;

  /**
   * Creates new list-mapping between xml-element and list in java class.
   * <p>For principle look at the {@link XmlListItemMapping class definition}.
   * This constructor is used specific class should be used for specific xml-element (according to its name)
   * in the list xml-element.</p>
   * @param listPathRegex The regex defining the name of xml-element which contains list items.
   * @param itemPathRegexOrNull The regex defining the name of xml-subelement inside the list xml-element.
   * @param itemType The class used to represent item in the list.
   */
  public XmlListItemMapping(@NotNull String listPathRegex, @Nullable String itemPathRegexOrNull, @NotNull Class itemType) {
    this.listPathRegex = listPathRegex;
    this.itemPathRegexOrNull = itemPathRegexOrNull;
    this.itemType = itemType;
  }

  /**
   * Creates new list-mapping between xml-element and list in java class.
   * <p>For principle look at the {@link XmlListItemMapping class definition}.
   * This constructor is used when all elements in the list has the same
   * instance type.</p>
   * @param listPathRegex The regex defining the name of xml-element which contains list items.
   * @param itemType The class used to represent item in the list.
   */
  public XmlListItemMapping(@NotNull String listPathRegex, @NotNull Class itemType) {
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
