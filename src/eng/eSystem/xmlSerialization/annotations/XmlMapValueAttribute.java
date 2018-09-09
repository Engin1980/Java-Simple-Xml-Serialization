package eng.eSystem.xmlSerialization.annotations;

import eng.eSystem.xmlSerialization.supports.IValueParser;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
@Repeatable(XmlMapValueAttributes.class)
public @interface XmlMapValueAttribute {
  Class type();

  String attributeName() ;

  boolean subclassesIncluded() default false;

  Class<? extends IValueParser> parser() default Empty.class;
}