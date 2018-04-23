package eng.eSystem.xmlSerialization;

public class XmlCustomFieldMapping {
  private final String fieldName;
  private final Class classDeclaringField;
  private final String xmlElementName;
  private final Class targetFieldClass;
  private final Class declaredFieldClass;


  /**
   * Creates a new instance of declaration.
   * @param fieldName Name of mapped field. Mandatory.
   * @param declaredFieldClass Class of declared value of mapped field. Mandatory.
   * @param classDeclaringField Class declaring field, or null. Optional.
   * @param targetFieldClass Class of instance in field value (real value), or null. Optional.
   * @param xmlElementName Element name with serialized field data. Mandatory.
   */
  public XmlCustomFieldMapping(String fieldName, Class declaredFieldClass, Class targetFieldClass, Class classDeclaringField, String xmlElementName) {
    if (fieldName == null) {
        throw new IllegalArgumentException("Value of {fieldName} cannot not be null.");
    }
    if (declaredFieldClass == null) {
        throw new IllegalArgumentException("Value of {declaredFieldClass} cannot not be null.");
    }
    if (xmlElementName == null) {
        throw new IllegalArgumentException("Value of {xmlElementName} cannot not be null.");
    }
    
    this.fieldName = fieldName;
    this.classDeclaringField = classDeclaringField;
    this.xmlElementName = xmlElementName;
    this.targetFieldClass = targetFieldClass;
    this.declaredFieldClass = declaredFieldClass;
  }

  /**
   * Creates a new instance of declaration.
   * @param fieldName Name of mapped field. Mandatory.
   * @param declaredFieldClass Class of value of mapped field. Mandatory.
   * @param xmlElementName Element name with serialized field data. Mandatory.
   */
  public XmlCustomFieldMapping(String fieldName, Class declaredFieldClass, String xmlElementName) {
    if (fieldName == null) {
      throw new IllegalArgumentException("Value of {fieldName} cannot not be null.");
    }
    if (declaredFieldClass == null) {
      throw new IllegalArgumentException("Value of {declaredFieldClass} cannot not be null.");
    }
    if (xmlElementName == null) {
      throw new IllegalArgumentException("Value of {xmlElementName} cannot not be null.");
    }

    this.fieldName = fieldName;
    this.classDeclaringField = null;
    this.xmlElementName = xmlElementName;
    this.targetFieldClass = null;
    this.declaredFieldClass = declaredFieldClass;
  }

  public String getFieldName() {
    return fieldName;
  }

  public Class getClassDeclaringField() {
    return classDeclaringField;
  }

  public String getXmlElementName() {
    return xmlElementName;
  }

  public Class getTargetFieldClass() {
    return targetFieldClass;
  }

  public Class getDeclaredFieldClass() {
    return declaredFieldClass;
  }
}
