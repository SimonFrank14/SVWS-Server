import { JsonCoreTypeReader } from "./JsonCoreTypeReader";
import Schulform from "@json/schule/Schulform.json";
import BerufskollegAnlage from "@json/schule/BerufskollegAnlage.json";
import AllgemeinbildendOrganisationsformen from "@json/schule/AllgemeinbildendOrganisationsformen.json";
import BerufskollegOrganisationsformen from "@json/schule/BerufskollegOrganisationsformen.json";
import WeiterbildungskollegOrganisationsformen from "@json/schule/WeiterbildungskollegOrganisationsformen.json";
import SchulabschlussAllgemeinbildend from "@json/schule/SchulabschlussAllgemeinbildend.json";
import SchulabschlussBerufsbildend from "@json/schule/SchulabschlussBerufsbildend.json";
import HerkunftBildungsgang from "@json/schueler/HerkunftBildungsgang.json";
import HerkunftBildungsgangTyp from "@json/schueler/HerkunftBildungsgangTyp.json";
import Jahrgaenge from "@json/jahrgang/Jahrgaenge.json";
import PrimarstufeSchuleingangsphaseBesuchsjahre from "@json/jahrgang/PrimarstufeSchuleingangsphaseBesuchsjahre.json";
import Religion from "@json/schule/Religion.json";
import Kindergartenbesuch from "@json/schule/Kindergartenbesuch.json";
import SchuelerStatus from "@json/schueler/SchuelerStatus.json";
import Note from "@json/Note.json";
import Sprachreferenzniveau from "@json/fach/Sprachreferenzniveau.json";
import BerufskollegBildungsgangTyp from "@json/schule/BerufskollegBildungsgangTyp.json";
import WeiterbildungskollegBildungsgangTyp from "@json/schule/WeiterbildungskollegBildungsgangTyp.json";
import Schulgliederung from "@json/schule/Schulgliederung.json";
import Fachgruppe from "@json/fach/Fachgruppe.json";
import Fach from "@json/fach/Fach.json";
import LehrerAbgangsgrund from "@json/lehrer/LehrerAbgangsgrund.json";
import LehrerBeschaeftigungsart from "@json/lehrer/LehrerBeschaeftigungsart.json";
import LehrerEinsatzstatus from "@json/lehrer/LehrerEinsatzstatus.json";
import LehrerFachrichtung from "@json/lehrer/LehrerFachrichtung.json";
import LehrerLehrbefaehigung from "@json/lehrer/LehrerLehrbefaehigung.json";
import LehrerFachrichtungAnerkennung from "@json/lehrer/LehrerFachrichtungAnerkennung.json";
import LehrerLehramt from "@json/lehrer/LehrerLehramt.json";
import LehrerLehramtAnerkennung from "@json/lehrer/LehrerLehramtAnerkennung.json";
import LehrerLehrbefaehigungAnerkennung from "@json/lehrer/LehrerLehrbefaehigungAnerkennung.json";
import LehrerLeitungsfunktion from "@json/lehrer/LehrerLeitungsfunktion.json";
import LehrerRechtsverhaeltnis from "@json/lehrer/LehrerRechtsverhaeltnis.json";
import LehrerZugangsgrund from "@json/lehrer/LehrerZugangsgrund.json";
import BilingualeSprache from "@json/fach/BilingualeSprache.json";
import KAOABerufsfeld from "@json/kaoa/KAOABerufsfeld.json";
import KAOAMerkmaleOptionsarten from "@json/kaoa/KAOAMerkmaleOptionsarten.json";
import KAOAZusatzmerkmaleOptionsarten from "@json/kaoa/KAOAZusatzmerkmaleOptionsarten.json";
import KAOAEbene4 from "@json/kaoa/KAOAEbene4.json";
import KAOAZusatzmerkmal from "@json/kaoa/KAOAZusatzmerkmal.json";
import KAOAAnschlussoptionen from "@json/kaoa/KAOAAnschlussoptionen.json";
import KAOAKategorie from "@json/kaoa/KAOAKategorie.json";
import KAOAMerkmal from "@json/kaoa/KAOAMerkmal.json";
import Klassenart from "@json/klassen/Klassenart.json";
import Uebergangsempfehlung from "@json/schueler/Uebergangsempfehlung.json";
import ZulaessigeKursart from "@json/kurse/ZulaessigeKursart.json";
import Foerderschwerpunkt from "@json/schule/Foerderschwerpunkt.json";
import LehrerAnrechnungsgrund from "@json/lehrer/LehrerAnrechnungsgrund.json";
import LehrerMehrleistungsarten from "@json/lehrer/LehrerMehrleistungsarten.json";
import LehrerMinderleistungsarten from "@json/lehrer/LehrerMinderleistungsarten.json";
import ValidatorenFehlerartKontext from "@json/../validate/ValidatorenFehlerartKontext.json";

export class JsonCoreTypeReaderStatic extends JsonCoreTypeReader {

	mapCoreTypeNameJsonDataImport = new Map<string, object>();

	constructor() {
		super();
		this.mapCoreTypeNameJsonDataImport.set("Schulform", Schulform);
		this.mapCoreTypeNameJsonDataImport.set("BerufskollegAnlage", BerufskollegAnlage);
		this.mapCoreTypeNameJsonDataImport.set("AllgemeinbildendOrganisationsformen", AllgemeinbildendOrganisationsformen);
		this.mapCoreTypeNameJsonDataImport.set("BerufskollegOrganisationsformen", BerufskollegOrganisationsformen);
		this.mapCoreTypeNameJsonDataImport.set("WeiterbildungskollegOrganisationsformen", WeiterbildungskollegOrganisationsformen);
		this.mapCoreTypeNameJsonDataImport.set("SchulabschlussAllgemeinbildend", SchulabschlussAllgemeinbildend);
		this.mapCoreTypeNameJsonDataImport.set("SchulabschlussBerufsbildend", SchulabschlussBerufsbildend);
		this.mapCoreTypeNameJsonDataImport.set("HerkunftBildungsgang", HerkunftBildungsgang);
		this.mapCoreTypeNameJsonDataImport.set("HerkunftBildungsgangTyp", HerkunftBildungsgangTyp);
		this.mapCoreTypeNameJsonDataImport.set("Jahrgaenge", Jahrgaenge);
		this.mapCoreTypeNameJsonDataImport.set("PrimarstufeSchuleingangsphaseBesuchsjahre", PrimarstufeSchuleingangsphaseBesuchsjahre);
		this.mapCoreTypeNameJsonDataImport.set("Religion", Religion);
		this.mapCoreTypeNameJsonDataImport.set("Kindergartenbesuch", Kindergartenbesuch);
		this.mapCoreTypeNameJsonDataImport.set("SchuelerStatus", SchuelerStatus);
		this.mapCoreTypeNameJsonDataImport.set("Note", Note);
		this.mapCoreTypeNameJsonDataImport.set("Sprachreferenzniveau", Sprachreferenzniveau);
		this.mapCoreTypeNameJsonDataImport.set("BerufskollegBildungsgangTyp", BerufskollegBildungsgangTyp);
		this.mapCoreTypeNameJsonDataImport.set("WeiterbildungskollegBildungsgangTyp", WeiterbildungskollegBildungsgangTyp);
		this.mapCoreTypeNameJsonDataImport.set("Schulgliederung", Schulgliederung);
		this.mapCoreTypeNameJsonDataImport.set("Fachgruppe", Fachgruppe);
		this.mapCoreTypeNameJsonDataImport.set("Fach", Fach);
		this.mapCoreTypeNameJsonDataImport.set("LehrerAbgangsgrund", LehrerAbgangsgrund);
		this.mapCoreTypeNameJsonDataImport.set("LehrerBeschaeftigungsart", LehrerBeschaeftigungsart);
		this.mapCoreTypeNameJsonDataImport.set("LehrerEinsatzstatus", LehrerEinsatzstatus);
		this.mapCoreTypeNameJsonDataImport.set("LehrerFachrichtung", LehrerFachrichtung);
		this.mapCoreTypeNameJsonDataImport.set("LehrerLehrbefaehigung", LehrerLehrbefaehigung);
		this.mapCoreTypeNameJsonDataImport.set("LehrerFachrichtungAnerkennung", LehrerFachrichtungAnerkennung);
		this.mapCoreTypeNameJsonDataImport.set("LehrerLehramt", LehrerLehramt);
		this.mapCoreTypeNameJsonDataImport.set("LehrerLehramtAnerkennung", LehrerLehramtAnerkennung);
		this.mapCoreTypeNameJsonDataImport.set("LehrerLehrbefaehigungAnerkennung", LehrerLehrbefaehigungAnerkennung);
		this.mapCoreTypeNameJsonDataImport.set("LehrerLeitungsfunktion", LehrerLeitungsfunktion);
		this.mapCoreTypeNameJsonDataImport.set("LehrerRechtsverhaeltnis", LehrerRechtsverhaeltnis);
		this.mapCoreTypeNameJsonDataImport.set("LehrerZugangsgrund", LehrerZugangsgrund);
		this.mapCoreTypeNameJsonDataImport.set("BilingualeSprache", BilingualeSprache);
		this.mapCoreTypeNameJsonDataImport.set("KAOABerufsfeld", KAOABerufsfeld);
		this.mapCoreTypeNameJsonDataImport.set("KAOAMerkmaleOptionsarten", KAOAMerkmaleOptionsarten);
		this.mapCoreTypeNameJsonDataImport.set("KAOAZusatzmerkmaleOptionsarten", KAOAZusatzmerkmaleOptionsarten);
		this.mapCoreTypeNameJsonDataImport.set("KAOAEbene4", KAOAEbene4);
		this.mapCoreTypeNameJsonDataImport.set("KAOAZusatzmerkmal", KAOAZusatzmerkmal);
		this.mapCoreTypeNameJsonDataImport.set("KAOAAnschlussoptionen", KAOAAnschlussoptionen);
		this.mapCoreTypeNameJsonDataImport.set("KAOAKategorie", KAOAKategorie);
		this.mapCoreTypeNameJsonDataImport.set("KAOAMerkmal", KAOAMerkmal);
		this.mapCoreTypeNameJsonDataImport.set("Klassenart", Klassenart);
		this.mapCoreTypeNameJsonDataImport.set("Uebergangsempfehlung", Uebergangsempfehlung);
		this.mapCoreTypeNameJsonDataImport.set("ZulaessigeKursart", ZulaessigeKursart);
		this.mapCoreTypeNameJsonDataImport.set("Foerderschwerpunkt", Foerderschwerpunkt);
		this.mapCoreTypeNameJsonDataImport.set("LehrerAnrechnungsgrund", LehrerAnrechnungsgrund);
		this.mapCoreTypeNameJsonDataImport.set("LehrerMehrleistungsarten", LehrerMehrleistungsarten);
		this.mapCoreTypeNameJsonDataImport.set("LehrerMinderleistungsarten", LehrerMinderleistungsarten);
		this.mapCoreTypeNameJsonDataImport.set("ValidatorenFehlerartKontext", ValidatorenFehlerartKontext);
		for (const [k,v] of this.mapCoreTypeNameJsonDataImport.entries())
			this.mapCoreTypeNameJsonData.set(k, JSON.stringify(v));
	}
}