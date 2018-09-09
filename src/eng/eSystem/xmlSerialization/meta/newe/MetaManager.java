package eng.eSystem.xmlSerialization.meta.newe;

import eng.eSystem.collections.EList;
import eng.eSystem.collections.IList;

public class MetaManager {
  final IList<CoercionMapping> coercionMapping = new EList<>();
  final IList<FieldMapping> fieldMappings = new EList<>();
  final IList<FieldMapping> itemMappings = new EList<>();
  final IList<FieldMapping> keyMappings = new EList<>();
  final IList<FieldMapping> valueMappings = new EList<>();
  final IList<TypeMapping> typeMappings = new EList<>();
  final IList<TypeMapping> typeItemMappings = new EList<>();
  final IList<TypeMapping> typeKeyMappings = new EList<>();
  final IList<TypeMapping> typeValueMappings = new EList<>();
  final IList<CustomValueParser> valueParsers = new EList<>();
  final IList<CustomElementParser> elementParsers = new EList<>();
  final IList<Factory> factories = new EList<>();
}
