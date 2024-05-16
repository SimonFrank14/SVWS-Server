import { JavaObject } from '../../../java/lang/JavaObject';
import { SchuldateiKatalogeintrag } from '../../../schuldatei/v1/data/SchuldateiKatalogeintrag';
import { HashMap } from '../../../java/util/HashMap';
import { ArrayList } from '../../../java/util/ArrayList';
import type { List } from '../../../java/util/List';
import type { JavaMap } from '../../../java/util/JavaMap';
import { IllegalArgumentException } from '../../../java/lang/IllegalArgumentException';

export class SchuldateiKatalogManager extends JavaObject {

	/**
	 * Der Name des Katalogs
	 */
	private readonly _name : string;

	/**
	 * Die Liste aller Katalog-Einträge dieses Katalogs
	 */
	private readonly _eintraege : List<SchuldateiKatalogeintrag> = new ArrayList<SchuldateiKatalogeintrag>();

	/**
	 * Eine Map von dem Wert der Katalog-Einträge auf diese
	 */
	private readonly _mapEintragByWert : JavaMap<string, SchuldateiKatalogeintrag> = new HashMap<string, SchuldateiKatalogeintrag>();


	/**
	 * Erstellt einen neuen Katalog-Manager.
	 *
	 * @param name   der Name des Katalogs
	 */
	public constructor(name : string) {
		super();
		this._name = name;
	}

	/**
	 * Fügt einen neuen Eintrag zum Manager hinzu.
	 *
	 * @param eintrag   der Eintrag
	 */
	addEintrag(eintrag : SchuldateiKatalogeintrag) : void {
		this._eintraege.add(eintrag);
		if (this._mapEintragByWert.containsKey(eintrag.wert))
			throw new IllegalArgumentException("Katalog " + this._name + ": Es existiert bereits ein anderer Katalog-Eintrag mit dem angegebenen Wert " + eintrag.wert + ".")
		this._mapEintragByWert.put(eintrag.wert, eintrag);
	}

	/**
	 * Gibt den Namen des Katalogs zurück.
	 *
	 * @return der Name des Katalogs
	 */
	public getName() : string {
		return this._name;
	}

	/**
	 * Gibt den Katalog-Eintrag zu dem Wert zurück, sofern der Wert gültig ist.
	 *
	 * @param wert   der Wert des gesuchten Katalog-Eintrags
	 *
	 * @return der Katalog-Eintrag oder null, wenn es keinen für den Wert gibt.
	 */
	public getEintrag(wert : string | null) : SchuldateiKatalogeintrag | null {
		return this._mapEintragByWert.get(wert);
	}

	/**
	 * Gibt die Bezeichnung des Katalog-Eintrag zu dem Wert zurück, sofern
	 * der Wert gültig ist.
	 *
	 * @param wert   der Wert des Katalog-Eintrags
	 *
	 * @return die Bezeichnung
	 *
	 * @throws IllegalArgumentException   falls der Wert ungültig ist
	 */
	public getBezeichnung(wert : string | null) : string {
		const eintrag : SchuldateiKatalogeintrag | null = this.getEintrag(wert);
		if (eintrag === null)
			throw new IllegalArgumentException("Es konnte kein Katalog-Eintrag für den Wert " + wert! + " gefunden werden.")
		return eintrag.bezeichnung;
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.schuldatei.v1.utils.SchuldateiKatalogManager';
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.schuldatei.v1.utils.SchuldateiKatalogManager'].includes(name);
	}

}

export function cast_de_svws_nrw_schuldatei_v1_utils_SchuldateiKatalogManager(obj : unknown) : SchuldateiKatalogManager {
	return obj as SchuldateiKatalogManager;
}