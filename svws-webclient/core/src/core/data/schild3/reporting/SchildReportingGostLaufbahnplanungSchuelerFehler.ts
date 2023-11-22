import { JavaObject } from '../../../../java/lang/JavaObject';

export class SchildReportingGostLaufbahnplanungSchuelerFehler extends JavaObject {

	/**
	 * Die ID des Schülers, zu dem die Laufbahnplanungsdaten gehören.
	 */
	public schuelerID : number = 0;

	/**
	 * Fehler aus der Belegprüfung
	 */
	public belegungsfehler : string = "";


	public constructor() {
		super();
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.data.schild3.reporting.SchildReportingGostLaufbahnplanungSchuelerFehler'].includes(name);
	}

	public static transpilerFromJSON(json : string): SchildReportingGostLaufbahnplanungSchuelerFehler {
		const obj = JSON.parse(json);
		const result = new SchildReportingGostLaufbahnplanungSchuelerFehler();
		if (typeof obj.schuelerID === "undefined")
			 throw new Error('invalid json format, missing attribute schuelerID');
		result.schuelerID = obj.schuelerID;
		if (typeof obj.belegungsfehler === "undefined")
			 throw new Error('invalid json format, missing attribute belegungsfehler');
		result.belegungsfehler = obj.belegungsfehler;
		return result;
	}

	public static transpilerToJSON(obj : SchildReportingGostLaufbahnplanungSchuelerFehler) : string {
		let result = '{';
		result += '"schuelerID" : ' + obj.schuelerID + ',';
		result += '"belegungsfehler" : ' + JSON.stringify(obj.belegungsfehler!) + ',';
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

	public static transpilerToJSONPatch(obj : Partial<SchildReportingGostLaufbahnplanungSchuelerFehler>) : string {
		let result = '{';
		if (typeof obj.schuelerID !== "undefined") {
			result += '"schuelerID" : ' + obj.schuelerID + ',';
		}
		if (typeof obj.belegungsfehler !== "undefined") {
			result += '"belegungsfehler" : ' + JSON.stringify(obj.belegungsfehler!) + ',';
		}
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

}

export function cast_de_svws_nrw_core_data_schild3_reporting_SchildReportingGostLaufbahnplanungSchuelerFehler(obj : unknown) : SchildReportingGostLaufbahnplanungSchuelerFehler {
	return obj as SchildReportingGostLaufbahnplanungSchuelerFehler;
}