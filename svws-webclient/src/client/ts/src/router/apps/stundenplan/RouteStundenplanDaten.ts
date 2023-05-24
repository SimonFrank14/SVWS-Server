import type { RouteLocationNormalized, RouteLocationRaw, RouteParams } from "vue-router";
import type { RouteStundenplan} from "../RouteStundenplan";
import type { StundenplanDatenProps } from "~/components/stundenplan/daten/SStundenplanDatenProps";
import { routeStundenplan} from "../RouteStundenplan";
import { BenutzerKompetenz, Schulform } from "@svws-nrw/svws-core";
import { RouteNode } from "~/router/RouteNode";

const SStundenplanDaten = () => import("~/components/stundenplan/daten/SStundenplanDaten.vue");

export class RouteStundenplanDaten extends RouteNode<unknown, RouteStundenplan> {

	public constructor() {
		super(Schulform.values(), [ BenutzerKompetenz.KEINE ], "stundenplan.daten", "daten", SStundenplanDaten);
		super.propHandler = (route) => this.getProps(route);
		super.text = "Daten";
	}

	public async update(to: RouteNode<unknown, any>, to_params: RouteParams): Promise<any> {
	}

	public getRoute(id: number) : RouteLocationRaw {
		return { name: this.name, params: { id }};
	}

	public getProps(to: RouteLocationNormalized): StundenplanDatenProps {
		return {
			data: () => routeStundenplan.data.daten,
			patch: routeStundenplan.data.patch,
		};
	}

}

export const routeStundenplanDaten = new RouteStundenplanDaten();

