<template>
	<div class="page--content">
		<div class="flex flex-col gap-y-16 lg:gap-y-16">
			<svws-ui-action-button title="Löschen" description="Ausgewählte Klassen werden gelöscht." icon="i-ri-delete-bin-line"
				:action-function="entferneKlassen" action-label="Löschen" :is-loading="loading" :is-active="currentAction === 'delete'"
				:action-disabled="!preConditionCheck[0]" @click="toggleDeleteKlassen">
				<span v-if="preConditionCheck[0] == true">Alle ausgewählten Klassen sind bereit zum Löschen.</span>
				<template v-else v-for="message in preConditionCheck[1]" :key="message">
					<span class="text-error"> {{ message }} <br> </span>
				</template>
			</svws-ui-action-button>
			<log-box :logs :status>
				<template #button>
					<svws-ui-button v-if="status !== undefined" type="transparent" @click="clearLog" title="Log verwerfen">Log verwerfen</svws-ui-button>
				</template>
			</log-box>
		</div>
	</div>
</template>

<script setup lang="ts">

	import { ref, computed } from "vue";
	import type { KlassenGruppenprozesseProps } from "./SKlassenGruppenprozesseProps";
	import type { List } from "@core";

	const props = defineProps<KlassenGruppenprozesseProps>();

	const currentAction = ref<string>('');
	const loading = ref<boolean>(false);
	const logs = ref<List<string | null> | undefined>();
	const status = ref<boolean | undefined>();

	const preConditionCheck = computed(() => {
		if (currentAction.value === 'delete')
			return props.deleteKlassenCheck();
		return [false, []];
	})

	function toggleDeleteKlassen() {
		currentAction.value = currentAction.value === 'delete' ? '' : 'delete';
	}

	function clearLog() {
		loading.value = false;
		logs.value = undefined;
		status.value = undefined;
	}

	async function entferneKlassen() {
		loading.value = true;

		const [delStatus, logMessages] = await props.deleteKlassen();
		logs.value = logMessages;
		status.value = delStatus;
		currentAction.value = '';

		loading.value = false;
	}

</script>
