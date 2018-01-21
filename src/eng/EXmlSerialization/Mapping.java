/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eng.EXmlSerialization;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Marek
 */
class Mapping {
  private final static List<MapItem> listMapping = new ArrayList();
  private final static List<Class> simpleTypes = new ArrayList();

  static {
    /*
    mapAdd("Area.airports", Airport.class);
    mapAdd("Airport.runways", Runway.class);
    mapAdd("Airport.atcTemplates", AtcTemplate.class);
    mapAdd("Airport.holds", PublishedHold.class);
    mapAdd("Airport.vfrPoints", VfrPoint.class);
    mapAdd("Runway.thresholds", RunwayThreshold.class);
    mapAdd("RunwayThreshold.approaches", Approach.class);
    mapAdd("RunwayThreshold.routes", Route.class);
    mapAdd("Area.navaids", Navaid.class);
    mapAdd("Area.borders", Border.class);
    mapAdd("Border.points", "point", BorderExactPoint.class);
    mapAdd("Border.points", "arc", BorderArcPoint.class);

    mapAdd("AirplaneTypes", AirplaneType.class);

    mapAdd("Settings.dispItems", DispItem.class);
    mapAdd("Settings.dispPlanes", DispPlane.class);
    mapAdd("Settings.dispTexts", DispText.class);
    */

    simpleTypes.add(Integer.class);
    simpleTypes.add(int.class);
    simpleTypes.add(Double.class);
    simpleTypes.add(double.class);
    simpleTypes.add(Boolean.class);
    simpleTypes.add(boolean.class);
    simpleTypes.add(String.class);
//    simpleTypes.add(Coordinate.class);
    simpleTypes.add(Color.class);
    simpleTypes.add(char.class);
    simpleTypes.add(Character.class);
  }

  private static void mapAdd (String typeAndPropertyName, String elementName, Class targetType){
    MapItem mi = new MapItem(typeAndPropertyName, elementName, targetType);
    listMapping.add(mi);
  }
  private static void mapAdd (String typeAndPropertyName, Class targetType){
    mapAdd(typeAndPropertyName, null, targetType);
  }

  static boolean isSimpleTypeOrEnum(Class c) {
    return  simpleTypes.contains(c) || c.isEnum();
  }

  static Class getMappedType(String key, String elementName) {
    Class ret = null;
    for (MapItem mi : listMapping){
      if (mi.collectionProperty.equals(key)){
        if (mi.elementNameOrNull == null){
          ret = mi.itemType;
        } else if (
            elementName.equals(mi.elementNameOrNull)){
          ret = mi.itemType;
        }
      }
    }

    return ret;
  }
}
