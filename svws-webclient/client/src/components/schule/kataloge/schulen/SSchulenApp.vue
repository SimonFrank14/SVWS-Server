<template>
	<template v-if="auswahl">
		<svws-ui-header>
			<div>
				<span class="inline-block mr-3">{{ auswahl.kuerzel }}</span>
				<svws-ui-badge type="light" title="ID" class="font-mono" size="small">
					Schulnummer: {{ auswahl.schulnummer }}
				</svws-ui-badge>
			</div>
			<div>
				<span class="opacity-40">{{ auswahl.name }}</span>
			</div>
		</svws-ui-header>
		<svws-ui-tab-bar :tab-manager>
			<router-view />
		</svws-ui-tab-bar>
	</template>
	<div v-else class="app--content--placeholder">
		<span class="icon i-ri-archive-line" />
	</div>
</template>

<script setup lang="ts">

	import type { SchulenAppProps } from "./SSchulenAppProps";

	const props = defineProps<SchulenAppProps>();

</script>
