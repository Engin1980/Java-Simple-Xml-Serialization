package eng.eSystem.xmlSerialization;

public interface IInstanceCreator <T> {
  String getTypeName();

  T createInstance();
}
