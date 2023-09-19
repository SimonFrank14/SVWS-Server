import type { RouteLocationNormalized, RouteLocationRaw } from "vue-router";

import { BenutzerKompetenz, Schulform, ServerMode } from "@core";

import { RouteNode } from "~/router/RouteNode";
import { routeSchuleDatenaustausch, type RouteSchuleDatenaustausch } from "~/router/apps/schule/datenaustausch/RouteSchuleDatenaustausch";

import type { SchuleDatenaustauschLaufbahnplanungProps } from "~/components/schule/datenaustausch/laufbahnplanung/SSchuleDatenaustauschLaufbahnplanungProps";

const SSchuleDatenaustauschLaufbahnplanung = () => import("~/components/schule/datenaustausch/laufbahnplanung/SSchuleDatenaustauschLaufbahnplanung.vue");

export class RouteSchuleDatenaustauschLaufbahnplanung extends RouteNode<unknown, RouteSchuleDatenaustausch> {

	public constructor() {
		super(Schulform.values(), [ BenutzerKompetenz.KEINE ], "schule.datenaustausch.laufbahnplanung.daten", "laufbahnplanung", SSchuleDatenaustauschLaufbahnplanung);
		super.mode = ServerMode.STABLE;
		super.propHandler = (route) => this.getProps(route);
		super.text = "LuPO Laufbahnplanung";
	}

	public getRoute(id: number) : RouteLocationRaw {
		return { name: this.name, params: { }};
	}

	public getProps(to: RouteLocationNormalized): SchuleDatenaustauschLaufbahnplanungProps {
		return {
			setGostLupoImportMDBFuerJahrgang: routeSchuleDatenaustausch.data.setGostLupoImportMDBFuerJahrgang,
		};
	}
}

export const routeSchuleDatenaustauschLaufbahnplanung = new RouteSchuleDatenaustauschLaufbahnplanung();
