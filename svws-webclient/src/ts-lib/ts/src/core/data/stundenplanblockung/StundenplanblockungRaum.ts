import { JavaObject, cast_java_lang_Object } from '../../../java/lang/JavaObject';
import { JavaString, cast_java_lang_String } from '../../../java/lang/JavaString';

export class StundenplanblockungRaum extends JavaObject {

	public id : number = 0;

	public kuerzel : String = "";

	public maxSuS : number = -1;


	public constructor() {
		super();
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.nrw.schule.svws.core.data.stundenplanblockung.StundenplanblockungRaum'].includes(name);
	}

	public static transpilerFromJSON(json : string): StundenplanblockungRaum {
		const obj = JSON.parse(json);
		const result = new StundenplanblockungRaum();
		if (typeof obj.id === "undefined")
			 throw new Error('invalid json format, missing attribute id');
		result.id = obj.id;
		if (typeof obj.kuerzel === "undefined")
			 throw new Error('invalid json format, missing attribute kuerzel');
		result.kuerzel = String(obj.kuerzel);
		if (typeof obj.maxSuS === "undefined")
			 throw new Error('invalid json format, missing attribute maxSuS');
		result.maxSuS = obj.maxSuS;
		return result;
	}

	public static transpilerToJSON(obj : StundenplanblockungRaum) : string {
		let result = '{';
		result += '"id" : ' + obj.id + ',';
		result += '"kuerzel" : ' + '"' + obj.kuerzel.valueOf() + '"' + ',';
		result += '"maxSuS" : ' + obj.maxSuS + ',';
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

	public static transpilerToJSONPatch(obj : Partial<StundenplanblockungRaum>) : string {
		let result = '{';
		if (typeof obj.id !== "undefined") {
			result += '"id" : ' + obj.id + ',';
		}
		if (typeof obj.kuerzel !== "undefined") {
			result += '"kuerzel" : ' + '"' + obj.kuerzel.valueOf() + '"' + ',';
		}
		if (typeof obj.maxSuS !== "undefined") {
			result += '"maxSuS" : ' + obj.maxSuS + ',';
		}
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

}

export function cast_de_nrw_schule_svws_core_data_stundenplanblockung_StundenplanblockungRaum(obj : unknown) : StundenplanblockungRaum {
	return obj as StundenplanblockungRaum;
}
