import { SchulformSchulgliederung } from '../../../asd/data/schule/SchulformSchulgliederung';
import { ArrayList } from '../../../java/util/ArrayList';
import type { List } from '../../../java/util/List';
import { Class } from '../../../java/lang/Class';
import { CoreTypeData } from '../../../asd/data/CoreTypeData';

export class HerkunftBildungsgangKatalogEintrag extends CoreTypeData {

	/**
	 * Die Kürzel der Schulformen, bei welchen der Bildungsgang als Herkunft vorkommen kann (BK und SB).
	 */
	public zulaessig : List<SchulformSchulgliederung> = new ArrayList<SchulformSchulgliederung>();


	public constructor() {
		super();
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.asd.data.schueler.HerkunftBildungsgangKatalogEintrag';
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.asd.data.schueler.HerkunftBildungsgangKatalogEintrag', 'de.svws_nrw.asd.data.CoreTypeData'].includes(name);
	}

	public static class = new Class<HerkunftBildungsgangKatalogEintrag>('de.svws_nrw.asd.data.schueler.HerkunftBildungsgangKatalogEintrag');

	public static transpilerFromJSON(json : string): HerkunftBildungsgangKatalogEintrag {
		const obj = JSON.parse(json) as Partial<HerkunftBildungsgangKatalogEintrag>;
		const result = new HerkunftBildungsgangKatalogEintrag();
		if (obj.id === undefined)
			throw new Error('invalid json format, missing attribute id');
		result.id = obj.id;
		if (obj.schluessel === undefined)
			throw new Error('invalid json format, missing attribute schluessel');
		result.schluessel = obj.schluessel;
		if (obj.kuerzel === undefined)
			throw new Error('invalid json format, missing attribute kuerzel');
		result.kuerzel = obj.kuerzel;
		if (obj.text === undefined)
			throw new Error('invalid json format, missing attribute text');
		result.text = obj.text;
		result.gueltigVon = (obj.gueltigVon === undefined) ? null : obj.gueltigVon === null ? null : obj.gueltigVon;
		result.gueltigBis = (obj.gueltigBis === undefined) ? null : obj.gueltigBis === null ? null : obj.gueltigBis;
		if (obj.zulaessig !== undefined) {
			for (const elem of obj.zulaessig) {
				result.zulaessig.add(SchulformSchulgliederung.transpilerFromJSON(JSON.stringify(elem)));
			}
		}
		return result;
	}

	public static transpilerToJSON(obj : HerkunftBildungsgangKatalogEintrag) : string {
		let result = '{';
		result += '"id" : ' + obj.id.toString() + ',';
		result += '"schluessel" : ' + JSON.stringify(obj.schluessel) + ',';
		result += '"kuerzel" : ' + JSON.stringify(obj.kuerzel) + ',';
		result += '"text" : ' + JSON.stringify(obj.text) + ',';
		result += '"gueltigVon" : ' + ((obj.gueltigVon === null) ? 'null' : obj.gueltigVon.toString()) + ',';
		result += '"gueltigBis" : ' + ((obj.gueltigBis === null) ? 'null' : obj.gueltigBis.toString()) + ',';
		result += '"zulaessig" : [ ';
		for (let i = 0; i < obj.zulaessig.size(); i++) {
			const elem = obj.zulaessig.get(i);
			result += SchulformSchulgliederung.transpilerToJSON(elem);
			if (i < obj.zulaessig.size() - 1)
				result += ',';
		}
		result += ' ]' + ',';
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

	public static transpilerToJSONPatch(obj : Partial<HerkunftBildungsgangKatalogEintrag>) : string {
		let result = '{';
		if (obj.id !== undefined) {
			result += '"id" : ' + obj.id.toString() + ',';
		}
		if (obj.schluessel !== undefined) {
			result += '"schluessel" : ' + JSON.stringify(obj.schluessel) + ',';
		}
		if (obj.kuerzel !== undefined) {
			result += '"kuerzel" : ' + JSON.stringify(obj.kuerzel) + ',';
		}
		if (obj.text !== undefined) {
			result += '"text" : ' + JSON.stringify(obj.text) + ',';
		}
		if (obj.gueltigVon !== undefined) {
			result += '"gueltigVon" : ' + ((obj.gueltigVon === null) ? 'null' : obj.gueltigVon.toString()) + ',';
		}
		if (obj.gueltigBis !== undefined) {
			result += '"gueltigBis" : ' + ((obj.gueltigBis === null) ? 'null' : obj.gueltigBis.toString()) + ',';
		}
		if (obj.zulaessig !== undefined) {
			result += '"zulaessig" : [ ';
			for (let i = 0; i < obj.zulaessig.size(); i++) {
				const elem = obj.zulaessig.get(i);
				result += SchulformSchulgliederung.transpilerToJSON(elem);
				if (i < obj.zulaessig.size() - 1)
					result += ',';
			}
			result += ' ]' + ',';
		}
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

}

export function cast_de_svws_nrw_asd_data_schueler_HerkunftBildungsgangKatalogEintrag(obj : unknown) : HerkunftBildungsgangKatalogEintrag {
	return obj as HerkunftBildungsgangKatalogEintrag;
}
