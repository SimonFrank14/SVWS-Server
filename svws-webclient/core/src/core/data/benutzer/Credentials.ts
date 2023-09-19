import { JavaObject } from '../../../java/lang/JavaObject';

export class Credentials extends JavaObject {

	/**
	 * Benutzername des Account-Credentials
	 */
	public benutzername : string = "";

	/**
	 * Passwort des Account-Credentials
	 */
	public password : string = "";


	public constructor() {
		super();
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.data.benutzer.Credentials'].includes(name);
	}

	public static transpilerFromJSON(json : string): Credentials {
		const obj = JSON.parse(json);
		const result = new Credentials();
		if (typeof obj.benutzername === "undefined")
			 throw new Error('invalid json format, missing attribute benutzername');
		result.benutzername = obj.benutzername;
		if (typeof obj.password === "undefined")
			 throw new Error('invalid json format, missing attribute password');
		result.password = obj.password;
		return result;
	}

	public static transpilerToJSON(obj : Credentials) : string {
		let result = '{';
		result += '"benutzername" : ' + JSON.stringify(obj.benutzername!) + ',';
		result += '"password" : ' + JSON.stringify(obj.password!) + ',';
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

	public static transpilerToJSONPatch(obj : Partial<Credentials>) : string {
		let result = '{';
		if (typeof obj.benutzername !== "undefined") {
			result += '"benutzername" : ' + JSON.stringify(obj.benutzername!) + ',';
		}
		if (typeof obj.password !== "undefined") {
			result += '"password" : ' + JSON.stringify(obj.password!) + ',';
		}
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

}

export function cast_de_svws_nrw_core_data_benutzer_Credentials(obj : unknown) : Credentials {
	return obj as Credentials;
}