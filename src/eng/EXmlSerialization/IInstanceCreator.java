package eng.EXmlSerialization;

public interface IInstanceCreator <T> {
  String getTypeName();

  T createInstance();
}
