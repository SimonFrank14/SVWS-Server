import { JavaObject } from '../../../java/lang/JavaObject';

export class DruckGostLaufbahnplanungSchuelerHinweise extends JavaObject {

	/**
	 * Die ID des Schülers, zu dem die Laufbahnplanungsdaten gehören.
	 */
	public schuelerID : number = 0;

	/**
	 * Fehler aus der Belegprüfung
	 */
	public belegungshinweis : string = "";


	public constructor() {
		super();
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.data.druck.DruckGostLaufbahnplanungSchuelerHinweise'].includes(name);
	}

	public static transpilerFromJSON(json : string): DruckGostLaufbahnplanungSchuelerHinweise {
		const obj = JSON.parse(json);
		const result = new DruckGostLaufbahnplanungSchuelerHinweise();
		if (typeof obj.schuelerID === "undefined")
			 throw new Error('invalid json format, missing attribute schuelerID');
		result.schuelerID = obj.schuelerID;
		if (typeof obj.belegungshinweis === "undefined")
			 throw new Error('invalid json format, missing attribute belegungshinweis');
		result.belegungshinweis = obj.belegungshinweis;
		return result;
	}

	public static transpilerToJSON(obj : DruckGostLaufbahnplanungSchuelerHinweise) : string {
		let result = '{';
		result += '"schuelerID" : ' + obj.schuelerID + ',';
		result += '"belegungshinweis" : ' + JSON.stringify(obj.belegungshinweis!) + ',';
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

	public static transpilerToJSONPatch(obj : Partial<DruckGostLaufbahnplanungSchuelerHinweise>) : string {
		let result = '{';
		if (typeof obj.schuelerID !== "undefined") {
			result += '"schuelerID" : ' + obj.schuelerID + ',';
		}
		if (typeof obj.belegungshinweis !== "undefined") {
			result += '"belegungshinweis" : ' + JSON.stringify(obj.belegungshinweis!) + ',';
		}
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

}

export function cast_de_svws_nrw_core_data_druck_DruckGostLaufbahnplanungSchuelerHinweise(obj : unknown) : DruckGostLaufbahnplanungSchuelerHinweise {
	return obj as DruckGostLaufbahnplanungSchuelerHinweise;
}
