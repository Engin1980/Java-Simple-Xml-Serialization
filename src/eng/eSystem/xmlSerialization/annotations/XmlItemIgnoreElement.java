package eng.eSystem.xmlSerialization.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
@Repeatable(XmlItemIgnoreElements.class)
public @interface XmlItemIgnoreElement {
  String elementName();
}
