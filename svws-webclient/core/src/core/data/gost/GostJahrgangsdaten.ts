import { JavaObject } from '../../../java/lang/JavaObject';
import { GostBeratungslehrer } from '../../../core/data/gost/GostBeratungslehrer';
import { ArrayList } from '../../../java/util/ArrayList';
import type { List } from '../../../java/util/List';
import { Class } from '../../../java/lang/Class';

export class GostJahrgangsdaten extends JavaObject {

	/**
	 * Das Jahr, in welchem der Jahrgang Abitur machen wird oder -1 für die Vorlage für einen neuen Abiturjahrgang.
	 */
	public abiturjahr : number = 0;

	/**
	 * Die aktuelle Jahrgangsstufe, welche dem Abiturjahrgang zugeordnet ist.
	 */
	public jahrgang : string | null = null;

	/**
	 * Das aktuelle Halbjahr, in dem sich der Jahrgang laut Schuljahrsabschnitt der Schule befindet.
	 */
	public halbjahr : number = 0;

	/**
	 * Die textuelle Bezeichnung für den Abiturjahrgang
	 */
	public bezeichnung : string | null = null;

	/**
	 * Gibt an, ob das Abitur für diesen Jahrgang bereits abgeschlossen ist, d.h. die Schule sich bereits in einem späteren Schuljahr befindet.
	 */
	public istAbgeschlossen : boolean = false;

	/**
	 * Der derzeitige Beratungstext, welcher auf einem Ausdruck eines Schülerlaufbahnbogens für die
	 *  gymnasiale Oberstufe gedruckt wird.
	 */
	public textBeratungsbogen : string | null = null;

	/**
	 * Der derzeitige Text, der beim Versenden einer Beratungsdatei per Mail verwendet wird.
	 */
	public textMailversand : string | null = null;

	/**
	 * Legt fest, ob ein Zusatzkurs in Geschichte angeboten wird.
	 */
	public hatZusatzkursGE : boolean = true;

	/**
	 * Das eindeutige Kürzel des Halbjahrs, zu dem ein Zusatzkurs in Geschichte beginnt.
	 */
	public beginnZusatzkursGE : string | null = null;

	/**
	 * Legt fest, ob ein Zusatzkurs in Sozialwissenschaften angeboten wird.
	 */
	public hatZusatzkursSW : boolean = true;

	/**
	 * Das eindeutige Kürzel des Halbjahrs, zu dem ein Zusatzkurs in Sozialwissenschaften beginnt.
	 */
	public beginnZusatzkursSW : string | null = null;

	/**
	 * Gibt die Anzahl der Blockung in der Kursplanung für die jeweilige Halbjahre der Oberstufe bei dem Abiturjahrgang an. Diese müssen nicht zwingend persistiert sein (Index 0=EF.1, 1=EF.2, ...)
	 */
	public anzahlKursblockungen : Array<number> = Array(6).fill(0);

	/**
	 * Gibt an, ob für die jeweiligen Halbjahre der Oberstufe bereits eine Blockung in den Leistungsdaten persistiert wurde (0=EF.1, 1=EF.2, ...)
	 */
	public istBlockungFestgelegt : Array<boolean> = Array(6).fill(false);

	/**
	 * Gibt an, ob für die jeweiligen Halbjahre der Oberstufe bereits (Quartals-)Noten in den Leistungsdaten vorhanden sind (0=EF.1, 1=EF.2, ...)
	 */
	public existierenNotenInLeistungsdaten : Array<boolean> = Array(6).fill(false);

	/**
	 * Die Liste der Beratungslehrer für diesen Jahrgang der gymnasialen Oberstufe
	 */
	public readonly beratungslehrer : List<GostBeratungslehrer> = new ArrayList<GostBeratungslehrer>();


	public constructor() {
		super();
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.core.data.gost.GostJahrgangsdaten';
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.data.gost.GostJahrgangsdaten'].includes(name);
	}

	public static class = new Class<GostJahrgangsdaten>('de.svws_nrw.core.data.gost.GostJahrgangsdaten');

	public static transpilerFromJSON(json : string): GostJahrgangsdaten {
		const obj = JSON.parse(json) as Partial<GostJahrgangsdaten>;
		const result = new GostJahrgangsdaten();
		if (obj.abiturjahr === undefined)
			throw new Error('invalid json format, missing attribute abiturjahr');
		result.abiturjahr = obj.abiturjahr;
		result.jahrgang = (obj.jahrgang === undefined) ? null : obj.jahrgang === null ? null : obj.jahrgang;
		if (obj.halbjahr === undefined)
			throw new Error('invalid json format, missing attribute halbjahr');
		result.halbjahr = obj.halbjahr;
		result.bezeichnung = (obj.bezeichnung === undefined) ? null : obj.bezeichnung === null ? null : obj.bezeichnung;
		if (obj.istAbgeschlossen === undefined)
			throw new Error('invalid json format, missing attribute istAbgeschlossen');
		result.istAbgeschlossen = obj.istAbgeschlossen;
		result.textBeratungsbogen = (obj.textBeratungsbogen === undefined) ? null : obj.textBeratungsbogen === null ? null : obj.textBeratungsbogen;
		result.textMailversand = (obj.textMailversand === undefined) ? null : obj.textMailversand === null ? null : obj.textMailversand;
		if (obj.hatZusatzkursGE === undefined)
			throw new Error('invalid json format, missing attribute hatZusatzkursGE');
		result.hatZusatzkursGE = obj.hatZusatzkursGE;
		result.beginnZusatzkursGE = (obj.beginnZusatzkursGE === undefined) ? null : obj.beginnZusatzkursGE === null ? null : obj.beginnZusatzkursGE;
		if (obj.hatZusatzkursSW === undefined)
			throw new Error('invalid json format, missing attribute hatZusatzkursSW');
		result.hatZusatzkursSW = obj.hatZusatzkursSW;
		result.beginnZusatzkursSW = (obj.beginnZusatzkursSW === undefined) ? null : obj.beginnZusatzkursSW === null ? null : obj.beginnZusatzkursSW;
		if (obj.anzahlKursblockungen !== undefined) {
			for (let i = 0; i < obj.anzahlKursblockungen.length; i++) {
				result.anzahlKursblockungen[i] = obj.anzahlKursblockungen[i];
			}
		}
		if (obj.istBlockungFestgelegt !== undefined) {
			for (let i = 0; i < obj.istBlockungFestgelegt.length; i++) {
				result.istBlockungFestgelegt[i] = obj.istBlockungFestgelegt[i];
			}
		}
		if (obj.existierenNotenInLeistungsdaten !== undefined) {
			for (let i = 0; i < obj.existierenNotenInLeistungsdaten.length; i++) {
				result.existierenNotenInLeistungsdaten[i] = obj.existierenNotenInLeistungsdaten[i];
			}
		}
		if (obj.beratungslehrer !== undefined) {
			for (const elem of obj.beratungslehrer) {
				result.beratungslehrer.add(GostBeratungslehrer.transpilerFromJSON(JSON.stringify(elem)));
			}
		}
		return result;
	}

	public static transpilerToJSON(obj : GostJahrgangsdaten) : string {
		let result = '{';
		result += '"abiturjahr" : ' + obj.abiturjahr.toString() + ',';
		result += '"jahrgang" : ' + ((obj.jahrgang === null) ? 'null' : JSON.stringify(obj.jahrgang)) + ',';
		result += '"halbjahr" : ' + obj.halbjahr.toString() + ',';
		result += '"bezeichnung" : ' + ((obj.bezeichnung === null) ? 'null' : JSON.stringify(obj.bezeichnung)) + ',';
		result += '"istAbgeschlossen" : ' + obj.istAbgeschlossen.toString() + ',';
		result += '"textBeratungsbogen" : ' + ((obj.textBeratungsbogen === null) ? 'null' : JSON.stringify(obj.textBeratungsbogen)) + ',';
		result += '"textMailversand" : ' + ((obj.textMailversand === null) ? 'null' : JSON.stringify(obj.textMailversand)) + ',';
		result += '"hatZusatzkursGE" : ' + obj.hatZusatzkursGE.toString() + ',';
		result += '"beginnZusatzkursGE" : ' + ((obj.beginnZusatzkursGE === null) ? 'null' : JSON.stringify(obj.beginnZusatzkursGE)) + ',';
		result += '"hatZusatzkursSW" : ' + obj.hatZusatzkursSW.toString() + ',';
		result += '"beginnZusatzkursSW" : ' + ((obj.beginnZusatzkursSW === null) ? 'null' : JSON.stringify(obj.beginnZusatzkursSW)) + ',';
		result += '"anzahlKursblockungen" : [ ';
		for (let i = 0; i < obj.anzahlKursblockungen.length; i++) {
			const elem = obj.anzahlKursblockungen[i];
			result += JSON.stringify(elem);
			if (i < obj.anzahlKursblockungen.length - 1)
				result += ',';
		}
		result += ' ]' + ',';
		result += '"istBlockungFestgelegt" : [ ';
		for (let i = 0; i < obj.istBlockungFestgelegt.length; i++) {
			const elem = obj.istBlockungFestgelegt[i];
			result += JSON.stringify(elem);
			if (i < obj.istBlockungFestgelegt.length - 1)
				result += ',';
		}
		result += ' ]' + ',';
		result += '"existierenNotenInLeistungsdaten" : [ ';
		for (let i = 0; i < obj.existierenNotenInLeistungsdaten.length; i++) {
			const elem = obj.existierenNotenInLeistungsdaten[i];
			result += JSON.stringify(elem);
			if (i < obj.existierenNotenInLeistungsdaten.length - 1)
				result += ',';
		}
		result += ' ]' + ',';
		result += '"beratungslehrer" : [ ';
		for (let i = 0; i < obj.beratungslehrer.size(); i++) {
			const elem = obj.beratungslehrer.get(i);
			result += GostBeratungslehrer.transpilerToJSON(elem);
			if (i < obj.beratungslehrer.size() - 1)
				result += ',';
		}
		result += ' ]' + ',';
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

	public static transpilerToJSONPatch(obj : Partial<GostJahrgangsdaten>) : string {
		let result = '{';
		if (obj.abiturjahr !== undefined) {
			result += '"abiturjahr" : ' + obj.abiturjahr.toString() + ',';
		}
		if (obj.jahrgang !== undefined) {
			result += '"jahrgang" : ' + ((obj.jahrgang === null) ? 'null' : JSON.stringify(obj.jahrgang)) + ',';
		}
		if (obj.halbjahr !== undefined) {
			result += '"halbjahr" : ' + obj.halbjahr.toString() + ',';
		}
		if (obj.bezeichnung !== undefined) {
			result += '"bezeichnung" : ' + ((obj.bezeichnung === null) ? 'null' : JSON.stringify(obj.bezeichnung)) + ',';
		}
		if (obj.istAbgeschlossen !== undefined) {
			result += '"istAbgeschlossen" : ' + obj.istAbgeschlossen.toString() + ',';
		}
		if (obj.textBeratungsbogen !== undefined) {
			result += '"textBeratungsbogen" : ' + ((obj.textBeratungsbogen === null) ? 'null' : JSON.stringify(obj.textBeratungsbogen)) + ',';
		}
		if (obj.textMailversand !== undefined) {
			result += '"textMailversand" : ' + ((obj.textMailversand === null) ? 'null' : JSON.stringify(obj.textMailversand)) + ',';
		}
		if (obj.hatZusatzkursGE !== undefined) {
			result += '"hatZusatzkursGE" : ' + obj.hatZusatzkursGE.toString() + ',';
		}
		if (obj.beginnZusatzkursGE !== undefined) {
			result += '"beginnZusatzkursGE" : ' + ((obj.beginnZusatzkursGE === null) ? 'null' : JSON.stringify(obj.beginnZusatzkursGE)) + ',';
		}
		if (obj.hatZusatzkursSW !== undefined) {
			result += '"hatZusatzkursSW" : ' + obj.hatZusatzkursSW.toString() + ',';
		}
		if (obj.beginnZusatzkursSW !== undefined) {
			result += '"beginnZusatzkursSW" : ' + ((obj.beginnZusatzkursSW === null) ? 'null' : JSON.stringify(obj.beginnZusatzkursSW)) + ',';
		}
		if (obj.anzahlKursblockungen !== undefined) {
			const a = obj.anzahlKursblockungen;
			result += '"anzahlKursblockungen" : [ ';
			for (let i = 0; i < a.length; i++) {
				const elem = a[i];
				result += JSON.stringify(elem);
				if (i < a.length - 1)
					result += ',';
			}
			result += ' ]' + ',';
		}
		if (obj.istBlockungFestgelegt !== undefined) {
			const a = obj.istBlockungFestgelegt;
			result += '"istBlockungFestgelegt" : [ ';
			for (let i = 0; i < a.length; i++) {
				const elem = a[i];
				result += JSON.stringify(elem);
				if (i < a.length - 1)
					result += ',';
			}
			result += ' ]' + ',';
		}
		if (obj.existierenNotenInLeistungsdaten !== undefined) {
			const a = obj.existierenNotenInLeistungsdaten;
			result += '"existierenNotenInLeistungsdaten" : [ ';
			for (let i = 0; i < a.length; i++) {
				const elem = a[i];
				result += JSON.stringify(elem);
				if (i < a.length - 1)
					result += ',';
			}
			result += ' ]' + ',';
		}
		if (obj.beratungslehrer !== undefined) {
			result += '"beratungslehrer" : [ ';
			for (let i = 0; i < obj.beratungslehrer.size(); i++) {
				const elem = obj.beratungslehrer.get(i);
				result += GostBeratungslehrer.transpilerToJSON(elem);
				if (i < obj.beratungslehrer.size() - 1)
					result += ',';
			}
			result += ' ]' + ',';
		}
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

}

export function cast_de_svws_nrw_core_data_gost_GostJahrgangsdaten(obj : unknown) : GostJahrgangsdaten {
	return obj as GostJahrgangsdaten;
}
