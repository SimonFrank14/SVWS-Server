import { JavaObject } from '../../../java/lang/JavaObject';
import { Schulform, cast_de_svws_nrw_core_types_schule_Schulform } from '../../../core/types/schule/Schulform';

export class HerkunftsartKatalogEintragBezeichnung extends JavaObject {

	/**
	 * Das Kürzel der Schulform
	 */
	public schulform : string = "";

	/**
	 * Die Kurz-Bezeichnung der Herkunftsart
	 */
	public kurzBezeichnung : string = "";

	/**
	 * Die Bezeichnung der Herkunftsart
	 */
	public bezeichnung : string = "";


	/**
	 * Erstellt einen Eintrag mit Standardwerten
	 */
	public constructor();

	/**
	 * Erstellt einen Eintrag mit den angegebenen Werten
	 *
	 * @param schulform         die Schulform
	 * @param kurzBezeichnung   die Kurz-Bezeichnung der Herkunftsart
	 * @param bezeichnung       die Bezeichnung der Herkunftsart
	 */
	public constructor(schulform : Schulform, kurzBezeichnung : string, bezeichnung : string);

	/**
	 * Implementation for method overloads of 'constructor'
	 */
	public constructor(__param0? : Schulform, __param1? : string, __param2? : string) {
		super();
		if ((typeof __param0 === "undefined") && (typeof __param1 === "undefined") && (typeof __param2 === "undefined")) {
			// empty block
		} else if (((typeof __param0 !== "undefined") && ((__param0 instanceof JavaObject) && ((__param0 as JavaObject).isTranspiledInstanceOf('de.svws_nrw.core.types.schule.Schulform')))) && ((typeof __param1 !== "undefined") && (typeof __param1 === "string")) && ((typeof __param2 !== "undefined") && (typeof __param2 === "string"))) {
			const schulform : Schulform = cast_de_svws_nrw_core_types_schule_Schulform(__param0);
			const kurzBezeichnung : string = __param1;
			const bezeichnung : string = __param2;
			this.schulform = schulform.daten.kuerzel;
			this.kurzBezeichnung = kurzBezeichnung;
			this.bezeichnung = bezeichnung;
		} else throw new Error('invalid method overload');
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.data.schule.HerkunftsartKatalogEintragBezeichnung'].includes(name);
	}

	public static transpilerFromJSON(json : string): HerkunftsartKatalogEintragBezeichnung {
		const obj = JSON.parse(json);
		const result = new HerkunftsartKatalogEintragBezeichnung();
		if (typeof obj.schulform === "undefined")
			 throw new Error('invalid json format, missing attribute schulform');
		result.schulform = obj.schulform;
		if (typeof obj.kurzBezeichnung === "undefined")
			 throw new Error('invalid json format, missing attribute kurzBezeichnung');
		result.kurzBezeichnung = obj.kurzBezeichnung;
		if (typeof obj.bezeichnung === "undefined")
			 throw new Error('invalid json format, missing attribute bezeichnung');
		result.bezeichnung = obj.bezeichnung;
		return result;
	}

	public static transpilerToJSON(obj : HerkunftsartKatalogEintragBezeichnung) : string {
		let result = '{';
		result += '"schulform" : ' + JSON.stringify(obj.schulform!) + ',';
		result += '"kurzBezeichnung" : ' + JSON.stringify(obj.kurzBezeichnung!) + ',';
		result += '"bezeichnung" : ' + JSON.stringify(obj.bezeichnung!) + ',';
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

	public static transpilerToJSONPatch(obj : Partial<HerkunftsartKatalogEintragBezeichnung>) : string {
		let result = '{';
		if (typeof obj.schulform !== "undefined") {
			result += '"schulform" : ' + JSON.stringify(obj.schulform!) + ',';
		}
		if (typeof obj.kurzBezeichnung !== "undefined") {
			result += '"kurzBezeichnung" : ' + JSON.stringify(obj.kurzBezeichnung!) + ',';
		}
		if (typeof obj.bezeichnung !== "undefined") {
			result += '"bezeichnung" : ' + JSON.stringify(obj.bezeichnung!) + ',';
		}
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

}

export function cast_de_svws_nrw_core_data_schule_HerkunftsartKatalogEintragBezeichnung(obj : unknown) : HerkunftsartKatalogEintragBezeichnung {
	return obj as HerkunftsartKatalogEintragBezeichnung;
}