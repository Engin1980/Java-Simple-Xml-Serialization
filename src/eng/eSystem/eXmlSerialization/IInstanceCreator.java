package eng.eSystem.eXmlSerialization;

public interface IInstanceCreator <T> {
  String getTypeName();

  T createInstance();
}
