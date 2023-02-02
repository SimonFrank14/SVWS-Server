import { FaecherListeEintrag, LehrerListeEintrag, SchuelerLernabschnittListeEintrag } from "@svws-nrw/svws-core-ts";
import { WritableComputedRef } from "vue";
import { RouteLocationNormalized, RouteLocationRaw, RouteParams } from "vue-router";
import { RouteNodeListView } from "~/router/RouteNodeListView";
import { computed } from "vue";
import { DataSchuelerAbschnittsdaten } from "~/apps/schueler/DataSchuelerAbschnittsdaten";
import { ListAbschnitte } from "~/apps/schueler/ListAbschnitte";
import { ListFaecher } from "~/apps/kataloge/faecher/ListFaecher";
import { ListLehrer } from "~/apps/lehrer/ListLehrer";
import { RouteNode } from "~/router/RouteNode";
import { RouteSchuelerLeistungen } from "~/router/apps/schueler/RouteSchuelerLeistungen";
import { RouteManager } from "~/router/RouteManager";
import { routeApp } from "~/router/RouteApp";

export class RouteDataSchuelerLeistungenDaten {
	auswahl: ListAbschnitte = new ListAbschnitte();
	daten: DataSchuelerAbschnittsdaten = new DataSchuelerAbschnittsdaten();
	listFaecher: ListFaecher = new ListFaecher();
	mapFaecher: Map<number, FaecherListeEintrag> = new Map();
	listLehrer: ListLehrer = new ListLehrer();
	mapLehrer: Map<number, LehrerListeEintrag> = new Map();

	setLernabschnitt = async (value: SchuelerLernabschnittListeEintrag | undefined) => {
		await RouteManager.doRoute({ name: routeSchuelerLeistungenDaten.name, params: { id: value?.schuelerID, idLernabschnitt: value?.id } });
	}

}

const SSchuelerLeistungenDaten = () => import("~/components/schueler/leistungsdaten/SSchuelerLeistungenDaten.vue");
const SSchuelerLeistungenAuswahl = () => import("~/components/schueler/leistungsdaten/SSchuelerLeistungenAuswahl.vue")

export class RouteSchuelerLeistungenDaten extends RouteNodeListView<ListAbschnitte, SchuelerLernabschnittListeEintrag, RouteDataSchuelerLeistungenDaten, RouteSchuelerLeistungen> {

	public constructor() {
		super("schueler_leistungen_daten", ":idLernabschnitt(\\d+)?", SSchuelerLeistungenAuswahl, SSchuelerLeistungenDaten, new ListAbschnitte(), 'id', new RouteDataSchuelerLeistungenDaten());
		super.propHandler = (route) => this.getProps(route);
		super.text = "Leistungsdaten";
		super.setView("lernabschnittauswahl", SSchuelerLeistungenAuswahl, (route) => this.getAuswahlProps(route));
		super.children = [
		];
	}

	public async beforeEach(to: RouteNode<unknown, any>, to_params: RouteParams, from: RouteNode<unknown, any> | undefined, from_params: RouteParams): Promise<any> {
		if (to_params.id === undefined)
			return false;
		const id = parseInt(to_params.id as string);
		if (to.name !== from?.name) {
			await this.data.listFaecher.update_list();
			this.data.mapFaecher.clear();
			this.data.listFaecher.liste.forEach(f => this.data.mapFaecher.set(f.id, f));
			await this.data.listLehrer.update_list();
			this.data.mapLehrer.clear();
			this.data.listLehrer.liste.forEach(l => this.data.mapLehrer.set(l.id, l));
		}
		if ((to.name !== from?.name) || (from_params.id === undefined) || (parseInt(from_params.id as string) != id))
			await this.data.auswahl.update_list(id);
		if ((to.name === this.name) && (to_params.idLernabschnitt === undefined)) {
			if (routeApp.data.schule.daten === undefined)
				throw new Error("Keine Daten für die Schule geladen!");
			const akt_schuljahresabschnitt = routeApp.data.schule.daten.idSchuljahresabschnitt;
			let lernabschnitt = this.data.auswahl.liste.find(l => l.schuljahresabschnitt === akt_schuljahresabschnitt);
			if (lernabschnitt === undefined)
				lernabschnitt = this.data.auswahl.liste[0];
			if (lernabschnitt === undefined)
				return false;
			return { name: this.name, params: { id: to_params.id, idLernabschnitt: lernabschnitt.id }};
		}
		return true;
	}

	protected async update(to: RouteNode<unknown, any>, to_params: RouteParams) {
		if (to_params.idLernabschnitt === undefined) {
			await this.onSelect(undefined);
		} else {
			const idLernabschnitt = parseInt(to_params.idLernabschnitt as string);
			await this.onSelect(this.data.auswahl.liste.find(s => s.id === idLernabschnitt));
		}
	}

	protected async onSelect(item?: SchuelerLernabschnittListeEintrag) {
		if (item === this.data.auswahl.ausgewaehlt)
			return;
		this.data.auswahl.ausgewaehlt = item;
		if (item === undefined) {
			await this.data.daten.unselect();
		} else {
			await this.data.daten.select(this.data.auswahl.ausgewaehlt);
		}
	}

	// TODO deprecated
	protected getAuswahlComputedProperty(): WritableComputedRef<SchuelerLernabschnittListeEintrag | undefined> {
		return computed({
			get: () => undefined,
			set: (value) => {}
		});
	}

	public getRoute(id: number, idLernabschnitt: number) : RouteLocationRaw {
		return { name: this.name, params: { id, idLernabschnitt }};
	}

	public getAuswahlProps(to: RouteLocationNormalized): Record<string, any> {
		return {
			lernabschnitt: this.data.auswahl.ausgewaehlt,
			lernabschnitte: this.data.auswahl.liste,
			setLernabschnitt: this.data.setLernabschnitt
		};
	}

	public getProps(to: RouteLocationNormalized): Record<string, any> {
		return {
			lernabschnitt: this.data.auswahl.ausgewaehlt,
			data: this.data.daten,
			mapFaecher: this.data.mapFaecher,
			mapLehrer: this.data.mapLehrer
		};
	}

}

export const routeSchuelerLeistungenDaten = new RouteSchuelerLeistungenDaten();

