package eng.EXmlSerialization;

import java.util.ArrayList;
import java.util.List;

public class Settings {
  /**
   * If true then there are debug values printed on the console.
   */
  private boolean verbose = false;

  /**
   * Contains a list of regexes defining which field(-names) should be skipped.
   */
  private List<String> ignoredFieldsRegex = new ArrayList<>();

  public boolean isVerbose() {
    return verbose;
  }

  public List<String> getIgnoredFieldsRegex() {
    return ignoredFieldsRegex;
  }

  public void setVerbose(boolean verbose) {
    this.verbose = verbose;
  }
}
