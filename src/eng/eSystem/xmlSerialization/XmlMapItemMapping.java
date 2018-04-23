package eng.eSystem.xmlSerialization;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

public class XmlMapItemMapping extends XmlItemMapping {
  /**
   * Target class to which the element value should be mapped into.
   */
  @NotNull
  public final Class valueType;

  /**
   * Target class to which the element key should be mapped into.
   */
  @NotNull
  public final Class keyType;

  /**
   * Creates new list-mapping between xml-element and map in java class.
   * <p>For principle look at the {@link XmlMapItemMapping class definition}.
   * This constructor is used when a specific class should be used for specific xml-element (according to its name)
   * in the map xml-element.</p>
   * @param collectionElementXPathRegex The regex defining the name of xml-element which contains map items.
   * @param itemElementName The regex defining the name of xml-subelement inside the map xml-element.
   * @param keyType The class used to represent key type in the map.
   * @param valueType The class used to represent value type in the map.
   */
  public XmlMapItemMapping(@NotNull String collectionElementXPathRegex, @Nullable String itemElementName, @NotNull Class keyType, @NotNull Class valueType) {
    super(collectionElementXPathRegex, itemElementName);
    this.keyType = keyType;
    this.valueType = valueType;
  }

  /**
   * Creates new list-mapping between xml-element and map in java class.
   * <p>For principle look at the {@link XmlListItemMapping class definition}.
   * This constructor is used when all elements in the map has the same
   * instance type.</p>
   * @param collectionElementXPathRegex The regex defining the name of xml-element which contains map items.
   * @param keyType The class used to represent key type in the map.
   * @param valueType The class used to represent value type in the map.
   */
  public XmlMapItemMapping(@NotNull String collectionElementXPathRegex, @NotNull Class keyType, @NotNull Class valueType) {
    super(collectionElementXPathRegex);
    this.keyType = keyType;
    this.valueType = valueType;
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
    ret.append(keyType.getName()).append(": ").append(valueType.getName());
    return ret.toString();
  }
}
