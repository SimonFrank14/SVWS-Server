<template>
	<template v-if="daten">
		<header class="svws-ui-header">
			<div class="svws-ui-header--title">
				<svws-ui-avatar :src="daten.foto ? `data:image/png;base64, ${daten.foto}` : undefined"
					:alt="daten.foto ? `Foto ${daten.vorname} ${daten.nachname}` : ''" upload capture />
				<div class="svws-headline-wrapper">
					<h2 class="svws-headline">
						{{ daten.titel }} {{ daten.vorname }} {{ daten.nachname }}
						<svws-ui-badge type="light" title="ID" class="font-mono" size="small">
							ID: {{ daten.id }}
						</svws-ui-badge>
					</h2>
					<span class="svws-subline">{{ daten.kuerzel }}</span>
				</div>
			</div>
			<div class="svws-ui-header--actions" />
		</header>
		<svws-ui-tab-bar :tab-manager>
			<router-view />
		</svws-ui-tab-bar>
	</template>
	<div v-else class="app--content--placeholder">
		<span class="icon i-ri-briefcase-line" />
	</div>
</template>

<script setup lang="ts">

	import { computed } from "vue";
	import type { LehrerAppProps } from "./SLehrerAppProps";

	const props = defineProps<LehrerAppProps>();

	const daten = computed(() => props.lehrerListeManager().hasDaten() && props.lehrerListeManager().daten())
</script>
