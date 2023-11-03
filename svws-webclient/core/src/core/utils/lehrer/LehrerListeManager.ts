import { JavaObject } from '../../../java/lang/JavaObject';
import { HashMap2D } from '../../../core/adt/map/HashMap2D';
import { AttributMitAuswahl } from '../../../core/utils/AttributMitAuswahl';
import { Schulform } from '../../../core/types/schule/Schulform';
import { ArrayList } from '../../../java/util/ArrayList';
import { LehrerStammdaten } from '../../../core/data/lehrer/LehrerStammdaten';
import { JavaString } from '../../../java/lang/JavaString';
import { DeveloperNotificationException } from '../../../core/exceptions/DeveloperNotificationException';
import { PersonalTyp } from '../../../core/types/PersonalTyp';
import type { Comparator } from '../../../java/util/Comparator';
import { AuswahlManager } from '../../../core/utils/AuswahlManager';
import type { JavaFunction } from '../../../java/util/function/JavaFunction';
import { LehrerListeEintrag } from '../../../core/data/lehrer/LehrerListeEintrag';
import { LehrerUtils } from '../../../core/utils/lehrer/LehrerUtils';
import { JavaLong } from '../../../java/lang/JavaLong';
import type { List } from '../../../java/util/List';
import { Arrays } from '../../../java/util/Arrays';
import { Pair } from '../../../core/adt/Pair';

export class LehrerListeManager extends AuswahlManager<number, LehrerListeEintrag, LehrerStammdaten> {

	/**
	 * Funktionen zum Mappen von Auswahl- bzw. Daten-Objekten auf deren ID-Typ
	 */
	private static readonly _lehrerToId : JavaFunction<LehrerListeEintrag, number> = { apply : (k: LehrerListeEintrag) => k.id };

	private static readonly _lehrerDatenToId : JavaFunction<LehrerStammdaten, number> = { apply : (k: LehrerStammdaten) => k.id };

	/**
	 * Zusätzliche Maps, welche zum schnellen Zugriff auf Teilmengen der Liste verwendet werden können
	 */
	private readonly _mapKlasseIstSichtbar : HashMap2D<boolean, number, LehrerListeEintrag> = new HashMap2D();

	private readonly _mapLehrerIstStatistikrelevant : HashMap2D<boolean, number, LehrerListeEintrag> = new HashMap2D();

	private readonly _mapKlasseHatPersonaltyp : HashMap2D<PersonalTyp, number, LehrerListeEintrag> = new HashMap2D();

	/**
	 * Das Filter-Attribut für die Personal-Typen
	 */
	public readonly personaltypen : AttributMitAuswahl<number, PersonalTyp>;

	private static readonly _personaltypToId : JavaFunction<PersonalTyp, number> = { apply : (pt: PersonalTyp) => pt.id };

	private static readonly _comparatorPersonaltypen : Comparator<PersonalTyp> = { compare : (a: PersonalTyp, b: PersonalTyp) => a.ordinal() - b.ordinal() };

	/**
	 * Das Filter-Attribut auf nur sichtbare Lehrer
	 */
	private _filterNurSichtbar : boolean = true;

	/**
	 * Das Filter-Attribut auf nur Statistik-relevante Lehrer
	 */
	private _filterNurStatistikrelevant : boolean = true;


	/**
	 * Erstellt einen neuen Manager und initialisiert diesen mit den übergebenen Daten
	 *
	 * @param schulform     die Schulform der Schule
	 * @param lehrer        die Liste der Lehrer
	 */
	public constructor(schulform : Schulform | null, lehrer : List<LehrerListeEintrag>) {
		super(schulform, lehrer, LehrerUtils.comparator, LehrerListeManager._lehrerToId, LehrerListeManager._lehrerDatenToId, Arrays.asList(new Pair("nachname", true), new Pair("vorname", true), new Pair("kuerzel", true)));
		this.personaltypen = new AttributMitAuswahl(Arrays.asList(...PersonalTyp.values()), LehrerListeManager._personaltypToId, LehrerListeManager._comparatorPersonaltypen, this._eventHandlerFilterChanged);
		this.initLehrer();
	}

	private initLehrer() : void {
		for (const l of this.liste.list()) {
			this._mapKlasseIstSichtbar.put(l.istSichtbar, l.id, l);
			this._mapLehrerIstStatistikrelevant.put(l.istRelevantFuerStatistik, l.id, l);
			const personalTyp : PersonalTyp | null = PersonalTyp.fromBezeichnung(l.personTyp);
			if (personalTyp !== null)
				this._mapKlasseHatPersonaltyp.put(personalTyp, l.id, l);
		}
	}

	/**
	 * Passt bei Änderungen an den Daten ggf. das Auswahl-Objekt an.
	 *
	 * @param eintrag   der Auswahl-Eintrag
	 * @param daten     das neue Daten-Objekt zu der Auswahl
	 */
	protected onSetDaten(eintrag : LehrerListeEintrag, daten : LehrerStammdaten) : boolean {
		let updateEintrag : boolean = false;
		if (!JavaObject.equalsTranspiler(daten.kuerzel, (eintrag.kuerzel))) {
			eintrag.kuerzel = daten.kuerzel;
			updateEintrag = true;
		}
		if (!JavaObject.equalsTranspiler(daten.nachname, (eintrag.nachname))) {
			eintrag.nachname = daten.nachname;
			updateEintrag = true;
		}
		if (!JavaObject.equalsTranspiler(daten.vorname, (eintrag.vorname))) {
			eintrag.vorname = daten.vorname;
			updateEintrag = true;
		}
		return updateEintrag;
	}

	/**
	 * Gibt den Personaltyp für den aktuell ausgewählten Lehrer zurück.
	 *
	 * @return der Personaltyp
	 */
	public datenGetPersonalTyp() : PersonalTyp | null {
		if ((this._daten === null) || (this._daten.personalTyp === null))
			return null;
		return PersonalTyp.fromBezeichnung(this._daten.personalTyp);
	}

	/**
	 * Gibt die aktuelle Filtereinstellung auf nur sichtbare Lehrer zurück.
	 *
	 * @return true, wenn nur nichtbare Lehrer angezeigt werden und ansonsten false
	 */
	public filterNurSichtbar() : boolean {
		return this._filterNurSichtbar;
	}

	/**
	 * Setzt die Filtereinstellung auf nur sichtbare Lehrer.
	 *
	 * @param value   true, wenn der Filter aktiviert werden soll, und ansonsten false
	 */
	public setFilterNurSichtbar(value : boolean) : void {
		this._filterNurSichtbar = value;
		this._eventHandlerFilterChanged.run();
	}

	/**
	 * Gibt die aktuelle Filtereinstellung auf nur Statistik-relevante Lehrer zurück.
	 *
	 * @return true, wenn nur Statistik-relevante Lehrer angezeigt werden und ansonsten false
	 */
	public filterNurStatistikRelevant() : boolean {
		return this._filterNurStatistikrelevant;
	}

	/**
	 * Setzt die Filtereinstellung auf nur Statistik-relevante Lehrer.
	 *
	 * @param value   true, wenn der Filter aktiviert werden soll, und ansonsten false
	 */
	public setFilterNurStatistikRelevant(value : boolean) : void {
		this._filterNurStatistikrelevant = value;
		this._eventHandlerFilterChanged.run();
	}

	/**
	 * Vergleicht zwei Lehrerlisteneinträge anhand der spezifizierten Ordnung.
	 *
	 * @param a   der erste Eintrag
	 * @param b   der zweite Eintrag
	 *
	 * @return das Ergebnis des Vergleichs (-1 kleine, 0 gleich und 1 größer)
	 */
	protected compare(a : LehrerListeEintrag, b : LehrerListeEintrag) : number {
		for (const criteria of this._order) {
			const field : string | null = criteria.a;
			const asc : boolean = (criteria.b === null) || criteria.b;
			let cmp : number = 0;
			if (JavaObject.equalsTranspiler("kuerzel", (field))) {
				cmp = JavaString.compareTo(a.kuerzel, b.kuerzel);
			} else
				if (JavaObject.equalsTranspiler("nachname", (field))) {
					cmp = JavaString.compareTo(a.nachname, b.nachname);
				} else
					if (JavaObject.equalsTranspiler("vorname", (field))) {
						cmp = JavaString.compareTo(a.vorname, b.vorname);
					} else
						throw new DeveloperNotificationException("Fehler bei der Sortierung. Das Sortierkriterium wird vom Manager nicht unterstützt.")
			if (cmp === 0)
				continue;
			return asc ? cmp : -cmp;
		}
		return JavaLong.compare(a.id, b.id);
	}

	/**
	 * Gibt eine gefilterte Liste der Lehrer zurück. Als Filter werden dabei
	 * die Personaltypen, ein Filter auf nur sichtbare Lehrer und einer
	 * auf nur Statistik-relevante Lehrer beachtet.
	 *
	 * @return die gefilterte Liste
	 */
	protected onFilter() : List<LehrerListeEintrag> {
		const tmpList : List<LehrerListeEintrag> = new ArrayList();
		for (const eintrag of this.liste.list()) {
			if (this._filterNurSichtbar && !eintrag.istSichtbar)
				continue;
			if (this._filterNurStatistikrelevant && !eintrag.istRelevantFuerStatistik)
				continue;
			if (this.personaltypen.auswahlExists()) {
				if ((eintrag.personTyp === null) || (JavaString.isEmpty(eintrag.personTyp)))
					continue;
				const personalTyp : PersonalTyp | null = PersonalTyp.fromBezeichnung(eintrag.personTyp);
				if ((personalTyp === null) || (!this.personaltypen.auswahlHas(personalTyp)))
					continue;
			}
			tmpList.add(eintrag);
		}
		const comparator : Comparator<LehrerListeEintrag> = { compare : (a: LehrerListeEintrag, b: LehrerListeEintrag) => this.compare(a, b) };
		tmpList.sort(comparator);
		return tmpList;
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.utils.AuswahlManager', 'de.svws_nrw.core.utils.lehrer.LehrerListeManager'].includes(name);
	}

}

export function cast_de_svws_nrw_core_utils_lehrer_LehrerListeManager(obj : unknown) : LehrerListeManager {
	return obj as LehrerListeManager;
}
