<template>
	<slot :open-modal="openModal" />
	<svws-ui-modal :show="showModal" type="danger" class="hidden">
		<template #modalTitle>Zeitraster importieren</template>
		<template #modalContent>
			Beim Import der Katalogzeitraster werden alle bisher für den Stundenplan angelegten Zeitraster entfernt und durch Zeitraster aus dem Schulkatalog ersetzt.
		</template>
		<template #modalActions>
			<svws-ui-button type="secondary" @click="showModal().value = false"> Abbrechen </svws-ui-button>
			<svws-ui-button type="danger" @click="importer()"> Importieren </svws-ui-button>
		</template>
	</svws-ui-modal>
</template>

<script setup lang="ts">
	import { ref } from "vue";
	import type { StundenplanManager} from "@core";
	import type { StundenplanZeitraster } from "@core";

	const props = defineProps<{
		stundenplanManager: () => StundenplanManager;
		importZeitraster: () => Promise<void>;
		removeZeitraster: (zeitraster: Iterable<StundenplanZeitraster>) => Promise<void>;
	}>();

	const _showModal = ref<boolean>(false);
	const showModal = () => _showModal;

	const openModal = () => {
		showModal().value = true;
	}

	async function importer() {
		const list = props.stundenplanManager().getListZeitraster();
		await props.removeZeitraster(list);
		await props.importZeitraster();
		showModal().value = false;
	}

</script>
