import { JavaObject } from '../../../java/lang/JavaObject';
import { Class } from '../../../java/lang/Class';

export class BerufskollegBerufsebeneKatalogEintrag extends JavaObject {

	/**
	 * Die ID des Katalog-Eintrags.
	 */
	public id : number = 0;

	/**
	 * Die Berufsebene.
	 */
	public ebene : number = 0;

	/**
	 * Das Kürzel der Berufsebene
	 */
	public kuerzel : string = "";

	/**
	 * Die Bezeichnung der Berufsebene.
	 */
	public bezeichnung : string = "";

	/**
	 * Gibt an, in welchem Schuljahr die Berufsebene einführt wurde. Ist kein Schuljahr bekannt, so ist null gesetzt.
	 */
	public gueltigVon : number | null = null;

	/**
	 * Gibt an, bis zu welchem Schuljahr die Berufsebene gültig ist. Ist kein Schuljahr bekannt, so ist null gesetzt.
	 */
	public gueltigBis : number | null = null;


	/**
	 * Erstellt einen Berufsebene-Eintrag mit Standardwerten
	 */
	public constructor();

	/**
	 * Erstellt einen Berufsebene-Eintrag mit den angegebenen Werten
	 *
	 * @param id                 die ID
	 * @param ebene              die Berufsebene
	 * @param kuerzel            das Kürzel
	 * @param bezeichnung        die Bezeichnung
	 * @param gueltigVon         das Schuljahr, wann der Eintrag eingeführt wurde oder null, falls es nicht bekannt ist und "schon immer gültig war"
	 * @param gueltigBis         das Schuljahr, bis zu welchem der Eintrag gültig ist
	 */
	public constructor(id : number, ebene : number, kuerzel : string, bezeichnung : string, gueltigVon : number | null, gueltigBis : number | null);

	/**
	 * Implementation for method overloads of 'constructor'
	 */
	public constructor(__param0? : number, __param1? : number, __param2? : string, __param3? : string, __param4? : null | number, __param5? : null | number) {
		super();
		if ((__param0 === undefined) && (__param1 === undefined) && (__param2 === undefined) && (__param3 === undefined) && (__param4 === undefined) && (__param5 === undefined)) {
			// empty method body
		} else if (((__param0 !== undefined) && typeof __param0 === "number") && ((__param1 !== undefined) && typeof __param1 === "number") && ((__param2 !== undefined) && (typeof __param2 === "string")) && ((__param3 !== undefined) && (typeof __param3 === "string")) && ((__param4 !== undefined) && (typeof __param4 === "number") || (__param4 === null)) && ((__param5 !== undefined) && (typeof __param5 === "number") || (__param5 === null))) {
			const id : number = __param0 as number;
			const ebene : number = __param1 as number;
			const kuerzel : string = __param2;
			const bezeichnung : string = __param3;
			const gueltigVon : number | null = __param4;
			const gueltigBis : number | null = __param5;
			this.id = id;
			this.ebene = ebene;
			this.kuerzel = kuerzel;
			this.bezeichnung = bezeichnung;
			this.gueltigVon = gueltigVon;
			this.gueltigBis = gueltigBis;
		} else throw new Error('invalid method overload');
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.core.data.schule.BerufskollegBerufsebeneKatalogEintrag';
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.data.schule.BerufskollegBerufsebeneKatalogEintrag'].includes(name);
	}

	public static class = new Class<BerufskollegBerufsebeneKatalogEintrag>('de.svws_nrw.core.data.schule.BerufskollegBerufsebeneKatalogEintrag');

	public static transpilerFromJSON(json : string): BerufskollegBerufsebeneKatalogEintrag {
		const obj = JSON.parse(json) as Partial<BerufskollegBerufsebeneKatalogEintrag>;
		const result = new BerufskollegBerufsebeneKatalogEintrag();
		if (obj.id === undefined)
			throw new Error('invalid json format, missing attribute id');
		result.id = obj.id;
		if (obj.ebene === undefined)
			throw new Error('invalid json format, missing attribute ebene');
		result.ebene = obj.ebene;
		if (obj.kuerzel === undefined)
			throw new Error('invalid json format, missing attribute kuerzel');
		result.kuerzel = obj.kuerzel;
		if (obj.bezeichnung === undefined)
			throw new Error('invalid json format, missing attribute bezeichnung');
		result.bezeichnung = obj.bezeichnung;
		result.gueltigVon = (obj.gueltigVon === undefined) ? null : obj.gueltigVon === null ? null : obj.gueltigVon;
		result.gueltigBis = (obj.gueltigBis === undefined) ? null : obj.gueltigBis === null ? null : obj.gueltigBis;
		return result;
	}

	public static transpilerToJSON(obj : BerufskollegBerufsebeneKatalogEintrag) : string {
		let result = '{';
		result += '"id" : ' + obj.id.toString() + ',';
		result += '"ebene" : ' + obj.ebene.toString() + ',';
		result += '"kuerzel" : ' + JSON.stringify(obj.kuerzel) + ',';
		result += '"bezeichnung" : ' + JSON.stringify(obj.bezeichnung) + ',';
		result += '"gueltigVon" : ' + ((obj.gueltigVon === null) ? 'null' : obj.gueltigVon.toString()) + ',';
		result += '"gueltigBis" : ' + ((obj.gueltigBis === null) ? 'null' : obj.gueltigBis.toString()) + ',';
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

	public static transpilerToJSONPatch(obj : Partial<BerufskollegBerufsebeneKatalogEintrag>) : string {
		let result = '{';
		if (obj.id !== undefined) {
			result += '"id" : ' + obj.id.toString() + ',';
		}
		if (obj.ebene !== undefined) {
			result += '"ebene" : ' + obj.ebene.toString() + ',';
		}
		if (obj.kuerzel !== undefined) {
			result += '"kuerzel" : ' + JSON.stringify(obj.kuerzel) + ',';
		}
		if (obj.bezeichnung !== undefined) {
			result += '"bezeichnung" : ' + JSON.stringify(obj.bezeichnung) + ',';
		}
		if (obj.gueltigVon !== undefined) {
			result += '"gueltigVon" : ' + ((obj.gueltigVon === null) ? 'null' : obj.gueltigVon.toString()) + ',';
		}
		if (obj.gueltigBis !== undefined) {
			result += '"gueltigBis" : ' + ((obj.gueltigBis === null) ? 'null' : obj.gueltigBis.toString()) + ',';
		}
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

}

export function cast_de_svws_nrw_core_data_schule_BerufskollegBerufsebeneKatalogEintrag(obj : unknown) : BerufskollegBerufsebeneKatalogEintrag {
	return obj as BerufskollegBerufsebeneKatalogEintrag;
}
