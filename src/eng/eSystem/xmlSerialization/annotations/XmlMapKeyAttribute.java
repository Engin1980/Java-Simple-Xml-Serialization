package eng.eSystem.xmlSerialization.annotations;

import eng.eSystem.xmlSerialization.supports.IValueParser;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
@Repeatable(XmlMapKeyAttributes.class)
public @interface XmlMapKeyAttribute  {
  Class type();
  String attributeName() ;
  boolean subclassesIncluded () default false;
  Class<? extends IValueParser> parser() default Empty.class;
}