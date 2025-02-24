<template>
	<svws-ui-secondary-menu>
		<template #headline>Kurse</template>
		<template #abschnitt>
			<abschnitt-auswahl :daten="schuljahresabschnittsauswahl" />
		</template>
		<template #content>
			<svws-ui-table clickable :clicked="kursListeManager().hasDaten() ? kursListeManager().auswahl() : null" @update:clicked="gotoEintrag"
				:items="rowsFiltered" :model-value="selectedItems" @update:model-value="items => setAuswahl(items)"
				:columns selectable count :filter-open="true" :filtered="filterChanged()" :filterReset scroll-into-view scroll
				v-model:sort-by-and-order="sortByAndOrder" :sort-by-multi allow-arrow-key-selection>
				<template #search>
					<svws-ui-text-input v-model="search" type="search" placeholder="Suche nach Kurs" removable />
				</template>
				<template #filterAdvanced>
					<svws-ui-checkbox type="toggle" v-model="filterNurSichtbare">Nur Sichtbare</svws-ui-checkbox>
					<svws-ui-multi-select v-model="filterSchueler" title="Schüler" :items="kursListeManager().schueler.list()" :item-text="textSchueler" :item-filter="findSchueler" />
					<svws-ui-multi-select v-model="filterFaecher" title="Fach" :items="kursListeManager().faecher.list()" :item-text="text" :item-filter="find" />
					<svws-ui-multi-select v-model="filterLehrer" title="Fachlehrer" :items="kursListeManager().lehrer.list()" :item-text="text" :item-filter="find" />
					<svws-ui-multi-select v-model="filterJahrgaenge" title="Jahrgang" :items="kursListeManager().jahrgaenge.list()" :item-text="text" :item-filter="find" />
					<svws-ui-multi-select v-model="filterSchulgliederung" title="Schulgliederung" :items="kursListeManager().schulgliederungen.list()" :item-text="text_schulgliederung" />
				</template>
				<template #cell(lehrer)="{ value }"> {{ getLehrerKuerzel(value) }} </template>
				<template #cell(idJahrgaenge)="{ value }"> {{ getJahrgangsKuerzel(value) }} </template>
				<template #cell(schueler)="{ value }">{{ value.size() }}</template>
				<!-- TODO: Beim Implementieren des '+'-Buttons zum Hinfügen eines Eintrags die property hasFocus auf die svws-ui-button-Komponente setzen. true, wenn Liste leer, sonst false (z.B. :hasFocus="rowsFiltered.length === 0") -->
			</svws-ui-table>
		</template>
	</svws-ui-secondary-menu>
</template>

<script setup lang="ts">

	import { ref, computed, shallowRef } from "vue";
	import type { KurseAuswahlProps } from "./SKurseAuswahlProps";
	import type { DataTableColumn, SortByAndOrder } from "@ui";
	import type { ArrayList, FachDaten, JahrgangsDaten, KursDaten, LehrerListeEintrag, SchuelerListeEintrag, Schulgliederung } from "@core";

	const props = defineProps<KurseAuswahlProps>();

	const schuljahr = computed<number>(() => props.schuljahresabschnittsauswahl().aktuell.schuljahr);

	const columns: DataTableColumn[] = [
		{ key: "kuerzel", label: "Kürzel", sortable: true, defaultSort: "asc"},
		{ key: "lehrer", label: "Fachlehrer", sortable: true },
		{ key: "idJahrgaenge", label: "JG", tooltip: "Jahrgang", sortable: true, span: 0.5 },
		{ key: "schueler", label: "Schüler", span: 0.5, align: "right" },
	];

	const sortByMulti = computed<Map<string, boolean>>(() => {
		const map = new Map<string, boolean>();
		for (const pair of props.kursListeManager().orderGet())
			if (pair.b !== null)
				map.set(pair.a === "kuerzel" ? "kurse" : pair.a, pair.b);
		return map;
	})

	const sortByAndOrder = computed<SortByAndOrder | undefined>({
		get: () => {
			const list = props.kursListeManager().orderGet();
			if (list.isEmpty())
				return undefined;
			else {
				const { a: key, b: order} = list.get(0);
				return { key, order };
			}
		},
		set: (value) => {
			if ((value === undefined) || (value.key === null))
				return;
			props.kursListeManager().orderUpdate(value.key, value.order);
			void props.setFilter();
		}
	})

	function text(eintrag: LehrerListeEintrag | JahrgangsDaten | FachDaten): string {
		return eintrag.kuerzel ?? "";
	}

	const find = (items: Iterable<LehrerListeEintrag | JahrgangsDaten | FachDaten>, search: string) => {
		const list = [];
		for (const i of items)
			if ((i.kuerzel !== null) && i.kuerzel.toLocaleLowerCase().includes(search.toLocaleLowerCase()))
				list.push(i);
		return list;
	}

	function textSchueler(eintrag: SchuelerListeEintrag): string {
		return eintrag.nachname + ", " + eintrag.vorname;
	}

	const findSchueler = (items: Iterable<SchuelerListeEintrag>, search: string) => {
		const list = [];
		for (const i of items)
			if ((i.nachname.toLocaleLowerCase().includes(search.toLocaleLowerCase())) || (i.vorname.toLocaleLowerCase().includes(search.toLocaleLowerCase())))
				list.push(i);
		return list;
	}

	function text_schulgliederung(schulgliederung: Schulgliederung): string {
		return schulgliederung.daten(schuljahr.value)?.kuerzel ?? '—';
	}

	const filterNurSichtbare = computed<boolean>({
		get: () => props.kursListeManager().filterNurSichtbar(),
		set: (value) => {
			props.kursListeManager().setFilterNurSichtbar(value);
			void props.setFilter();
		}
	});

	const filterSchulgliederung = computed<Schulgliederung[]>({
		get: () => [...props.kursListeManager().schulgliederungen.auswahl()],
		set: (value) => {
			props.kursListeManager().schulgliederungen.auswahlClear();
			for (const v of value)
				props.kursListeManager().schulgliederungen.auswahlAdd(v);
			void props.setFilter();
		}
	});

	const filterJahrgaenge = computed<JahrgangsDaten[]>({
		get: () => [...props.kursListeManager().jahrgaenge.auswahl()],
		set: (value) => {
			props.kursListeManager().jahrgaenge.auswahlClear();
			for (const v of value)
				props.kursListeManager().jahrgaenge.auswahlAdd(v);
			void props.setFilter();
		}
	});

	const filterFaecher = computed<FachDaten[]>({
		get: () => [...props.kursListeManager().faecher.auswahl()],
		set: (value) => {
			props.kursListeManager().faecher.auswahlClear();
			for (const v of value)
				props.kursListeManager().faecher.auswahlAdd(v);
			void props.setFilter();
		}
	});

	const filterLehrer = computed<LehrerListeEintrag[]>({
		get: () => [...props.kursListeManager().lehrer.auswahl()],
		set: (value) => {
			props.kursListeManager().lehrer.auswahlClear();
			for (const v of value)
				props.kursListeManager().lehrer.auswahlAdd(v);
			void props.setFilter();
		}
	});

	const filterSchueler = computed<SchuelerListeEintrag[]>({
		get: () => [...props.kursListeManager().schueler.auswahl()],
		set: (value) => {
			props.kursListeManager().schueler.auswahlClear();
			for (const v of value)
				props.kursListeManager().schueler.auswahlAdd(v);
			void props.setFilter();
		}
	});


	const search = ref("");

	const rowsFiltered = computed<KursDaten[]>(() => {
		const arr = [];
		for (const e of props.kursListeManager().filtered())
			if (e.kuerzel.toLocaleLowerCase().includes(search.value.toLocaleLowerCase()))
				arr.push(e);
		return arr;
	});


	async function filterReset() {
		props.kursListeManager().schulgliederungen.auswahlClear();
		props.kursListeManager().lehrer.auswahlClear();
		props.kursListeManager().schueler.auswahlClear();
		props.kursListeManager().jahrgaenge.auswahlClear();
		props.kursListeManager().setFilterNurSichtbar(true);
		await props.setFilter();
	}

	function filterChanged(): boolean {
		return (props.kursListeManager().schulgliederungen.auswahlExists()
			|| props.kursListeManager().lehrer.auswahlExists()
			|| props.kursListeManager().schueler.auswahlExists()
			|| props.kursListeManager().jahrgaenge.auswahlExists());
	}

	const selectedItems = shallowRef<KursDaten[]>([]);

	function setAuswahl(items : KursDaten[]) {
		const auswahl = props.kursListeManager().liste;
		for (const vorhanden of auswahl.auswahl())
			if (!items.includes(vorhanden))
				auswahl.auswahlRemove(vorhanden);
		for (const item of items)
			auswahl.auswahlAdd(item);
		selectedItems.value = [ ... auswahl.auswahl() ];
	}


	// TODO komma-separierte Liste mit Zusatzkräften
	function getLehrerKuerzel(idLehrer: number) {
		const lehrer = props.kursListeManager().lehrer.get(idLehrer);
		if (lehrer === null)
			return "---";
		return lehrer.kuerzel;
	}


	/**
	 * Ermittel eine komma-separierte Liste der Kürzel der Jahrgänge mit den übergebenen IDs.
	 *
	 * @param jahrgaenge   die Liste von Jahrgangs-IDs
	 */
	function getJahrgangsKuerzel(jahrgaenge: ArrayList<number>) : string {
		// Prüfe zunächst, ob die Liste der Jahrgänge von dem Kurs einen Jahrgang der Map beinhaltet.
		let found = false;
		let result = "";
		for (const jg of jahrgaenge) {
			const jahrgang = props.kursListeManager().jahrgaenge.get(jg);
			if ((jahrgang !== null) && (jahrgang.kuerzel !== null)) {
				if (found)
					result += ",";
				result += jahrgang.kuerzel;
				found = true;
			}
		}
		return result;
	}

</script>
