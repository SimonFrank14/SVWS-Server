
import type { GostJahrgangsdaten, GostKursklausur, LehrerListeEintrag, SchuelerListeEintrag, GostKlausurvorgabe, GostKlausurraum, Schuljahresabschnitt, List, GostSchuelerklausur, GostKlausurterminblockungDaten} from "@core";
import { GostKlausurenCollectionSkrsKrs} from "@core";
import type { RouteNode } from "~/router/RouteNode";
import { GostKlausurraumManager, StundenplanManager, KursManager, GostFaecherManager, GostHalbjahr, GostKursklausurManager, GostKlausurvorgabenManager, StundenplanListUtils, DeveloperNotificationException } from "@core";
import { GostKlausurtermin, ArrayList} from "@core";
import { computed, shallowRef } from "vue";

import { api } from "~/router/Api";
import { RouteManager } from "~/router/RouteManager";

import { routeGostKlausurplanungKalender } from "~/router/apps/gost/klausurplanung/RouteGostKlausurplanungKalender";
import { routeGostKlausurplanungVorgaben } from "~/router/apps/gost/klausurplanung/RouteGostKlausurplanungVorgaben";
import { routeGostKlausurplanungDetailAnsicht } from "~/router/apps/gost/klausurplanung/RouteGostKlausurplanungDetailAnsicht";
import { routeGostKlausurplanungRaumzeit } from "~/router/apps/gost/klausurplanung/RouteGostKlausurplanungRaumzeit";
import { routeGostKlausurplanungSchienen } from "~/router/apps/gost/klausurplanung/RouteGostKlausurplanungSchienen";

interface RouteStateGostKlausurplanung {
	// Daten nur abhängig von dem Abiturjahrgang
	abiturjahr: number | undefined;
	abschnitt : Schuljahresabschnitt | undefined;
	jahrgangsdaten: GostJahrgangsdaten | undefined;
	mapSchueler: Map<number, SchuelerListeEintrag>;
	faecherManager: GostFaecherManager;
	// ... die mit dem Abiturjahrgang aktualisiert/mitgeladen werden
	mapLehrer: Map<number, LehrerListeEintrag>;
	// ... auch abhängig vom ausgewählten Halbjahr der gymnasialen Oberstufe
	halbjahr: GostHalbjahr;
	kursklausurmanager: GostKursklausurManager | undefined;
	klausurvorgabenmanager: GostKlausurvorgabenManager | undefined;
	stundenplanmanager: StundenplanManager | undefined;
	kursmanager: KursManager;
	quartalsauswahl: 0 | 1 | 2 ;
	view: RouteNode<any, any>;
}

export class RouteDataGostKlausurplanung {

	private static _defaultState : RouteStateGostKlausurplanung = {
		abiturjahr: undefined,
		abschnitt: undefined,
		jahrgangsdaten: undefined,
		mapSchueler: new Map(),
		faecherManager: new GostFaecherManager(),
		mapLehrer: new Map(),
		halbjahr: GostHalbjahr.EF1,
		kursklausurmanager: undefined,
		klausurvorgabenmanager: undefined,
		stundenplanmanager: undefined,
		kursmanager: new KursManager(),
		quartalsauswahl: 0,
		view: routeGostKlausurplanungVorgaben,
	}

	private _state = shallowRef<RouteStateGostKlausurplanung>(RouteDataGostKlausurplanung._defaultState);

	private setPatchedDefaultState(patch: Partial<RouteStateGostKlausurplanung>) {
		this._state.value = Object.assign({ ... RouteDataGostKlausurplanung._defaultState }, patch);
	}

	private setPatchedState(patch: Partial<RouteStateGostKlausurplanung>) {
		this._state.value = Object.assign({ ... this._state.value }, patch);
	}

	private commit(): void {
		this._state.value = { ... this._state.value };
	}


	public get hatAbiturjahr(): boolean {
		return this._state.value.abiturjahr !== undefined;
	}

	public get abiturjahrIstVorlage() : boolean {
		return (this._state.value.abiturjahr !== undefined) && (this._state.value.abiturjahr === -1);
	}

	public get abiturjahr() : number {
		if (this._state.value.abiturjahr === undefined)
			throw new Error("Es wurde noch kein Abiturjahrgang geladen.");
		return this._state.value.abiturjahr;
	}

	public async setAbiturjahr(abiturjahr: number | undefined) {
		if (abiturjahr === this._state.value.abiturjahr)
			return;
		if (abiturjahr === undefined) {
			this._state.value = RouteDataGostKlausurplanung._defaultState;
			return;
		}
		try {
			api.status.start();
			// Lade die Daten für die Kursplanung, die nur vom Abiturjahrgang abhängen
			const jahrgangsdaten = await api.server.getGostAbiturjahrgang(api.schema, abiturjahr)
			const listFaecher = await api.server.getGostAbiturjahrgangFaecher(api.schema, abiturjahr);
			const faecherManager = new GostFaecherManager(listFaecher);
			const mapSchueler = new Map<number, SchuelerListeEintrag>();
			const mapLehrer: Map<number, LehrerListeEintrag> = new Map();
			let view: RouteNode<any, any> = this._state.value.view;
			// TODO schieben in getHalbjahr und durch getKurseFuerAbschnitt ersetzen
			const listKurse = await api.server.getKurse(api.schema);
			const kursManager = new KursManager(listKurse);

			if (abiturjahr !== -1) {
				const listSchueler = await api.server.getGostAbiturjahrgangSchueler(api.schema, abiturjahr);
				// Lade die Schülerliste des Abiturjahrgangs
				for (const s of listSchueler)
					mapSchueler.set(s.id, s);
				// Lade die Lehrerliste
				const listLehrer = await api.server.getLehrer(api.schema);
				for (const l of listLehrer)
					mapLehrer.set(l.id, l);
			} else {
				if ((view !== routeGostKlausurplanungKalender) && (view !== routeGostKlausurplanungVorgaben))
					view = routeGostKlausurplanungVorgaben;
			}
			const listKlausurvorgaben = await api.server.getGostKlausurenVorgabenJahrgang(api.schema, abiturjahr);
			const klausurvorgabenmanager = new GostKlausurvorgabenManager(listKlausurvorgaben, faecherManager);

			// Setze den State neu
			this.setPatchedDefaultState({
				abiturjahr: abiturjahr,
				jahrgangsdaten: jahrgangsdaten,
				mapSchueler: mapSchueler,
				faecherManager: faecherManager,
				mapLehrer: mapLehrer,
				halbjahr: this._state.value.halbjahr,
				kursmanager: kursManager,
				view: view,
				klausurvorgabenmanager: klausurvorgabenmanager,
			});
		} finally {
			api.status.stop();
		}
	}

	public get hatJahrgangsdaten(): boolean {
		return this._state.value.jahrgangsdaten !== undefined;
	}

	public get jahrgangsdaten(): GostJahrgangsdaten {
		if (this._state.value.jahrgangsdaten === undefined)
			throw new Error("Es wurde noch kein Abiturjahrgang geladen, so dass keine Jahrgangsdaten zur Verfügung stehen.");
		return this._state.value.jahrgangsdaten;
	}

	public get mapSchueler(): Map<number, SchuelerListeEintrag> {
		return this._state.value.mapSchueler;
	}

	public get faecherManager() : GostFaecherManager {
		return this._state.value.faecherManager;
	}

	public get kursManager() : KursManager {
		return this._state.value.kursmanager;
	}

	public get mapLehrer(): Map<number, LehrerListeEintrag> {
		return this._state.value.mapLehrer;
	}


	public get halbjahr() : GostHalbjahr {
		return this._state.value.halbjahr;
	}

	public async setHalbjahr(halbjahr: GostHalbjahr, hjChanged: boolean): Promise<boolean> {
		if (this._state.value.abiturjahr === undefined)
			throw new Error("Es kann kein Halbjahr ausgewählt werden, wenn zuvor kein Abiturjahrgang ausgewählt wurde.");
		if (!hjChanged && halbjahr === this._state.value.halbjahr)
			return false;
		try {
			api.status.start();
			// const listKlausurvorgaben = await api.server.getGostKlausurenVorgabenJahrgang(api.schema, this.abiturjahr);
			// console.log(listKlausurvorgaben.size())
			// const klausurvorgabenmanager = new GostKlausurvorgabenManager(listKlausurvorgaben, this.faecherManager);
			const result: Partial<RouteStateGostKlausurplanung> = {
				// klausurvorgabenmanager: klausurvorgabenmanager,
				abschnitt: undefined,
				halbjahr: halbjahr,
				kursklausurmanager: undefined,
				stundenplanmanager: undefined,
			};
			if (this._state.value.abiturjahr === -1) {
				this.setPatchedState(result);
				return true;
			}
			const schuljahr = halbjahr.getSchuljahrFromAbiturjahr(this._state.value.abiturjahr);
			const abschnitt : Schuljahresabschnitt | undefined = api.getAbschnittBySchuljahrUndHalbjahr(schuljahr, halbjahr.halbjahr);
			if (abschnitt === undefined) {
				this.setPatchedState(result);
				return true;
			}
			Object.assign(result, {abschnitt});
			const kursklausurmanager = await this.reloadKursklausurmanager(halbjahr, this.klausurvorgabenmanager);
			Object.assign(result, {kursklausurmanager});
			const listStundenplaene = await api.server.getStundenplanlisteFuerAbschnitt(api.schema, abschnitt.id);
			if (listStundenplaene.isEmpty()) {
				this.setPatchedState(result);
				return true;
			}
			const stundenplan = StundenplanListUtils.get(listStundenplaene, new Date().toISOString().substring(0, 10));
			if (stundenplan === null)
				throw new Error("Es konnte kein aktiver Stundenplan gefunden werden.");
			const stundenplandaten = await api.server.getStundenplan(api.schema, stundenplan.id);
			const unterrichte = await api.server.getStundenplanUnterrichte(api.schema, stundenplan.id);
			const pausenaufsichten = await api.server.getStundenplanPausenaufsichten(api.schema, stundenplan.id);
			const unterrichtsverteilung = await api.server.getStundenplanUnterrichtsverteilung(api.schema, stundenplan.id);
			const stundenplanmanager = new StundenplanManager(stundenplandaten, unterrichte, pausenaufsichten, unterrichtsverteilung);
			this.setPatchedState(Object.assign(result, {
				stundenplanmanager,
			}));
			return true;
		} finally {
			api.status.stop();
		}
	}

	public async reloadKursklausurmanager(halbjahr: GostHalbjahr | null, vorgabenmanager: GostKlausurvorgabenManager) : Promise<GostKursklausurManager> {
		const listKlausurtermine = await api.server.getGostKlausurenKlausurtermineJahrgangSchuljahr(api.schema, this.abiturjahr, halbjahr !== null ? halbjahr.id : this._state.value.halbjahr.id);
		const listKursklausuren = await api.server.getGostKlausurenKursklausurenJahrgangSchuljahr(api.schema, this.abiturjahr, halbjahr !== null ? halbjahr.id : this._state.value.halbjahr.id);
		return new GostKursklausurManager(vorgabenmanager, listKursklausuren, listKlausurtermine);
	}

	public get hatStundenplanManager(): boolean {
		return this._state.value.stundenplanmanager !== undefined;
	}

	public get stundenplanmanager(): StundenplanManager {
		if (this._state.value.stundenplanmanager === undefined)
			throw new Error("Es wurde noch keine Daten geladen, so dass kein Stundenplan-Manager zur Verfügung steht.");
		return this._state.value.stundenplanmanager;
	}


	public get hatKursklausurManager(): boolean {
		return this._state.value.kursklausurmanager !== undefined;
	}

	public get kursklausurmanager(): GostKursklausurManager {
		if (this._state.value.kursklausurmanager === undefined)
			throw new Error("Es wurde noch keine Daten geladen, so dass kein Kurs-Klausur-Manager zur Verfügung steht.");
		return this._state.value.kursklausurmanager;
	}

	public get hatKlausurvorgabenManager(): boolean {
		return this._state.value.klausurvorgabenmanager !== undefined;
	}

	public get klausurvorgabenmanager(): GostKlausurvorgabenManager {
		if (this._state.value.klausurvorgabenmanager === undefined)
			throw new Error("Es wurde noch keine Daten geladen, so dass kein Klausur-Vorgaben-Manager zur Verfügung steht.");
		return this._state.value.klausurvorgabenmanager;
	}

	quartalsauswahl = computed({
		get: () : 0 | 1 | 2 => this._state.value.quartalsauswahl,
		set: (value: 0 | 1 | 2): void => {
			this._state.value.quartalsauswahl = value;
			this.commit();
		}
	  });

	public async setView(view: RouteNode<any,any>) {
		if ((view !== routeGostKlausurplanungVorgaben) &&
			(view !== routeGostKlausurplanungSchienen) &&
			(view !== routeGostKlausurplanungKalender) &&
			(view !== routeGostKlausurplanungRaumzeit) &&
			(view !== routeGostKlausurplanungDetailAnsicht))
			throw new Error("Die gewählte Ansicht für die Klausurplanung wird nicht unterstützt. ");
		this.setPatchedState({ view: view });
	}

	public get view(): RouteNode<any,any> {
		return this._state.value.view;
	}


	gotoHalbjahr = async (value: GostHalbjahr) => {
		await RouteManager.doRoute(this.view.getRoute(this.abiturjahr, value.id));
	}

	erzeugeKlausurtermin = async (quartal: number): Promise<GostKlausurtermin> => {
		api.status.start();
		const terminNeu : Partial<GostKlausurtermin> = new GostKlausurtermin();
		terminNeu.abijahr = this.abiturjahr;
		terminNeu.halbjahr = this.halbjahr.id;
		terminNeu.quartal = quartal;
		delete terminNeu.id;
		const termin = await api.server.createGostKlausurenKlausurtermin(terminNeu, api.schema);
		this.kursklausurmanager.terminAdd(termin);
		this.commit();
		api.status.stop();
		return termin;
	}

	loescheKlausurtermine = async (termine: List<GostKlausurtermin>) => {
		api.status.start();
		const terminIds = new ArrayList<number>();
		for (const termin of termine)
			terminIds.add(termin.id);
		await api.server.deleteGostKlausurenKlausurtermine(terminIds, api.schema);
		this.kursklausurmanager.terminRemoveAll(termine);
		this.commit();
		api.status.stop();
	}

	patchKlausur = async (id: number, klausur: Partial<GostKursklausur | GostSchuelerklausur>): Promise<GostKlausurenCollectionSkrsKrs> => {
		// if ('id' in klausur)
		return await this.patchKursklausur(id, klausur);
	}

	patchKursklausur = async (id: number, klausur: Partial<GostKursklausur>): Promise<GostKlausurenCollectionSkrsKrs> => {
		if (this._state.value.abschnitt === undefined)
			throw new DeveloperNotificationException('Es wurde kein gültiger Abschnitt für diese Planung gesetzt')
		api.status.start();
		delete klausur.id;
		const result = await api.server.patchGostKlausurenKursklausur(klausur, api.schema, id, this._state.value.abschnitt.id);
		if (result.kursKlausurPatched !== null)
			this.kursklausurmanager.kursklausurPatchAttributes(result.kursKlausurPatched);
		this.commit();
		api.status.stop();
		return result;
	}

	erzeugeDefaultKlausurvorgaben = async (quartal: number) => {
		api.status.start();
		const neueVorgaben = await api.server.createDefaultGostKlausurenVorgaben(api.schema, this.halbjahr.id, quartal);
		this.klausurvorgabenmanager.vorgabeAddAll(neueVorgaben);
		this.commit();
		api.status.stop();
	}

	erzeugeKlausurvorgabe = async (vorgabe: Partial<GostKlausurvorgabe>) => {
		api.status.start();
		delete vorgabe.idVorgabe;
		vorgabe.abiJahrgang = this.abiturjahr;
		vorgabe.halbjahr = this.halbjahr.id;
		try {
			const neueVorgabe = await api.server.createGostKlausurenVorgabe(vorgabe, api.schema);
			this.klausurvorgabenmanager.vorgabeAdd(neueVorgabe);
		} finally {
			this.commit();
			api.status.stop();
		}
	}

	patchKlausurvorgabe = async (vorgabe: Partial<GostKlausurvorgabe>, id: number) => {
		api.status.start();
		await api.server.patchGostKlausurenVorgabe(vorgabe, api.schema, id);
		this.klausurvorgabenmanager.vorgabePatchAttributes(Object.assign(this.klausurvorgabenmanager.vorgabeGetByIdOrException(id), vorgabe));
		this.commit();
		api.status.stop();
	}

	loescheKlausurvorgabe = async (idVorgabe: number) => {
		api.status.start();
		await api.server.deleteGostKlausurenVorgabe(api.schema, idVorgabe);
		this.klausurvorgabenmanager.vorgabeRemoveById(idVorgabe);
		this.commit();
		api.status.stop();
	}

	erzeugeKursklausurenAusVorgaben = async (quartal: number) => {
		api.status.start();
		try {
			const result = await api.server.createGostKlausurenKursklausurenJahrgangHalbjahrQuartal(api.schema, this.abiturjahr, this.halbjahr.id, quartal);
			this.kursklausurmanager.kursklausurAddAll(result);
			this.commit();
		} finally {
			api.status.stop();
		}
	}

	patchKlausurtermin = async (id: number, termin: Partial<GostKlausurtermin>) => {
		api.status.start();
		const oldTtermin = this.kursklausurmanager.terminGetByIdOrException(id);
		await api.server.patchGostKlausurenKlausurtermin(termin, api.schema, id);
		Object.assign(oldTtermin, termin);
		this.kursklausurmanager.terminPatchAttributes(oldTtermin);
		this.commit();
		api.status.stop();
	}

	erzeugeVorgabenAusVorlage = async (quartal: number) => {
		api.status.start();
		this._state.value.klausurvorgabenmanager?.vorgabeRemoveAll(this._state.value.klausurvorgabenmanager.vorgabeGetMengeAsList());
		await api.server.copyGostKlausurenVorgaben(api.schema, this.abiturjahr, this.halbjahr.id, quartal);
		const listKlausurvorgaben = await api.server.getGostKlausurenVorgabenJahrgangHalbjahr(api.schema, this.abiturjahr, this.halbjahr.id);
		this._state.value.klausurvorgabenmanager?.vorgabeAddAll(listKlausurvorgaben);
		this.commit();
		api.status.stop();
	}

	createKlausurraum = async (raum: Partial<GostKlausurraum>, manager: GostKlausurraumManager) => {
		api.status.start();
		const neuerRaum = await api.server.createGostKlausurenRaum(raum, api.schema);
		manager.raumAdd(neuerRaum);
		this.commit();
		api.status.stop();
	}

	loescheKlausurraum = async (id: number, manager: GostKlausurraumManager): Promise<boolean> => {
		api.status.start();
		const ergebnis = await api.server.deleteGostKlausurenRaum(api.schema, id);
		manager.raumRemoveById(id);
		this.commit();
		api.status.stop();
		return true;
	}

	patchKlausurraum = async (id: number, raum: Partial<GostKlausurraum>, manager: GostKlausurraumManager): Promise<boolean> => {
		api.status.start();
		const oldRaum: GostKlausurraum = manager.raumGetByIdOrException(id);
		await api.server.patchGostKlausurenRaum(raum, api.schema, id);
		manager.raumPatchAttributes(Object.assign(oldRaum, raum));
		this.commit();
		api.status.stop();
		return true;
	}

	erzeugeKlausurraummanager = async (termin: GostKlausurtermin): Promise<GostKlausurraumManager> => {
		api.status.start();
		const raeume = await api.server.getGostKlausurenRaeumeTermin(api.schema, termin.id);
		const krsCollection = await api.server.getGostKlausurenSchuelerraumstundenTermin(api.schema, termin.id);
		const schuelerklausuren = await api.server.getGostKlausurenSchuelerklausuren(api.schema, termin.id);
		this.commit();
		api.status.stop();
		return new GostKlausurraumManager(raeume, krsCollection.raumstunden, krsCollection.skRaumstunden, schuelerklausuren, this.kursklausurmanager, termin);
	}

	setzeRaumZuSchuelerklausuren = async (raum: GostKlausurraum | null, sks: List<GostSchuelerklausur>, manager: GostKlausurraumManager): Promise<GostKlausurenCollectionSkrsKrs> => {
		if (this._state.value.abschnitt === undefined)
			throw new DeveloperNotificationException('Es wurde kein gültiger Abschnitt für diese Planung gesetzt');
		if (sks.isEmpty())
			return new GostKlausurenCollectionSkrsKrs();
		api.status.start();
		const skids = new ArrayList<number>();
		for (const sk of sks)
			skids.add(sk.idSchuelerklausur);
		const collectionSkrsKrs = await api.server.setzeGostSchuelerklausurenZuRaum(skids, api.schema, raum === null ? -1 : raum.id, this._state.value.abschnitt.id);
		manager.setzeRaumZuSchuelerklausuren(collectionSkrsKrs);
		this.commit();
		api.status.stop();
		return collectionSkrsKrs;
	}

	blockenKursklausuren = async (blockungDaten: GostKlausurterminblockungDaten): Promise<boolean> => {
		api.status.start();
		await api.server.blockenGostKursklausuren(blockungDaten, api.schema);
		this.setPatchedState({
			kursklausurmanager: await this.reloadKursklausurmanager(null, this.klausurvorgabenmanager),
		});
		this.commit();
		api.status.stop();
		return true;
	}

	gotoVorgaben = async () => {
		await RouteManager.doRoute({ name: routeGostKlausurplanungVorgaben.name, params: { abiturjahr: this.abiturjahr, halbjahr: this.halbjahr.id } });
	}


}