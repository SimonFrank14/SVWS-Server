import { JavaObject } from '../../../java/lang/JavaObject';

export class KatalogEintragOrtsteile extends JavaObject {

	/**
	 * Katalog von IT.NRW PLZ Ortsteile: die ID des Katalog-Eintrags
	 */
	public ID : number = -1;

	/**
	 * Katalog von IT.NRW PLZ Ortsteile: die Postleitzahl
	 */
	public PLZ : string = "";

	/**
	 * Katalog von IT.NRW PLZ Ortsteile: der Regionalschlüssel
	 */
	public RegSchl : string = "";

	/**
	 * Katalog von IT.NRW PLZ Ortsteile: die Ortsbezeichnung
	 */
	public Ort : string = "";

	/**
	 * Katalog von IT.NRW PLZ Ortsteile: die Bezeichnung des Ortsteils
	 */
	public Ortsteil : string = "";

	/**
	 * Katalog von IT.NRW PLZ Ortsteile: der Stand des Katalog-Eintrags
	 */
	public Stand : string = "";

	/**
	 * Katalog von IT.NRW PLZ Ortsteile: Gibt die Gültigkeit ab welchem Schuljahr an
	 */
	public gueltigVon : number | null = null;

	/**
	 * Katalog von IT.NRW PLZ Ortsteile: Gibt die Gültigkeit bis zu welchem Schuljahr an
	 */
	public gueltigBis : number | null = null;


	public constructor() {
		super();
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.data.kataloge.KatalogEintragOrtsteile'].includes(name);
	}

	public static transpilerFromJSON(json : string): KatalogEintragOrtsteile {
		const obj = JSON.parse(json);
		const result = new KatalogEintragOrtsteile();
		if (typeof obj.ID === "undefined")
			 throw new Error('invalid json format, missing attribute ID');
		result.ID = obj.ID;
		if (typeof obj.PLZ === "undefined")
			 throw new Error('invalid json format, missing attribute PLZ');
		result.PLZ = obj.PLZ;
		if (typeof obj.RegSchl === "undefined")
			 throw new Error('invalid json format, missing attribute RegSchl');
		result.RegSchl = obj.RegSchl;
		if (typeof obj.Ort === "undefined")
			 throw new Error('invalid json format, missing attribute Ort');
		result.Ort = obj.Ort;
		if (typeof obj.Ortsteil === "undefined")
			 throw new Error('invalid json format, missing attribute Ortsteil');
		result.Ortsteil = obj.Ortsteil;
		if (typeof obj.Stand === "undefined")
			 throw new Error('invalid json format, missing attribute Stand');
		result.Stand = obj.Stand;
		result.gueltigVon = typeof obj.gueltigVon === "undefined" ? null : obj.gueltigVon === null ? null : obj.gueltigVon;
		result.gueltigBis = typeof obj.gueltigBis === "undefined" ? null : obj.gueltigBis === null ? null : obj.gueltigBis;
		return result;
	}

	public static transpilerToJSON(obj : KatalogEintragOrtsteile) : string {
		let result = '{';
		result += '"ID" : ' + obj.ID + ',';
		result += '"PLZ" : ' + JSON.stringify(obj.PLZ!) + ',';
		result += '"RegSchl" : ' + JSON.stringify(obj.RegSchl!) + ',';
		result += '"Ort" : ' + JSON.stringify(obj.Ort!) + ',';
		result += '"Ortsteil" : ' + JSON.stringify(obj.Ortsteil!) + ',';
		result += '"Stand" : ' + JSON.stringify(obj.Stand!) + ',';
		result += '"gueltigVon" : ' + ((!obj.gueltigVon) ? 'null' : obj.gueltigVon) + ',';
		result += '"gueltigBis" : ' + ((!obj.gueltigBis) ? 'null' : obj.gueltigBis) + ',';
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

	public static transpilerToJSONPatch(obj : Partial<KatalogEintragOrtsteile>) : string {
		let result = '{';
		if (typeof obj.ID !== "undefined") {
			result += '"ID" : ' + obj.ID + ',';
		}
		if (typeof obj.PLZ !== "undefined") {
			result += '"PLZ" : ' + JSON.stringify(obj.PLZ!) + ',';
		}
		if (typeof obj.RegSchl !== "undefined") {
			result += '"RegSchl" : ' + JSON.stringify(obj.RegSchl!) + ',';
		}
		if (typeof obj.Ort !== "undefined") {
			result += '"Ort" : ' + JSON.stringify(obj.Ort!) + ',';
		}
		if (typeof obj.Ortsteil !== "undefined") {
			result += '"Ortsteil" : ' + JSON.stringify(obj.Ortsteil!) + ',';
		}
		if (typeof obj.Stand !== "undefined") {
			result += '"Stand" : ' + JSON.stringify(obj.Stand!) + ',';
		}
		if (typeof obj.gueltigVon !== "undefined") {
			result += '"gueltigVon" : ' + ((!obj.gueltigVon) ? 'null' : obj.gueltigVon) + ',';
		}
		if (typeof obj.gueltigBis !== "undefined") {
			result += '"gueltigBis" : ' + ((!obj.gueltigBis) ? 'null' : obj.gueltigBis) + ',';
		}
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

}

export function cast_de_svws_nrw_core_data_kataloge_KatalogEintragOrtsteile(obj : unknown) : KatalogEintragOrtsteile {
	return obj as KatalogEintragOrtsteile;
}