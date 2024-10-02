import type { RouteLocationNormalized, RouteLocationRaw, RouteParams } from "vue-router";

import { BenutzerKompetenz, DeveloperNotificationException, Schulform, ServerMode } from "@core";

import { RouteManager } from "~/router/RouteManager";
import { RouteNode } from "~/router/RouteNode";

import type { RouteApp } from "~/router/apps/RouteApp";
import { routeApp } from "~/router/apps/RouteApp";
import { routeKatalogReligionDaten } from "~/router/apps/schule/religionen/RouteKatalogReligionDaten";

import type { TabData } from "@ui";
import type { ReligionenAppProps } from "~/components/schule/kataloge/religionen/SReligionenAppProps";
import type { ReligionenAuswahlProps } from "~/components/schule/kataloge/religionen/SReligionenAuswahlPops";
import { RouteDataKatalogReligionen } from "./RouteDataKatalogReligionen";
import { routeSchule } from "../RouteSchule";


const SReligionenAuswahl = () => import("~/components/schule/kataloge/religionen/SReligionenAuswahl.vue")
const SReligionenApp = () => import("~/components/schule/kataloge/religionen/SReligionenApp.vue")

export class RouteKatalogReligionen extends RouteNode<RouteDataKatalogReligionen, RouteApp> {

	public constructor() {
		super(Schulform.values(), [ BenutzerKompetenz.KEINE ], "schule.religionen", "schule/religion/:id(\\d+)?", SReligionenApp, new RouteDataKatalogReligionen());
		super.mode = ServerMode.STABLE;
		super.propHandler = (route) => this.getProps(route);
		super.text = "Religionen";
		super.setView("liste", SReligionenAuswahl, (route) => this.getAuswahlProps(route));
		super.children = [
			routeKatalogReligionDaten
		];
		super.defaultChild = routeKatalogReligionDaten;
	}

	protected async update(to: RouteNode<any, any>, to_params: RouteParams, from: RouteNode<any, any> | undefined, from_params: RouteParams, isEntering: boolean) : Promise<void | Error | RouteLocationRaw> {
		if (isEntering)
			await this.data.ladeListe();
		if (to_params.id instanceof Array)
			throw new DeveloperNotificationException("Fehler: Die Parameter der Route dürfen keine Arrays sein");
		if ((to_params.id === undefined) || (to_params.id === "")) {
			await this.data.ladeListe();
		} else {
			const id = parseInt(to_params.id);
			const eintrag = this.data.religionListeManager.liste.get(id);
			if ((eintrag === null) && (this.data.religionListeManager.auswahlID() !== null)) {
				await this.data.ladeListe();
				return this.getRoute(this.data.religionListeManager.auswahlID() ?? undefined);
			} else if (eintrag !== null)
				this.data.setEintrag(eintrag);
		}
		if ((to.name === this.name) && (this.data.religionListeManager.auswahlID() !== null))
			return this.getRoute(this.data.religionListeManager.auswahlID() ?? undefined);
	}

	public getRoute(id: number|undefined) : RouteLocationRaw {
		return { name: this.defaultChild!.name, params: { idSchuljahresabschnitt: routeApp.data.idSchuljahresabschnitt, id }};
	}

	public getAuswahlProps(to: RouteLocationNormalized): ReligionenAuswahlProps {
		return {
			religionListeManager: () => this.data.religionListeManager,
			setFilter: this.data.setFilter,
			addEintrag: this.data.addEintrag,
			deleteEintraege: this.data.deleteEintraege,
			schuljahresabschnittsauswahl: () => routeApp.data.getSchuljahresabschnittsauswahl(false),
			gotoEintrag: this.data.gotoEintrag,
			gotoSchule: routeSchule.gotoSchule,
		};
	}

	public getProps(to: RouteLocationNormalized): ReligionenAppProps {
		return {
			religionListeManager: () => this.data.religionListeManager,
			// Props für die Navigation
			setTab: this.setTab,
			tab: this.getTab(),
			tabs: this.getTabs(),
			tabsHidden: this.children_hidden().value,
		};
	}

	private getTab(): TabData {
		return { name: this.data.view.name, text: this.data.view.text };
	}

	private getTabs(): TabData[] {
		const result: TabData[] = [];
		for (const c of super.children)
			if (c.hatEineKompetenz() && c.hatSchulform())
				result.push({ name: c.name, text: c.text });
		return result;
	}

	private setTab = async (value: TabData) => {
		if (value.name === this.data.view.name)
			return;
		const node = RouteNode.getNodeByName(value.name);
		if (node === undefined)
			throw new DeveloperNotificationException("Unbekannte Route");
		await RouteManager.doRoute({ name: value.name, params: { idSchuljahresabschnitt: routeApp.data.idSchuljahresabschnitt, id: this.data.religionListeManager.auswahlID() ?? undefined } });
		this.data.setView(node, this.children);
	}
}

export const routeKatalogReligionen = new RouteKatalogReligionen();