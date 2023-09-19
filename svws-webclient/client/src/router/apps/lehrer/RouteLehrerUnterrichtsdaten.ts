import type { RouteLocationNormalized, RouteLocationRaw } from "vue-router";

import { BenutzerKompetenz, Schulform, ServerMode } from "@core";

import { RouteNode } from "~/router/RouteNode";
import { routeLehrer, type RouteLehrer } from "~/router/apps/lehrer/RouteLehrer";

import type { LehrerUnterrichtsdatenProps } from "~/components/lehrer/unterrichtsdaten/SLehrerUnterrichtsdatenProps";

const SLehrerUnterrichtsdaten = () => import("~/components/lehrer/unterrichtsdaten/SLehrerUnterrichtsdaten.vue");

export class RouteLehrerUnterrichtsdaten extends RouteNode<unknown, RouteLehrer> {

	public constructor() {
		super(Schulform.values(), [ BenutzerKompetenz.KEINE ], "lehrer.unterrichtsdaten", "unterrichtsdaten", SLehrerUnterrichtsdaten);
		super.mode = ServerMode.DEV;
		super.propHandler = (route) => routeLehrer.getProps(route);
		super.text = "Unterricht";
	}

	public getRoute(id: number) : RouteLocationRaw {
		return { name: this.name, params: { id: id }};
	}

	public getProps(to: RouteLocationNormalized): LehrerUnterrichtsdatenProps {
		return {
			stammdaten: routeLehrer.data.stammdaten
		};
	}

}

export const routeLehrerUnterrichtsdaten = new RouteLehrerUnterrichtsdaten();