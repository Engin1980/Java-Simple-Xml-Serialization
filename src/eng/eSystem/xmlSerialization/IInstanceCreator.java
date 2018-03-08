package eng.eSystem.xmlSerialization;

public interface IInstanceCreator <T> {
  Class getType();

  T createInstance();
}
