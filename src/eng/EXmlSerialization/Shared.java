package eng.EXmlSerialization;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Shared {
  public static boolean isRegexMatch(String regex, String text){
    Pattern p = Pattern.compile(regex);
    Matcher m = p.matcher(text);
    boolean ret = m.find();
    return ret;
  }
}
