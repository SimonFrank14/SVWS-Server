<template>
	<div :id="'gost-klausurtermin-' + termin.id" class="svws-ui-termin h-full flex flex-col group bg-white dark:bg-black rounded-lg"
		:class="{
			'border shadow-md shadow-black/5': !inTooltip,
			'border-black/10 dark:border-white/10': !inTooltip && !terminSelected,
			'-mx-2 -my-1': inTooltip,
			'border-r-2 border-r-svws border-l-transparent': inTooltip && !termin.istHaupttermin,
			'border-black/50 dark:border-white/50 ring-4 ring-black/10 dark:ring-white/10': termin.istHaupttermin && !inTooltip && terminSelected,
			'border-svws/50 dark:border-svws/50 ring-4 ring-svws/10 dark:ring-svws/10': !termin.istHaupttermin && !inTooltip && terminSelected,
			'border-l-svws border-l-2': !termin.istHaupttermin,
		}">
		<slot name="header">
			<section class="text-headline-md leading-none px-3 pt-3" :class="{'pb-2': !$slots.tableTitle}">
				<template v-if="!$slots.tableTitle">
					<slot name="title">
						<span class="leading-tight inline-flex gap-0.5" :class="{'text-base': compact || compactWithDate}">
							<span v-if="dragIcon && !compact" class="group-hover:bg-black/10 dark:group-hover:bg-white/10 -ml-1 mr-0.5 rounded">
								<span class="icon i-ri-draggable" :class="{'-mx-0.5': !compact}" />
							</span>
							<span class="line-clamp-1 break-all">{{ terminBezeichnung() }}</span>
						</span>
						<div v-if="compactWithDate && termin.datum" class="mb-1 -mt-0.5 opacity-50 text-base">{{ DateUtils.gibDatumGermanFormat(termin.datum) }}</div>
						<div v-if="compact || compactWithDate" class="svws-compact-data text-sm font-medium flex flex-wrap mt-0.5">
							<span class="font-bold">{{ kMan().schuelerklausurterminAktuellGetMengeByTermin(termin).size() }} Schüler,&nbsp;</span>
							<span><span v-if="kMan().minKlausurdauerGetByTermin(termin, true) < kMan().maxKlausurdauerGetByTermin(termin, true)">{{ kMan().minKlausurdauerGetByTermin(termin, true) }} - </span>{{ kMan().maxKlausurdauerGetByTermin(termin, true) }} Minuten</span>

							<span v-if="quartalsauswahl && quartalsauswahl.value === 0">, {{ termin.quartal ? termin.quartal + ' . Quartal' : 'Beide Quartale' }}</span>
						</div>
					</slot>
				</template>
				<div class="flex justify-between w-full gap-1 items-center">
					<div v-if="!compact && !compactWithDate">
						<slot name="datum">
							<template v-if="termin.datum === null">
								<span class="opacity-25 inline-flex items-center gap-1">
									<span class="icon i-ri-calendar-2-line" />
									<svws-ui-button type="transparent" :disabled="!hatKompetenzUpdate" @click="gotoKalenderdatum( termin )" :title="`Datum setzen`" size="small"><span class="icon i-ri-link" /> Datum setzen</svws-ui-button>
								</span>
							</template>
							<template v-else>
								<span class="opacity-50 inline-flex items-center gap-1">
									<span>{{ DateUtils.gibDatumGermanFormat(termin.datum) }}</span>
									<svws-ui-button v-if="!hideButtonRaeumePlanen" :disabled="!hatKompetenzUpdate" type="transparent" @click="gotoRaumzeitTermin(termin.abijahr, GostHalbjahr.fromIDorException(termin.halbjahr), termin.id)" :title="`Räume planen`" size="small"><span class="icon i-ri-link" /> Räume planen</svws-ui-button>
								</span>
							</template>
						</slot>
					</div>
					<div v-if="$slots.actions" class="flex gap-0.5 items-center -mr-2 -my-1">
						<slot name="actions" />
					</div>
				</div>
			</section>
		</slot>
		<slot name="main" v-if="!compact">
			<section class="flex flex-col flex-grow" :class="{'mt-2': !$slots.tableTitle, 'px-3': !inTooltip}">
				<slot name="klausuren">
					<div v-if="kursklausuren().size() === 0 && (schuelerklausurtermine().size() === 0)">
						Keine Klausuren
					</div>
					<slot name="kursklausuren" v-if="kursklausuren().size()">
						<svws-ui-table :columns="cols" :disable-header="!$slots.tableTitle" :class="{'border-t border-black/25 dark:border-white/25': !$slots.tableTitle}">
							<template #header>
								<div class="svws-ui-tr" role="row">
									<div class="svws-ui-td col-span-full" role="columnheader">
										<slot name="tableTitle" />
									</div>
								</div>
							</template>
							<template #body>
								<div v-for="klausur in kursklausuren()"
									:key="klausur.id"
									:data="klausur"
									:draggable="onDrag !== undefined && draggable(klausur, termin)"
									@dragstart="onDrag && onDrag(klausur);$event.stopPropagation()"
									@dragend="onDrag && onDrag(undefined);$event.stopPropagation()"
									class="svws-ui-tr" role="row"
									:class="[
										props.klausurCssClasses === undefined ? '' : props.klausurCssClasses(klausur, termin),
										{
											'cursor-grab active:cursor-grabbing group': onDrag !== undefined && (draggable === undefined || draggable(klausur, termin))
										}
									]">
									<div class="svws-ui-td" role="cell">
										<span class="icon i-ri-draggable -m-0.5 -ml-3" v-if="onDrag !== undefined && (draggable === undefined || draggable(klausur, termin))" />
									</div>
									<div class="svws-ui-td" :class="{'-ml-2': inTooltip}" role="cell">
										{{ GostHalbjahr.fromIDorException(kMan().vorgabeByKursklausur(klausur).halbjahr).jahrgang }}
									</div>
									<div class="svws-ui-td" role="cell">
										<svws-ui-tooltip :hover="false" :indicator="false" :keep-open>
											<template #content>
												<s-gost-klausurplanung-kursliste :k-man :kursklausur="klausur" :termin :patch-klausur :create-schuelerklausur-termin @modal="keepOpen = $event" />
											</template>
											<span class="svws-ui-badge hover:opacity-75" :style="`--background-color: ${ kMan().fachHTMLFarbeRgbaByKursklausur(klausur) };`">{{ kMan().kursKurzbezeichnungByKursklausur(klausur) }}</span>
										</svws-ui-tooltip>
									</div>
									<div class="svws-ui-td" role="cell">{{ kMan().kursLehrerKuerzelByKursklausur(klausur) }}</div>
									<div class="svws-ui-td flex" role="cell">
										<div>
											<span v-if="kMan().schuelerklausurterminAktuellGetMengeByTerminAndKursklausur(termin, klausur).size() !== kMan().kursAnzahlKlausurschreiberByKursklausur(klausur)" class="font-bold">{{ kMan().schuelerklausurterminAktuellGetMengeByTerminAndKursklausur(termin, klausur).size() }}/</span>
											<span :class="kMan().schuelerklausurterminAktuellGetMengeByTerminAndKursklausur(termin, klausur).size() !== kMan().kursAnzahlKlausurschreiberByKursklausur(klausur) ? 'line-through' : ''">{{ kMan().kursAnzahlKlausurschreiberByKursklausur(klausur) }}/</span>
											<span class="">{{ kMan().kursAnzahlSchuelerGesamtByKursklausur(klausur) }}</span>
										</div>
										<svws-ui-tooltip :hover="true" :indicator="false">
											<template #content>
												Kurs enthält externe Schüler
											</template>
											<svws-ui-badge v-if="kMan().kursklausurMitExternenS(klausur)" type="highlight" size="normal">E</svws-ui-badge>
										</svws-ui-tooltip>
									</div>
									<div class="svws-ui-td svws-align-right" :class="{'pr-3': inTooltip}" role="cell">{{ kMan().vorgabeByKursklausur(klausur).dauer }}</div>
									<div v-if="showKursschiene === true" class="svws-ui-td svws-align-right"><span class="opacity-50">{{ kMan().kursSchieneByKursklausur(klausur).isEmpty() ? "-" : kMan().kursSchieneByKursklausur(klausur).get(0) }}</span></div>
									<div v-if="kMan().quartalGetByTermin(termin) === -1" class="svws-ui-td svws-align-right" role="cell"><span class="opacity-50">{{ kMan().vorgabeByKursklausur(klausur).quartal }}.</span></div>
									<div v-if="showLastKlausurtermin === true" class="svws-ui-td svws-align-right" role="cell"><span class="opacity-50">{{ datumVorklausur(klausur) }}</span></div>
								</div>
							</template>
						</svws-ui-table>
					</slot>
					<slot name="schuelerklausuren" v-if="showSchuelerklausuren && schuelerklausurtermine().size()">
						<s-gost-klausurplanung-schuelerklausur-table :schuelerklausuren="schuelerklausurtermine()"
							:k-man
							:termin
							:on-drag
							:draggable
							:patch-klausur
							:klausur-css-classes />
					</slot>
					<!--<div v-else-if="schuelerklausurtermine().size()">
						{{ schuelerklausurtermine().size() }} Nachschreibklausuren
					</div>-->
					<span class="flex w-full justify-between items-center gap-1 text-sm mt-auto pr-2" :class="{'pl-3': inTooltip}">
						<div class="py-3" :class="{'opacity-50': !kursklausuren().size() && (showSchuelerklausuren && !schuelerklausurtermine().size())}">
							<span class="font-bold">{{ kMan().schuelerklausurterminAktuellGetMengeByTermin(termin).size() }} Schüler, </span>
							<span><span v-if="kMan().minKlausurdauerGetByTermin(termin, true) < kMan().maxKlausurdauerGetByTermin(termin, true)">{{ kMan().minKlausurdauerGetByTermin(termin, true) }} - </span>{{ kMan().maxKlausurdauerGetByTermin(termin, true) }} Minuten</span>
						</div>
						<slot name="loeschen" />
					</span>
				</slot>
			</section>
		</slot>
	</div>
</template>

<script setup lang="ts">

	import { computed, ref } from "vue";
	import type {DataTableColumn} from "@ui";
	import type { GostKlausurplanungDragData } from "./SGostKlausurplanung";
	import type { GostKlausurplanManager, GostKursklausur, GostKlausurtermin, GostSchuelerklausurTermin, GostKlausurenCollectionSkrsKrsData} from "@core";
	import { GostHalbjahr, BenutzerKompetenz, DateUtils } from "@core";

	const props = withDefaults(defineProps<{
		benutzerKompetenzen: Set<BenutzerKompetenz>,
		termin: GostKlausurtermin;
		kMan: () => GostKlausurplanManager;
		klausurCssClasses?: (klausur: GostKlausurplanungDragData, termin: GostKlausurtermin | undefined) => void;
		onDrag?: (data: GostKlausurplanungDragData) => void;
		draggable?: (data: GostKlausurplanungDragData, termin: GostKlausurtermin) => boolean;
		//onDrop?: (zone: GostKlausurplanungDropZone) => void;
		compact?: boolean;
		compactWithDate?: boolean;
		quartalsauswahl?: {value: number};
		dragIcon?: boolean;
		terminSelected?: boolean;
		showKursschiene? : boolean;
		showLastKlausurtermin? : boolean;
		showSchuelerklausuren?: boolean;
		showKursklausurenNachschreiber?: boolean;
		showKlausurenSelbesDatum?: boolean;
		hideButtonRaeumePlanen?: boolean;
		createSchuelerklausurTermin?: (id: number) => Promise<void>;
		patchKlausur?: (klausur: GostKursklausur | GostSchuelerklausurTermin, patch: Partial<GostKursklausur | GostSchuelerklausurTermin>) => Promise<GostKlausurenCollectionSkrsKrsData>;
		inTooltip?: boolean;
		gotoKalenderdatum: (goto: string | GostKlausurtermin) => Promise<void>;
		gotoRaumzeitTermin: (abiturjahr: number, halbjahr: GostHalbjahr, value: number) => Promise<void>;
	}>(), {
		klausurCssClasses: undefined,
		onDrag: undefined,
		draggable: () => false,
		//onDrop: undefined,
		quartalsauswahl: undefined,
		showSchuelerklausuren: false,
		createSchuelerklausurTermin: undefined,
		patchKlausur: undefined,
		showKlausurenSelbesDatum: false,
		showKursklausurenNachschreiber: false,
		hideButtonRaeumePlanen: false,
		inTooltip: false,
	});

	const keepOpen = ref<boolean>(false);
	const hatKompetenzUpdate = computed<boolean>(() => props.benutzerKompetenzen.has(BenutzerKompetenz.OBERSTUFE_KLAUSURPLANUNG_AENDERN));

	const kursklausuren = () => props.kMan().kursklausurMitNachschreibernGetMengeByTermin(props.termin, props.showKursklausurenNachschreiber);
	const schuelerklausurtermine = () => props.kMan().schuelerklausurterminNtGetMengeByTermin(props.termin);

	const datumVorklausur = (klausur: GostKursklausur) => {
		const vorklausur = props.kMan().kursklausurVorterminByKursklausur(klausur);
		if (vorklausur === null)
			return "-";
		const termin = props.kMan().terminOrNullByKursklausur(vorklausur);
		return termin === null || termin.datum === null ? "-" : DateUtils.gibDatumGermanFormat(termin.datum).substring(0,6);
	};

	const terminBezeichnung = () => {
		if (props.termin.bezeichnung !== null && props.termin.bezeichnung.length > 0)
			return props.termin.bezeichnung;
		if (!props.termin.istHaupttermin)
			return "Nachschreibtermin";
		if (kursklausuren().size() > 0)
			return [...props.kMan().kursklausurGetMengeByTermin(props.termin)].map(k => props.kMan().kursKurzbezeichnungByKursklausur(k)).join(", ")
		return "Klausurtermin";
	}

	function calculateColumns() {
		const cols: DataTableColumn[] = [
			{ key: "dragHandle", label: " ", fixedWidth: 1 },
			{ key: "jgst", label: "Jgst.", fixedWidth: 2 },
			{ key: "kurs", label: "Kurs", span: 1.25 },
			{ key: "kuerzel", label: "Lehrkraft" },
			{ key: "schriftlich", label: "Schriftlich", span: 0.5, align: "right", minWidth: 4.5 },
			{ key: "dauer", label: "Dauer", tooltip: "Dauer in Minuten", span: 0.5, align: "right", minWidth: 3.25 },
		];

		if (props.showKursschiene === true) {
			cols.push({ key: "kursSchiene", label: "S", tooltip: "Schiene", span: 0.25, align: "right", minWidth: 1.75 })
		}

		if (props.kMan().quartalGetByTermin(props.termin) === -1) {
			cols.push({ key: "quartal", label: "Q", tooltip: "Quartal", span: 0.25, align: "center", minWidth: 1.75 })
		}

		if (props.showLastKlausurtermin === true) {
			cols.push({ key: "lastDate", label: "Vordatum", tooltip: "Datum der letzten Klausur", span: 0.25, align: "center", minWidth: 4.75 })
		}

		return cols;
	}

	const cols = computed(() => calculateColumns());

</script>

<style lang="postcss" scoped>
.svws-warning {
  .i-ri-draggable {
    @apply opacity-10;
  }

  &:hover {
    .i-ri-draggable {
      @apply opacity-100 text-black dark:text-white;
    }
  }
}
</style>

<style lang="postcss">
.svws-ui-termin {
  .text-input--headless {
    @apply text-headline-md;

    &:not(:focus) {
      &::placeholder {
        @apply text-black dark:text-white;
      }
    }

    &::placeholder {
      @apply font-bold;
    }
  }

  .svws-klausurplanung-schienen-termin & {
	@apply border-0 rounded-xl;

	}

  .svws-selected & {
    .text-input--headless {
      &:not(:focus) {
        &::placeholder {
          @apply text-svws dark:text-svws;
        }
      }

      &:focus {
        &::placeholder {
          @apply text-svws/50 dark:text-svws/50;
        }
      }
    }
  }

}

.svws-ui-stundenplan--unterricht .svws-ui-termin {
  @apply z-10;

  .px-3 {
    @apply my-auto;
    padding: 0 0.25rem;
  }

  .svws-compact-data {
    @apply justify-center;
  }
}
</style>
