package eng.eSystem.xmlSerialization;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

public abstract class XmlItemMapping {
  /**
   * Represents an XML path to list.
   * Note that this is a regex, xml sub-elements are divided by a dot ".".
   * This should contain either full name to element name containing list, like "^person.addresses" or
   * just regex like "person.addresses".
   */
  @NotNull
  public final String collectionElementXPathRegex;
  /**
   * Represents element name inside list if requested.
   * This is used when there is a need to map different items in the list to different classes
   * according to the element names. If empty, all elements in the list
   * specified by {@linkplain #collectionElementXPathRegex} will be mapped.
   * If specified, only elements inside list {@linkplain #collectionElementXPathRegex} with this tag will be mapped.
   */
  @Nullable
  public final String itemElementName;

  /**
   * Creates new list-mapping between xml-element and list in java class.
   * <p>For principle look at the {@link XmlListItemMapping class definition}.
   * This constructor is used when a specific class should be used for specific xml-element (according to its name)
   * in the list xml-element.</p>
   * @param collectionElementXPathRegex The regex defining the name of xml-element which contains list items.
   * @param itemElementName The regex defining the name of xml-subelement inside the list xml-element.
   */
  public XmlItemMapping(@NotNull String collectionElementXPathRegex, @Nullable String itemElementName) {
    this.collectionElementXPathRegex = collectionElementXPathRegex;
    this.itemElementName = itemElementName;
  }

  /**
   * Creates new list-mapping between xml-element and list in java class.
   * <p>For principle look at the {@link XmlListItemMapping class definition}.
   * This constructor is used when all elements in the list has the same
   * instance type.</p>
   * @param collectionElementXPathRegex The regex defining the name of xml-element which contains list items.
   */
  public XmlItemMapping(@NotNull String collectionElementXPathRegex) {
    this.collectionElementXPathRegex = collectionElementXPathRegex;
    this.itemElementName = null;
  }

  @Override
  public String toString() {
    StringBuilder ret = new StringBuilder();
    ret.append("XmlListItemMapping{");
    ret.append(collectionElementXPathRegex).append("/");
    if (itemElementName == null)
      ret.append("*");
    else
      ret.append(itemElementName);
    return ret.toString();
  }
}
