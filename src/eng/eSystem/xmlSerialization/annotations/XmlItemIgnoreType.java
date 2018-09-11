package eng.eSystem.xmlSerialization.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
@Repeatable(XmlItemIgnoreTypes.class)
public @interface XmlItemIgnoreType {
  Class type();
  boolean subClassIncluded () default false;
}
