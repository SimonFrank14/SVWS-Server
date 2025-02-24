<template>
	<div v-if="jahrgangsdaten() !== undefined" class="page--content page--content--full page--content--gost-beratung">
		<Teleport to=".svws-sub-nav-target" v-if="isMounted && hatUpdateKompetenz">
			<svws-ui-sub-nav>
				<svws-ui-button :type="modus === 'normal' ? 'transparent' : 'danger'" @click="switchModus" title="Modus wechseln">
					<span class="icon-sm i-ri-loop-right-line" />
					Modus: <span>{{ modus }}</span>
				</svws-ui-button>
				<s-modal-laufbahnplanung-kurswahlen-loeschen :gost-jahrgangsdaten="jahrgangsdaten()" :reset-fachwahlen />
				<s-modal-laufbahnplanung-alle-fachwahlen-loeschen v-if="jahrgangsdaten().abiturjahr !== -1" :gost-jahrgangsdaten="jahrgangsdaten" :reset-fachwahlen="resetFachwahlenAlle" />
			</svws-ui-sub-nav>
		</Teleport>
		<Teleport to=".svws-ui-header--actions" v-if="isMounted">
			<svws-ui-modal-hilfe> <hilfe-gost-beratung /> </svws-ui-modal-hilfe>
		</Teleport>
		<div class="flex-grow overflow-y-auto overflow-x-hidden min-w-fit">
			<s-laufbahnplanung-card-planung title="Vorlage für Schüler des Abiturjahrgangs" :goto-kursblockung :hat-update-kompetenz
				:abiturdaten-manager :modus :faecher-anzeigen="'alle'" :gost-jahrgangsdaten="jahrgangsdaten()" :set-wahl ignoriere-sprachenfolge />
		</div>
		<div class="overflow-y-auto overflow-x-hidden flex flex-col gap-y-8 lg:gap-y-12 scrollbar-thin pr-4">
			<svws-ui-content-card v-if="istAbiturjahrgang" title="Beratungslehrer" class="m-0">
				<svws-ui-table :items="beratungslehrer()" :selectable="hatUpdateKompetenz" :model-value="selected" @update:model-value="selected=$event" count :columns="[{key: 'kuerzel', label: 'Kürzel', span: 0.25}, {key: 'name', label: 'Name'}]">
					<template #cell(name)="{ rowData: l }">
						{{ `${l.nachname}, ${l.vorname}` }}
					</template>
					<template #actions v-if="hatUpdateKompetenz">
						<svws-ui-select :model-value="undefined" @update:model-value="lehrer => lehrer && addBeratungslehrer(lehrer.id)" headless indeterminate
							autocomplete :item-filter="lehrer_filter" :items="lehrer" removable title="Lehrkraft hinzufügen…" :item-text="l=> `${l.nachname}, ${l.vorname} (${l.kuerzel})`" />
						<svws-ui-button @click="removeBeratungslehrer(selected)" type="trash" :disabled="!selected.length" />
					</template>
				</svws-ui-table>
			</svws-ui-content-card>
			<svws-ui-content-card title="Textvorlagen" class="m-0">
				<svws-ui-input-wrapper>
					<svws-ui-textarea-input :disabled="!hatUpdateKompetenz" placeholder="Beratungsbögen" :model-value="jahrgangsdaten().textBeratungsbogen"
						@change="textBeratungsbogen => patchJahrgangsdaten({ textBeratungsbogen }, props.jahrgangsdaten().abiturjahr)" resizeable="vertical" autoresize />
					<svws-ui-textarea-input :disabled="!hatUpdateKompetenz" placeholder="Mailversand" :model-value="jahrgangsdaten().textMailversand"
						@change="textMailversand => patchJahrgangsdaten({ textMailversand }, props.jahrgangsdaten().abiturjahr)" resizeable="vertical" autoresize />
				</svws-ui-input-wrapper>
			</svws-ui-content-card>
			<s-laufbahnplanung-card-status :abiturdaten-manager :fehlerliste="() => gostBelegpruefungErgebnis().fehlercodes" :gost-belegpruefungs-art :set-gost-belegpruefungs-art />
		</div>
	</div>
</template>

<script setup lang="ts">

	import type { GostBeratungProps } from "./SGostBeratungProps";
	import { BenutzerKompetenz, BenutzerTyp, type GostBeratungslehrer, type LehrerListeEintrag } from "@core";
	import { onMounted, computed, ref } from "vue";
	import { lehrer_filter } from '~/utils/helfer';

	const props = defineProps<GostBeratungProps>();

	const selected = ref<GostBeratungslehrer[]>([]);

	const hatUpdateKompetenz = computed<boolean>(() => {
		let beratungslehrer = false;
		for (const b of props.beratungslehrer())
			if (b.id === props.benutzerdaten.id) {
				beratungslehrer = true;
				break;
			}
		return props.benutzerKompetenzen.has(BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_ALLGEMEIN)
			|| (props.benutzerKompetenzen.has(BenutzerKompetenz.OBERSTUFE_LAUFBAHNPLANUNG_FUNKTIONSBEZOGEN)
				&& props.benutzerdaten.typ === BenutzerTyp.LEHRER.id && beratungslehrer)
	});

	const istAbiturjahrgang = computed<boolean>(() => (props.jahrgangsdaten().abiturjahr > 0));

	const modus = ref<'manuell' | 'normal' | 'hochschreiben'>('normal');

	function switchModus() {
		switch (modus.value) {
			case 'normal':
				modus.value = 'hochschreiben';
				break;
			case 'hochschreiben':
				modus.value = 'manuell';
				break;
			case 'manuell':
				modus.value = 'normal';
				break;
		}
	}

	const lehrer = computed<Map<number, LehrerListeEintrag>>(() => {
		const map = new Map<number, LehrerListeEintrag>(props.mapLehrer);
		for (const l of props.beratungslehrer())
			map.delete(l.id);
		return map;
	})

	// Check if component is mounted
	const isMounted = ref(false);
	onMounted(() => isMounted.value = true);

</script>

<style lang="postcss" scoped>

	.page--content {
		@apply grid overflow-y-hidden overflow-x-auto h-full pb-3 pt-6 lg:gap-x-12;
		grid-auto-rows: 100%;
		grid-template-columns: 1fr minmax(36rem, 1fr);
		grid-auto-columns: max-content;
	}

	.page--content--gost-beratung {
		@apply gap-x-8 2xl:gap-x-12 relative overflow-y-hidden h-full;
		@apply px-4 lg:px-6 3xl:px-8 4xl:px-12 pt-8 pb-8;
	}

	.scrollbar-thin {
		scrollbar-gutter: stable;
		scrollbar-width: thin;
		scrollbar-color: rgba(0,0,0,0.2) transparent;
	}

</style>
