import { JavaObject } from '../../../java/lang/JavaObject';

export class PrimarstufeSchuleingangsphaseBesuchsjahreKatalogEintrag extends JavaObject {

	/**
	 * Die ID des Katalog-Eintrags.
	 */
	public id : number = 0;

	/**
	 * Das Kürzel für die Besuchsjahre
	 */
	public kuerzel : string = "";

	/**
	 * Der Text für die Besuchsjahre
	 */
	public text : string = "";

	/**
	 * Gibt an, in welchem Schuljahr die Besuchsjahre ergänzt wurden. Ist kein Schuljahr bekannt, so ist null gesetzt.
	 */
	public gueltigVon : number | null = null;

	/**
	 * Gibt an, bis zu welchem Schuljahr die Besuchsjahre verwendet werden. Ist kein Schuljahr bekannt, so ist null gesetzt.
	 */
	public gueltigBis : number | null = null;


	/**
	 * Erstellt einen Katalog-Eintrag mit Standardwerten
	 */
	public constructor();

	/**
	 * Erstellt einen Katalog-Eintrag mit den angegebenen Werten
	 *
	 * @param id            die ID des Katalog-Eintrags
	 * @param kuerzel       das Kürzel für die Besuchsjahre
	 * @param text          die textuelle Bezeichung für die Besuchsjahre
	 * @param gueltigVon    das Schuljahr, wann der Eintrag eingeführt wurde oder null, falls es nicht bekannt ist und "schon immer gültig war"
	 * @param gueltigBis    das Schuljahr, bis zu welchem der Eintrag gültig ist
	 */
	public constructor(id : number, kuerzel : string, text : string, gueltigVon : number | null, gueltigBis : number | null);

	/**
	 * Implementation for method overloads of 'constructor'
	 */
	public constructor(__param0? : number, __param1? : string, __param2? : string, __param3? : null | number, __param4? : null | number) {
		super();
		if ((__param0 === undefined) && (__param1 === undefined) && (__param2 === undefined) && (__param3 === undefined) && (__param4 === undefined)) {
			// empty method body
		} else if (((__param0 !== undefined) && typeof __param0 === "number") && ((__param1 !== undefined) && (typeof __param1 === "string")) && ((__param2 !== undefined) && (typeof __param2 === "string")) && ((__param3 !== undefined) && (typeof __param3 === "number") || (__param3 === null)) && ((__param4 !== undefined) && (typeof __param4 === "number") || (__param4 === null))) {
			const id : number = __param0 as number;
			const kuerzel : string = __param1;
			const text : string = __param2;
			const gueltigVon : number | null = __param3;
			const gueltigBis : number | null = __param4;
			this.id = id;
			this.kuerzel = kuerzel;
			this.text = text;
			this.gueltigVon = gueltigVon;
			this.gueltigBis = gueltigBis;
		} else throw new Error('invalid method overload');
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.core.data.jahrgang.PrimarstufeSchuleingangsphaseBesuchsjahreKatalogEintrag';
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.data.jahrgang.PrimarstufeSchuleingangsphaseBesuchsjahreKatalogEintrag'].includes(name);
	}

	public static transpilerFromJSON(json : string): PrimarstufeSchuleingangsphaseBesuchsjahreKatalogEintrag {
		const obj = JSON.parse(json);
		const result = new PrimarstufeSchuleingangsphaseBesuchsjahreKatalogEintrag();
		if (obj.id === undefined)
			 throw new Error('invalid json format, missing attribute id');
		result.id = obj.id;
		if (obj.kuerzel === undefined)
			 throw new Error('invalid json format, missing attribute kuerzel');
		result.kuerzel = obj.kuerzel;
		if (obj.text === undefined)
			 throw new Error('invalid json format, missing attribute text');
		result.text = obj.text;
		result.gueltigVon = (obj.gueltigVon === undefined) ? null : obj.gueltigVon === null ? null : obj.gueltigVon;
		result.gueltigBis = (obj.gueltigBis === undefined) ? null : obj.gueltigBis === null ? null : obj.gueltigBis;
		return result;
	}

	public static transpilerToJSON(obj : PrimarstufeSchuleingangsphaseBesuchsjahreKatalogEintrag) : string {
		let result = '{';
		result += '"id" : ' + obj.id + ',';
		result += '"kuerzel" : ' + JSON.stringify(obj.kuerzel!) + ',';
		result += '"text" : ' + JSON.stringify(obj.text!) + ',';
		result += '"gueltigVon" : ' + ((!obj.gueltigVon) ? 'null' : obj.gueltigVon) + ',';
		result += '"gueltigBis" : ' + ((!obj.gueltigBis) ? 'null' : obj.gueltigBis) + ',';
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

	public static transpilerToJSONPatch(obj : Partial<PrimarstufeSchuleingangsphaseBesuchsjahreKatalogEintrag>) : string {
		let result = '{';
		if (obj.id !== undefined) {
			result += '"id" : ' + obj.id + ',';
		}
		if (obj.kuerzel !== undefined) {
			result += '"kuerzel" : ' + JSON.stringify(obj.kuerzel!) + ',';
		}
		if (obj.text !== undefined) {
			result += '"text" : ' + JSON.stringify(obj.text!) + ',';
		}
		if (obj.gueltigVon !== undefined) {
			result += '"gueltigVon" : ' + ((!obj.gueltigVon) ? 'null' : obj.gueltigVon) + ',';
		}
		if (obj.gueltigBis !== undefined) {
			result += '"gueltigBis" : ' + ((!obj.gueltigBis) ? 'null' : obj.gueltigBis) + ',';
		}
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

}

export function cast_de_svws_nrw_core_data_jahrgang_PrimarstufeSchuleingangsphaseBesuchsjahreKatalogEintrag(obj : unknown) : PrimarstufeSchuleingangsphaseBesuchsjahreKatalogEintrag {
	return obj as PrimarstufeSchuleingangsphaseBesuchsjahreKatalogEintrag;
}
