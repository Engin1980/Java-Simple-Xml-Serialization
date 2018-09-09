package eng.eSystem.xmlSerialization.annotations;

import eng.eSystem.xmlSerialization.supports.IValueParser;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface XmlValueParser {
  Class<? extends IValueParser> value();
}
