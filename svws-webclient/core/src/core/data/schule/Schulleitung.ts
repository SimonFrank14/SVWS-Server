import { JavaObject } from '../../../java/lang/JavaObject';

export class Schulleitung extends JavaObject {

	/**
	 * Die ID des Eintrags für die Schulleitungsfunktion
	 */
	public id : number = -1;

	/**
	 * Die ID der Leitungsfunktion des Lehrers.
	 */
	public idLeitungsfunktion : number = 0;

	/**
	 * Die Bezeichnung der Leitungsfunktion (max. 255 Zeichen)
	 */
	public bezeichnung : string = "";

	/**
	 * Die ID des Lehrers
	 */
	public idLehrer : number = -1;

	/**
	 * Das Datum, mit welchem die Leitunsfunktion übernommen wurde
	 */
	public beginn : string | null = null;

	/**
	 * Das Datum, bis zu welchem die Leitunsfunktion übernommen wurde
	 */
	public ende : string | null = null;


	public constructor() {
		super();
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.core.data.schule.Schulleitung';
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.data.schule.Schulleitung'].includes(name);
	}

	public static transpilerFromJSON(json : string): Schulleitung {
		const obj = JSON.parse(json);
		const result = new Schulleitung();
		if (obj.id === undefined)
			 throw new Error('invalid json format, missing attribute id');
		result.id = obj.id;
		if (obj.idLeitungsfunktion === undefined)
			 throw new Error('invalid json format, missing attribute idLeitungsfunktion');
		result.idLeitungsfunktion = obj.idLeitungsfunktion;
		if (obj.bezeichnung === undefined)
			 throw new Error('invalid json format, missing attribute bezeichnung');
		result.bezeichnung = obj.bezeichnung;
		if (obj.idLehrer === undefined)
			 throw new Error('invalid json format, missing attribute idLehrer');
		result.idLehrer = obj.idLehrer;
		result.beginn = (obj.beginn === undefined) ? null : obj.beginn === null ? null : obj.beginn;
		result.ende = (obj.ende === undefined) ? null : obj.ende === null ? null : obj.ende;
		return result;
	}

	public static transpilerToJSON(obj : Schulleitung) : string {
		let result = '{';
		result += '"id" : ' + obj.id + ',';
		result += '"idLeitungsfunktion" : ' + obj.idLeitungsfunktion + ',';
		result += '"bezeichnung" : ' + JSON.stringify(obj.bezeichnung!) + ',';
		result += '"idLehrer" : ' + obj.idLehrer + ',';
		result += '"beginn" : ' + ((!obj.beginn) ? 'null' : JSON.stringify(obj.beginn)) + ',';
		result += '"ende" : ' + ((!obj.ende) ? 'null' : JSON.stringify(obj.ende)) + ',';
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

	public static transpilerToJSONPatch(obj : Partial<Schulleitung>) : string {
		let result = '{';
		if (obj.id !== undefined) {
			result += '"id" : ' + obj.id + ',';
		}
		if (obj.idLeitungsfunktion !== undefined) {
			result += '"idLeitungsfunktion" : ' + obj.idLeitungsfunktion + ',';
		}
		if (obj.bezeichnung !== undefined) {
			result += '"bezeichnung" : ' + JSON.stringify(obj.bezeichnung!) + ',';
		}
		if (obj.idLehrer !== undefined) {
			result += '"idLehrer" : ' + obj.idLehrer + ',';
		}
		if (obj.beginn !== undefined) {
			result += '"beginn" : ' + ((!obj.beginn) ? 'null' : JSON.stringify(obj.beginn)) + ',';
		}
		if (obj.ende !== undefined) {
			result += '"ende" : ' + ((!obj.ende) ? 'null' : JSON.stringify(obj.ende)) + ',';
		}
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

}

export function cast_de_svws_nrw_core_data_schule_Schulleitung(obj : unknown) : Schulleitung {
	return obj as Schulleitung;
}
