import { JavaObject } from '../../../java/lang/JavaObject';
import { JahrgaengeKatalogEintrag } from '../../../asd/data/jahrgang/JahrgaengeKatalogEintrag';
import { KAOAZusatzmerkmalKatalogEintrag } from '../../../asd/data/kaoa/KAOAZusatzmerkmalKatalogEintrag';
import { HashMap } from '../../../java/util/HashMap';
import { Schulform } from '../../../asd/types/schule/Schulform';
import { ArrayList } from '../../../java/util/ArrayList';
import { JavaString } from '../../../java/lang/JavaString';
import { DeveloperNotificationException } from '../../../core/exceptions/DeveloperNotificationException';
import type { Comparator } from '../../../java/util/Comparator';
import type { JavaFunction } from '../../../java/util/function/JavaFunction';
import { KAOAKategorieKatalogEintrag } from '../../../asd/data/kaoa/KAOAKategorieKatalogEintrag';
import { KAOAEbene4 } from '../../../asd/types/kaoa/KAOAEbene4';
import { SchuelerKAoADaten } from '../../../core/data/schueler/SchuelerKAoADaten';
import { KAOAKategorie } from '../../../asd/types/kaoa/KAOAKategorie';
import { KAOAEbene4KatalogEintrag } from '../../../asd/data/kaoa/KAOAEbene4KatalogEintrag';
import type { List } from '../../../java/util/List';
import { Collections } from '../../../java/util/Collections';
import { KAOAMerkmal } from '../../../asd/types/kaoa/KAOAMerkmal';
import { IllegalArgumentException } from '../../../java/lang/IllegalArgumentException';
import { Pair } from '../../../asd/adt/Pair';
import { AttributMitAuswahl } from '../../../core/utils/AttributMitAuswahl';
import { KAOAAnschlussoptionen } from '../../../asd/types/kaoa/KAOAAnschlussoptionen';
import { MapUtils } from '../../../core/utils/MapUtils';
import { AuswahlManager } from '../../../core/utils/AuswahlManager';
import { KAOABerufsfeld } from '../../../asd/types/kaoa/KAOABerufsfeld';
import { Jahrgaenge } from '../../../asd/types/jahrgang/Jahrgaenge';
import { JavaLong } from '../../../java/lang/JavaLong';
import { KAOAAnschlussoptionenKatalogEintrag } from '../../../asd/data/kaoa/KAOAAnschlussoptionenKatalogEintrag';
import { Class } from '../../../java/lang/Class';
import { Arrays } from '../../../java/util/Arrays';
import { KAOABerufsfeldKatalogEintrag } from '../../../asd/data/kaoa/KAOABerufsfeldKatalogEintrag';
import type { JavaMap } from '../../../java/util/JavaMap';
import { Schuljahresabschnitt } from '../../../asd/data/schule/Schuljahresabschnitt';
import { KAOAMerkmalKatalogEintrag } from '../../../asd/data/kaoa/KAOAMerkmalKatalogEintrag';
import { KAOAZusatzmerkmal } from '../../../asd/types/kaoa/KAOAZusatzmerkmal';

export class SchuelerKAoAManager extends AuswahlManager<number, SchuelerKAoADaten, SchuelerKAoADaten> {

	/**
	 * Ein Default-Comparator für den Vergleich von KAoA in KAoA-Listen.
	 */
	public static readonly comparator : Comparator<SchuelerKAoADaten> = { compare : (a: SchuelerKAoADaten, b: SchuelerKAoADaten) => {
		let cmp : number = JavaLong.compare(a.idSchuljahresabschnitt, b.idSchuljahresabschnitt);
		if (cmp !== 0)
			return cmp;
		cmp = JavaLong.compare(a.idKategorie, b.idKategorie);
		if (cmp !== 0)
			return cmp;
		return JavaLong.compare(a.id, b.id);
	} };

	/**
	 *  Funktionen zum Mappen von Auswahl- bzw. Daten-Objekten auf deren ID-Typ
	 */
	private static readonly _kaoaToId : JavaFunction<SchuelerKAoADaten, number> = { apply : (kaoa: SchuelerKAoADaten) => kaoa.id };

	private readonly _kategorieToId : JavaFunction<KAOAKategorie, number> = { apply : (kategorie: KAOAKategorie) => {
		const ke : KAOAKategorieKatalogEintrag | null = kategorie.daten(this.getSchuljahr());
		if (ke === null)
			throw new IllegalArgumentException(JavaString.format("Die KAOA-Kategorie %s ist in dem Schuljahr %d nicht gültig.", kategorie.name(), this.getSchuljahr()))
		return ke.id;
	} };

	private static readonly _comparatorKategorie : Comparator<KAOAKategorie> = { compare : (a: KAOAKategorie, b: KAOAKategorie) => a.ordinal() - b.ordinal() };

	private readonly _merkmalToId : JavaFunction<KAOAMerkmal, number> = { apply : (merkmal: KAOAMerkmal) => {
		const ke : KAOAMerkmalKatalogEintrag | null = merkmal.daten(this.getSchuljahr());
		if (ke === null)
			throw new IllegalArgumentException(JavaString.format("Die KAOA-Merkmal %s ist in dem Schuljahr %d nicht gültig.", merkmal.name(), this.getSchuljahr()))
		return ke.id;
	} };

	private static readonly _comparatorMerkmal : Comparator<KAOAMerkmal> = { compare : (a: KAOAMerkmal, b: KAOAMerkmal) => a.ordinal() - b.ordinal() };

	private readonly _zusatzmerkmalToId : JavaFunction<KAOAZusatzmerkmal, number> = { apply : (zusatzmerkmal: KAOAZusatzmerkmal) => {
		const ke : KAOAZusatzmerkmalKatalogEintrag | null = zusatzmerkmal.daten(this.getSchuljahr());
		if (ke === null)
			throw new IllegalArgumentException(JavaString.format("Die KAOA-Zusatzmerkmal %s ist in dem Schuljahr %d nicht gültig.", zusatzmerkmal.name(), this.getSchuljahr()))
		return ke.id;
	} };

	private static readonly _comparatorZusatzmerkmal : Comparator<KAOAZusatzmerkmal> = { compare : (a: KAOAZusatzmerkmal, b: KAOAZusatzmerkmal) => a.ordinal() - b.ordinal() };

	private readonly _anschlussoptionToId : JavaFunction<KAOAAnschlussoptionen, number> = { apply : (anschlussoption: KAOAAnschlussoptionen) => {
		const ke : KAOAAnschlussoptionenKatalogEintrag | null = anschlussoption.daten(this.getSchuljahr());
		if (ke === null)
			throw new IllegalArgumentException(JavaString.format("Die KAOA-Anschlussoption %s ist in dem Schuljahr %d nicht gültig.", anschlussoption.name(), this.getSchuljahr()))
		return ke.id;
	} };

	private static readonly _comparatorAnschlussoptionen : Comparator<KAOAAnschlussoptionen> = { compare : (a: KAOAAnschlussoptionen, b: KAOAAnschlussoptionen) => a.ordinal() - b.ordinal() };

	private readonly _ebene4ToId : JavaFunction<KAOAEbene4, number> = { apply : (ebene4: KAOAEbene4) => {
		const ke : KAOAEbene4KatalogEintrag | null = ebene4.daten(this.getSchuljahr());
		if (ke === null)
			throw new IllegalArgumentException(JavaString.format("Die KAOA-Ebene 4 %s ist in dem Schuljahr %d nicht gültig.", ebene4.name(), this.getSchuljahr()))
		return ke.id;
	} };

	private static readonly _comparatorEbene4 : Comparator<KAOAEbene4> = { compare : (a: KAOAEbene4, b: KAOAEbene4) => a.ordinal() - b.ordinal() };

	private static readonly _comparatorBerufsfelder : Comparator<KAOABerufsfeld> = { compare : (a: KAOABerufsfeld, b: KAOABerufsfeld) => a.ordinal() - b.ordinal() };

	/**
	 * Das Filter-Attribut für die Kategorien
	 */
	public readonly _kategorien : AttributMitAuswahl<number, KAOAKategorie>;

	/**
	 * Das Filter-Attribut für die Merkmale
	 */
	public readonly _merkmale : AttributMitAuswahl<number, KAOAMerkmal>;

	/**
	 * Das Filter-Attribut für die Zusatzmerkmale
	 */
	public readonly _zusatzmerkmale : AttributMitAuswahl<number, KAOAZusatzmerkmal>;

	/**
	 * Das Filter-Attribut für die Anschlussoptionen
	 */
	public readonly _anschlussoptionen : AttributMitAuswahl<number, KAOAAnschlussoptionen>;

	/**
	 * Das Filter-Attribut für die Ebene4
	 */
	public readonly _ebene4 : AttributMitAuswahl<number, KAOAEbene4>;

	/**
	 * Das Filter-Attribut für die Berufsfelder
	 */
	private readonly _berufsfelder : List<KAOABerufsfeld>;

	/**
	 *  Zusätzliche Maps, welche zum schnellen Zugriff auf Teilmengen der Liste
	 *  verwendet werden können
	 */
	private readonly _mapMerkmalByKategorie : JavaMap<string, List<KAOAMerkmal>> = new HashMap<string, List<KAOAMerkmal>>();

	private readonly _mapZusatzmerkmalByMerkmal : JavaMap<string, List<KAOAZusatzmerkmal>> = new HashMap<string, List<KAOAZusatzmerkmal>>();

	private readonly _mapAnschlussoptionByZusatzmerkmal : JavaMap<string, List<KAOAAnschlussoptionen>> = new HashMap<string, List<KAOAAnschlussoptionen>>();

	private readonly _mapEbene4ByZusatzmerkmal : JavaMap<string, List<KAOAEbene4>> = new HashMap<string, List<KAOAEbene4>>();


	/**
	 * Erstellt einen neuen Manager mit den übergebenen KAoA Daten
	 *
	 * @param schuljahresabschnitt       Der Schuljahresabschnitt, auf den sich dien KAoA-Daten bezieht
	 * @param schuljahresabschnittSchule Die Liste der Schuljahresabschnitte
	 * @param schuljahresabschnitte      Der Schuljahresabschnitt, in welchem sich die Schule aktuell befindet.
	 * @param schulform                  Die Schulform der Schule
	 * @param schuelerKAoA               KAoA Daten des Schülers
	 */
	public constructor(schuljahresabschnitt : number, schuljahresabschnittSchule : number, schuljahresabschnitte : List<Schuljahresabschnitt>, schulform : Schulform | null, schuelerKAoA : List<SchuelerKAoADaten>) {
		super(schuljahresabschnitt, schuljahresabschnittSchule, schuljahresabschnitte, schulform, schuelerKAoA, SchuelerKAoAManager.comparator, SchuelerKAoAManager._kaoaToId, SchuelerKAoAManager._kaoaToId, Arrays.asList(new Pair("schuljahr", true), new Pair("kategorie", true)));
		this._kategorien = new AttributMitAuswahl(Arrays.asList(...KAOAKategorie.values()), this._kategorieToId, SchuelerKAoAManager._comparatorKategorie, this._eventHandlerFilterChanged);
		this._merkmale = new AttributMitAuswahl(Arrays.asList(...KAOAMerkmal.values()), this._merkmalToId, SchuelerKAoAManager._comparatorMerkmal, this._eventHandlerFilterChanged);
		this._zusatzmerkmale = new AttributMitAuswahl(Arrays.asList(...KAOAZusatzmerkmal.values()), this._zusatzmerkmalToId, SchuelerKAoAManager._comparatorZusatzmerkmal, this._eventHandlerFilterChanged);
		this._anschlussoptionen = new AttributMitAuswahl(Arrays.asList(...KAOAAnschlussoptionen.values()), this._anschlussoptionToId, SchuelerKAoAManager._comparatorAnschlussoptionen, this._eventHandlerFilterChanged);
		this._berufsfelder = Arrays.asList(...KAOABerufsfeld.values());
		this._berufsfelder.sort(SchuelerKAoAManager._comparatorBerufsfelder);
		this._ebene4 = new AttributMitAuswahl(Arrays.asList(...KAOAEbene4.values()), this._ebene4ToId, SchuelerKAoAManager._comparatorEbene4, this._eventHandlerFilterChanged);
		this.initKAoA();
	}

	private initKAoA() : void {
		for (const kategorie of this._kategorien.list()) {
			const kategorieEintrag : KAOAKategorieKatalogEintrag | null = kategorie.daten(this.getSchuljahr());
			if (kategorieEintrag === null)
				continue;
			const merkmaleOfKategorie : List<KAOAMerkmal> = new ArrayList<KAOAMerkmal>();
			for (const merkmal of this._merkmale.list()) {
				const merkmalEintrag : KAOAMerkmalKatalogEintrag | null = merkmal.daten(this.getSchuljahr());
				if (merkmalEintrag === null)
					continue;
				if (JavaObject.equalsTranspiler(merkmalEintrag.kategorie, (kategorie.name())))
					merkmaleOfKategorie.add(merkmal);
				const zusatzmerkmaleOfMerkmal : List<KAOAZusatzmerkmal> = new ArrayList<KAOAZusatzmerkmal>();
				for (const zusatzmerkmal of this._zusatzmerkmale.list()) {
					const zusatzmerkmalEintrag : KAOAZusatzmerkmalKatalogEintrag | null = zusatzmerkmal.daten(this.getSchuljahr());
					if (zusatzmerkmalEintrag === null)
						continue;
					if (JavaObject.equalsTranspiler(zusatzmerkmalEintrag.merkmal, (merkmal.name())))
						zusatzmerkmaleOfMerkmal.add(zusatzmerkmal);
					this.processZusatzmerkmal(zusatzmerkmalEintrag, zusatzmerkmal.name());
				}
				this._mapZusatzmerkmalByMerkmal.put(merkmalEintrag.kuerzel, zusatzmerkmaleOfMerkmal);
			}
			this._mapMerkmalByKategorie.put(kategorieEintrag.kuerzel, merkmaleOfKategorie);
		}
	}

	private processZusatzmerkmal(zusatzmerkmalEintrag : KAOAZusatzmerkmalKatalogEintrag, nameZusatzmerkmal : string) : void {
		const anschlussoptionOfZusatzmerkmal : List<KAOAAnschlussoptionen> = new ArrayList<KAOAAnschlussoptionen>();
		for (const anschlussoption of this._anschlussoptionen.list()) {
			const anschlussoptionEintrag : KAOAAnschlussoptionenKatalogEintrag | null = anschlussoption.daten(this.getSchuljahr());
			if (anschlussoptionEintrag === null)
				continue;
			for (const anzeigeMerkmal of anschlussoptionEintrag.anzeigeZusatzmerkmal)
				if (JavaObject.equalsTranspiler(anzeigeMerkmal, (nameZusatzmerkmal)))
					anschlussoptionOfZusatzmerkmal.add(anschlussoption);
		}
		this._mapAnschlussoptionByZusatzmerkmal.put(zusatzmerkmalEintrag.kuerzel, anschlussoptionOfZusatzmerkmal);
		const ebene4OfZusatzmerkmal : List<KAOAEbene4> = new ArrayList<KAOAEbene4>();
		for (const ebene4 of this._ebene4.list()) {
			const ebene4Eintrag : KAOAEbene4KatalogEintrag | null = ebene4.daten(this.getSchuljahr());
			if (ebene4Eintrag === null)
				continue;
			if (JavaObject.equalsTranspiler(ebene4Eintrag.zusatzmerkmal, (nameZusatzmerkmal)))
				ebene4OfZusatzmerkmal.add(ebene4);
		}
		this._mapEbene4ByZusatzmerkmal.put(zusatzmerkmalEintrag.kuerzel, ebene4OfZusatzmerkmal);
	}

	/**
	 * Prüft, ob der angegebene KAoA-Eintrag durch den Filter durchgelassen wird oder nicht.
	 *
	 * @param eintrag Der zu prüfende KAoA-Eintrag
	 *
	 * @return true, wenn der Eintrag den Filter passiert, und ansonsten false
	 */
	protected checkFilter(eintrag : SchuelerKAoADaten) : boolean {
		if (this._kategorien.auswahlExists() && !this._kategorien.auswahlHasKey(eintrag.idKategorie))
			return false;
		if (this._merkmale.auswahlExists() && !this._merkmale.auswahlHasKey(eintrag.idMerkmal))
			return false;
		if (this._zusatzmerkmale.auswahlExists() && !this._zusatzmerkmale.auswahlHasKey(eintrag.idZusatzmerkmal))
			return false;
		if ((eintrag.idAnschlussoption !== null) && this._anschlussoptionen.auswahlExists() && !this._anschlussoptionen.auswahlHasKey(eintrag.idAnschlussoption))
			return false;
		return ((eintrag.idEbene4 !== null) && this._ebene4.auswahlExists() && !this._ebene4.auswahlHasKey(eintrag.idEbene4));
	}

	/**
	 * Vergleicht zwei KAoA-Einträge anhand der spezifizierten Ordnung.
	 *
	 * @param a Der erste Eintrag
	 * @param b Der zweite Eintrag
	 *
	 * @return Das Ergebnis des Vergleichs (-1 kleiner, 0 gleich und 1 größer)
	 */
	protected compareAuswahl(a : SchuelerKAoADaten, b : SchuelerKAoADaten) : number {
		for (const criteria of this._order) {
			const field : string | null = criteria.a;
			const asc : boolean = (criteria.b === null) || criteria.b;
			if (!(JavaObject.equalsTranspiler("schuljahr", (field)) || JavaObject.equalsTranspiler("kategorie", (field))))
				throw new DeveloperNotificationException("Fehler bei der Sortierung. Das Sortierkriterium wird vom SchuelerKAoAManager nicht unterstützt.")
			const cmp : number = SchuelerKAoAManager.comparator.compare(a, b);
			if (cmp === 0)
				continue;
			return asc ? cmp : -cmp;
		}
		return JavaLong.compare(a.id, b.id);
	}

	/**
	 * Passt bei Änderungen an den Daten ggf. das Auswahl-Objekt an.
	 *
	 * @param eintrag           Der Auswahl-Eintrag
	 * @param schuelerKAoADaten Das neue KAoA-Daten-Objekt zu der Auswahl
	 */
	protected onSetDaten(eintrag : SchuelerKAoADaten, schuelerKAoADaten : SchuelerKAoADaten) : boolean {
		let updateEintrag : boolean = false;
		if (schuelerKAoADaten.id !== eintrag.id) {
			eintrag.id = schuelerKAoADaten.id;
			updateEintrag = true;
		}
		return updateEintrag;
	}

	/**
	 * Gibt die KAoA Daten des Schülers aus dem ausgewählten Schuljahresabschnitt zurück. Falls keine gefunden wurden, wird eine leere Liste zurückgegeben.
	 *
	 * @return Die KAoA Daten des Schülers
	 */
	public getSchuelerKAoADatenAuswahl() : List<SchuelerKAoADaten> {
		const result : List<SchuelerKAoADaten> | null = new ArrayList<SchuelerKAoADaten>();
		const schuljahresabschnittAuswahl : Schuljahresabschnitt | null = this.getSchuljahresabschnittAuswahl();
		if (schuljahresabschnittAuswahl === null)
			throw new DeveloperNotificationException("Kein Schuljahresabschnitt ausgewählt")
		for (const d of this.liste.list())
			if (d.idSchuljahresabschnitt === schuljahresabschnittAuswahl.id)
				result.add(d);
		return result;
	}

	/**
	 * Fügt der Liste der SchuelerKaoaDaten den Eintrag hinzu
	 *
	 * @param eintrag SchuelerKaoaDaten
	 */
	public addKaoaDaten(eintrag : SchuelerKAoADaten) : void {
		this.liste.add(eintrag);
	}

	/**
	 * Löscht den Eintrag aus der Liste der SchuelerKaoaDaten
	 *
	 * @param id Id der zu löschenden SchuelerKaoaDaten
	 */
	public deleteKaoaDaten(id : number) : void {
		const schuelerKAoADaten : SchuelerKAoADaten | null = this.liste.get(id);
		if (schuelerKAoADaten !== null)
			this.liste.remove(schuelerKAoADaten);
	}

	/**
	 * Gibt alle KAoA Kategorien zurück.
	 *
	 * @return Die KAoA Kategorien
	 */
	public getKAOAKategorien() : List<KAOAKategorie> {
		const kategorien : List<KAOAKategorie> | null = new ArrayList<KAOAKategorie>();
		for (const k of this._kategorien.list())
			kategorien.add(KAOAKategorie.data().getWertByBezeichner(k.name()));
		return kategorien;
	}

	/**
	 * Gibt alle KAoA Kategorien zurück, die den gegebenen Jahrgang beinhalten
	 *
	 * @param jahrgaengeEintrag jahrgang
	 *
	 * @return Die KAoA Kategorien
	 */
	public getKAOAKategorienByJahrgangAuswahl(jahrgaengeEintrag : JahrgaengeKatalogEintrag | null) : List<KAOAKategorieKatalogEintrag> {
		const result : List<KAOAKategorieKatalogEintrag> = new ArrayList<KAOAKategorieKatalogEintrag>();
		if (jahrgaengeEintrag === null)
			return result;
		const kategorien : List<KAOAKategorie> | null = this.getKAOAKategorien();
		for (const k of kategorien)
			if (k.hatJahrgang(this.getSchuljahr(), Jahrgaenge.data().getWertByID(jahrgaengeEintrag.id)))
				result.add(k.daten(this.getSchuljahr()));
		return result;
	}

	/**
	 * Gibt alle KAoA Merkmale zurück, die der angegebenen Kategorie zugeordnet sind. Falls keine gefunden wurden, wird eine leere Liste zurückgegeben.
	 *
	 * @param kategorieEintrag Die Kategorie
	 *
	 * @return Die KAoA Merkmale
	 */
	public getKAOAMerkmaleByKategorie(kategorieEintrag : KAOAKategorieKatalogEintrag | null) : List<KAOAMerkmalKatalogEintrag> {
		if (kategorieEintrag === null)
			return Collections.emptyList();
		const merkmale : List<KAOAMerkmalKatalogEintrag> | null = new ArrayList<KAOAMerkmalKatalogEintrag>();
		for (const merkmal of MapUtils.getOrCreateArrayList(this._mapMerkmalByKategorie, kategorieEintrag.kuerzel))
			merkmale.add(merkmal.daten(this.getSchuljahr()));
		return merkmale;
	}

	/**
	 * Gibt alle Zusatzmerkmale zurück, die dem angegebenen Merkmal zugeordnet sind. Falls keine gefunden wurden, wird eine leere Liste zurückgegeben.
	 *
	 * @param merkmalEintrag Das Merkmal
	 *
	 * @return Die Zusatzmerkmale
	 */
	public getKAOAZusatzmerkmaleByMerkmal(merkmalEintrag : KAOAMerkmalKatalogEintrag | null) : List<KAOAZusatzmerkmalKatalogEintrag> {
		if (merkmalEintrag === null)
			return Collections.emptyList();
		const zusatzmerkmale : List<KAOAZusatzmerkmalKatalogEintrag> | null = new ArrayList<KAOAZusatzmerkmalKatalogEintrag>();
		for (const zusatzmerkmal of MapUtils.getOrCreateArrayList(this._mapZusatzmerkmalByMerkmal, merkmalEintrag.kuerzel))
			zusatzmerkmale.add(zusatzmerkmal.daten(this.getSchuljahr()));
		return zusatzmerkmale;
	}

	/**
	 * Gibt alle KAoA Anschlussoptionen zurück, die dem angegebenen Zusatzmerkmal zugeordnet sind. Falls keine gefunden wurden, wird eine leere Liste
	 * zurückgegeben.
	 *
	 * @param zusatzmerkmalEintrag Das Zusatzmerkmal
	 *
	 * @return Die Anschlussoptionen
	 */
	public getKAOAAnschlussoptionenByZusatzmerkmal(zusatzmerkmalEintrag : KAOAZusatzmerkmalKatalogEintrag | null) : List<KAOAAnschlussoptionenKatalogEintrag> {
		if (zusatzmerkmalEintrag === null)
			return Collections.emptyList();
		const anschlussoptionen : List<KAOAAnschlussoptionenKatalogEintrag> | null = new ArrayList<KAOAAnschlussoptionenKatalogEintrag>();
		for (const anschlussoption of MapUtils.getOrCreateArrayList(this._mapAnschlussoptionByZusatzmerkmal, zusatzmerkmalEintrag.kuerzel))
			anschlussoptionen.add(anschlussoption.daten(this.getSchuljahr()));
		return anschlussoptionen;
	}

	/**
	 * Gibt alle Ebene4 Optionen zurück, die dem angegebenen Zusatzmerkmal zugeordnet sind. Falls keine gefunden wurden, wird eine leere Liste zurückgegeben.
	 *
	 * @param zusatzmerkmalEintrag Das Zusatzmerkmal
	 *
	 * @return Die Ebene4 Optionen
	 */
	public getKAOAEbene4ByZusatzmerkmal(zusatzmerkmalEintrag : KAOAZusatzmerkmalKatalogEintrag | null) : List<KAOAEbene4KatalogEintrag> {
		if (zusatzmerkmalEintrag === null)
			return Collections.emptyList();
		const ebene4liste : List<KAOAEbene4KatalogEintrag> | null = new ArrayList<KAOAEbene4KatalogEintrag>();
		for (const ebene4 of MapUtils.getOrCreateArrayList(this._mapEbene4ByZusatzmerkmal, zusatzmerkmalEintrag.kuerzel))
			ebene4liste.add(ebene4.daten(this.getSchuljahr()));
		return ebene4liste;
	}

	/**
	 * Gibt alle Berufsfelder zurück.
	 *
	 * @return Die Berufsfelder
	 */
	public getKAOABerufsfelder() : List<KAOABerufsfeldKatalogEintrag> {
		const berufsfelder : List<KAOABerufsfeldKatalogEintrag> | null = new ArrayList<KAOABerufsfeldKatalogEintrag>();
		for (const berufsfeld of this._berufsfelder)
			berufsfelder.add(berufsfeld.daten(this.getSchuljahr()));
		return berufsfelder;
	}

	transpilerCanonicalName(): string {
		return 'de.svws_nrw.core.utils.schueler.SchuelerKAoAManager';
	}

	isTranspiledInstanceOf(name : string): boolean {
		return ['de.svws_nrw.core.utils.AuswahlManager', 'de.svws_nrw.core.utils.schueler.SchuelerKAoAManager'].includes(name);
	}

	public static class = new Class<SchuelerKAoAManager>('de.svws_nrw.core.utils.schueler.SchuelerKAoAManager');

}

export function cast_de_svws_nrw_core_utils_schueler_SchuelerKAoAManager(obj : unknown) : SchuelerKAoAManager {
	return obj as SchuelerKAoAManager;
}
