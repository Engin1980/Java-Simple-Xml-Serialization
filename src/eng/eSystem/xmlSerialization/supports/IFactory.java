package eng.eSystem.xmlSerialization.supports;

public interface IFactory<T> {
  Class<? extends T> getType();

  T createInstance();
}
