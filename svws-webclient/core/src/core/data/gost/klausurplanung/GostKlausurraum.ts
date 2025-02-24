import { JavaLong } from '../../../../java/lang/JavaLong';
import { JavaObject } from '../../../../java/lang/JavaObject';
import { Class } from '../../../../java/lang/Class';

export class GostKlausurraum extends JavaObject {

	/**
	 * Die ID des Klausurraums.
	 */
	public id : number = -1;

	/**
	 * Die ID des Klausurtermins.
	 */
	public idTermin : number = -1;

	/**
	 * Die ID des Stundenplan_Raumes.
	 */
	public idStundenplanRaum : number | null = null;

	/**
	 * Die textuelle Bemerkung zum Klausurraum, sofern vorhanden.
	 */
	public bemerkung : string | null = null;


	/**
	 * Default-Konstruktor
	 */
	public constructor() {
		super();
	}

	/**
	 * Vergleicht, ob das akutelle dasselbe Objekt, wie ein anderes übergebenes Objekt ist.
	 *
	 * @param another     das zu vergleichende Objekt
	 * @return true, falls die Objekte indentisch sind, sonst false
	 */
	public equals(another : unknown | null) : boolean {
		return (another !== null) && (((another instanceof JavaObject) && (another.isTranspiledInstanceOf('de.svws_nrw.core.data.gost.klausurplanung.GostKlausurraum')))) && (this.id === (cast_de_svws_nrw_core_data_gost_klausurplanung_GostKlausurraum(another)).id);
	}

	/**
	 * Erzeugt den Hashcode zu Objekt auf Basis der id.
	 *
	 * @return den HashCode
	 */
	public hashCode() : number {
		return JavaLong.hashCode((this.id));
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.core.data.gost.klausurplanung.GostKlausurraum';
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.data.gost.klausurplanung.GostKlausurraum'].includes(name);
	}

	public static class = new Class<GostKlausurraum>('de.svws_nrw.core.data.gost.klausurplanung.GostKlausurraum');

	public static transpilerFromJSON(json : string): GostKlausurraum {
		const obj = JSON.parse(json) as Partial<GostKlausurraum>;
		const result = new GostKlausurraum();
		if (obj.id === undefined)
			throw new Error('invalid json format, missing attribute id');
		result.id = obj.id;
		if (obj.idTermin === undefined)
			throw new Error('invalid json format, missing attribute idTermin');
		result.idTermin = obj.idTermin;
		result.idStundenplanRaum = (obj.idStundenplanRaum === undefined) ? null : obj.idStundenplanRaum === null ? null : obj.idStundenplanRaum;
		result.bemerkung = (obj.bemerkung === undefined) ? null : obj.bemerkung === null ? null : obj.bemerkung;
		return result;
	}

	public static transpilerToJSON(obj : GostKlausurraum) : string {
		let result = '{';
		result += '"id" : ' + obj.id.toString() + ',';
		result += '"idTermin" : ' + obj.idTermin.toString() + ',';
		result += '"idStundenplanRaum" : ' + ((obj.idStundenplanRaum === null) ? 'null' : obj.idStundenplanRaum.toString()) + ',';
		result += '"bemerkung" : ' + ((obj.bemerkung === null) ? 'null' : JSON.stringify(obj.bemerkung)) + ',';
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

	public static transpilerToJSONPatch(obj : Partial<GostKlausurraum>) : string {
		let result = '{';
		if (obj.id !== undefined) {
			result += '"id" : ' + obj.id.toString() + ',';
		}
		if (obj.idTermin !== undefined) {
			result += '"idTermin" : ' + obj.idTermin.toString() + ',';
		}
		if (obj.idStundenplanRaum !== undefined) {
			result += '"idStundenplanRaum" : ' + ((obj.idStundenplanRaum === null) ? 'null' : obj.idStundenplanRaum.toString()) + ',';
		}
		if (obj.bemerkung !== undefined) {
			result += '"bemerkung" : ' + ((obj.bemerkung === null) ? 'null' : JSON.stringify(obj.bemerkung)) + ',';
		}
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

}

export function cast_de_svws_nrw_core_data_gost_klausurplanung_GostKlausurraum(obj : unknown) : GostKlausurraum {
	return obj as GostKlausurraum;
}
