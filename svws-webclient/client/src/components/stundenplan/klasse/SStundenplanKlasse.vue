<template>
	<div class="page--content page--content--full">
		<Teleport to=".svws-sub-nav-target" v-if="isMounted">
			<svws-ui-sub-nav>
				<div class="ml-4 flex gap-0.5 items-center leading-none">
					<div class="text-button font-bold mr-1 -mt-px">Klasse:</div>
					<svws-ui-select headless title="Klasse" v-model="klasse" :items="stundenplanManager().klasseGetMengeAsList()" :item-text="i => i.kuerzel" autocomplete
						:item-filter="(i, text)=> i.filter(k => k.kuerzel.includes(text.toLocaleLowerCase()))" :item-sort="() => 0" type="transparent" />
					<template v-if="stundenplanManager().getWochenTypModell() > 0">
						<div class="text-button font-bold mr-1 -mt-px">Wochentyp:</div>
						<svws-ui-select headless title="Wochentyp" v-model="wochentypAnzeige" :items="wochentypen()" class="print:hidden" type="transparent"
							:disabled="wochentypen().size() <= 0" :item-text="wt => stundenplanManager().stundenplanGetWochenTypAsString(wt)" />
					</template>
					<svws-ui-button v-if="hatUpdateKompetenz" type="transparent" @click.stop="doppelstundenModus = !doppelstundenModus" title="Doppelstundenmodus ein- und ausschalten" class="text-black dark:text-white">
						{{ doppelstundenModus ? 'Doppelstundenmodus' : 'Einzelstundenmodus' }}
					</svws-ui-button>
					<template v-if="(stundenplanManager().unterrichtsgruppenMergeableGet().size() > 0) && hatUpdateKompetenz">
						<span class="ml-4">Unterricht:</span>
						<s-stundenplan-klasse-modal-merge :stundenplan-manager :merge-unterrichte v-slot="{ openModal }">
							<svws-ui-button type="error" size="small" class="ml-1" @click="openModal()" title="Unterricht, der zusammengelegt werden kann, weil es Doppelungen gibt">
								<span class="icon-sm icon-error i-ri-error-warning-line" />zusammenlegen
							</svws-ui-button>
						</s-stundenplan-klasse-modal-merge>
					</template>
				</div>
			</svws-ui-sub-nav>
		</Teleport>
		<template v-if="props.stundenplanManager().klasseGetMengeAsList().isEmpty()">
			<span>Für diesen Stundenplan ist keine Klasse vorhanden.</span>
		</template>
		<template v-else>
			<div v-if="hatUpdateKompetenz" @dragover="checkDropZone($event)" @drop="onDrop(undefined, -1)" class="flex flex-col justify-start mb-auto svws-table-offset h-full overflow-y-scroll overflow-x-hidden pr-4 border-2 rounded-xl border-dashed p-1"
				:class="[dragData === undefined ? 'border-black/0' : ' border-error ring-4 ring-error/10']">
				<div class="fixed flex items-center justify-center h-3/4 w-72 z-20 pointer-events-none"><span :class="dragData === undefined ? '':'icon-lg icon-error opacity-50 i-ri-delete-bin-line scale-[4]'" /></div>
				<svws-ui-table :items="stundenplanManager().klassenunterrichtGetMengeByKlasseIdAsList(klasse.id)" :columns="colsKlassenunterricht">
					<template #body>
						<div v-for="ku in stundenplanManager().klassenunterrichtGetMengeByKlasseIdAsList(klasse.id)" :key="ku.idKlasse + '/' + ku.idFach" role="row" class="svws-ui-tr"
							@dragstart="onDrag(ku, $event)" @dragend="onDrag(undefined)"
							:style="`--background-color: ${stundenplanManager().klassenunterrichtGetWochenminutenREST(klasse.id, ku.idFach) > 0 ? getBgColor(stundenplanManager().fachGetByIdOrException(ku.idFach).kuerzelStatistik) : ''}`">
							<div role="cell" class="select-none svws-ui-td" :class="{
								'text-error font-bold': stundenplanManager().klassenunterrichtGetWochenminutenREST(klasse.id, ku.idFach) < 0,
								'font-bold': stundenplanManager().klassenunterrichtGetWochenminutenREST(klasse.id, ku.idFach) > 0 }">
								<div class="ml-1">
									<div class="svws-ui-badge select-none group cursor-grab flex place-items-center"
										:class="{ 'cursor-grabbing': dragData !== undefined, '!border-black/5': stundenplanManager().klassenunterrichtGetWochenminutenREST(klasse.id, ku.idFach) <= 0 }" draggable="true">
										<span class="icon i-ri-draggable inline-block -ml-1 icon-dark opacity-60 group-hover:opacity-100 group-hover:icon-dark" />
										<span :class="{'font-bold': stundenplanManager().klassenunterrichtGetWochenminutenREST(klasse.id, ku.idFach) > 0}">
											{{ stundenplanManager().fachGetByIdOrException(ku.idFach).bezeichnung }}
										</span>
									</div>
								</div>
							</div>
							<div role="cell" class="select-none svws-ui-td svws-align-center">
								<span class="rounded p-0.5 -m-0.5" :class="{
									'bg-error text-white font-bold': stundenplanManager().klassenunterrichtGetWochenminutenREST(klasse.id, ku.idFach) < 0,
									'bg-light': stundenplanManager().klassenunterrichtGetWochenminutenREST(klasse.id, ku.idFach) > 0,
									'bg-success': stundenplanManager().klassenunterrichtGetWochenminutenREST(klasse.id, ku.idFach) === 0 }">
									{{ stundenplanManager().klassenunterrichtGetWochenstundenIST(ku.idKlasse, ku.idFach) }}/{{ stundenplanManager().klassenunterrichtGetWochenstundenSOLL(ku.idKlasse, ku.idFach) }}
								</span>
							</div>
						</div>
					</template>
				</svws-ui-table>
				<svws-ui-checkbox v-if="!stundenplanManager().schieneGetMengeByKlasseId(klasse.id).isEmpty()" type="toggle" v-model="schienSortierung" class="mt-8">Nach Schienen sortieren</svws-ui-checkbox>
				<svws-ui-table :items="stundenplanManager().kursGetMengeByKlasseIdAsList(klasse.id)" :columns="colsKursunterricht">
					<template #body>
						<template v-if="(schienSortierung === true) && (!stundenplanManager().schieneGetMengeByKlasseId(klasse.id).isEmpty())">
							<div v-for="schiene of stundenplanManager().schieneGetMengeByKlasseId(klasse.id)" :key="schiene.id" @dragstart="onDrag(schiene, $event)" @dragend="onDrag(undefined)" draggable="true">
								<!-- Die Schienenzeile -->
								<div role="row" class="svws-ui-tr bg-light">
									<div role="cell" class="select-none svws-ui-td font-bold group" :class="{ 'cursor-grabbing': dragData !== undefined }">
										<div class="select-none group cursor-grab flex place-items-center">
											<span class="icon i-ri-draggable inline-block opacity-60 group-hover:opacity-100 group-hover:icon-dark" />
											<span>{{ schiene.bezeichnung }}</span>
										</div>
									</div>
									<div role="cell" class="select-none svws-ui-td" />
								</div>
								<!-- Die Kurszeilen -->
								<div v-for="kurs in stundenplanManager().kursGetMengeByKlasseIdAndSchieneId(klasse.id, schiene.id)" :key="kurs.id" role="row" class="svws-ui-tr"
									@dragstart.stop="onDrag(kurs, $event)" @dragend.stop="onDrag(undefined)"
									:class="{ 'cursor-grabbing': dragData !== undefined, '!border-black/5': stundenplanManager().kursGetWochenstundenREST(kurs.id) <= 0 }"
									:style="`--background-color: ${(stundenplanManager().kursGetWochenstundenREST(kurs.id) > 0) ? getBgColor(stundenplanManager().fachGetByIdOrException(kurs.idFach).kuerzelStatistik) : ''}`">
									<div role="cell" class="select-none svws-ui-td" :class="{
										'text-error font-bold': stundenplanManager().kursGetWochenstundenREST(kurs.id) < 0,
										'font-bold': stundenplanManager().kursGetWochenstundenREST(kurs.id) > 0 }">
										<div class="ml-3">
											<div class="svws-ui-badge select-none group cursor-grab flex place-items-center" draggable="true" :class="{ 'cursor-grabbing': dragData !== undefined, '!border-black/5': stundenplanManager().kursGetWochenstundenREST(kurs.id) <= 0 }">
												<span class="icon i-ri-draggable inline-block -ml-1 icon-dark opacity-60 group-hover:opacity-100 group-hover:icon-dark" />
												<span :class="{'font-bold': stundenplanManager().kursGetWochenstundenREST(kurs.id) > 0}">{{ kurs.bezeichnung }}</span>
											</div>
										</div>
									</div>
									<div role="cell" class="select-none svws-ui-td svws-align-center">
										<span class="rounded p-0.5" :class="{
											'bg-error text-white font-bold': stundenplanManager().kursGetWochenstundenREST(kurs.id) < 0,
											'bg-light': stundenplanManager().kursGetWochenstundenREST(kurs.id) > 0,
											'bg-success': stundenplanManager().kursGetWochenstundenREST(kurs.id) === 0 }">
											{{ stundenplanManager().kursGetWochenstundenIST(kurs.id) }}/{{ stundenplanManager().kursGetWochenstundenSOLL(kurs.id) }}
										</span>
									</div>
								</div>
							</div>
						</template>
						<template v-else>
							<div v-for="kurs in stundenplanManager().kursGetMengeByKlasseIdAsList(klasse.id)" :key="kurs.id" role="row" class="svws-ui-tr"
								@dragstart="onDrag(kurs, $event)" @dragend="onDrag(undefined)"
								:style="`--background-color: ${(stundenplanManager().kursGetWochenstundenREST(kurs.id) > 0) ? getBgColor(stundenplanManager().fachGetByIdOrException(kurs.idFach).kuerzelStatistik) : ''}`">
								<div role="cell" class="select-none svws-ui-td" :class="{
									'text-error font-bold': stundenplanManager().kursGetWochenstundenREST(kurs.id) < 0,
									'font-bold': stundenplanManager().kursGetWochenstundenREST(kurs.id) > 0 }">
									<div class="ml-1">
										<div class="svws-ui-badge select-none group cursor-grab flex place-items-center"
											:class="{ 'cursor-grabbing': dragData !== undefined, '!border-black/5': stundenplanManager().kursGetWochenstundenREST(kurs.id) <= 0 }" draggable="true">
											<span class="icon i-ri-draggable inline-block -ml-1 icon-dark opacity-60 group-hover:opacity-100 group-hover:icon-dark" />
											<span :class="{'font-bold': stundenplanManager().kursGetWochenstundenREST(kurs.id) > 0}">{{ kurs.bezeichnung }}</span>
										</div>
									</div>
								</div>
								<div role="cell" class="select-none svws-ui-td svws-align-center">
									<span class="rounded p-0.5 -m-0.5" :class="{
										'bg-error text-white font-bold': stundenplanManager().kursGetWochenstundenREST(kurs.id) < 0,
										'bg-light': stundenplanManager().kursGetWochenstundenREST(kurs.id) > 0,
										'bg-success': stundenplanManager().kursGetWochenstundenREST(kurs.id) === 0 }">
										{{ stundenplanManager().kursGetWochenstundenIST(kurs.id) }}/{{ stundenplanManager().kursGetWochenstundenSOLL(kurs.id) }}
									</span>
								</div>
							</div>
						</template>
					</template>
				</svws-ui-table>
			</div>
			<!-- Das Zeitraster des Stundenplans, in welches von der linken Seite die Kurs-Unterrichte oder die Klassen-Unterricht hineingezogen werden können.-->
			<stundenplan-klasse mode-pausenaufsichten="tooltip" :id="klasse.id" :manager="stundenplanManager" :wochentyp="()=>wochentypAnzeige" :kalenderwoche="() => undefined"
				:use-drag-and-drop="hatUpdateKompetenz" :drag-data="() => dragData" :on-drag :on-drop class="h-full overflow-scroll pr-4" />
		</template>
	</div>
</template>

<script setup lang="ts">

	import type { DataTableColumn } from "@ui";
	import type { List, StundenplanKlasse } from "@core";
	import type { StundenplanKlasseProps } from "./SStundenplanKlasseProps";
	import type { StundenplanAnsichtDragData, StundenplanAnsichtDropZone } from "@comp";
	import { ArrayList, StundenplanKurs, StundenplanKlassenunterricht, Fach, StundenplanUnterricht, StundenplanZeitraster, HashSet, StundenplanSchiene, BenutzerKompetenz } from "@core";
	import { computed, onMounted, shallowRef } from "vue";
	import { cast_java_util_List } from "../../../../../core/src/java/util/List";

	const props = defineProps<StundenplanKlasseProps>();

	const isMounted = shallowRef(false);
	onMounted(() => isMounted.value = true);

	const _klasse = shallowRef<StundenplanKlasse | undefined>(undefined);
	const wochentypAnzeige = shallowRef<number>(0);
	const doppelstundenModus = shallowRef<boolean>(false);
	const schienSortierung = shallowRef<boolean>(true);

	const hatUpdateKompetenz = computed<boolean>(() => props.benutzerKompetenzen.has(BenutzerKompetenz.STUNDENPLAN_AENDERN));

	const schuljahr = computed<number>(() => props.stundenplanManager().getSchuljahr());

	const klasse = computed<StundenplanKlasse>({
		get: () : StundenplanKlasse => {
			if (_klasse.value !== undefined)
				try {
					return props.stundenplanManager().klasseGetByIdOrException(_klasse.value.id);
				} catch (e) { /* empty */ }
			return props.stundenplanManager().klasseGetMengeAsList().get(0);
		},
		set: (value : StundenplanKlasse) => _klasse.value = value
	});

	function getBgColor(fach: string): string {
		return Fach.getBySchluesselOrDefault(fach).getHMTLFarbeRGB(schuljahr.value);
	}

	function wochentypen(): List<number> {
		let modell = props.stundenplanManager().getWochenTypModell();
		if (modell <= 1)
			modell = 0;
		const result = new ArrayList<number>();
		for (let n = 0; n <= modell; n++)
			result.add(n);
		return result;
	}

	const dragData = shallowRef<StundenplanAnsichtDragData>(undefined);

	function onDrag(data: StundenplanAnsichtDragData, event?: DragEvent) {
		dragData.value = data;
	}

	async function onDrop(zone: StundenplanAnsichtDropZone, wochentyp?: number) {
		// else if oder return der api-methode, sonst wird weiter geprüft
		if (dragData.value === undefined)
			return;
		// Fall StundenplanUnterricht -> StundenplanZeitraster
		if ((dragData.value instanceof StundenplanUnterricht) && (zone instanceof StundenplanZeitraster) && (wochentyp !== undefined))
			return await props.patchUnterricht([dragData.value], zone, wochentyp);
		// Fall List<StundenplanUnterricht> -> StundenplanZeitraster
		// Fall List<StundenplanKurs> -> StundenplanZeitraster
		// Fall List<StundenplanUnterricht> -> undefined
		if (dragData.value.isTranspiledInstanceOf("java.util.List")) {
			const listStundenplanUnterricht = new ArrayList<StundenplanUnterricht>();
			const listStundenplanKurs = new ArrayList<StundenplanKurs>();
			const casted: List<unknown> = cast_java_util_List(dragData.value);
			for (const item of casted)
				if (item instanceof StundenplanUnterricht)
					listStundenplanUnterricht.add(item);
				else if ((item instanceof StundenplanKurs) && (zone instanceof StundenplanZeitraster) && (wochentyp !== undefined) && props.stundenplanManager().kursDarfInZelle(item, zone.wochentag, zone.unterrichtstunde, wochentyp))
					listStundenplanKurs.add(item);
			if (listStundenplanKurs.size() > 0)
				return await props.addUnterrichtKlasse(listStundenplanKurs);
			if (listStundenplanUnterricht.size() > 0)
				if ((zone instanceof StundenplanZeitraster) && (wochentyp !== undefined))
					return await props.patchUnterricht(listStundenplanUnterricht, zone, wochentyp);
				else if (zone === undefined)
					return await props.removeUnterrichtKlasse(listStundenplanUnterricht);
		}
		// Fall StundenplanKlassenunterricht -> StundenplanZeitraster
		if ((dragData.value instanceof StundenplanKlassenunterricht) && (zone instanceof StundenplanZeitraster) && (wochentyp !== undefined) && props.stundenplanManager().klassenunterrichtDarfInZelle(dragData.value, zone.wochentag, zone.unterrichtstunde, wochentyp)) {
			const klassen = new ArrayList<number>();
			klassen.add(dragData.value.idKlasse);
			const stunde = { idZeitraster: zone.id, wochentyp, idKurs: null, idFach: dragData.value.idFach, klassen, lehrer: dragData.value.lehrer, schienen: dragData.value.schienen };
			const arr = [];
			arr.push(stunde);
			if (doppelstundenModus.value === true && props.stundenplanManager().klassenunterrichtGetWochenstundenREST(klasse.value.id, dragData.value.idFach) >= 2) {
				const next = props.stundenplanManager().getZeitrasterNext(zone);
				if (next && props.stundenplanManager().klassenunterrichtDarfInZelle(dragData.value, zone.wochentag, next.unterrichtstunde, wochentyp))
					arr.push({ idZeitraster: next.id, wochentyp, idKurs: null, idFach: dragData.value.idFach, klassen, lehrer: dragData.value.lehrer, schienen: dragData.value.schienen });
			}
			await props.addUnterrichtKlasse(arr);
			return;
		}
		// Fall StundenplanUnterricht -> undefined
		if ((dragData.value instanceof StundenplanUnterricht) && (zone === undefined))
			return await props.removeUnterrichtKlasse([dragData.value]);
		// TODO Fall StundenplanKurs -> StundenplanZeitraster
		if ((dragData.value instanceof StundenplanKurs) && (zone instanceof StundenplanZeitraster) && (wochentyp !== undefined) && props.stundenplanManager().kursDarfInZelle(dragData.value, zone.wochentag, zone.unterrichtstunde, wochentyp)) {
			const klassen = new HashSet<number>();
			const listSchueler = props.stundenplanManager().schuelerGetMengeByKursIdAsListOrException(dragData.value.id);
			for (const schueler of listSchueler)
				klassen.add(schueler.idKlasse);
			const stunde = { idZeitraster: zone.id, wochentyp, idKurs: dragData.value.id, idFach: dragData.value.idFach, klassen: new ArrayList(klassen), schienen: dragData.value.schienen, lehrer: dragData.value.lehrer };
			const arr = [];
			arr.push(stunde);
			//stundenplanManager().klassenunterrichtGetWochenstundenIST(ku.idKlasse, ku.idFach) }}/{{ stundenplanManager().klassenunterrichtGetWochenstundenSOLL(ku.idKlasse, ku.idFach)
			if (doppelstundenModus.value === true && props.stundenplanManager().kursGetWochenstundenREST(dragData.value.id) >= 2) {
				const next = props.stundenplanManager().getZeitrasterNext(zone);
				if (next && props.stundenplanManager().kursDarfInZelle(dragData.value, zone.wochentag, next.unterrichtstunde, wochentyp))
					arr.push({ idZeitraster: next.id, wochentyp, idKurs: dragData.value.id, idFach: dragData.value.idFach, klassen: new ArrayList(klassen), schienen: dragData.value.schienen, lehrer: dragData.value.lehrer });
			}
			return await props.addUnterrichtKlasse(arr);
		}
		// Fall StundenplanSchiene -> StundenplanZeitraster
		if (dragData.value instanceof StundenplanSchiene) {
			const listStundenplanKursRaw = props.stundenplanManager().kursGetMengeByKlasseIdAndSchieneId(klasse.value.id, dragData.value.id);
			const arr = [];
			for (const kurs of listStundenplanKursRaw) {
				if ((zone instanceof StundenplanZeitraster) && (wochentyp !== undefined) && props.stundenplanManager().kursDarfInZelle(kurs, zone.wochentag, zone.unterrichtstunde, wochentyp)) {
					const klassen = new HashSet<number>();
					const listSchueler = props.stundenplanManager().schuelerGetMengeByKursIdAsListOrException(kurs.id);
					for (const schueler of listSchueler)
						klassen.add(schueler.idKlasse);
					const stunde = { idZeitraster: zone.id, wochentyp, idKurs: kurs.id, idFach: kurs.idFach, klassen: new ArrayList(klassen), schienen: kurs.schienen, lehrer: kurs.lehrer };
					arr.push(stunde);
					if ((doppelstundenModus.value === true) && (props.stundenplanManager().kursGetWochenstundenREST(kurs.id) >= 2)) {
						const next = props.stundenplanManager().getZeitrasterNext(zone);
						if (next && (props.stundenplanManager().kursDarfInZelle(kurs, zone.wochentag, next.unterrichtstunde, wochentyp)))
							arr.push({ idZeitraster: next.id, wochentyp, idKurs: kurs.id, idFach: kurs.idFach, klassen: new ArrayList(klassen), schienen: kurs.schienen, lehrer: kurs.lehrer });
					}
				}
			}
			await props.addUnterrichtKlasse(arr);
			return;
		}
		// TODO Fall StundenplanZeitraster -> undefined
		// TODO Fall StundenplanPausenaufsicht -> StundenplanPausenzeit
		// TODO Fall StundenplanPausenaufsicht -> undefined
		// TODO Fall Lehrer -> StundenplanPausenzeit
	}

	function isDropZone() : boolean {
		if ((dragData.value === undefined) || (dragData.value instanceof StundenplanKurs) || (dragData.value instanceof StundenplanKlassenunterricht))
			return false;
		return true;
	}

	function checkDropZone(event: DragEvent) {
		if (isDropZone())
			event.preventDefault();
	}

	const colsKlassenunterricht: DataTableColumn[] = [
		{ key: "bezeichnung", label: "Klassenunterrichte", span: 1 },
		{ key: "wochenstunden", label: "WS", tooltip: "Wochenstunden", fixedWidth: 3, align: "center" }
	];

	const colsKursunterricht: DataTableColumn[] = [
		{ key: "bezeichnung", label: "Kursunterrichte", span: 1 },
		{ key: "wochenstunden", label: "WS", tooltip: "Wochenstunden", fixedWidth: 5, align: "center" }
	];

</script>

<style lang="postcss" scoped>
	.page--content {
		@apply grid overflow-y-hidden overflow-x-auto h-full pb-3 pt-6 lg:gap-x-8;
		grid-auto-rows: 100%;
		grid-template-columns: minmax(20rem, 0.5fr) 2fr;
		grid-auto-columns: max-content;
	}

</style>
