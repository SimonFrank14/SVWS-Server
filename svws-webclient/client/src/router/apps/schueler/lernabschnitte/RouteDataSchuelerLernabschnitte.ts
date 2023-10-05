import { shallowRef } from "vue";

import type { List, FaecherListeEintrag, LehrerListeEintrag, SchuelerLeistungsdaten, SchuelerLernabschnittListeEintrag, SchuelerLernabschnittsdaten, FoerderschwerpunktEintrag, JahrgangsListeEintrag, SchuelerLernabschnittBemerkungen} from "@core";
import { ArrayList, SchuelerLernabschnittManager, SchuelerListeEintrag } from "@core";

import { api } from "~/router/Api";
import { RouteManager } from "~/router/RouteManager";
import { type RouteNode } from "~/router/RouteNode";
import { routeApp } from "~/router/apps/RouteApp";
import { routeSchuelerLernabschnittLeistungen } from "~/router/apps/schueler/lernabschnitte/RouteSchuelerLernabschnittLeistungen";
import { routeSchueler } from "../RouteSchueler";


interface RouteStateDataSchuelerLernabschnitte {
	// Daten, die in Abhängigkeit des ausgewählten Schülers geladen werden
	idSchueler: number;
	listAbschnitte: List<SchuelerLernabschnittListeEintrag>;
	listFaecher: List<FaecherListeEintrag>;
	listFoerderschwerpunkte: List<FoerderschwerpunktEintrag>;
	listJahrgaenge: List<JahrgangsListeEintrag>;
	listLehrer: List<LehrerListeEintrag>;
	// Daten, die in Abhängigkeit des ausgewählten Lernabschnitts geladen werden
	auswahl: SchuelerLernabschnittListeEintrag | undefined;
	daten: SchuelerLernabschnittsdaten | undefined;
	manager: SchuelerLernabschnittManager | undefined;
	// und die ausgewählte View
	view: RouteNode<any, any>;
}


export class RouteDataSchuelerLernabschnitte {

	private static _defaultState: RouteStateDataSchuelerLernabschnitte = {
		idSchueler: -1,
		listAbschnitte: new ArrayList<SchuelerLernabschnittListeEintrag>(),
		listFaecher: new ArrayList(),
		listFoerderschwerpunkte: new ArrayList(),
		listJahrgaenge: new ArrayList(),
		listLehrer: new ArrayList(),
		auswahl: undefined,
		daten: undefined,
		manager: undefined,
		view: routeSchuelerLernabschnittLeistungen,
	}

	private _state = shallowRef(RouteDataSchuelerLernabschnitte._defaultState);

	private setPatchedDefaultState(patch: Partial<RouteStateDataSchuelerLernabschnitte>) {
		this._state.value = Object.assign({ ... RouteDataSchuelerLernabschnitte._defaultState }, patch);
	}

	private setPatchedState(patch: Partial<RouteStateDataSchuelerLernabschnitte>) {
		this._state.value = Object.assign({ ... this._state.value }, patch);
	}

	private commit(): void {
		this._state.value = { ... this._state.value };
	}



	get idSchueler(): number {
		return this._state.value.idSchueler;
	}

	get listAbschnitte(): List<SchuelerLernabschnittListeEintrag> {
		return this._state.value.listAbschnitte;
	}

	get manager(): SchuelerLernabschnittManager {
		if (this._state.value.manager === undefined)
			throw new Error("Unerwarteter Fehler: Schüler-Lernabschnittsdaten nicht initialisiert");
		return this._state.value.manager;
	}

	protected getLernabschnitt(curState : RouteStateDataSchuelerLernabschnitte, idSchuljahresabschnitt : number, wechselNr : number) : SchuelerLernabschnittListeEintrag | undefined {
		let found : SchuelerLernabschnittListeEintrag | undefined = undefined;
		for (const current of curState.listAbschnitte) {
			if (current.schuljahresabschnitt === idSchuljahresabschnitt) {
				found = current;
				if (current.wechselNr === wechselNr)
					break;
			}
		}
		return found;
	}

	protected async updateSchuljahresabschnitt(curState : RouteStateDataSchuelerLernabschnitte, newSchuljahresabschnitt : number | undefined, newWechselNr : number) : Promise<RouteStateDataSchuelerLernabschnitte> {
		let found : SchuelerLernabschnittListeEintrag | undefined = undefined;
		// Prüfe, ob ein alter Schuljahresabschnitt (und ggf. die Wechselnummer) in der Liste der Lernabschnitte existiert
		if (newSchuljahresabschnitt !== undefined)
			found = this.getLernabschnitt(curState, newSchuljahresabschnitt, newWechselNr);
		// Wenn kein passender Lernabschnitt gefunden wurde, dann prüfe, ob ein Lernabschnitt für das aktuelle Halbjahr vorhanden ist.
		if (found === undefined) {
			const idSchuljahresabschnitt = routeApp.data.aktAbschnitt.value.id;
			for (const current of curState.listAbschnitte) {
				if ((current.schuljahresabschnitt === idSchuljahresabschnitt) && (current.wechselNr === 0)) {
					found = current;
					break;
				}
			}
		}
		// Ansonsten nimm einfach den letzten Lernabschnitt der Liste
		if (found === undefined) {
			if (curState.listAbschnitte.isEmpty())
				return curState;
			found = curState.listAbschnitte.get(curState.listAbschnitte.size()-1);
		}
		const daten = await api.server.getSchuelerLernabschnittsdatenByID(api.schema, found.id);
		const listKurse = await api.server.getKurseFuerAbschnitt(api.schema, found.schuljahresabschnitt);
		const listKlassen = await api.server.getKlassenFuerAbschnitt(api.schema, found.schuljahresabschnitt);
		let schueler = routeSchueler.data.auswahl;
		if (schueler === undefined)
			schueler = new SchuelerListeEintrag();
		const manager = new SchuelerLernabschnittManager(schueler, daten, curState.listFaecher, curState.listFoerderschwerpunkte, curState.listJahrgaenge, listKlassen, listKurse, curState.listLehrer);
		curState = Object.assign({ ... curState }, { auswahl: found, daten, manager });
		return curState;
	}


	public async setSchueler(idSchueler: number) : Promise<void> {
		if (idSchueler == this._state.value.idSchueler)
			return;
		const listAbschnitte = await api.server.getSchuelerLernabschnittsliste(api.schema, idSchueler);
		const listFaecher = await api.server.getFaecher(api.schema);
		const listFoerderschwerpunkte = await api.server.getSchuelerFoerderschwerpunkte(api.schema);
		const listJahrgaenge = await api.server.getJahrgaenge(api.schema);
		const listLehrer = await api.server.getLehrer(api.schema);
		let newState = <RouteStateDataSchuelerLernabschnitte>{ idSchueler, listAbschnitte, listFaecher, listFoerderschwerpunkte, listJahrgaenge, listLehrer, view: this._state.value.view };
		const alteAuswahl = this._state.value.auswahl;
		newState = await this.updateSchuljahresabschnitt(newState,
			alteAuswahl === undefined ? undefined : alteAuswahl.schuljahresabschnitt,
			alteAuswahl === undefined ? 0 : alteAuswahl.wechselNr);
		this.setPatchedDefaultState(newState)
	}

	public async setView(view: RouteNode<any,any>) {
		this.setPatchedState({ view: view });
	}

	public get view(): RouteNode<any,any> {
		return this._state.value.view;
	}

	get hatAuswahl() : boolean {
		return (this._state.value.auswahl !== undefined);
	}

	get auswahl(): SchuelerLernabschnittListeEintrag {
		if (this._state.value.auswahl === undefined)
			throw new Error("Unerwarteter Fehler: Lernabschnittseintrag nicht festgelegt, es können keine Informationen zu den Leistungsdaten abgerufen oder eingegeben werden.");
		return this._state.value.auswahl;
	}

	get daten(): SchuelerLernabschnittsdaten {
		if (this._state.value.daten === undefined)
			throw new Error("Unerwarteter Fehler: Lernabschnittdaten nicht initialisiert");
		return this._state.value.daten;
	}

	public async setLernabschnitt(idSchuljahresabschnitt : number, wechselNr : number) {
		const curAuswahl = this._state.value.auswahl;
		if ((curAuswahl === undefined) || ((idSchuljahresabschnitt === curAuswahl.schuljahresabschnitt)) && (wechselNr === curAuswahl.wechselNr))
			return;
		const newState = await this.updateSchuljahresabschnitt(this._state.value, idSchuljahresabschnitt, wechselNr);
		this.setPatchedState(newState);
	}

	gotoLernabschnitt = async (value: SchuelerLernabschnittListeEintrag) => {
		await RouteManager.doRoute({ name: this._state.value.view.name, params: { id: value.schuelerID, abschnitt: value.schuljahresabschnitt, wechselNr: value.wechselNr } });
	}

	patchLernabschnitt = async (data : Partial<SchuelerLernabschnittsdaten>) => {
		await api.server.patchSchuelerLernabschnittsdaten(data, api.schema, this.daten.id);
		Object.assign(this.daten, data);
		this.commit();
	}

	patchLeistung = async (data : Partial<SchuelerLeistungsdaten>, id : number) => {
		await api.server.patchSchuelerLeistungsdaten(data, api.schema, id);
		const leistung = this.manager.leistungGetByIdOrException(id);
		Object.assign(leistung, data);
		this.commit();
	}

	patchBemerkungen = async (data : Partial<SchuelerLernabschnittBemerkungen>) : Promise<void> => {
		await api.server.patchSchuelerLernabschnittsdatenBemerkungen(data, api.schema, this.daten.id);
		Object.assign(this.daten.bemerkungen, data);
		this.commit();
	}

}