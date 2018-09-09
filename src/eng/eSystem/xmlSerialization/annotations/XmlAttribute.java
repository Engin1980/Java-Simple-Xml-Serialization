package eng.eSystem.xmlSerialization.annotations;

import eng.eSystem.xmlSerialization.supports.IValueParser;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Repeatable(XmlAttributes.class)
public @interface XmlAttribute{
  Class type() default Empty.class;
  String attributeName() default Empty.EMPTY_STRING;
  boolean subclassesIncluded () default false;
  Class<? extends IValueParser> parser() default Empty.class;
}

