import { JavaObject } from '../../../java/lang/JavaObject';
import { Class } from '../../../java/lang/Class';

export class Schild3KatalogEintragDQRNiveaus extends JavaObject {

	/**
	 * DQR-Niveau für Gliederung
	 */
	public Gliederung : string | null = null;

	/**
	 * DQR-Niveau für die Fachklasse
	 */
	public FKS : string | null = null;

	/**
	 * DQR-Niveau als Nummer
	 */
	public DQR_Niveau : number | null = null;

	/**
	 * Gültig ab Schuljahr
	 */
	public gueltigVon : number | null = null;

	/**
	 * Gültig bis Schuljahr
	 */
	public gueltigBis : number | null = null;


	public constructor() {
		super();
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.core.data.schild3.Schild3KatalogEintragDQRNiveaus';
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.data.schild3.Schild3KatalogEintragDQRNiveaus'].includes(name);
	}

	public static class = new Class<Schild3KatalogEintragDQRNiveaus>('de.svws_nrw.core.data.schild3.Schild3KatalogEintragDQRNiveaus');

	public static transpilerFromJSON(json : string): Schild3KatalogEintragDQRNiveaus {
		const obj = JSON.parse(json) as Partial<Schild3KatalogEintragDQRNiveaus>;
		const result = new Schild3KatalogEintragDQRNiveaus();
		result.Gliederung = (obj.Gliederung === undefined) ? null : obj.Gliederung === null ? null : obj.Gliederung;
		result.FKS = (obj.FKS === undefined) ? null : obj.FKS === null ? null : obj.FKS;
		result.DQR_Niveau = (obj.DQR_Niveau === undefined) ? null : obj.DQR_Niveau === null ? null : obj.DQR_Niveau;
		result.gueltigVon = (obj.gueltigVon === undefined) ? null : obj.gueltigVon === null ? null : obj.gueltigVon;
		result.gueltigBis = (obj.gueltigBis === undefined) ? null : obj.gueltigBis === null ? null : obj.gueltigBis;
		return result;
	}

	public static transpilerToJSON(obj : Schild3KatalogEintragDQRNiveaus) : string {
		let result = '{';
		result += '"Gliederung" : ' + ((obj.Gliederung === null) ? 'null' : JSON.stringify(obj.Gliederung)) + ',';
		result += '"FKS" : ' + ((obj.FKS === null) ? 'null' : JSON.stringify(obj.FKS)) + ',';
		result += '"DQR_Niveau" : ' + ((obj.DQR_Niveau === null) ? 'null' : obj.DQR_Niveau.toString()) + ',';
		result += '"gueltigVon" : ' + ((obj.gueltigVon === null) ? 'null' : obj.gueltigVon.toString()) + ',';
		result += '"gueltigBis" : ' + ((obj.gueltigBis === null) ? 'null' : obj.gueltigBis.toString()) + ',';
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

	public static transpilerToJSONPatch(obj : Partial<Schild3KatalogEintragDQRNiveaus>) : string {
		let result = '{';
		if (obj.Gliederung !== undefined) {
			result += '"Gliederung" : ' + ((obj.Gliederung === null) ? 'null' : JSON.stringify(obj.Gliederung)) + ',';
		}
		if (obj.FKS !== undefined) {
			result += '"FKS" : ' + ((obj.FKS === null) ? 'null' : JSON.stringify(obj.FKS)) + ',';
		}
		if (obj.DQR_Niveau !== undefined) {
			result += '"DQR_Niveau" : ' + ((obj.DQR_Niveau === null) ? 'null' : obj.DQR_Niveau.toString()) + ',';
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

export function cast_de_svws_nrw_core_data_schild3_Schild3KatalogEintragDQRNiveaus(obj : unknown) : Schild3KatalogEintragDQRNiveaus {
	return obj as Schild3KatalogEintragDQRNiveaus;
}
