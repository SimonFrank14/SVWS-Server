import type { RouteLocationNormalized, RouteLocationRaw } from "vue-router";

import { BenutzerKompetenz, Schulform, ServerMode } from "@core";

import { api } from "~/router/Api";
import { RouteNode } from "~/router/RouteNode";
import { routeKlassen, type RouteKlassen } from "~/router/apps/klassen/RouteKlassen";
import type { KlassenDatenProps } from "~/components/klassen/daten/SKlassenDatenProps";
import { routeApp } from "../RouteApp";

const SKlassenDaten = () => import("~/components/klassen/daten/SKlassenDaten.vue");

export class RouteKlassenDaten extends RouteNode<any, RouteKlassen> {

	public constructor() {
		super(Schulform.values(), [ BenutzerKompetenz.UNTERRICHTSVERTEILUNG_ANSEHEN ], "klassen.daten", "daten", SKlassenDaten);
		super.mode = ServerMode.STABLE;
		super.propHandler = (route) => this.getProps(route);
		super.text = "Klasse";
	}

	public getRoute(id: number): RouteLocationRaw {
		return { name: this.name, params: {idSchuljahresabschnitt: routeApp.data.idSchuljahresabschnitt, id: id} };
	}

	public getProps(to: RouteLocationNormalized): KlassenDatenProps {
		return {
			schulform: api.schulform,
			schulgliederungen: api.schulgliederungen,
			serverMode: api.mode,
			benutzerKompetenzen: api.benutzerKompetenzen,
			patch: routeKlassen.data.patch,
			klassenListeManager: () => routeKlassen.data.klassenListeManager,
			setFilter: routeKlassen.data.setFilter,
			mapKlassenVorigerAbschnitt: () => routeKlassen.data.mapKlassenVorigerAbschnitt,
			mapKlassenFolgenderAbschnitt: () => routeKlassen.data.mapKlassenFolgenderAbschnitt,
			gotoSchueler: routeKlassen.data.gotoSchueler,
			gotoLehrer: routeKlassen.data.gotoLehrer,
			addKlassenleitung: routeKlassen.data.addKlassenleitung,
			removeKlassenleitung: routeKlassen.data.removeKlassenleitung,
			updateReihenfolgeKlassenleitung: routeKlassen.data.updateReihenfolgeKlassenleitung
		};
	}

}

export const routeKlassenDaten = new RouteKlassenDaten();
