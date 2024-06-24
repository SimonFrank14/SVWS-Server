import { JavaObject } from '../../../java/lang/JavaObject';
import { ArrayList } from '../../../java/util/ArrayList';
import type { List } from '../../../java/util/List';

export class GostBlockungRegel extends JavaObject {

	/**
	 * Die ID der Regel
	 */
	public id : number = -1;

	/**
	 * Der Type der Regel - siehe {@link GostKursblockungRegelTyp}
	 */
	public typ : number = -1;

	/**
	 * Eine Liste der Regel-Parameter
	 */
	public parameter : List<number> = new ArrayList<number>();


	public constructor() {
		super();
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.core.data.gost.GostBlockungRegel';
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.data.gost.GostBlockungRegel'].includes(name);
	}

	public static transpilerFromJSON(json : string): GostBlockungRegel {
		const obj = JSON.parse(json);
		const result = new GostBlockungRegel();
		if (obj.id === undefined)
			 throw new Error('invalid json format, missing attribute id');
		result.id = obj.id;
		if (obj.typ === undefined)
			 throw new Error('invalid json format, missing attribute typ');
		result.typ = obj.typ;
		if ((obj.parameter !== undefined) && (obj.parameter !== null)) {
			for (const elem of obj.parameter) {
				result.parameter?.add(elem);
			}
		}
		return result;
	}

	public static transpilerToJSON(obj : GostBlockungRegel) : string {
		let result = '{';
		result += '"id" : ' + obj.id + ',';
		result += '"typ" : ' + obj.typ + ',';
		if (!obj.parameter) {
			result += '"parameter" : []';
		} else {
			result += '"parameter" : [ ';
			for (let i = 0; i < obj.parameter.size(); i++) {
				const elem = obj.parameter.get(i);
				result += elem;
				if (i < obj.parameter.size() - 1)
					result += ',';
			}
			result += ' ]' + ',';
		}
		result = result.slice(0, -1);
		result += '}';
		return result;
	}

	public static transpilerToJSONPatch(obj : Partial<GostBlockungRegel>) : string {
		let result = '{';
		if (obj.id !== undefined) {
			result += '"id" : ' + obj.id + ',';
		}
		if (obj.typ !== undefined) {
			result += '"typ" : ' + obj.typ + ',';
		}
		if (obj.parameter !== undefined) {
			if (!obj.parameter) {
				result += '"parameter" : []';
			} else {
				result += '"parameter" : [ ';
				for (let i = 0; i < obj.parameter.size(); i++) {
					const elem = obj.parameter.get(i);
					result += elem;
					if (i < obj.parameter.size() - 1)
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

export function cast_de_svws_nrw_core_data_gost_GostBlockungRegel(obj : unknown) : GostBlockungRegel {
	return obj as GostBlockungRegel;
}
