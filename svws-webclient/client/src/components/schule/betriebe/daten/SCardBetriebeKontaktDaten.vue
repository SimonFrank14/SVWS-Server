<template>
	<svws-ui-content-card title="Adresse">
		<svws-ui-input-wrapper :grid="2">
			<svws-ui-text-input placeholder="Straße / Hausnummer" :model-value="daten.strassenname" @change="strassenname=>patch({strassenname: strassenname ?? undefined})" type="text" />
			<svws-ui-text-input placeholder="Zusatz" :model-value="daten.hausnrzusatz" @change="hausnrzusatz=>patch({hausnrzusatz: hausnrzusatz ?? undefined})" type="text" />
			<svws-ui-select title="Wohnort" v-model="inputWohnortID" :items="mapOrte" :item-text="i => `${i.plz} ${i.ortsname}`" autocomplete class="col-span-full" />
			<!-- <svws-ui-select title="Ortsteil" v-model="inputOrtsteilID" :items="mapOrtsteile" :item-text="(i: OrtsteilKatalogEintrag) => i.ortsteil ?? ''"
            :item-sort="ortsteilSort" :item-filter="ortsteilFilter" /> -->
			<svws-ui-spacing />
			<svws-ui-text-input placeholder="Telefon" :model-value="daten.telefon1" @change="telefon1=>patch({telefon1: telefon1 ?? undefined})" type="text" />
			<svws-ui-text-input placeholder="2. Telefon" :model-value="daten.telefon2" @change="telefon2=>patch({telefon2: telefon2 ?? undefined})" type="text" />
			<svws-ui-text-input placeholder="E-Mail Adresse" :model-value="daten.email" @change="email=>patch({email: email ?? undefined})" type="email" verify-email />
			<svws-ui-text-input placeholder="Fax" :model-value="daten.fax" @change="fax=>patch({fax: fax ?? undefined})" type="text" />
		</svws-ui-input-wrapper>
	</svws-ui-content-card>
</template>

<script setup lang="ts">
	import type { BetriebStammdaten, OrtKatalogEintrag } from "@core";
	import { computed } from "vue";

	const props = defineProps<{
		daten: BetriebStammdaten;
		mapOrte: Map<number, OrtKatalogEintrag>;
		patch: (data : Partial<BetriebStammdaten>) => Promise<void>;
	}>();

	const inputWohnortID = computed<OrtKatalogEintrag | null>({
		get: () => props.daten.ort_id !== null ? props.mapOrte.get(props.daten.ort_id) ?? null : null,
		set: (val) =>	void props.patch({ ort_id : val?.id })
	});
</script>
