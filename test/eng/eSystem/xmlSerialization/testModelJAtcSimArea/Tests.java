package eng.eSystem.xmlSerialization.testModelJAtcSimArea;

import eng.eSystem.xmlSerialization.XmlCustomFieldMapping;
import eng.eSystem.xmlSerialization.XmlListItemMapping;
import eng.eSystem.xmlSerialization.XmlSerializer;
import org.junit.Test;

import java.io.ByteArrayInputStream;

public class Tests {

  @Test
  public void testDeserialize(){
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<area icao=\"LKAA\">\n" +
        "  <airports>\n" +
        "    <airport icao=\"LKPR\" name=\"Praha Ruzyně\" altitude=\"1247\" transitionAltitude=\"5000\" vfrAltitude=\"2500\"\n" +
        "             mainAirportNavaidName=\"OKL\">\n" +
        "      <densityTraffic useExtendedCallsigns=\"true\" nonCommercialFlightProbability=\"0.05\"\n" +
        "                      delayProbability=\"0.1\" maxDelayInMinutesPerStep=\"45\">\n" +
        "        <companies isFullDayTraffic=\"true\">\n" +
        "          <company code=\"JAI\" weight=\"3\"/>\n" +
        "          <company code=\"AEE\" weight=\"2\"/>\n" +
        "          <company code=\"HOP\" weight=\"2\"/>\n" +
        "          <company code=\"TGZ\" weight=\"2\"/>\n" +
        "          <company code=\"ACA\" weight=\"5\"/>\n" +
        "          <company code=\"AFR\" weight=\"5\"/>\n" +
        "          <company code=\"FIN\" weight=\"4\"/>\n" +
        "          <company code=\"AZA\" weight=\"16\"/>\n" +
        "          <company code=\"BRU\" weight=\"2\"/>\n" +
        "          <company code=\"BAW\" weight=\"8\"/>\n" +
        "          <company code=\"BEE\" weight=\"3\"/>\n" +
        "          <company code=\"BTI\" weight=\"7\"/>\n" +
        "          <company code=\"CFE\" weight=\"2\"/>\n" +
        "          <company code=\"IKB\" weight=\"4\"/>\n" +
        "          <company code=\"DAL\" weight=\"9\"/>\n" +
        "          <company code=\"EZS\" weight=\"2\"/>\n" +
        "          <company code=\"NAX\" weight=\"7\"/>\n" +
        "          <company code=\"EIN\" weight=\"2\"/>\n" +
        "          <company code=\"UAE\" weight=\"1\"/>\n" +
        "          <company code=\"EGW\" weight=\"10\"/>\n" +
        "          <company code=\"EZY\" weight=\"29\"/>\n" +
        "          <company code=\"LZB\" weight=\"2\"/>\n" +
        "          <company code=\"RYR\" weight=\"21\"/>\n" +
        "          <company code=\"SDM\" weight=\"4\"/>\n" +
        "          <company code=\"FDB\" weight=\"1\"/>\n" +
        "          <company code=\"GIA\" weight=\"1\"/>\n" +
        "          <company code=\"CHH\" weight=\"4\"/>\n" +
        "          <company code=\"TRA\" weight=\"3\"/>\n" +
        "          <company code=\"IBE\" weight=\"4\"/>\n" +
        "          <company code=\"ADR\" weight=\"2\"/>\n" +
        "          <company code=\"ASL\" weight=\"5\"/>\n" +
        "          <company code=\"KAL\" weight=\"7\"/>\n" +
        "          <company code=\"KLM\" weight=\"11\"/>\n" +
        "          <company code=\"LGL\" weight=\"1\"/>\n" +
        "          <company code=\"DLH\" weight=\"11\"/>\n" +
        "          <company code=\"LOT\" weight=\"9\"/>\n" +
        "          <company code=\"EXS\" weight=\"10\"/>\n" +
        "          <company code=\"SWR\" weight=\"8\"/>\n" +
        "          <company code=\"ELY\" weight=\"2\"/>\n" +
        "          <company code=\"ANA\" weight=\"1\"/>\n" +
        "          <company code=\"CSA\" weight=\"69\"/>\n" +
        "          <company code=\"AUA\" weight=\"8\"/>\n" +
        "          <company code=\"CTN\" weight=\"1\"/>\n" +
        "          <company code=\"PGT\" weight=\"2\"/>\n" +
        "          <company code=\"AUI\" weight=\"7\"/>\n" +
        "          <company code=\"QFA\" weight=\"2\"/>\n" +
        "          <company code=\"QTR\" weight=\"4\"/>\n" +
        "          <company code=\"TVS\" weight=\"60\"/>\n" +
        "          <company code=\"ROT\" weight=\"3\"/>\n" +
        "          <company code=\"SBI\" weight=\"2\"/>\n" +
        "          <company code=\"SAS\" weight=\"5\"/>\n" +
        "          <company code=\"BEL\" weight=\"5\"/>\n" +
        "          <company code=\"AFL\" weight=\"9\"/>\n" +
        "          <company code=\"THY\" weight=\"6\"/>\n" +
        "          <company code=\"TVF\" weight=\"3\"/>\n" +
        "          <company code=\"TAP\" weight=\"5\"/>\n" +
        "          <company code=\"SVR\" weight=\"2\"/>\n" +
        "          <company code=\"USA\" weight=\"4\"/>\n" +
        "          <company code=\"AEA\" weight=\"1\"/>\n" +
        "          <company code=\"VOE\" weight=\"8\"/>\n" +
        "          <company code=\"HVN\" weight=\"1\"/>\n" +
        "          <company code=\"VLG\" weight=\"9\"/>\n" +
        "          <company code=\"WZZ\" weight=\"6\"/>\n" +
        "        </companies>\n" +
        "        <countries>\n" +
        "          <country code=\"OK\" weight=\"70\" />\n" +
        "          <country code=\"OM\" weight=\"10\" />\n" +
        "          <country code=\"G\" weight=\"10\" />\n" +
        "          <country code=\"D\" weight=\"10\" />\n" +
        "        </countries>\n" +
        "        <density>\n" +
        "          <item hour=\"0\" departures=\"2\" arrivals=\"0\"/>\n" +
        "          <item hour=\"1\" departures=\"0\" arrivals=\"1\"/>\n" +
        "          <item hour=\"2\" departures=\"0\" arrivals=\"0\"/>\n" +
        "          <item hour=\"3\" departures=\"0\" arrivals=\"0\"/>\n" +
        "          <item hour=\"4\" departures=\"0\" arrivals=\"0\"/>\n" +
        "          <item hour=\"5\" departures=\"0\" arrivals=\"1\"/>\n" +
        "          <item hour=\"6\" departures=\"16\" arrivals=\"7\"/>\n" +
        "          <item hour=\"7\" departures=\"15\" arrivals=\"2\"/>\n" +
        "          <item hour=\"8\" departures=\"5\" arrivals=\"9\"/>\n" +
        "          <item hour=\"9\" departures=\"9\" arrivals=\"14\"/>\n" +
        "          <item hour=\"10\" departures=\"15\" arrivals=\"29\"/>\n" +
        "          <item hour=\"11\" departures=\"28\" arrivals=\"21\"/>\n" +
        "          <item hour=\"12\" departures=\"21\" arrivals=\"14\"/>\n" +
        "          <item hour=\"13\" departures=\"10\" arrivals=\"9\"/>\n" +
        "          <item hour=\"14\" departures=\"12\" arrivals=\"11\"/>\n" +
        "          <item hour=\"15\" departures=\"10\" arrivals=\"6\"/>\n" +
        "          <item hour=\"16\" departures=\"9\" arrivals=\"15\"/>\n" +
        "          <item hour=\"17\" departures=\"24\" arrivals=\"28\"/>\n" +
        "          <item hour=\"18\" departures=\"13\" arrivals=\"9\"/>\n" +
        "          <item hour=\"19\" departures=\"8\" arrivals=\"5\"/>\n" +
        "          <item hour=\"20\" departures=\"7\" arrivals=\"26\"/>\n" +
        "          <item hour=\"21\" departures=\"0\" arrivals=\"7\"/>\n" +
        "          <item hour=\"22\" departures=\"13\" arrivals=\"15\"/>\n" +
        "          <item hour=\"23\" departures=\"1\" arrivals=\"7\"/>\n" +
        "        </density>\n" +
        "        <directions>\n" +
        "          <direction heading=\"280\" weight=\"0.4\" />\n" +
        "          <direction heading=\"030\" weight=\"0.1\" />\n" +
        "          <direction heading=\"140\" weight=\"0.3\" />\n" +
        "          <direction heading=\"220\" weight=\"0.2\" />\n" +
        "        </directions>\n" +
        "      </densityTraffic>\n" +
        "      <initialPosition coordinate=\"50.095867 14.265608\" range=\"85\"/>\n" +
        "      <atcTemplates>\n" +
        "        <atcTemplate type=\"ctr\" name=\"LKAA_CTR\" frequency=\"127.12\"\n" +
        "                     releaseAltitude=\"19000\" acceptAltitude=\"7000\" orderedAltitude=\"17000\"/>\n" +
        "        <atcTemplate type=\"twr\" name=\"LKPR_TWR\" frequency=\"118.10\"\n" +
        "                     releaseAltitude=\"300\" acceptAltitude=\"4000\" orderedAltitude=\"5000\"/>\n" +
        "        <atcTemplate type=\"app\" name=\"LKPR_APP\" frequency=\"119.00\"\n" +
        "                     releaseAltitude=\"-1\" acceptAltitude=\"-1\" orderedAltitude=\"-1\"/>\n" +
        "      </atcTemplates>\n" +
        "      <runways>\n" +
        "        <runway active=\"true\">\n" +
        "          <thresholds>\n" +
        "            <threshold name=\"06\" coordinate=\"50 06 06.61 N 014 13 34.58 E\" initialDepartureAltitude=\"5000\"\n" +
        "                       fafCross=\"50 03 44.80 N 014 08 14.12 E\">\n" +
        "              <approaches>\n" +
        "                <approach type=\"ILS_I\" point=\"50 06 06.61 N 014 13 34.58 E\" radial=\"063\" da=\"1380\"\n" +
        "                          gaRoute=\"CM 040\"/>\n" +
        "                <approach type=\"GNSS\" point=\"50 06 06.61 N 014 13 34.58 E\" radial=\"062\" da=\"1600\"\n" +
        "                          gaRoute=\"CM 040\"/>\n" +
        "                <approach type=\"NDB\" point=\"50 07 12 N 014 17 12 E\" radial=\"062\" da=\"1600\"\n" +
        "                          gaRoute=\"FH 062 CM 040\"/>\n" +
        "              </approaches>\n" +
        "              <routes>\n" +
        "                <route type=\"star\" name=\"LOMKI5T\"\n" +
        "                       route=\"PD LOMKI T SL 250 PD PR707 T SL 220 PD BAROX T H BAROX\"/>\n" +
        "                <route type=\"star\" name=\"GOLOP1T\"\n" +
        "                       route=\"PD GOLOP T SL 250 PD PR711 T PD PR712 T PD PR513 T SL 220 PD KUVIX T FH 242\"/>\n" +
        "                <route type=\"star\" name=\"VLM1T\"\n" +
        "                       route=\"PD VLM T PD PR721 T PD PR722 T SL 250 PD PR723 T PD PR523 T SL 220 PD PR521 T PD AKEVA T FH 242\"/>\n" +
        "                <route type=\"star\" name=\"GOSEK2T\"\n" +
        "                       route=\"PD GOSEK T SL 250 PD PR718 T PD PR719 T SL 220 PD PR521 T PD AKEVA T FH 242\"/>\n" +
        "                <route type=\"sid\" name=\"VOZ1E\" category=\"CD\"\n" +
        "                       route=\"PD PR619 T PD PR633 T PD PR635 T PD VOZ\"/>\n" +
        "                <route type=\"sid\" name=\"VOZ1D\" category=\"AB\"\n" +
        "                       route=\"PD PR619 T PD PR625 T PD PR626 T PD PR627 T PD VOZ\"/>\n" +
        "                <route type=\"sid\" name=\"BALTU2E\" category=\"CD\"\n" +
        "                       route=\"PD PR619 T PD PR631 T PD PR632 T PD PR621 T PD ESINU T PD BALTU\"/>\n" +
        "                <route type=\"sid\" name=\"BALTU2D\" category=\"AB\"\n" +
        "                       route=\"PD PR619 T PD PR621 T PD ESINU T PD BALTU\"/>\n" +
        "                <route type=\"sid\" name=\"BALTU2E\" category=\"CD\"\n" +
        "                       route=\"PD PR619 T PD PR631 T PD PR632 T PD PR621 T PD ESINU T PD DOBEN\"/>\n" +
        "                <route type=\"sid\" name=\"BALTU2D\" category=\"AB\"\n" +
        "                       route=\"PD PR619 T PD PR621 T PD ESINU T PD DOBEN\"/>\n" +
        "                <route type=\"sid\" name=\"VENOX2D\" category=\"AB\"\n" +
        "                       route=\"PD PR619 T PD PR621 T PD PR622 T PD VENOX\"/>\n" +
        "                <route type=\"sid\" name=\"VENOX1E\" category=\"CD\"\n" +
        "                       route=\"PD PR619 T PD PR631 T PD PR632 T PD VENOX\"/>\n" +
        "                <route type=\"sid\" name=\"ARTUP1E\" route=\"PD PR619 T PD PR633 T PD PR634 T PD ARTUP\"/>\n" +
        "              </routes>\n" +
        "            </threshold>\n" +
        "            <threshold name=\"24\" coordinate=\"50 06 57.43 N 014 16 24.12 E\" initialDepartureAltitude=\"5000\"\n" +
        "                       preferred=\"true\"\n" +
        "                       fafCross=\"50.14389457717 14.37693911194\">\n" +
        "              <approaches>\n" +
        "                <approach type=\"ILS_I\" point=\"50 06 57.43 N 014 16 24.12 E\" radial=\"242\" da=\"1330\"\n" +
        "                          gaRoute=\"FH 242 CM 040\"/>\n" +
        "                <approach type=\"ILS_II\" point=\"50 06 57.43 N 014 16 24.12 E\" radial=\"242\" da=\"1250\"\n" +
        "                          gaRoute=\"FH 242 CM 040\"/>\n" +
        "                <approach type=\"ILS_III\" point=\"50 06 57.43 N 014 16 24.12 E\" radial=\"242\" da=\"1247\"\n" +
        "                          gaRoute=\"FH 242 CM 040\"/>\n" +
        "                <approach type=\"GNSS\" point=\"50 06 57.43 N 014 16 24.12 E\" radial=\"242\" da=\"1460\"\n" +
        "                          gaRoute=\"FH 242 CM 040\"/>\n" +
        "                <approach type=\"NDB\" point=\"50 07 12 N 014 17 12 E\" radial=\"242\" da=\"1460\"\n" +
        "                          gaRoute=\"FH 242 CM 040\"/>\n" +
        "              </approaches>\n" +
        "              <routes>\n" +
        "                <route type=\"star\" name=\"VLM1S\"\n" +
        "                       route=\"PD VLM T SL 250 PD PR522 T SL 220 PD PR523 T PD RATEV T FH 062\"/>\n" +
        "                <route type=\"star\" name=\"GOSEK2S\"\n" +
        "                       route=\"PD GOSEK T SL 250 PD PR521 T SL 220 PD PR523 T PD RATEV T FH 062\"/>\n" +
        "                <route type=\"star\" name=\"LOMKI4S\"\n" +
        "                       route=\"PD LOMKI T PD PR511 T SL 250 PD PR512 T SL 220 PD PR513 T PD PR518 T PD ERASU T FH 062\"/>\n" +
        "                <route type=\"star\" name=\"GOLOP1S\"\n" +
        "                       route=\"PD GOLOP T SL 250 PD PR516 T SL 220 PD PR517 T PD PR518 T PD ERASU T FH 062\"/>\n" +
        "                <route type=\"sid\" name=\"ARTUP1M\" category=\"AB\"\n" +
        "                       route=\"AA 017 PD PR407 T PD BAGRU T PD PR409 T PD ARTUP\"/>\n" +
        "                <route type=\"sid\" name=\"ARTUP1A\" category=\"CD\"\n" +
        "                       route=\"PD PR402 T PD PR405 T PD PR406 T PD PR407 T PD BAGRU T PD PR409 T PD ARTUP\"/>\n" +
        "                <route type=\"sid\" name=\"VENOX1A\" category=\"AB\"\n" +
        "                       route=\"AA 017 PD PR407 T PD VENOX\"/>\n" +
        "                <route type=\"sid\" name=\"VENOX1A\" category=\"CD\"\n" +
        "                       route=\"PD PR402 T PD PR405 T PD PR406 T PD VENOX\"/>\n" +
        "                <route type=\"sid\" name=\"BALTU2A\"\n" +
        "                       route=\"PD PR402 T PD PR405 T PD BALTU\"/>\n" +
        "                <route type=\"sid\" name=\"DOBEN2A\"\n" +
        "                       route=\"PD PR402 T PD DOBEN\"/>\n" +
        "                <route type=\"sid\" name=\"VOZ1M\" category=\"AB\"\n" +
        "                       route=\"AA 017 PD PR411 T PD PR412 T PD VOZ\"/>\n" +
        "                <route type=\"sid\" name=\"VOZ1A\" category=\"CD\"\n" +
        "                       route=\"PD PR402 T PD PR403 T PD PR404 T PD VOZ\"/>\n" +
        "              </routes>\n" +
        "            </threshold>\n" +
        "          </thresholds>\n" +
        "        </runway>\n" +
        "        <runway active=\"true\">\n" +
        "          <thresholds>\n" +
        "            <threshold name=\"12\" coordinate=\"50 06 28.84 N 014 14 43.32 E\" initialDepartureAltitude=\"5000\"\n" +
        "                       fafCross=\"50.04654820513 14.37903063684\">\n" +
        "              <approaches>\n" +
        "                <approach type=\"GNSS\" point=\"50 06 28.84 N 014 14 43.32 E\" radial=\"124\" da=\"1550\"\n" +
        "                          gaRoute=\"FH 124 CM 040\"/>\n" +
        "                <approach type=\"ILS_I\" point=\"50 06 28.84 N 014 14 43.32 E\" radial=\"124\" da=\"1350\"\n" +
        "                          gaRoute=\"FH 124 CM 040\"/>\n" +
        "                <approach type=\"VORDME\" point=\"50 05 44.80 N 014 15 55.81 E\" radial=\"128\" da=\"1530\"\n" +
        "                          gaRoute=\"FH 124 CM 040\"/>\n" +
        "              </approaches>\n" +
        "              <routes>\n" +
        "                <route type=\"star\" name=\"GOLOP1P\"\n" +
        "                       route=\"PD GOLOP T SL 250 PD PR957 T SL 220 PD PR958 T PD EVEMI T FH 304\"/>\n" +
        "                <route type=\"star\" name=\"VLM1P\"\n" +
        "                       route=\"PD VLM T PD PR950 T SL 250 PD PR951 T PD PR952 T SL 220 PD PR958 T PD EVEMI T FH 304\"/>\n" +
        "                <route type=\"star\" name=\"LOMKI4P\"\n" +
        "                       route=\"PD LOMKI T SL 250 PD PR707 T PD PR956 T SL 220 PD PR954 T PD SOMIS T FH 304\"/>\n" +
        "                <route type=\"star\" name=\"GOSEK2P\"\n" +
        "                       route=\"PD GOSEK T SL 250 PD PR953 T SL 220 PD PR954 T PD SOMIS T FH 304\"/>\n" +
        "                <route type=\"sid\" name=\"VENOX1H\"\n" +
        "                       route=\"PD PR626 T PD PR856 T PD UTORO T PD PR858 T PD VENOX\"/>\n" +
        "                <route type=\"sid\" name=\"BALTU2H\"\n" +
        "                       route=\"PD PR626 T PD PR856 T PD UTORO T PD PR858 T PD ESINU T PD BALTU\"/>\n" +
        "                <route type=\"sid\" name=\"DOBEN2H\" category=\"CD\"\n" +
        "                       route=\"PD PR626 T PD PR856 T PD UTORO T PD PR858 T PD ESINU T PD DOBEN\"/>\n" +
        "                <route type=\"sid\" name=\"DOBEN1G\" category=\"AB\"\n" +
        "                       route=\"AA 017 PD PR854 T PD PR855 T PD DOBEN\"/>\n" +
        "                <route type=\"sid\" name=\"VOZ1G\" category=\"AB\"\n" +
        "                       route=\"AA 017 PD PR411 T PD PR404 T PD VOZ\"/>\n" +
        "                <route type=\"sid\" name=\"VOZ1Q\" category=\"AB\"\n" +
        "                       route=\"AA 017 PD PR411 T PD PR412 T PD VOZ\"/>\n" +
        "                <route type=\"sid\" name=\"VOZ1K\" category=\"CD\"\n" +
        "                       route=\"PD PR626 T PD PR627 T PD VOZ\"/>\n" +
        "                <route type=\"sid\" name=\"VOZ1H\" category=\"CD\"\n" +
        "                       route=\"PD PR626 T PD PR412 T PD VOZ\"/>\n" +
        "                <route type=\"sid\" name=\"ARTUP1H\"\n" +
        "                       route=\"PD PR626 T PD PR861 T PD ARTUP\"/>\n" +
        "              </routes>\n" +
        "            </threshold>\n" +
        "            <threshold name=\"30\" coordinate=\"50 05 25.68 N 014 16 53.02 E\" initialDepartureAltitude=\"5000\"\n" +
        "                       fafCross=\"50.00877 14.47320\">\n" +
        "              <approaches>\n" +
        "                <approach type=\"ILS_I\" point=\"50 06 28.84 N 014 14 43.32 E\" radial=\"304\" da=\"1380\"\n" +
        "                          gaRoute=\"FH 304 CM 040\"/>\n" +
        "                <approach type=\"GNSS\" point=\"50 06 28.84 N 014 14 43.32 E\" radial=\"304\" da=\"1580\"\n" +
        "                          gaRoute=\"FH 304 CM 040\"/>\n" +
        "                <approach type=\"VORDME\" point=\"50 05 44.80 N 014 15 55.81 E\" radial=\"299\" da=\"1630\"\n" +
        "                          gaRoute=\"FH 304 CM 040\"/>\n" +
        "              </approaches>\n" +
        "              <routes>\n" +
        "                <route type=\"star\" name=\"GOLOP2R\"\n" +
        "                       route=\"PD GOLOP T SL 250 PD BAGRU T PD PR914 T SL 220 PD PR915 T PD ARVEG T FH 124\"/>\n" +
        "                <route type=\"star\" name=\"LOMKI6R\"\n" +
        "                       route=\"PD LOMKI T PD PR511 T SL 250 PD PR512 T PD PR914 T SL 220 PD PR915 T PD ARVEG T FH 124\"/>\n" +
        "                <route type=\"star\" name=\"VLM1R\"\n" +
        "                       route=\"PD VLM T SL 250 PD PR904 T SL 220 PD KENOK T H KENOK\"/>\n" +
        "                <route type=\"star\" name=\"GOSEK3R\"\n" +
        "                       route=\"PD GOSEK T SL 250 PD PR901 T PD PR902 T PD PR903 T SL 220 PD PR904 T PD KENOK T H KENOK\"/>\n" +
        "                <route type=\"sid\" name=\"VOZ1N\" category=\"AB\"\n" +
        "                       route=\"AA 017 PD PR807 T PD PR808 T PD VOZ\"/>\n" +
        "                <route type=\"sid\" name=\"VOZ1B\" category=\"CD\"\n" +
        "                       route=\"PD PR813 T PD PR814 T PD PR403 T PD PR404 T PD VOZ\"/>\n" +
        "                <route type=\"sid\" name=\"DOBEN2B\"\n" +
        "                       route=\"PD PR813 T PD PR814 T PD PR817 T PD DOBEN\"/>\n" +
        "                <route type=\"sid\" name=\"BALTU2B\"\n" +
        "                       route=\"PD PR813 T PD PR815 T PD BALTU\"/>\n" +
        "                <route type=\"sid\" name=\"VENOX1B\" category=\"CD\"\n" +
        "                       route=\"PD PR813 T PD PR815 T PD PR816 T PD VENOX\"/>\n" +
        "                <route type=\"sid\" name=\"VENOX1N\" category=\"AB\"\n" +
        "                       route=\"AA 017 PD PR818 T PD VENOX\"/>\n" +
        "                <route type=\"sid\" name=\"ARTUP1N\" category=\"AB\"\n" +
        "                       route=\"AA 017 PD PR818 T PD PR819 T PD ARTUP\"/>\n" +
        "                <route type=\"sid\" name=\"ARTUP1B\" category=\"CD\"\n" +
        "                       route=\"PD PR813 T PD PR815 T PD PR816 T PD PR818 T PD PR819 T PD ARTUP\"/>\n" +
        "              </routes>\n" +
        "            </threshold>\n" +
        "          </thresholds>\n" +
        "        </runway>\n" +
        "        <runway active=\"false\">\n" +
        "          <thresholds>\n" +
        "            <threshold name=\"04\" coordinate=\"50 05 15.77 N 014 16 00.06 E\" initialDepartureAltitude=\"5000\"/>\n" +
        "            <threshold name=\"22\" coordinate=\"50 06 10.50 N 014 17 04.00 E\" initialDepartureAltitude=\"5000\"/>\n" +
        "          </thresholds>\n" +
        "        </runway>\n" +
        "      </runways>\n" +
        "      <holds>\n" +
        "        <!-- generic -->\n" +
        "        <hold navaidName=\"LOMKI\" inboundRadial=\"091\" turn=\"right\"/>\n" +
        "        <hold navaidName=\"BAROX\" inboundRadial=\"062\" turn=\"left\"/>\n" +
        "        <hold navaidName=\"GOSEK\" inboundRadial=\"038\" turn=\"left\"/>\n" +
        "        <hold navaidName=\"VLM\" inboundRadial=\"304\" turn=\"left\"/>\n" +
        "        <hold navaidName=\"GOLOP\" inboundRadial=\"171\" turn=\"left\"/>\n" +
        "        <hold navaidName=\"OKL\" inboundRadial=\"90\" turn=\"right\"/>\n" +
        "        <!-- apps  -->\n" +
        "        <hold navaidName=\"KUVIX\" inboundRadial=\"242\" turn=\"right\"/>\n" +
        "        <hold navaidName=\"AKEVA\" inboundRadial=\"242\" turn=\"left\"/>\n" +
        "        <hold navaidName=\"SOMIS\" inboundRadial=\"304\" turn=\"left\"/>\n" +
        "        <hold navaidName=\"EVEMI\" inboundRadial=\"304\" turn=\"right\"/>\n" +
        "        <hold navaidName=\"ERASU\" inboundRadial=\"62\" turn=\"left\"/>\n" +
        "        <hold navaidName=\"RATEV\" inboundRadial=\"62\" turn=\"right\"/>\n" +
        "        <hold navaidName=\"ARVEG\" inboundRadial=\"123\" turn=\"left\"/>\n" +
        "        <hold navaidName=\"KENOK\" inboundRadial=\"304\" turn=\"left\"/>\n" +
        "      </holds>\n" +
        "      <vfrPoints>\n" +
        "        <vfrPoint name=\"A\" coordinate=\"50 08 17 N 014 14 38 E\" forArrivals=\"false\" forDepartures=\"false\"/>\n" +
        "        <vfrPoint name=\"B\" coordinate=\"50 11 16 N 014 11 09 E\" forArrivals=\"false\" forDepartures=\"false\"/>\n" +
        "        <vfrPoint name=\"E\" coordinate=\"49 59 10 N 014 21 41 E\" forArrivals=\"true\" forDepartures=\"true\"/>\n" +
        "        <vfrPoint name=\"C\" coordinate=\"50 11 18 N 014 02 28 E\" forArrivals=\"false\" forDepartures=\"false\"/>\n" +
        "        <vfrPoint name=\"N\" coordinate=\"50 16 06 N 014 14 21 E\" forArrivals=\"true\" forDepartures=\"true\"/>\n" +
        "        <vfrPoint name=\"S\" coordinate=\"49 57 42 N 014 04 58 E\" forArrivals=\"true\" forDepartures=\"true\"/>\n" +
        "        <vfrPoint name=\"T\" coordinate=\"50 02 59 N 014 16 22 E\" forArrivals=\"false\" forDepartures=\"false\"/>\n" +
        "        <vfrPoint name=\"W\" coordinate=\"50 09 10 N 013 58 59 E\" forArrivals=\"true\" forDepartures=\"true\"/>\n" +
        "      </vfrPoints>\n" +
        "    </airport>\n" +
        "    <airport icao=\"LKMT\" name=\"Ostrava Mošnov\" altitude=\"844\" transitionAltitude=\"5000\" vfrAltitude=\"2500\"\n" +
        "             mainAirportNavaidName=\"OTA\">\n" +
        "      <genericTraffic probabilityOfNonCommercialFlight=\"0.7\" probabilityOfDeparture=\"0.5\"\n" +
        "                      delayProbability=\"0.03\" maxDelayInMinutesPerStep=\"30\" useExtendedCallsigns=\"true\">\n" +
        "        <movementsPerHour>\n" +
        "          <item>0</item>\n" +
        "          <item>0</item>\n" +
        "          <item>0</item>\n" +
        "          <item>0</item>\n" +
        "          <item>0</item>\n" +
        "          <item>0</item>\n" +
        "          <item>2</item>\n" +
        "          <item>0</item>\n" +
        "          <item>4</item>\n" +
        "          <item>2</item>\n" +
        "          <item>7</item>\n" +
        "          <item>12</item>\n" +
        "          <item>3</item>\n" +
        "          <item>3</item>\n" +
        "          <item>5</item>\n" +
        "          <item>17</item>\n" +
        "          <item>13</item>\n" +
        "          <item>11</item>\n" +
        "          <item>3</item>\n" +
        "          <item>1</item>\n" +
        "          <item>0</item>\n" +
        "          <item>0</item>\n" +
        "          <item>0</item>\n" +
        "          <item>0</item>\n" +
        "        </movementsPerHour>\n" +
        "        <probabilityOfCategory>\n" +
        "          <item>0.3</item>\n" +
        "          <item>0.3</item>\n" +
        "          <item>0.2</item>\n" +
        "          <item>0.2</item>\n" +
        "        </probabilityOfCategory>\n" +
        "      </genericTraffic>\n" +
        "      <trafficCategories categoryA=\"0.01\" categoryB=\"0.3\" categoryC=\"0.8\" categoryD=\"0.1\"/>\n" +
        "      <initialPosition coordinate=\"49.697492 18.109075\" range=\"85\"/>\n" +
        "      <atcTemplates>\n" +
        "        <atcTemplate type=\"ctr\" name=\"LKAA_CTR\" frequency=\"127.12\"\n" +
        "                     releaseAltitude=\"14000\" acceptAltitude=\"7000\" orderedAltitude=\"10000\"/>\n" +
        "        <atcTemplate type=\"twr\" name=\"LKMT_TWR\" frequency=\"120.80\"\n" +
        "                     releaseAltitude=\"300\" acceptAltitude=\"4000\" orderedAltitude=\"4000\"/>\n" +
        "        <atcTemplate type=\"app\" name=\"LKMT_APP\" frequency=\"125.10\"\n" +
        "                     releaseAltitude=\"-1\" acceptAltitude=\"-1\" orderedAltitude=\"-1\"/>\n" +
        "      </atcTemplates>\n" +
        "      <runways>\n" +
        "        <runway active=\"true\">\n" +
        "          <thresholds>\n" +
        "            <threshold name=\"04\" coordinate=\"49 41 07.16 N 018 05 35.69 E\" initialDepartureAltitude=\"4000\">\n" +
        "              <approaches>\n" +
        "                <approach type=\"GNSS\" point=\"49 41 07.16 N 018 05 35.69 E\" radial=\"042\" da=\"1190\"\n" +
        "                          gaRoute=\"CM 025 PD MT596 T PD MT598 T CM 035 PD ODRAN\"/>\n" +
        "                <approach type=\"NDB\" point=\"49 40 46.00 N 018 06 32.67 E\" radial=\"042\" da=\"1220\"\n" +
        "                          gaRoute=\"CM 025 PD MT596 T PD MT598 T CM 035 PD ODRAN\"/>\n" +
        "                <approach type=\"VORDME\" point=\"49 41 50.97 N 018 06 32.67 E\" radial=\"039\" da=\"1220\"\n" +
        "                          gaRoute=\"FH 039 CM 025 AA 025 PD MT598 CM 035 AN MT598 PD ODRAN\"/>\n" +
        "              </approaches>\n" +
        "              <routes>\n" +
        "                <route type=\"star\" name=\"BAXEV1W\"\n" +
        "                       route=\"PD BAXEV T PD OPAVO T PD MT602 T PD ODRAN T H ODRAN\"/>\n" +
        "                <route type=\"star\" name=\"REGLI1W\" route=\"PD REGLI T PD MT602 T PD ODRAN T H ODRAN\"/>\n" +
        "                <route type=\"star\" name=\"TUSIN1W\" route=\"PD TUSIN T PD MT715 T PD MORUV T H MORUV\"/>\n" +
        "                <route type=\"star\" name=\"BILNA2W\"\n" +
        "                       route=\"PD BILNA T PD MT714 T PD MT715 T PD MORUV T H MORUV\"/>\n" +
        "                <route type=\"star\" name=\"HLV5W\" route=\"PD HLV T PD POLOM T H POLOM\"/>\n" +
        "                <route type=\"sid\" name=\"HLV5H\"\n" +
        "                       route=\"PD MT701 SM 230 T PD MT715 SR T PD MT716 T PD HLV\"/>\n" +
        "                <route type=\"sid\" name=\"BILNA3H\"\n" +
        "                       route=\"PD MT701 T PD MT702 T PD MT712 T PD MT713 T PD BILNA\"/>\n" +
        "                <route type=\"sid\" name=\"NETIR1H\"\n" +
        "                       route=\"PD MT701 T PD MT702 T PD MT705 T PD MT708 T PD MT709 T PD NETIR\"/>\n" +
        "                <route type=\"sid\" name=\"BAVOK1H\"\n" +
        "                       route=\"PD MT701 T PD MT702 T PD MT705 T PD MT706 T PD BAVOK\"/>\n" +
        "                <route type=\"sid\" name=\"REGLI1H\" route=\"PD MT701 T PD MT702 T PD MT703 T PD REGLI\"/>\n" +
        "                <route type=\"sid\" name=\"BAXEV1H\"\n" +
        "                       route=\"PD MT701 T PD MT702 T PD MT703 T PD OPAVO T PD BAXEV\"/>\n" +
        "              </routes>\n" +
        "            </threshold>\n" +
        "            <threshold name=\"22\" coordinate=\"49 42 25.65 N 018 07 42.41 E\" initialDepartureAltitude=\"4000\"\n" +
        "                       preferred=\"true\">\n" +
        "              <approaches>\n" +
        "                <approach type=\"ILS_I\" point=\"49 42 25.65 N 018 07 42.41 E\" radial=\"222\" da=\"970\"\n" +
        "                          gaRoute=\"CM 030 PD MT597 T PD MT598 T PD BOGTU\"/>\n" +
        "                <approach type=\"ILS_II\" point=\"49 42 25.65 N 018 07 42.41 E\" radial=\"222\" da=\"890\"\n" +
        "                          gaRoute=\"CM 030 PD MT597 T PD MT598 T PD BOGTU\"/>\n" +
        "                <approach type=\"GNSS\" point=\"49 42 25.65 N 018 07 42.41 E\" radial=\"222\" da=\"1175\"\n" +
        "                          gaRoute=\"CM 030 PD MT597 T PD MT598 T PD BOGTU\"/>\n" +
        "                <approach type=\"NDB\" point=\"49 42 48.00 N 018 08 17.00 E\" radial=\"222\" da=\"1170\"\n" +
        "                          gaRoute=\"FH 222 CM 025 T PD MT598 CM 030 AN MT598 PD BOGTU\"/>\n" +
        "                <approach type=\"VORDME\" point=\"49 41 50.97 N 018 06 32.67 E\" radial=\"226\" da=\"1170\"\n" +
        "                          gaRoute=\"FH 222 CM 025 T PD MT598 CM 030 AN MT598 PD BOGTU\"/>\n" +
        "              </approaches>\n" +
        "              <routes>\n" +
        "                <route type=\"star\" name=\"BAXEV1T\"\n" +
        "                       route=\"PD BAXEV T PD OPAVO T PD MT807 T PD BOGTU T H BOGTU\"/>\n" +
        "                <route type=\"star\" name=\"REGLI1T\"\n" +
        "                       route=\"PD REGLI T PD MT806 T PD MT807 T PD BOGTU T H BOGTU\"/>\n" +
        "                <route type=\"star\" name=\"BAVOK1T\"\n" +
        "                       route=\"PD BAVOK T PD MT806 T PD MT807 T PD BOGTU T H BOGTU\"/>\n" +
        "                <route type=\"star\" name=\"TUSIN1T\"\n" +
        "                       route=\"PD TUSIN T PD MT805 T PD MT807 T PD BOGTU T H BOGTU\"/>\n" +
        "                <route type=\"star\" name=\"HLV2T\"\n" +
        "                       route=\"PD HLV T PD MT716 T PD MT715 T PD EKMIT T H EKMIT\"/>\n" +
        "                <route type=\"star\" name=\"BILNA2T\"\n" +
        "                       route=\"PD BILNA T PD MT714 T PD MT715 T PD EKMIT T H EKMIT\"/>\n" +
        "                <route type=\"sid\" name=\"BAXEV1F\" route=\"PD MT511 T PD MT512 T PD OPAVO T PD BAXEV\"/>\n" +
        "                <route type=\"sid\" name=\"REGLI1F\" route=\"PD MT511 T PD MT512 T PD REGLI\"/>\n" +
        "                <route type=\"sid\" name=\"BAVOK1F\" route=\"PD MT511 T PD MT512 T PD BAVOK\"/>\n" +
        "                <route type=\"sid\" name=\"HLV5F\" route=\"PD MT521 T PD POLOM T PD HLV\"/>\n" +
        "                <route type=\"sid\" name=\"BILNA2F\" route=\"PD MT521 T PD MT522 T PD MT523 T PD BILNA\"/>\n" +
        "                <route type=\"sid\" name=\"NETIR1F\" route=\"PD MT521 T PD MT522 T PD MT523 T PD NETIR\"/>\n" +
        "              </routes>\n" +
        "            </threshold>\n" +
        "          </thresholds>\n" +
        "        </runway>\n" +
        "      </runways>\n" +
        "      <holds>\n" +
        "        <!-- generic -->\n" +
        "        <hold navaidName=\"OTA\" inboundRadial=\"042\" turn=\"left\"/>\n" +
        "        <!-- apps  -->\n" +
        "        <hold navaidName=\"ODRAN\" inboundRadial=\"212\" turn=\"right\"/>\n" +
        "        <hold navaidName=\"POLOM\" inboundRadial=\"041\" turn=\"right\"/>\n" +
        "        <hold navaidName=\"MORUV\" inboundRadial=\"222\" turn=\"left\"/>\n" +
        "        <hold navaidName=\"BOGTU\" inboundRadial=\"042\" turn=\"left\"/>\n" +
        "        <hold navaidName=\"EKMIT\" inboundRadial=\"042\" turn=\"right\"/>\n" +
        "      </holds>\n" +
        "      <vfrPoints>\n" +
        "        <!-- TODO -->\n" +
        "      </vfrPoints>\n" +
        "    </airport>\n" +
        "  </airports>\n" +
        "</area>\n";


    eng.eSystem.xmlSerialization.Settings sett = new eng.eSystem.xmlSerialization.Settings();

    // ignores
    sett.getIgnoredFieldsRegex().add("^_.+");
    sett.getIgnoredFieldsRegex().add("^parent$");
    sett.getIgnoredFieldsRegex().add("^binded$");

    // list mappings
    sett.getListItemMappings().add(
        new XmlListItemMapping("/airports$", Airport.class));
    sett.getListItemMappings().add(
        new XmlListItemMapping("/runways$", Runway.class));
//    sett.getListItemMappings().add(
//        new XmlListItemMapping("thresholds", RunwayThreshold.class));
//    sett.getListItemMappings().add(
//        new XmlListItemMapping("approaches", Approach.class));
//    sett.getListItemMappings().add(
//        new XmlListItemMapping("routes", Route.class));
//    sett.getListItemMappings().add(
//        new XmlListItemMapping("atcTemplates", AtcTemplate.class));
//    sett.getListItemMappings().add(
//        new XmlListItemMapping("holds", PublishedHold.class));
//    sett.getListItemMappings().add(
//        new XmlListItemMapping("vfrPoints", VfrPoint.class));
//    sett.getListItemMappings().add(
//        new XmlListItemMapping("navaids", Navaid.class));
//    sett.getListItemMappings().add(
//        new XmlListItemMapping("borders", Border.class));
//    sett.getListItemMappings().add(
//        new XmlListItemMapping("points", "point", BorderExactPoint.class));
//    sett.getListItemMappings().add(
//        new XmlListItemMapping("points", "arc", BorderArcPoint.class));
//    sett.getListItemMappings().add(
//        new XmlListItemMapping("companies", DensityBasedTraffic.CodeWeight.class));
//    sett.getListItemMappings().add(
//        new XmlListItemMapping("countries", DensityBasedTraffic.CodeWeight.class));
//    sett.getListItemMappings().add(
//        new XmlListItemMapping("density", DensityBasedTraffic.HourBlockMovements.class));
//    sett.getListItemMappings().add(
//        new XmlListItemMapping("directions", DensityBasedTraffic.DirectionWeight.class));

    // own parsers
//    sett.getValueParsers().add(new CoordinateValueParser());

    // instance creators
//    sett.getInstanceCreators().add(new AreaCreator());

    // traffic inherited parsers
//    sett.getCustomFieldMappings().add(
//        new XmlCustomFieldMapping(
//            "traffic", Traffic.class, GenericTraffic.class, Airport.class, "genericTraffic"));
//    sett.getCustomFieldMappings().add(
//        new XmlCustomFieldMapping(
//            "traffic", Traffic.class, DensityBasedTraffic.class, Airport.class, "densityTraffic"));
//    sett.getCustomFieldMappings().add(
//        new XmlCustomFieldMapping(
//            "traffic", Traffic.class, FlightListTraffic.class, Airport.class, "flightListTrafic"));

    //sett.setVerbose(true);

    XmlSerializer ser = new XmlSerializer(sett);
    ByteArrayInputStream bis = new ByteArrayInputStream(xml.getBytes());

    Area ret = (Area) ser.deserialize(bis, Area.class);
  }

}
