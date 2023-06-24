import { BenutzerKompetenz, Schulform, ServerMode } from "@core";
import type { RouteLocationNormalized, RouteLocationRaw } from "vue-router";
import type { SchuleDatenaustauschENMProps } from "~/components/schule/datenaustausch/enm/SSchuleDatenaustauschENMProps";
import { RouteNode } from "~/router/RouteNode";
import type { RouteSchuleDatenaustausch} from "../schule/RouteSchuleDatenaustausch";
import { routeSchuleDatenaustausch } from "../schule/RouteSchuleDatenaustausch";

const SSchuleDatenaustauschENM = () => import("~/components/schule/datenaustausch/enm/SSchuleDatenaustauschENM.vue");

export class RouteSchuleDatenaustauschENM extends RouteNode<unknown, RouteSchuleDatenaustausch> {

	public constructor() {
		super(Schulform.values(), [ BenutzerKompetenz.KEINE ], "schule.datenaustausch.enm.daten", "enm", SSchuleDatenaustauschENM);
		super.mode = ServerMode.DEV;
		super.propHandler = (route) => this.getProps(route);
		super.text = "ENM Notenmanager";
	}

	public getRoute(id: number) : RouteLocationRaw {
		return { name: this.name, params: { }};
	}

	public getProps(to: RouteLocationNormalized): SchuleDatenaustauschENMProps {
		return {
			setImportENM: routeSchuleDatenaustausch.data.setImportENM,
		};
	}
}

export const routeSchuleDatenaustauschENM = new RouteSchuleDatenaustauschENM();

