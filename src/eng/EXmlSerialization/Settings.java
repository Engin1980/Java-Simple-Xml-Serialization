package eng.EXmlSerialization;

import java.util.ArrayList;
import java.util.List;

public class Settings {
  /**
   * If true then there are debug values printed on the console.
   */
  private boolean verbose = false;

  /**
   * Gets the default list implementation used when an implementation of list must be created.
   */
  private Class defaultListTypeImplementation = ArrayList.class;

  /**
   * Contains a list of regexes defining which field(-names) should be skipped.
   */
  private final List<String> ignoredFieldsRegex = new ArrayList<>();

  /**
   * Contains objects doing custom class instance creation.
   */
  private final List<IInstanceCreator> instanceCreators = new ArrayList<>();

  /**
   * Contains custom parsers from XMl to specific type
   */
  private final List<IValueParser> valueParsers = new ArrayList<>();

  public List<XmlListItemMapping> getListItemMapping() {
    return listItemMapping;
  }

  /**
   * Contains definition into which type list items are mapped.
   */
  private final List<XmlListItemMapping> listItemMapping = new ArrayList();

  public boolean isVerbose() {
    return verbose;
  }

  public List<String> getIgnoredFieldsRegex() {
    return ignoredFieldsRegex;
  }

  public void setVerbose(boolean verbose) {
    this.verbose = verbose;
  }

  public Class getDefaultListTypeImplementation() {
    return defaultListTypeImplementation;
  }

  public void setDefaultListTypeImplementation(Class defaultListTypeImplementation) {
    this.defaultListTypeImplementation = defaultListTypeImplementation;
  }

  public List<IInstanceCreator> getInstanceCreators() {
    return instanceCreators;
  }

  public List<IValueParser> getValueParsers() {
    return valueParsers;
  }
}
