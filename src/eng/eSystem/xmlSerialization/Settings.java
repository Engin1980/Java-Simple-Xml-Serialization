package eng.eSystem.xmlSerialization;

import com.sun.istack.internal.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains setting used during deserialization.
 */
public class Settings {

  private final List<String> ignoredFieldsRegex = new ArrayList<>();
  private final List<IInstanceCreator> instanceCreators = new ArrayList<>();
  private final List<IValueParser> valueParsers = new ArrayList<>();
  private final List<IElementParser> elementParsers = new ArrayList<>();
  private final List<XmlListItemMapping> listItemMapping = new ArrayList();
  private final List<XmlCustomFieldMapping> customFieldMappings = new ArrayList<>();
  private boolean verbose = false;
  private String nullString = "(null)";
  private Class defaultListTypeImplementation = ArrayList.class;
  private boolean useSimpleTypeNamesInReferences = true;

  public boolean isUseSimpleTypeNamesInReferences() {
    return useSimpleTypeNamesInReferences;
  }

  public void setUseSimpleTypeNamesInReferences(boolean useSimpleTypeNamesInReferences) {
    this.useSimpleTypeNamesInReferences = useSimpleTypeNamesInReferences;
  }

  /**
   * Gets list of mappings defining how list in deserialized class will be handled. <br />
   * List of mappings defines which datatype will be used for element during deserialization of the list.
   * The mappings are used in the order specified in this list (first matching will be used).
   * For more info look for the explanation of {@linkplain XmlListItemMapping}.
   * @return List of mappings.
   * @see XmlListItemMapping
   */
  @NotNull
  public List<XmlListItemMapping> getListItemMappings() {
    return listItemMapping;
  }

  /**
   * If true, verbose info is printed into console during operation.
   * @return true/false
   */
  public boolean isVerbose() {
    return verbose;
  }

  /**
   * Sets verbose parameter.
   * @param verbose
   * @see #isVerbose()
   */
  public void setVerbose(boolean verbose) {
    this.verbose = verbose;
  }

  /**
   * Contains list of regexes. If field name match one of the regexes, then will be skipped. <br />
   * This list is used to skip processing of specified fields regardless their class. Only field names
   * are tested against regexes. If field name matches any of the regex in the list, will be skipped.
   * @return List of regexes to be matched against field name.
   */
  @NotNull
  public List<String> getIgnoredFieldsRegex() {
    return ignoredFieldsRegex;
  }

  /**
   * Returns class which will be used as default list implementation. <br />
   * During deserialization, when list is found, it is needed to be created an instance. However,
   * java.util.List is abstract and offers a set of different implementations. Class in this property
   * says which implementation will be used for the list.
   * @return Class used to instantiate java.util.List object.
   */
  @NotNull
  public Class getDefaultListTypeImplementation() {
    return defaultListTypeImplementation;
  }

  /**
   * See {@linkplain #getDefaultListTypeImplementation()} .
   * @param defaultListTypeImplementation
   */
  public void setDefaultListTypeImplementation(@NotNull Class defaultListTypeImplementation) {
    if (defaultListTypeImplementation == null) {
        throw new IllegalArgumentException("Value of {defaultListTypeImplementation} cannot not be null.");
    }
    
    this.defaultListTypeImplementation = defaultListTypeImplementation;
  }

  /**
   * Gets a list of factories creating specified instances. <br />
   * When a creation of instance is needed, and there exists an instance
   * creator in this list for that type, this creator will be used.
   * Otherwise, public parameter-less constructor will be invoked, what can
   * cause an error if an instantiated type has no such constructor.
   * For more info see {@linkplain IInstanceCreator}.
   * @return List of classes instantiating specified types.
   * @see IInstanceCreator
   */
  public List<IInstanceCreator> getInstanceCreators() {
    return instanceCreators;
  }

  /**
   * Gets a parsers creating a specific object from the attribute value
   * by custom parsing. <br />
   * When specific parsing to convert the value from the xml-attribute to
   * create object is needed, a custom instance of {@linkplain IValueParser} is needed.
   * Anytime when creation of the instance of the requested type occurs and
   * an implementation for that type is in this list, this parser will be used
   * instead of default parsing of the attribute value. For more info about
   * parsers see {@linkplain IValueParser}.
   * @return
   * @see IValueParser
   * @see IElementParser
   */
  public List<IValueParser> getValueParsers() {
    return valueParsers;
  }

  /**
   * Gets a parsers creating a specific object from the element
   * by custom parsing. <br />
   * When specific conversion of the sub-elements from the xml-element to
   * create object is needed, a custom instance of {@linkplain IElementParser} is needed.
   * Anytime when creation of the instance of the requested type occurs and
   * an implementation for that type is in this list, this parser will be used
   * instead of default parsing of the element. For more info about
   * parsers see {@linkplain IElementParser}.
   * @return
   * @see IElementParser
   * @see IValueParser
   */
  public List<IElementParser> getElementParsers() {
    return elementParsers;
  }

  /**
   * Returns the string representing "null" when found as xml-attribute value or as xml-element text. Default
   * value is '(null)'.
   * @return
   */
  @NotNull
  public String getNullString() {
    return nullString;
  }

  /**
   * See {@linkplain #getNullString()}.
   * @param nullString
   * @see #getNullString()
   */
  public void setNullString(@NotNull String nullString) {
    if (nullString == null) {
        throw new IllegalArgumentException("Value of {nullString} cannot not be null.");
    }

    this.nullString = nullString;
  }

  /**
   * Gets a list of definitions of custom mappings between element-field based on type.<br />
   * This is mainly used for inheritance. If a instance of a specific class should be used instead of parent class, this mapping must be
   * defined in this list. For <i>abstract</i> classes it is required as it is not possible to create
   * an instance of abstract class. How element is defined see {@linkplain XmlCustomFieldMapping}.
   * @return
   * @see XmlCustomFieldMapping
   */
  public List<XmlCustomFieldMapping> getCustomFieldMappings() {
    return customFieldMappings;
  }
}
