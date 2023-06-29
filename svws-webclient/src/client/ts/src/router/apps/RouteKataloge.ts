import type { RouteLocationNormalized, RouteLocationRaw } from "vue-router";
import { shallowRef } from "vue";

import { BenutzerKompetenz, Schulform, ServerMode } from "@core";

import { api } from "~/router/Api";
import { RouteManager } from "~/router/RouteManager";
import { RouteNode } from "~/router/RouteNode";

import type { RouteApp } from "~/router/apps/RouteApp";
import { routeApp } from "~/router/apps/RouteApp";

import { routeKatalogFaecher } from "~/router/apps/RouteKatalogFaecher";
import { routeKatalogFoerderschwerpunkte } from "~/router/apps/RouteKatalogFoerderschwerpunkte";
import { routeKatalogJahrgaenge } from "~/router/apps/RouteKatalogJahrgaenge";
import { routeKatalogReligion } from "~/router/apps/RouteKatalogReligionen";
import { routeKatalogRaeume } from "~/router/apps/RouteKatalogRaeume";
import { routeKatalogAufsichtsbereiche } from "~/router/apps/RouteKatalogAufsichtsbereiche";
import { routeKatalogPausenzeiten } from "~/router/apps/RouteKatalogPausenzeiten";
import { routeKatalogZeitraster } from "~/router/apps/RouteKatalogZeitraster";
import { routeKatalogBetriebe } from "~/router/apps/RouteKatalogBetriebe";

import type { KatalogeAuswahlProps } from "~/components/kataloge/SKatalogeAuswahlProps";
import type { AuswahlChildData } from "~/components/AuswahlChildData";


interface RouteStateKataloge {
	view: RouteNode<any, any>;
}
export class RouteDataKataloge {

	private static _defaultState: RouteStateKataloge = {
		view: routeKatalogFaecher,
	}
	private _state = shallowRef(RouteDataKataloge._defaultState);

	private setPatchedDefaultState(patch: Partial<RouteStateKataloge>) {
		this._state.value = Object.assign({ ... RouteDataKataloge._defaultState }, patch);
	}

	private setPatchedState(patch: Partial<RouteStateKataloge>) {
		this._state.value = Object.assign({ ... this._state.value }, patch);
	}

	private commit(): void {
		this._state.value = { ... this._state.value };
	}

	public async setView(view: RouteNode<any,any>) {
		if (routeKataloge.menu.includes(view))
			this.setPatchedState({ view: view });
		else
			throw new Error("Diese für die Kataloge gewählte Ansicht wird nicht unterstützt.");
	}

	public get view(): RouteNode<any,any> {
		return this._state.value.view;
	}
}
const SKatalogeAuswahl = () => import("~/components/kataloge/SKatalogeAuswahl.vue")
const SKatalogeApp = () => import("~/components/kataloge/SKatalogeApp.vue")

export class RouteKataloge extends RouteNode<RouteDataKataloge, RouteApp> {

	public constructor() {
		super(Schulform.values(), [ BenutzerKompetenz.KEINE ], "kataloge", "/kataloge", SKatalogeApp, new RouteDataKataloge());
		super.mode = ServerMode.STABLE;
		super.propHandler = (route) => this.getNoProps(route);
		super.text = "Kataloge";
		super.setView("liste", SKatalogeAuswahl, (route) => this.getAuswahlProps(route));
		super.children = [
		];
		super.menu = [
			routeKatalogFaecher,
			routeKatalogReligion,
			routeKatalogJahrgaenge,
			routeKatalogFoerderschwerpunkte,
			routeKatalogRaeume,
			routeKatalogAufsichtsbereiche,
			routeKatalogPausenzeiten,
			routeKatalogZeitraster,
			routeKatalogBetriebe,
			// TODO { title: "Haltestellen", value: "haltestellen" },
		];
		super.defaultChild = undefined;
	}

	public getRoute(id?: number) : RouteLocationRaw {
		return { name: this.defaultChild!.name, params: { id }};
	}

	public getAuswahlProps(to: RouteLocationNormalized): KatalogeAuswahlProps {
		return {
			abschnitte: api.mapAbschnitte.value,
			aktAbschnitt: routeApp.data.aktAbschnitt.value,
			aktSchulabschnitt: api.schuleStammdaten.idSchuljahresabschnitt,
			setAbschnitt: routeApp.data.setAbschnitt,
			setChild: this.setChild,
			child: this.getChild(),
			children: this.getChildData(),
			childrenHidden: this.children_hidden().value,
		};
	}

	private getChild(): AuswahlChildData {
		return { name: this.data.view.name, text: this.data.view.text };
	}

	private getChildData(): AuswahlChildData[] {
		const result: AuswahlChildData[] = [];
		for (const c of this.menu)
			if (c.hatEineKompetenz() && c.hatSchulform())
				result.push({ name: c.name, text: c.text });
		return result;
	}

	private setChild = async (value: AuswahlChildData) => {
		const node = RouteNode.getNodeByName(value.name);
		if (node === undefined)
			throw new Error("Unbekannte Route");
		await RouteManager.doRoute({ name: value.name, params: {} });
		await this.data.setView(node);
	}

	returnToKataloge = async () => await RouteManager.doRoute({ name: this.name, params: {} });
}

export const routeKataloge = new RouteKataloge();
