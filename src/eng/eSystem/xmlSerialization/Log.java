package eng.eSystem.xmlSerialization;

import static eng.eSystem.utilites.FunctionShortcuts.sf;

public class Log {
  public enum LogLevel {
    verbose(1),
    info(2),
    warning(3),
    error(4);

    private final int value;

    LogLevel(int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }
  }

  private int indent = 0;
  private LogLevel logLevel = LogLevel.warning;

  Log() {
  }

  Log(LogLevel logLevel) {
    this.logLevel = logLevel;
  }

  public void increaseIndent() {
    indent++;
  }

  public void decreaseIndent() {
    indent--;
  }

  public void log(LogLevel logType, String format, Object... params) {
    if (!isAboveLogLevel(logType)) return;

    String s = String.format(format, params);
    String ind = "";
    for (int i = 0; i < indent; i++) {
      ind += "  ";
    }
    System.out.println(sf("EXmlSerialization :: %-8s %02d :: %s %s", logType, indent, ind, s));
  }

  private boolean isAboveLogLevel(LogLevel logType) {
    boolean ret = this.logLevel.getValue() <= logType.getValue();
    return ret;
  }
}
