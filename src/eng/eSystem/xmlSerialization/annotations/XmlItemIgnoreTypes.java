package eng.eSystem.xmlSerialization.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface XmlItemIgnoreTypes {
  XmlItemIgnoreType[] value();
}
