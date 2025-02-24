import { JavaObject } from '../../../java/lang/JavaObject';
import { StundenplanblockungManagerFach, cast_de_svws_nrw_core_utils_stundenplanblockung_StundenplanblockungManagerFach } from '../../../core/utils/stundenplanblockung/StundenplanblockungManagerFach';
import { HashMap } from '../../../java/util/HashMap';
import { NullPointerException } from '../../../java/lang/NullPointerException';
import { ArrayList } from '../../../java/util/ArrayList';
import type { List } from '../../../java/util/List';
import { Class } from '../../../java/lang/Class';
import type { JavaMap } from '../../../java/util/JavaMap';

export class StundenplanblockungManagerFachMenge extends JavaObject {

	private readonly _menge : List<StundenplanblockungManagerFach>;

	private readonly _map : JavaMap<number, StundenplanblockungManagerFach>;


	/**
	 * Erzeugt eine neues Objekt zur Verwaltung der Menge aller Fächer.
	 */
	public constructor() {
		super();
		this._menge = new ArrayList();
		this._map = new HashMap();
	}

	/**
	 * Fügt das Fach hinzu. <br>
	 * Wirft eine NullPointerException, falls die Fach-ID bereits existiert.
	 *
	 * @param pFachID                Die Datenbank-ID des Fach.
	 * @param pKuerzel               Das Kürzel des Faches.
	 * @throws NullPointerException  Falls die Fach-ID bereits existiert.
	 */
	public addOrException(pFachID : number, pKuerzel : string) : void {
		if (this._map.containsKey(pFachID))
			throw new NullPointerException("Die Fach-ID " + pFachID + " existiert bereits!")
		const fa : StundenplanblockungManagerFach = new StundenplanblockungManagerFach(pFachID, pKuerzel);
		this._map.put(pFachID, fa);
		this._menge.add(fa);
	}

	/**
	 * Liefert das {@link StundenplanblockungFach}-Objekt zur übergebenen ID. <br>
	 * Wirft eine NullPointerException, falls die Fach-ID unbekannt ist.
	 *
	 * @param pFachID                Die Datenbank-ID des Faches.
	 * @return                       Das {@link StundenplanblockungFach}-Objekt zur übergebenen ID.
	 * @throws NullPointerException  Falls die Fach-ID unbekannt ist.
	 */
	public getOrException(pFachID : number) : StundenplanblockungManagerFach {
		const fa : StundenplanblockungManagerFach | null = this._map.get(pFachID);
		if (fa === null)
			throw new NullPointerException("Fach-ID " + pFachID + " unbekannt!")
		return fa;
	}

	/**
	 * Löscht das übergebene Fach. <br>
	 * Wirft eine NullPointerException, falls die Fach-ID unbekannt ist.
	 *
	 * @param pFachID                Die Datenbank-ID des Faches.
	 * @throws NullPointerException  Falls die Fach-ID unbekannt ist.
	 */
	public removeOrException(pFachID : number) : void {
		const fa : StundenplanblockungManagerFach = this.getOrException(pFachID);
		this._map.remove(pFachID);
		this._menge.remove(fa);
	}

	/**
	 * Liefert TRUE, falls die Fach-ID existiert.
	 *
	 * @param pFachID   die Datenbank-ID des Faches.
	 *
	 * @return TRUE, falls die Fach-ID existiert.
	 */
	public exists(pFachID : number) : boolean {
		return this._map.containsKey(pFachID);
	}

	/**
	 * Liefert die Anzahl an Lehrkräften.
	 *
	 * @return Die Anzahl an Lehrkräften.
	 */
	public size() : number {
		return this._menge.size();
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.core.utils.stundenplanblockung.StundenplanblockungManagerFachMenge';
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.utils.stundenplanblockung.StundenplanblockungManagerFachMenge'].includes(name);
	}

	public static class = new Class<StundenplanblockungManagerFachMenge>('de.svws_nrw.core.utils.stundenplanblockung.StundenplanblockungManagerFachMenge');

}

export function cast_de_svws_nrw_core_utils_stundenplanblockung_StundenplanblockungManagerFachMenge(obj : unknown) : StundenplanblockungManagerFachMenge {
	return obj as StundenplanblockungManagerFachMenge;
}
