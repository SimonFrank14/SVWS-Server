import type { RouteLocationNormalized, RouteLocationRaw, RouteParams } from "vue-router";

import { BenutzerKompetenz, DeveloperNotificationException, GostKursklausurManager, Schulform, ServerMode, Vector } from "@core";

import { RouteNode } from "~/router/RouteNode";
import { routeGostKlausurplanung, type RouteGostKlausurplanung } from "~/router/apps/gost/klausurplanung/RouteGostKlausurplanung";
import type { GostKlausurplanungRaumzeitProps } from "~/components/gost/klausurplanung/SGostKlausurplanungRaumzeitProps";
import { routeApp } from "../../RouteApp";

const SGostKlausurplanungRaumzeit = () => import("~/components/gost/klausurplanung/SGostKlausurplanungRaumzeit.vue");

export class RouteGostKlausurplanungRaumzeit extends RouteNode<unknown, RouteGostKlausurplanung> {

	public constructor() {
		super(Schulform.getMitGymOb(), [ BenutzerKompetenz.KEINE ], "gost.klausurplanung.raumzeit", "raumzeit/:idtermin(\\d+)?", SGostKlausurplanungRaumzeit);
		super.mode = ServerMode.STABLE;
		super.propHandler = (route) => this.getProps(route);
		super.text = "Räume und Startzeiten";
		this.isHidden = (params?: RouteParams) => {
			return this.checkHidden(params);
		}
	}

	public checkHidden(params?: RouteParams) {
		if (!routeGostKlausurplanung.data.hatStundenplanManager)
			return { name: routeGostKlausurplanung.defaultChild!.name, params };
		return false;
	}

	public getRoute(abiturjahr: number, halbjahr: number) : RouteLocationRaw {
		return { name: this.name, params: { idSchuljahresabschnitt: routeApp.data.idSchuljahresabschnitt, abiturjahr: abiturjahr, halbjahr: halbjahr }};
	}

	protected async update(to: RouteNode<unknown, any>, to_params: RouteParams) : Promise<void | Error | RouteLocationRaw> {
		// Prüfe die Parameter zunächst allgemein
		console.log("LALALA");
		if (to_params.idtermin instanceof Array)
			throw new DeveloperNotificationException("Fehler: Die Parameter der Route dürfen keine Arrays sein");
		const idTermin = !to_params.idtermin ? null : parseInt(to_params.idtermin);
		routeGostKlausurplanung.data.terminauswahl.value = (idTermin === null) ? null : routeGostKlausurplanung.data.kursklausurmanager.terminGetByIdOrException(idTermin);
		console.log(routeGostKlausurplanung.data.terminauswahl.value);
	}

	public getProps(to: RouteLocationNormalized): GostKlausurplanungRaumzeitProps {
		return {
			jahrgangsdaten: routeGostKlausurplanung.data.jahrgangsdaten,
			halbjahr: routeGostKlausurplanung.data.halbjahr,
			kMan: () => { return routeGostKlausurplanung.data.hatKursklausurManager ? routeGostKlausurplanung.data.kursklausurmanager : new GostKursklausurManager()},
			stundenplanmanager: () => routeGostKlausurplanung.data.stundenplanmanager,
			hatStundenplanManager: routeGostKlausurplanung.data.hatStundenplanManager,
			createKlausurraum: routeGostKlausurplanung.data.createKlausurraum,
			loescheKlausurraum: routeGostKlausurplanung.data.loescheKlausurraum,
			patchKlausurraum: routeGostKlausurplanung.data.patchKlausurraum,
			erzeugeKlausurraummanager: routeGostKlausurplanung.data.erzeugeKlausurraummanager,
			setzeRaumZuSchuelerklausuren: routeGostKlausurplanung.data.setzeRaumZuSchuelerklausuren,
			patchKlausur: routeGostKlausurplanung.data.patchKlausur,
			quartalsauswahl: routeGostKlausurplanung.data.quartalsauswahl,
			terminauswahl: routeGostKlausurplanung.data.terminauswahl,
			zeigeAlleJahrgaenge: () => routeGostKlausurplanung.data.zeigeAlleJahrgaenge,
			setZeigeAlleJahrgaenge: routeGostKlausurplanung.data.setZeigeAlleJahrgaenge,
		}
	}

}

export const routeGostKlausurplanungRaumzeit = new RouteGostKlausurplanungRaumzeit();

