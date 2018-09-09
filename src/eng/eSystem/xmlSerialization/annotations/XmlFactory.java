package eng.eSystem.xmlSerialization.annotations;

import eng.eSystem.xmlSerialization.supports.IFactory;

public @interface XmlFactory {
  Class<? extends IFactory> value();
}
