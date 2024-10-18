import type { RouteLocationNormalized, RouteLocationRaw, RouteParams } from "vue-router";
import type { BenutzerprofilAppProps } from "~/components/benutzerprofil/SBenutzerprofilAppProps";
import { routeApp, type RouteApp} from "~/router/apps/RouteApp";
import { BenutzerKompetenz, Schulform, ServerMode } from "@core";
import { RouteNode } from "~/router/RouteNode";
import { RouteDataBenutzerprofil } from "~/router/apps/benutzerprofil/RouteDataBenutzerprofil";
import { api } from "~/router/Api";

const SBenutzerprofilApp = () => import("~/components/benutzerprofil/SBenutzerprofilApp.vue")

export class RouteBenutzerprofil extends RouteNode<RouteDataBenutzerprofil, RouteApp> {

	public constructor() {
		super(Schulform.values(), [ BenutzerKompetenz.KEINE ], "benutzerprofil", "benutzerprofil", SBenutzerprofilApp, new RouteDataBenutzerprofil());
		super.mode = ServerMode.STABLE;
		super.propHandler = (route) => this.getProps(route);
		super.text = "Benutzerprofil";
	}

	protected async update(to: RouteNode<any, any>, to_params: RouteParams) : Promise<void | Error | RouteLocationRaw> {
		const { id } = RouteNode.getIntParams(to_params, ["id"]);
		if (this.data.benutzerEMailDaten.id !== id)
			await this.data.ladeDaten();
	}

	public getRoute() : RouteLocationRaw {
		return { name: this.defaultChild!.name, params: { idSchuljahresabschnitt: routeApp.data.idSchuljahresabschnitt } };
	}

	public getProps(to: RouteLocationNormalized): BenutzerprofilAppProps {
		return {
			benutzer: () => this.data.benutzer,
			mode: api.mode,
			patch: this.data.patch,
			benutzerEMailDaten: () => this.data.benutzerEMailDaten,
			patchBenutzerEMailDaten: this.data.patchBenutzerEMailDaten,
			patchPasswort: this.data.patchPasswort,
			aes: api.aes,
		};
	}

}

export const routeBenutzerprofil = new RouteBenutzerprofil();
