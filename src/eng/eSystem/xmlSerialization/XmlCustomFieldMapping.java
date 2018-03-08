package eng.eSystem.xmlSerialization;

public class XmlCustomFieldMapping {
  private String fieldName;
  private Class declaredFieldType;
  private String xmlElementName;
  private Class targetFieldType;

  /**
   * Creates a new instance of declaration.
   * @param fieldName
   * @param declaredFieldType
   * @param xmlElementName
   * @param targetFieldType
   */
  public XmlCustomFieldMapping(Class declaredFieldType, String fieldName, String xmlElementName, Class targetFieldType) {
    this.fieldName = fieldName;
    this.declaredFieldType = declaredFieldType;
    this.xmlElementName = xmlElementName;
    this.targetFieldType = targetFieldType;
  }

  public String getFieldName() {
    return fieldName;
  }

  public Class getDeclaredFieldType() {
    return declaredFieldType;
  }

  public String getXmlElementName() {
    return xmlElementName;
  }

  public Class getTargetFieldType() {
    return targetFieldType;
  }
}
