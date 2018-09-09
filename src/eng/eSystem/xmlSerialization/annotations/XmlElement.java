package eng.eSystem.xmlSerialization.annotations;

import eng.eSystem.xmlSerialization.supports.IElementParser;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Repeatable(XmlElements.class)
public @interface XmlElement {
  Class type() default Empty.class;
  String elementName() default Empty.EMPTY_STRING;
  boolean subclassesIncluded () default false;
  Class<? extends IElementParser> parser() default Empty.class;
}
