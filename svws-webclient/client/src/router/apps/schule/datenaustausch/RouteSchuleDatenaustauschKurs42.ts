import type { RouteLocationNormalized, RouteLocationRaw } from "vue-router";

import { BenutzerKompetenz, Schulform, ServerMode } from "@core";

import { RouteNode } from "~/router/RouteNode";
import { routeSchuleDatenaustausch, type RouteSchuleDatenaustausch } from "~/router/apps/schule/datenaustausch/RouteSchuleDatenaustausch";

import type { SchuleDatenaustauschKurs42Props } from "~/components/schule/datenaustausch/kurs42/SSchuleDatenaustauschKurs42Props";

const SSchuleDatenaustauschKurs42 = () => import("~/components/schule/datenaustausch/kurs42/SSchuleDatenaustauschKurs42.vue");

export class RouteSchuleDatenaustauschKurs42 extends RouteNode<unknown, RouteSchuleDatenaustausch> {

	public constructor() {
		super(Schulform.values(), [ BenutzerKompetenz.KEINE ], "schule.datenaustausch.kurs42", "kurs42", SSchuleDatenaustauschKurs42);
		super.mode = ServerMode.STABLE;
		super.propHandler = (route) => this.getProps(route);
		super.text = "Kurs42 Blockungen";
	}

	public getRoute(id: number) : RouteLocationRaw {
		return { name: this.name, params: { }};
	}

	public getProps(to: RouteLocationNormalized): SchuleDatenaustauschKurs42Props {
		return {
			setGostKurs42ImportZip: routeSchuleDatenaustausch.data.setGostKurs42ImportZip,
		};
	}
}

export const routeSchuleDatenaustauschKurs42 = new RouteSchuleDatenaustauschKurs42();
