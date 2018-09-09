package eng.eSystem.xmlSerialization.annotations;

import eng.eSystem.xmlSerialization.supports.IElementParser;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
@Repeatable(XmlItemElements.class)
public @interface XmlItemElement  {
  Class type();
  String elementName();
  boolean subclassesIncluded () default false;
  Class<? extends IElementParser> parser() default Empty.class;
}
