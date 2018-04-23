/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eng.eSystem.xmlSerialization;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

/**
 * Represents mapping from xml-element representing list to list instance.
 * <p>
 *   During deserialization, when a mechanism finds a field in the class which is List
 *   (or one of its descendants), it will create a new list (according to settings in {@linkplain Settings#getDefaultListTypeImplementation()}).
 *   Then, to fill the list, it will take all sub-elements of the element as an object in the list. As Java is using
 *   "generic type erasure", during runtime there is not information what the type in the list should be put.
 *   This mapping defines, that for specific element ({@linkplain #collectionElementXPathRegex}) when used as list, (and optionally
 *   for specific sub-element in the list named to {@linkplain #itemElementName}) the instance of class
 *   {@linkplain #itemType} will be used.
 * </p>
 * @author Marek
 * @see Settings
 */
public class XmlListItemMapping extends XmlItemMapping {
  /**
   * Target class to which the element should be mapped into.
   */
  @NotNull
  public final Class itemType;

  /**
   * Creates new list-mapping between xml-element and list in java class.
   * <p>For principle look at the {@link XmlListItemMapping class definition}.
   * This constructor is used when a specific class should be used for specific xml-element (according to its name)
   * in the list xml-element.</p>
   * @param collectionElementXPathRegex The regex defining the name of xml-element which contains list items.
   * @param itemElementName The regex defining the name of xml-subelement inside the list xml-element.
   * @param itemType The class used to represent item in the list.
   */
  public XmlListItemMapping(@NotNull String collectionElementXPathRegex, @Nullable String itemElementName, @NotNull Class itemType) {
    super(collectionElementXPathRegex, itemElementName);
    this.itemType = itemType;
  }

  /**
   * Creates new list-mapping between xml-element and list in java class.
   * <p>For principle look at the {@link XmlListItemMapping class definition}.
   * This constructor is used when all elements in the list has the same
   * instance type.</p>
   * @param collectionElementXPathRegex The regex defining the name of xml-element which contains list items.
   * @param itemType The class used to represent item in the list.
   */
  public XmlListItemMapping(@NotNull String collectionElementXPathRegex, @NotNull Class itemType) {
    super(collectionElementXPathRegex);
    this.itemType = itemType;
  }

  @Override
  public String toString() {
    StringBuilder ret = new StringBuilder();
    ret.append("XmlListItemMapping{");
    ret.append(collectionElementXPathRegex).append("\\");
    if (itemElementName == null)
      ret.append("*");
    else
      ret.append(itemElementName);
    ret.append("-->");
    ret.append(itemType.getName());
    return ret.toString();
  }
}
