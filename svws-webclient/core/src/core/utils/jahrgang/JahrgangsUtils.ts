import { JavaObject } from '../../../java/lang/JavaObject';
import { IllegalStateException } from '../../../java/lang/IllegalStateException';
import { Schulform } from '../../../core/types/schule/Schulform';
import { Schulgliederung } from '../../../core/types/schule/Schulgliederung';

export class JahrgangsUtils extends JavaObject {


	private constructor() {
		super();
		throw new IllegalStateException("Instantiation not allowed")
	}

	/**
	 * Bestimmt für die angegebene Schulform, die übergebene Schulgliederung (auch beim Schüler eingetragenen Schulgliederung)
	 * und den angegebenen Jahrgang die restlichen Jahre an der Schule.
	 * Ist keine Berechnung für diese Kombination implementiert,
	 * so wird null zurückgegeben.
	 *
	 * @param schulform    die Schulform
	 * @param gliederung   die Schulgliederung
	 * @param jahrgang     der Jahrgang, für den die restlichen Jahre bestimmt werden sollen
	 *
	 * @return die restlichen Jahre oder null
	 */
	public static getRestlicheJahre(schulform : Schulform, gliederung : Schulgliederung, jahrgang : string) : number | null {
		if (gliederung === null)
			return null;
		if ((schulform as unknown === Schulform.FW as unknown) || (schulform as unknown === Schulform.WB as unknown) || (schulform as unknown === Schulform.BK as unknown) || (schulform as unknown === Schulform.SB as unknown))
			return null;
		if (jahrgang === null)
			return null;
		if (schulform as unknown === Schulform.GY as unknown) {
			switch (jahrgang) {
				case "05": {
					return gliederung.istG8() || (gliederung as unknown === Schulgliederung.DEFAULT as unknown) ? 8 : 9;
				}
				case "06": {
					return gliederung.istG8() || (gliederung as unknown === Schulgliederung.DEFAULT as unknown) ? 7 : 8;
				}
				case "07": {
					return gliederung.istG8() || (gliederung as unknown === Schulgliederung.DEFAULT as unknown) ? 6 : 7;
				}
				case "08": {
					return gliederung.istG8() || (gliederung as unknown === Schulgliederung.DEFAULT as unknown) ? 5 : 6;
				}
				case "09": {
					return gliederung.istG8() || (gliederung as unknown === Schulgliederung.DEFAULT as unknown) ? 4 : 5;
				}
				case "10": {
					return gliederung.istG8() ? null : 4;
				}
				case "EF": {
					return 3;
				}
				case "Q1": {
					return 2;
				}
				case "Q2": {
					return 1;
				}
				case "11": {
					return 3;
				}
				case "12": {
					return 2;
				}
				case "13": {
					return 1;
				}
				default: {
					return null;
				}
			}
		}
		switch (jahrgang) {
			case "E1": {
				return 4;
			}
			case "E2": {
				return 3;
			}
			case "E3": {
				return 3;
			}
			case "03": {
				return 2;
			}
			case "04": {
				return 1;
			}
			case "05": {
				return 6;
			}
			case "06": {
				return 5;
			}
			case "07": {
				return 4;
			}
			case "08": {
				return 3;
			}
			case "09": {
				return 2;
			}
			case "10": {
				return 1;
			}
			case "EF": {
				return 3;
			}
			case "Q1": {
				return 2;
			}
			case "Q2": {
				return 1;
			}
			case "11": {
				return 3;
			}
			case "12": {
				return 2;
			}
			case "13": {
				return 1;
			}
			default: {
				return null;
			}
		}
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.utils.jahrgang.JahrgangsUtils'].includes(name);
	}

}

export function cast_de_svws_nrw_core_utils_jahrgang_JahrgangsUtils(obj : unknown) : JahrgangsUtils {
	return obj as JahrgangsUtils;
}