<template>
	<svws-ui-table :columns :items="personaldaten.fachrichtungen" :selectable="hatUpdateKompetenz" :model-value="selected" @update:model-value="selected=$event" count>
		<template #cell(fachrichtung)="{ rowData }">
			{{ getFachrichtung(rowData).daten(schuljahr)?.text ?? '—' }}
		</template>
		<template #cell(anerkennung)="{ rowData }">
			<svws-ui-select title="Anerkennungsgrund Fachrichtung" v-if="hatUpdateKompetenz" :model-value="getFachrichtungAnerkennung(rowData)"
				@update:model-value="anerkennung => patchFachrichtungAnerkennung(rowData, anerkennung ?? null)"
				:items="LehrerFachrichtungAnerkennung.values()" :item-text="i => i.daten(schuljahr)?.text ?? '—'" headless />
			<div v-else> {{ getFachrichtungAnerkennung(rowData)?.daten(schuljahr)?.text ?? '—' }} </div>
		</template>
		<template #actions v-if="hatUpdateKompetenz">
			<svws-ui-button @click="removeFachrichtungen(Arrays.asList(selected))" type="trash" :disabled="selected.length <= 0" />
			<svws-ui-button @click="showModal().value = true" type="icon" title="Fachrichtung hinzufügen"> <span class="icon i-ri-add-line" /> </svws-ui-button>
		</template>
	</svws-ui-table>
	<s-lehrer-personaldaten-fachrichtungen-modal-add v-if="hatUpdateKompetenz" :show="showModal" :id-lehrer="personaldaten.id" :add-fachrichtung :schuljahr />
</template>

<script setup lang="ts">

	import { computed, ref } from "vue";
	import type { List, LehrerFachrichtungEintrag, LehrerListeManager, LehrerPersonaldaten} from "@core";
	import { DeveloperNotificationException, LehrerFachrichtung, LehrerFachrichtungAnerkennung, Arrays } from "@core";

	const props = defineProps<{
		hatUpdateKompetenz: boolean;
		lehrerListeManager: () => LehrerListeManager;
		patchFachrichtungAnerkennung: (eintrag: LehrerFachrichtungEintrag, anerkennung : LehrerFachrichtungAnerkennung | null) => Promise<void>;
		addFachrichtung: (eintrag: LehrerFachrichtungEintrag) => Promise<void>;
		removeFachrichtungen: (eintraege: List<LehrerFachrichtungEintrag>) => Promise<void>;
		schuljahr: number;
	}>();

	const personaldaten = computed<LehrerPersonaldaten>(() => props.lehrerListeManager().personalDaten());

	const _modal = ref<boolean>(false);
	const showModal = () => _modal;

	const selected = ref<LehrerFachrichtungEintrag[]>([]);

	const columns = [
		{key: 'fachrichtung', label: 'Fachrichtung', span: 1 },
		{key: 'anerkennung', label: 'Anerkennungsgrund', span: 1 }
	]

	function getFachrichtung(eintrag: LehrerFachrichtungEintrag) : LehrerFachrichtung {
		const fachrichtung = LehrerFachrichtung.data().getWertByID(eintrag.idFachrichtung);
		if (fachrichtung === null)
			throw new DeveloperNotificationException("Die ID der Fachrichtung ist unzulässig. Die vorhandenen Daten sind inkonsistent.");
		return fachrichtung;
	}

	function getFachrichtungAnerkennung(eintrag: LehrerFachrichtungEintrag) : LehrerFachrichtungAnerkennung | null {
		return (eintrag.idAnerkennungsgrund === null ? null : LehrerFachrichtungAnerkennung.data().getWertByID(eintrag.idAnerkennungsgrund));
	}

</script>
