<template>
	<svws-ui-secondary-menu>
		<template #headline>
			<span>Betriebe</span>
		</template>
		<template #abschnitt>
			<abschnitt-auswahl :daten="schuljahresabschnittsauswahl" />
		</template>
		<template #header />
		<template #content>
			<svws-ui-table :clicked="auswahl" @update:clicked="gotoEintrag" :items="rowsFiltered" :columns clickable scroll-into-view selectable :model-value="selected" @update:model-value="selected=$event" count>
				<template #search>
					<svws-ui-text-input v-model="search" type="search" placeholder="Suchen" removable />
				</template>
				<template #actions>
					<svws-ui-button @click="doDeleteEintraege()" type="trash" :disabled="selected.length === 0" />
					<s-betriebe-neu-modal v-slot="{ openModal }" :add-eintrag :delete-eintraege="doDeleteEintraege" :map-beschaeftigungsarten :map-orte :map-ortsteile>
						<svws-ui-button type="icon" @click="openModal()" :has-focus="rowsFiltered.length === 0">
							<span class="icon i-ri-add-line" />
						</svws-ui-button>
					</s-betriebe-neu-modal>
				</template>
			</svws-ui-table>
		</template>
	</svws-ui-secondary-menu>
</template>


<script setup lang="ts">

	import { computed, ref } from "vue";
	import type { BetriebeAuswahlProps } from "./SBetriebeAuswahlProps";
	import type { BetriebListeEintrag } from "@core";

	const props = defineProps<BetriebeAuswahlProps>();
	const search = ref("");
	const selected = ref<BetriebListeEintrag[]>([]);

	const columns = [
		{ key: "name1", label: "Name", sortable: true, span: 2 },
		{ key: "id", label: "ID", sortable: true, span: 0.5 },
	]

	async function doDeleteEintraege() {
		await props.deleteEintraege(selected.value);
		selected.value = [];
	}

	const rowsFiltered = computed<BetriebListeEintrag[]>(() => {
		const res = [];
		for(const k of props.mapKatalogeintraege.values())
			if((k.name1 !== null) && k.name1.toLocaleLowerCase().includes(search.value.toLocaleLowerCase()))
				res.push(k);
		return res;
	})
</script>
