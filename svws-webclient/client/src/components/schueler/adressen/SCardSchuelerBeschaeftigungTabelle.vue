<template>
	<div class="svws-ui-td" role="cell">
		<svws-ui-select title="—" headless v-model="inputBetrieb" :items="mapBetriebe" :item-text="(i: BetriebListeEintrag) => i.name1 ?? ''" />
	</div>
	<div class="svws-ui-td" role="cell">
		<svws-ui-select v-model="beschaeftigungsart" :items="mapBeschaeftigungsarten" :item-text="(i: KatalogEintrag) => i.text ?? ''" headless title="—" />
	</div>
	<div class="svws-ui-td" role="cell">
		<svws-ui-text-input :model-value="betrieb.vertragsbeginn" @change="vertragsbeginn=>patchSchuelerBetriebsdaten({vertragsbeginn}, betrieb.id)" type="date" />
	</div>
	<div class="svws-ui-td" role="cell">
		<svws-ui-text-input :model-value="betrieb.vertragsende" @change="vertragsende=>patchSchuelerBetriebsdaten({vertragsende}, betrieb.id)" type="date" />
	</div>
	<div class="svws-ui-td svws-align-center" role="cell">
		<svws-ui-checkbox v-model="praktikum" />
	</div>
	<div class="svws-ui-td" role="cell">
		<svws-ui-select v-model="inputBetreuungslehrer" :items="mapLehrer" :item-text="(i: LehrerListeEintrag) => i.nachname" headless title="—" />
	</div>
	<div class="svws-ui-td" role="cell">
		<svws-ui-select :model-value="betrieb.ansprechpartner_id === null ? undefined : mapAnsprechpartner.get(betrieb.ansprechpartner_id)"
			@change="(ansprechPartner : BetriebAnsprechpartner) => patchSchuelerBetriebsdaten({ ansprechpartner_id: ansprechPartner === undefined ? null : ansprechPartner.id }, betrieb.id)"
			:items="mapAnsprechpartner"
			:item-text="(i: BetriebAnsprechpartner) => i.name || ''" headless title="—" />
	</div>
	<div class="svws-ui-td" role="cell">
		<svws-ui-text-input :model-value="betrieb.ausbilder" @change="ausbilder=>patchSchuelerBetriebsdaten({ausbilder}, betrieb.id)" type="text" placeholder="—" headless />
	</div>
	<div class="svws-ui-td svws-align-center" role="cell">
		<svws-ui-checkbox v-model="anschreiben" />
	</div>
</template>

<script setup lang="ts">

	import { computed } from "vue";
	import type { BetriebAnsprechpartner, BetriebListeEintrag, KatalogEintrag, LehrerListeEintrag, SchuelerBetriebsdaten } from "@core";

	const props = defineProps<{
		patchSchuelerBetriebsdaten: (data : Partial<SchuelerBetriebsdaten>, id : number) => Promise<void>;
		betrieb: SchuelerBetriebsdaten;
		mapBeschaeftigungsarten: Map<number, KatalogEintrag>;
		mapLehrer: Map<number, LehrerListeEintrag>;
		mapBetriebe: Map<number, BetriebListeEintrag>;
		mapAnsprechpartner: Map<number, BetriebAnsprechpartner>;
	}>();

	const inputBetreuungslehrer = computed<LehrerListeEintrag | undefined>({
		get: () => props.betrieb.betreuungslehrer_id === null ? undefined : props.mapLehrer.get(props.betrieb.betreuungslehrer_id),
		set: (value) => void props.patchSchuelerBetriebsdaten({ betreuungslehrer_id: value === undefined ? null : value.id }, props.betrieb.id)
	});

	const inputBetrieb = computed<BetriebListeEintrag | undefined>({
		get: () => props.mapBetriebe.get(props.betrieb.betrieb_id),
		set: (value) => {
			if (value !== undefined)
				void props.patchSchuelerBetriebsdaten({ betrieb_id: value.id , ansprechpartner_id: null }, props.betrieb.id);
		}
	});

	const beschaeftigungsart = computed<KatalogEintrag | undefined>({
		get: () => (props.betrieb.beschaeftigungsart_id === null) ? undefined : props.mapBeschaeftigungsarten.get(props.betrieb.beschaeftigungsart_id),
		set: (value) => void props.patchSchuelerBetriebsdaten({ beschaeftigungsart_id: value === undefined ? null : value.id }, props.betrieb.id)
	});

	const praktikum = computed<boolean>({
		get: () => props.betrieb.praktikum,
		set: (value) => void props.patchSchuelerBetriebsdaten({ praktikum: value }, props.betrieb.id)
	});

	const anschreiben = computed<boolean>({
		get: () => props.betrieb.allgadranschreiben,
		set: (value) => void props.patchSchuelerBetriebsdaten({ allgadranschreiben: value }, props.betrieb.id)
	});

	const ansprechpartner = computed<BetriebAnsprechpartner | undefined>({
		get: () => props.betrieb.ansprechpartner_id === null ? undefined : props.mapAnsprechpartner.get(props.betrieb.ansprechpartner_id),
		set: (value) => {
			void props.patchSchuelerBetriebsdaten({ ansprechpartner_id: value === undefined ? null : value.id }, props.betrieb.id);
		}
	});

</script>

