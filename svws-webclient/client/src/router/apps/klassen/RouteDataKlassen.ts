
import type { KlassenDaten, Schueler, List, LehrerListeEintrag } from "@core";
import { ArrayList, DeveloperNotificationException, KlassenListeManager } from "@core";

import { api } from "~/router/Api";
import { RouteData, type RouteStateInterface } from "~/router/RouteData";
import { RouteManager } from "~/router/RouteManager";
import { RoutingStatus } from "~/router/RoutingStatus";
import { routeKlassenDaten } from "~/router/apps/klassen/RouteKlassenDaten";
import { routeSchueler } from "~/router/apps/schueler/RouteSchueler";
import { routeKlasseGruppenprozesse } from "./RouteKlassenGruppenprozesse";
import type { RouteNode } from "~/router/RouteNode";
import { routeLehrer } from "~/router/apps/lehrer/RouteLehrer";
import { routeKlassenNeu } from "~/router/apps/klassen/RouteKlassenNeu";
import { routeApp } from "~/router/apps/RouteApp";
import { routeKlassenStundenplan } from "~/router/apps/klassen/stundenplan/RouteKlassenStundenplan";
import { ViewType } from "@ui";

interface RouteStateKlassen extends RouteStateInterface {
	idSchuljahresabschnitt: number;
	klassenListeManager: KlassenListeManager | undefined;
	mapKlassenVorigerAbschnitt: Map<number, KlassenDaten>;
	mapKlassenFolgenderAbschnitt: Map<number, KlassenDaten>;
	oldView?: RouteNode<any, any>;
	activeRouteType: ViewType;
}

const defaultState = <RouteStateKlassen> {
	idSchuljahresabschnitt: -1,
	klassenListeManager: undefined,
	mapKlassenVorigerAbschnitt: new Map<number, KlassenDaten>(),
	mapKlassenFolgenderAbschnitt: new Map<number, KlassenDaten>(),
	view: routeKlassenDaten,
	oldView: undefined,
	activeRouteType: ViewType.DEFAULT
};

export class RouteDataKlassen extends RouteData<RouteStateKlassen> {

	public constructor() {
		super(defaultState);
	}

	get klassenListeManager(): KlassenListeManager {
		if (this._state.value.klassenListeManager === undefined)
			throw new DeveloperNotificationException("Zugriff auf den Klassen-Liste-Manager, bevor dieser initialisiert wurde.");
		return this._state.value.klassenListeManager;
	}

	get mapKlassenVorigerAbschnitt(): Map<number, KlassenDaten> {
		return this._state.value.mapKlassenVorigerAbschnitt;
	}

	get mapKlassenFolgenderAbschnitt(): Map<number, KlassenDaten> {
		return this._state.value.mapKlassenFolgenderAbschnitt;
	}

	get activeRouteType(): ViewType {
		return this._state.value.activeRouteType;
	}

	private async ladeSchuljahresabschnitt(idSchuljahresabschnitt : number) : Promise<number | null> {
		const schuljahresabschnitt = api.mapAbschnitte.value.get(idSchuljahresabschnitt);
		if (schuljahresabschnitt === undefined)
			return null;

		// Lade die Kataloge und erstelle den Manager
		const listKlassen = await api.server.getKlassenFuerAbschnitt(api.schema, idSchuljahresabschnitt);
		const mapKlassenVorigerAbschnitt = schuljahresabschnitt.idVorigerAbschnitt === null
			? new Map<number, KlassenDaten>()
			: await api.getKlassenListe(schuljahresabschnitt.idVorigerAbschnitt);
		const mapKlassenFolgenderAbschnitt = schuljahresabschnitt.idFolgeAbschnitt === null
			? new Map<number, KlassenDaten>()
			: await api.getKlassenListe(schuljahresabschnitt.idFolgeAbschnitt);
		const listSchueler = await api.server.getSchuelerFuerAbschnitt(api.schema, idSchuljahresabschnitt);
		const listJahrgaenge = await api.server.getJahrgaenge(api.schema);
		const listLehrer = await api.server.getLehrer(api.schema);
		const klassenListeManager = new KlassenListeManager(idSchuljahresabschnitt, api.schuleStammdaten.idSchuljahresabschnitt, api.schuleStammdaten.abschnitte,
			api.schulform, listKlassen, listSchueler, listJahrgaenge, listLehrer);

		if (this._state.value.klassenListeManager === undefined) {
			klassenListeManager.setFilterAuswahlPermitted(true);
			klassenListeManager.setFilterNurSichtbar(false);
		} else {
			klassenListeManager.useFilter(this._state.value.klassenListeManager);
		}

		// Versuche die ausgewählte Klasse von vorher zu laden
		let vorherigeAuswahl = ((this._state.value.klassenListeManager !== undefined) && this.klassenListeManager.hasDaten()) ? this.klassenListeManager.auswahl() : null;
		if ((vorherigeAuswahl !== null) && (vorherigeAuswahl.kuerzel !== null)) {
			vorherigeAuswahl = this.klassenListeManager.getByKuerzelOrNull(vorherigeAuswahl.kuerzel);
			this.klassenListeManager.setDaten(vorherigeAuswahl ?? klassenListeManager.liste.list().get(0));
		}

		// stellt die ursprünglich gefilterte Liste wieder her
		klassenListeManager.filtered();

		// Aktualisiere den State
		this.setPatchedDefaultState({ idSchuljahresabschnitt, klassenListeManager, mapKlassenVorigerAbschnitt, mapKlassenFolgenderAbschnitt, activeRouteType: this.activeRouteType });
		return this.klassenListeManager.auswahlID();
	}

	public async setSchuljahresabschnitt(idSchuljahresabschnitt : number) : Promise<number | null> {
		if (idSchuljahresabschnitt === this._state.value.idSchuljahresabschnitt)
			return null;
		return await this.ladeSchuljahresabschnitt(idSchuljahresabschnitt);
	}

	/**
	 * Gibt die ID des aktuell gesetzten Schuljahresabschnittes zurück.
	 *
	 * @returns die ID des aktuell gesetzten Schuljahresabschnittes
	 */
	get idSchuljahresabschnitt(): number {
		return this._state.value.idSchuljahresabschnitt;
	}

	public async setEintrag(klasse: KlassenDaten | null) {
		if ((klasse === null) && (!this.klassenListeManager.hasDaten()))
			return;
		if ((klasse === null) || (this.klassenListeManager.liste.list().isEmpty())) {
			this.klassenListeManager.setDaten(null);
			this.commit();
			return;
		}
		if (this.klassenListeManager.hasDaten() && (klasse.id === this.klassenListeManager.auswahl().id))
			return;
		let daten = this.klassenListeManager.liste.get(klasse.id);
		if (daten === null)
			daten = this.klassenListeManager.filtered().isEmpty() ? null : this.klassenListeManager.filtered().get(0);
		this.klassenListeManager.setDaten(daten);
		this.commit();
	}

	public deleteKlassenCheck = (): [boolean, List<string>] => {
		const errorLog: List<string> = new ArrayList<string>();
		if (!this.klassenListeManager.liste.auswahlExists())
			errorLog.add('Es wurde keine Klasse zum Löschen ausgewählt.')
		for (const klasse of this.klassenListeManager.liste.auswahlSorted())
			if (klasse.schueler.size() > 0)
				errorLog.add(`Klasse ${klasse.kuerzel ?? '???'} (ID: ${klasse.id.toString()}) kann nicht gelöscht werden, da ihr noch Schüler zugeordnet sind.`);
		return [errorLog.isEmpty(), errorLog];
	}

	public deleteKlassen = async (): Promise<[boolean, List<string>]> => {
		const ids = new ArrayList<number>();
		for (const klasse of this.klassenListeManager.liste.auswahlSorted())
			ids.add(klasse.id);

		const operationResponses = await api.server.deleteKlassen(ids, api.schema);

		const klassenToRemove = new ArrayList<KlassenDaten>();
		const logMessages: List<string> = new ArrayList<string>();
		let status = true;
		for (const response of operationResponses) {
			if (response.success && response.id !== null) {
				const klasse = this.klassenListeManager.liste.get(response.id);
				logMessages.add(`Klasse ${klasse?.kuerzel ?? '???'} (ID: ${response.id.toString()}) wurde erfolgreich gelöscht.`);
				klassenToRemove.add(klasse);
			} else {
				status = false;
				logMessages.addAll(response.log);
			}
		}

		if (!klassenToRemove.isEmpty()) {
			this.klassenListeManager.liste.auswahlClear();
			this.klassenListeManager.setDaten(null);
			await this.ladeSchuljahresabschnitt(this._state.value.idSchuljahresabschnitt);
		}

		return [status, logMessages];
	}

	patch = async (data : Partial<KlassenDaten>) => {
		if (!this.klassenListeManager.hasDaten())
			throw new DeveloperNotificationException("Beim Aufruf der Patch-Methode sind keine gültigen Daten geladen.");
		const idKlasse = this.klassenListeManager.auswahl().id;
		const daten = this.klassenListeManager.daten();
		await api.server.patchKlasse(data, api.schema, idKlasse);
		Object.assign(daten, data);
		const eintrag = this.klassenListeManager.liste.get(idKlasse);
		if (eintrag !== null) {
			if (data.kuerzel !== undefined)
				eintrag.kuerzel = data.kuerzel;
			if (data.sortierung !== undefined)
				eintrag.sortierung = data.sortierung;
		}
		this.klassenListeManager.setDaten(daten);
		this.commit();
	}

	addKlassenleitung = async (idLehrer : number, idKlasse : number) : Promise<void> => {
		// Prüfe zunächst, ob die Lehrer-ID bereits in der Liste der Klassenleitungen vorkommt
		if (this.klassenListeManager.daten().klassenLeitungen.contains(idLehrer))
			throw new DeveloperNotificationException("Die Klassenleitung mit der Lehrer-ID " + idLehrer + " kommt bereits in der Klasse mit der ID " + idKlasse + "vor.");

		// Erstelle die neue Klassenliste durch anhängen der neuen Lehrer-ID
		const listKlassenleitungenNeu = new ArrayList<number>(this.klassenListeManager.daten().klassenLeitungen);
		listKlassenleitungenNeu.add(idLehrer);

		// Führe den API-Aufruf durch
		const requestBody : Partial<KlassenDaten> = { klassenLeitungen: listKlassenleitungenNeu };
		await api.server.patchKlasse(requestBody, api.schema, idKlasse);

		// Aktualisiere die Liste der Klassenleitungen im Erfolgsfall
		this.klassenListeManager.daten().klassenLeitungen.add(idLehrer);
		this.commit();
	}

	removeKlassenleitung = async (eintrag: LehrerListeEintrag) => {
		// Bestimme die Position der Klassenleitung in der zugehörigen Liste
		const listKlassenleitungenNeu = new ArrayList<number>(this.klassenListeManager.daten().klassenLeitungen);
		const lehrerIndex : number = listKlassenleitungenNeu.indexOf(eintrag.id);
		listKlassenleitungenNeu.removeElementAt(lehrerIndex);

		// Führe den API-Aufruf durch
		const requestBody : Partial<KlassenDaten> = { klassenLeitungen: listKlassenleitungenNeu }
		const klassenId : number | null = this.klassenListeManager.auswahlID();
		if (klassenId === null)
			throw new DeveloperNotificationException("Keine Klasse ausgewählt, Klassenleitung kann nicht entfernt werden");
		await api.server.patchKlasse(requestBody, api.schema, klassenId);

		// Aktualisiere die Liste der Klassenleitungen im Erfolgsfall
		this.klassenListeManager.daten().klassenLeitungen = listKlassenleitungenNeu;
		this.commit();
	}

	updateReihenfolgeKlassenleitung = async (idLehrer : number, erhoehe : boolean) : Promise<void> => {
		const idKlasse : number | null = this.klassenListeManager.auswahlID();
		if (idKlasse === null)
			throw new DeveloperNotificationException("Für das Anpassen der Reihenfolge von Klassenlehrern muss eine Klasse ausgewählt sein.")

		// Erstelle eine Kopie der Liste der Klassenleitungen und führe an dieser die Änderungen durch
		const listKlassenleitungenNeu = new ArrayList<number>(this.klassenListeManager.daten().klassenLeitungen);
		if (!KlassenListeManager.updateReihenfolgeKlassenleitung(listKlassenleitungenNeu, idLehrer, erhoehe))
			return;

		// Führe den API-Aufruf durch
		const requestBody : Partial<KlassenDaten> = { klassenLeitungen: listKlassenleitungenNeu };
		await api.server.patchKlasse(requestBody, api.schema, idKlasse)

		// Aktualisiere die Liste der Klassenleitungen im Erfolgsfall
		this.klassenListeManager.daten().klassenLeitungen = listKlassenleitungenNeu;
		this.commit();
	}

	setFilter = async () => {
		if (!this.klassenListeManager.hasDaten() && (this.activeRouteType === ViewType.DEFAULT)) {
			const listFiltered = this.klassenListeManager.filtered();
			if (!listFiltered.isEmpty()) {
				return await this.gotoEintrag(listFiltered.get(0).id);
			}
		}
		this.commit();
	}

	setzeDefaultSortierung = async () => {
		const idSchuljahresabschnitt = this._state.value.idSchuljahresabschnitt;
		const auswahl_id = this.klassenListeManager.auswahl().id;
		await api.server.setKlassenSortierungFuerAbschnitt(api.schema, idSchuljahresabschnitt);
		await this.ladeSchuljahresabschnitt(idSchuljahresabschnitt);
		await this.setEintrag(this.klassenListeManager.liste.get(auswahl_id));
	}

	add = async (partialKlasse: Partial<KlassenDaten>): Promise<void> => {
		const neueKlasse = await api.server.addKlasse({ ...partialKlasse, idSchuljahresabschnitt: routeApp.data.idSchuljahresabschnitt }, api.schema);
		await this.ladeSchuljahresabschnitt(this.idSchuljahresabschnitt);
		await this.gotoEintrag(neueKlasse.id);
	}

	private setDefaults() {
		this.klassenListeManager.setAuswahlKlassenLeitung(null);
		this._state.value.activeRouteType = ViewType.DEFAULT;
		this._state.value.view = (this._state.value.view?.name === this.view.name) ? this.view : routeKlassenDaten;
	}

	gotoEintrag = async (eintragId?: number | null) => {
		this._state.value.oldView = this._state.value.view;
		if ((eintragId !== null) && (eintragId !== undefined) && this.klassenListeManager.liste.has(eintragId)) {
			const route = ((this.view === routeKlassenDaten) || (this.view === routeKlassenStundenplan)) ? this.view.getRoute(eintragId) : routeKlassenDaten.getRoute(eintragId)
			const result = await RouteManager.doRoute(route);
			if (result === RoutingStatus.STOPPED_ROUTING_IS_ACTIVE)
				await this.setEintrag(this.klassenListeManager.liste.get(eintragId));

			if ((result === RoutingStatus.SUCCESS) || (result === RoutingStatus.STOPPED_ROUTING_IS_ACTIVE))
				this.setDefaults();

			this.commit();
			return;
		}

		// Default Eintrag selektieren, wenn keine ID übergeben wurde
		const filtered = this.klassenListeManager.filtered();
		if (!filtered.isEmpty()) {
			const klasse = this.klassenListeManager.filtered().getFirst();
			const route = routeKlassenDaten.getRoute(klasse.id);
			const result = await RouteManager.doRoute(route);
			if ((result === RoutingStatus.SUCCESS) || (result === RoutingStatus.STOPPED_ROUTING_IS_ACTIVE))
				this.setDefaults();

			await this.setEintrag(klasse);
			this.commit();
		}
	}

	gotoSchueler = async (eintrag: Schueler) => {
		await RouteManager.doRoute(routeSchueler.getRoute(eintrag.id));
	}

	gotoLehrer = async (eintrag: LehrerListeEintrag) => {
		await RouteManager.doRoute(routeLehrer.getRoute(eintrag.id));
	}

	gotoCreationMode = async (navigate: boolean) => {
		if (this._state.value.activeRouteType === ViewType.HINZUFUEGEN || (this._state.value.view === routeKlassenNeu)) {
			this.commit();
			return;
		}

		this._state.value.activeRouteType = ViewType.HINZUFUEGEN;
		this._state.value.oldView = this._state.value.view;

		if (navigate) {
			const result = await RouteManager.doRoute(routeKlassenNeu.getRoute());
			if (result === RoutingStatus.SUCCESS)
				this.klassenListeManager.liste.auswahlClear();
		}

		this._state.value.view = routeKlassenNeu;
		this.klassenListeManager.setAuswahlKlassenLeitung(null);
		this.klassenListeManager.setDaten(null);
		this.commit();
	}

	gotoGruppenprozess = async (navigate: boolean) => {
		if (this._state.value.activeRouteType === ViewType.GRUPPENPROZESSE || (this._state.value.view === routeKlasseGruppenprozesse)) {
			this.commit();
			return;
		}

		this._state.value.activeRouteType = ViewType.GRUPPENPROZESSE;
		this._state.value.oldView = this._state.value.view;

		if (navigate)
			await RouteManager.doRoute(routeKlasseGruppenprozesse.getRoute());

		this._state.value.view = routeKlasseGruppenprozesse;
		this.klassenListeManager.setDaten(null);
		this.commit();
	}

}
