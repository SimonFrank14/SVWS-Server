import { BenutzergruppeListeEintrag } from "@svws-nrw/svws-core-ts";
import { computed, WritableComputedRef } from "vue";
import { RouteRecordRaw, useRoute, useRouter } from "vue-router";
import { BaseList } from "~/apps/BaseList";
import { injectMainApp } from "~/apps/Main";
import { RouteAppMeta } from "~/router/RouteUtils";
import { routeSchuleBenutzerverwaltungPropsAuswahlID } from "~/router/apps/RouteSchuleBenutzerverwaltung";

const ROUTE_NAME: string = "benutzergruppe";

export const RouteSchuleBenutzerverwaltungBenutzergruppe : RouteRecordRaw = {
	name: ROUTE_NAME,
	path: "/schule/benutzerverwaltung/:id(\\d+)?/benutzergruppe",
	component: () => import("~/components/schule/benutzerverwaltung/daten/SBenutzergruppe.vue"),
	props: (route) => routeSchuleBenutzerverwaltungPropsAuswahlID(route),
	meta: <RouteAppMeta<BenutzergruppeListeEintrag  | undefined>> {
		auswahl: () => routeBenutzerverwaltungBenutzergruppeAuswahlID(ROUTE_NAME, injectMainApp().apps.benutzergruppe.auswahl),
		text: "Benutzergruppe"
	}
}


/**
 * Eine Methode für Routen-Einträge zum Erzeugen der Computed-Property bei der
 * Auswahl einer Route eines Routen-Eintrags.
 * 
 * Dies ist eine Hilfsmethode für die Nutzung beim Definieren von Routen-Einträgen.
 * 
 * @param name      der Name des Routen-Eintrags, so dass dieser beim Setzen einer Route aktualisiert werden kann
 * @param auswahl   die Liste der Auswahl (siehe auch BaseList)
 * 
 * @returns die Computed-Property
 */
export function routeBenutzerverwaltungBenutzergruppeAuswahlID<TItem extends { id: number }, TAuswahl extends BaseList<TItem, unknown>>(name: string, auswahl: TAuswahl) : WritableComputedRef<TItem | undefined> {
	const router = useRouter();
	const route = useRoute();

	const selected = computed({
		get(): TItem | undefined {
			if (route.params.id === undefined)
				return undefined;
			if (route.name?.toString() !== name)
				return undefined;
			let tmp = auswahl.ausgewaehlt;
			if ((tmp === undefined) || (tmp.id.toString() !== route.params.id))
				tmp = auswahl.liste.find(s => s.id.toString() === route.params.id);
			return tmp;
		},
		set(value: TItem | undefined) {
			auswahl.ausgewaehlt = value;
			router.push({ name: name, params: { id: value?.id } });
		}
	});
	return selected;
}
