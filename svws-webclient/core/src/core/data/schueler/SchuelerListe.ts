import { JavaObject } from '../../../java/lang/JavaObject';
import { KursDaten } from '../../../core/data/kurse/KursDaten';
import { KlassenDaten } from '../../../core/data/klassen/KlassenDaten';
import { SchuelerListeEintrag } from '../../../core/data/schueler/SchuelerListeEintrag';
import { ArrayList } from '../../../java/util/ArrayList';
import type { List } from '../../../java/util/List';
import { JahrgangsDaten } from '../../../core/data/jahrgang/JahrgangsDaten';
import { GostJahrgang } from '../../../core/data/gost/GostJahrgang';

export class SchuelerListe extends JavaObject {

	/**
	 * Die ID des Schuljahresabschnitts.
	 */
	public idSchuljahresabschnitt : number = -1;

	/**
	 * Die Listen-Einträge für die Schüler
	 */
	public readonly schueler : List<SchuelerListeEintrag> = new ArrayList();

	/**
	 * Die Klassen-Daten
	 */
	public readonly klassen : List<KlassenDaten> = new ArrayList();

	/**
	 * Die Kurs-Daten
	 */
	public readonly kurse : List<KursDaten> = new ArrayList();

	/**
	 * Die Jahrgangs-Daten
	 */
	public readonly jahrgaenge : List<JahrgangsDaten> = new ArrayList();

	/**
	 * Die Daten zu den Jahrgängen der Gymnasialen Oberstufe
	 */
	public readonly jahrgaengeGost : List<GostJahrgang> = new ArrayList();


	public constructor() {
		super();
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.core.data.schueler.SchuelerListe';
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.data.schueler.SchuelerListe'].includes(name);
	}

	public static transpilerFromJSON(json : string): SchuelerListe {
		const obj = JSON.parse(json);
		const result = new SchuelerListe();
		if (typeof obj.idSchuljahresabschnitt === "undefined")
			 throw new Error('invalid json format, missing attribute idSchuljahresabschnitt');
		result.idSchuljahresabschnitt = obj.idSchuljahresabschnitt;
		if ((obj.schueler !== undefined) && (obj.schueler !== null)) {
			for (const elem of obj.schueler) {
				result.schueler?.add(SchuelerListeEintrag.transpilerFromJSON(JSON.stringify(elem)));
			}
		}
		if ((obj.klassen !== undefined) && (obj.klassen !== null)) {
			for (const elem of obj.klassen) {
				result.klassen?.add(KlassenDaten.transpilerFromJSON(JSON.stringify(elem)));
			}
		}
		if ((obj.kurse !== undefined) && (obj.kurse !== null)) {
			for (const elem of obj.kurse) {
				result.kurse?.add(KursDaten.transpilerFromJSON(JSON.stringify(elem)));
			}
		}
		if ((obj.jahrgaenge !== undefined) && (obj.jahrgaenge !== null)) {
			for (const elem of obj.jahrgaenge) {
				result.jahrgaenge?.add(JahrgangsDaten.transpilerFromJSON(JSON.stringify(elem)));
			}
		}
		if ((obj.jahrgaengeGost !== undefined) && (obj.jahrgaengeGost !== null)) {
			for (const elem of obj.jahrgaengeGost) {
				result.jahrgaengeGost?.add(GostJahrgang.transpilerFromJSON(JSON.stringify(elem)));
			}
		}
		return result;
	}

	public static transpilerToJSON(obj : SchuelerListe) : string {
		let result = '{';
		result += '"idSchuljahresabschnitt" : ' + obj.idSchuljahresabschnitt + ',';
		if (!obj.schueler) {
			result += '"schueler" : []';
		} else {
			result += '"schueler" : [ ';
			for (let i = 0; i < obj.schueler.size(); i++) {
				const elem = obj.schueler.get(i);
				result += SchuelerListeEintrag.transpilerToJSON(elem);
				if (i < obj.schueler.size() - 1)
					result += ',';
			}
			result += ' ]' + ',';
		}
		if (!obj.klassen) {
			result += '"klassen" : []';
		} else {
			result += '"klassen" : [ ';
			for (let i = 0; i < obj.klassen.size(); i++) {
				const elem = obj.klassen.get(i);
				result += KlassenDaten.transpilerToJSON(elem);
				if (i < obj.klassen.size() - 1)
					result += ',';
			}
			result += ' ]' + ',';
		}
		if (!obj.kurse) {
			result += '"kurse" : []';
		} else {
			result += '"kurse" : [ ';
			for (let i = 0; i < obj.kurse.size(); i++) {
				const elem = obj.kurse.get(i);
				result += KursDaten.transpilerToJSON(elem);
				if (i < obj.kurse.size() - 1)
					result += ',';
			}
			result += ' ]' + ',';
		}
		if (!obj.jahrgaenge) {
			result += '"jahrgaenge" : []';
		} else {
			result += '"jahrgaenge" : [ ';
			for (let i = 0; i < obj.jahrgaenge.size(); i++) {
				const elem = obj.jahrgaenge.get(i);
				result += JahrgangsDaten.transpilerToJSON(elem);
				if (i < obj.jahrgaenge.size() - 1)
					result += ',';
			}
			result += ' ]' + ',';
		}
		if (!obj.jahrgaengeGost) {
			result += '"jahrgaengeGost" : []';
		} else {
			result += '"jahrgaengeGost" : [ ';
			for (let i = 0; i < obj.jahrgaengeGost.size(); i++) {
				const elem = obj.jahrgaengeGost.get(i);
				result += GostJahrgang.transpilerToJSON(elem);
				if (i < obj.jahrgaengeGost.size() - 1)
					result += ',';
			}
			result += ' ]' + ',';
		}
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

	public static transpilerToJSONPatch(obj : Partial<SchuelerListe>) : string {
		let result = '{';
		if (typeof obj.idSchuljahresabschnitt !== "undefined") {
			result += '"idSchuljahresabschnitt" : ' + obj.idSchuljahresabschnitt + ',';
		}
		if (typeof obj.schueler !== "undefined") {
			if (!obj.schueler) {
				result += '"schueler" : []';
			} else {
				result += '"schueler" : [ ';
				for (let i = 0; i < obj.schueler.size(); i++) {
					const elem = obj.schueler.get(i);
					result += SchuelerListeEintrag.transpilerToJSON(elem);
					if (i < obj.schueler.size() - 1)
						result += ',';
				}
				result += ' ]' + ',';
			}
		}
		if (typeof obj.klassen !== "undefined") {
			if (!obj.klassen) {
				result += '"klassen" : []';
			} else {
				result += '"klassen" : [ ';
				for (let i = 0; i < obj.klassen.size(); i++) {
					const elem = obj.klassen.get(i);
					result += KlassenDaten.transpilerToJSON(elem);
					if (i < obj.klassen.size() - 1)
						result += ',';
				}
				result += ' ]' + ',';
			}
		}
		if (typeof obj.kurse !== "undefined") {
			if (!obj.kurse) {
				result += '"kurse" : []';
			} else {
				result += '"kurse" : [ ';
				for (let i = 0; i < obj.kurse.size(); i++) {
					const elem = obj.kurse.get(i);
					result += KursDaten.transpilerToJSON(elem);
					if (i < obj.kurse.size() - 1)
						result += ',';
				}
				result += ' ]' + ',';
			}
		}
		if (typeof obj.jahrgaenge !== "undefined") {
			if (!obj.jahrgaenge) {
				result += '"jahrgaenge" : []';
			} else {
				result += '"jahrgaenge" : [ ';
				for (let i = 0; i < obj.jahrgaenge.size(); i++) {
					const elem = obj.jahrgaenge.get(i);
					result += JahrgangsDaten.transpilerToJSON(elem);
					if (i < obj.jahrgaenge.size() - 1)
						result += ',';
				}
				result += ' ]' + ',';
			}
		}
		if (typeof obj.jahrgaengeGost !== "undefined") {
			if (!obj.jahrgaengeGost) {
				result += '"jahrgaengeGost" : []';
			} else {
				result += '"jahrgaengeGost" : [ ';
				for (let i = 0; i < obj.jahrgaengeGost.size(); i++) {
					const elem = obj.jahrgaengeGost.get(i);
					result += GostJahrgang.transpilerToJSON(elem);
					if (i < obj.jahrgaengeGost.size() - 1)
						result += ',';
				}
				result += ' ]' + ',';
			}
		}
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

}

export function cast_de_svws_nrw_core_data_schueler_SchuelerListe(obj : unknown) : SchuelerListe {
	return obj as SchuelerListe;
}