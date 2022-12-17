import { computed, WritableComputedRef } from "vue";
import { RouteLocationNormalized, RouteMeta, RouteRecordRaw, useRoute, useRouter } from "vue-router";
import { BaseList } from "~/apps/BaseList";

/**
 * Dieses Interface gibt an, welche Informationen bei Routen-Einträgen
 * im SVWS-Client als Meta-Informationen abgelegt werden.
 */
export interface RouteAppMeta<Item> extends RouteMeta {

	/** Eine Funktion, welche eine Computed-Property zurückliefert, die für die Auswahl eines Elements aus einer Liste von Elementen verantwirtlich ist. */
	auswahl: () => WritableComputedRef<Item>

}


/**
 * Eine interne Hilfsmethode, um Typ-sicher auf die Meta-Informationen von Routen-Einträgen zuzugreifen.
 * 
 * @param route   der Routen-Eintrag, bei der auf die Meta-Informationen zugegriffen werden soll
 * 
 * @returns Die Meta-Information des Routen-Eintrags
 */
function routeAppMeta<T extends RouteRecordRaw, Item>(route : T) : RouteAppMeta<Item> {
	if (route.meta === undefined)
		throw new Error("Meta-Informationen für die Route '" + route.name?.toString() + "' nicht definiert.");
	return route.meta as RouteAppMeta<Item>;
}

/**
 * Methode zum Erzeugen der Computed-Property für die Auswahl eines Routen-Eintrags.
 * 
 * @param route    der Routen-Eintrag, für den die Computed-Property erzeugt werden soll
 * 
 * @returns die Computed-Property
 */
export function routeAppAuswahl<T extends RouteRecordRaw, Item>(route : T) : WritableComputedRef<Item> {
	return routeAppMeta<T, Item>(route).auswahl();
}

/**
 * Eine Default-Methode zum Erzeugen der Properties "id" und "item" zu einer Route bei 
 * "normalen" Auswahl-Listen, welche ein numerisches ID-Attribut haben.
 * 
 * Dies ist eine Hilfsmethode für die Nutzung beim Definieren von Routen-Einträgen.
 * 
 * @param route     die aktuelle Route, für die die Properties erzeugt werden sollen
 * @param name      der Name des Routen-Eintrages, für welche die Properties erzeugt werden sollen.
 *                  Dieser wird genutzt, um zu prüfen, ob die übergebene Route zu dem Routen-Eintrag passt
 * @param auswahl   die Liste der Auswahl (siehe auch BaseList)
 * 
 * @returns das Objekt mit den Werten für die Properties
 */
export function routePropsAuswahlID<TAuswahl extends BaseList<{ id: number }, unknown>>(route: RouteLocationNormalized, name: string, auswahl: TAuswahl) {
	if ((route.name !== name) || (route.params.id === undefined))
		return { id: undefined, item: undefined };
	const id = parseInt(route.params.id as string);
	const item = auswahl.liste.find(s => s.id === id);
	return { id: id, item: item };
}


/**
 * Eine Default-Methode für Routen-Einträge zum Erzeugen der Computed-Property bei der
 * Auswahl einer Route eines Routen-Eintrags.
 * 
 * Dies ist eine Hilfsmethode für die Nutzung beim Definieren von Routen-Einträgen.
 * 
 * @param name      der Name des Routen-Eintrags, so dass dieser beim Setzen einer Route aktualisiert werden kann
 * @param auswahl   die Liste der Auswahl (siehe auch BaseList)
 * 
 * @returns die Computed-Property
 */
export function routeAuswahlID<TItem extends { id: number }, TAuswahl extends BaseList<TItem, unknown>>(name: string, auswahl: TAuswahl) : WritableComputedRef<TItem | undefined> {
	const router = useRouter();
	const route = useRoute();

	const selected = computed({
		get(): TItem | undefined {
			if (route.params.id === undefined)
				return undefined;
			return auswahl.liste.find(s => s.id.toString() === route.params.id);
		},
		set(value: TItem | undefined) {
			auswahl.ausgewaehlt = value;
			router.push({ name: name, params: { id: value?.id } });
		}
	});
	return selected;
}


