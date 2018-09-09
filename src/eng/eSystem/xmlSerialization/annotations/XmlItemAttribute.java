package eng.eSystem.xmlSerialization.annotations;

import eng.eSystem.xmlSerialization.supports.IValueParser;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
@Repeatable(XmlItemAttributes.class)
public @interface XmlItemAttribute  {
  Class type() ;
  String attributeName() ;
  boolean subclassesIncluded () default false;
  Class<? extends IValueParser> parser() default Empty.class;
}