<template>
	<div v-if="visible" class="page--content">
		<div class="flex flex-col gap-12 lg:gap-16">
			<s-card-benutzergruppe-daten :set-bezeichnung="setBezeichnung" :set-ist-admin="setIstAdmin" :get-benutzergruppen-manager="getBenutzergruppenManager" />
			<s-card-benutzergruppe-kompetenzen :get-benutzergruppen-manager="getBenutzergruppenManager"
				:add-kompetenz="addKompetenz" :remove-kompetenz="removeKompetenz"
				:add-benutzer-kompetenz-gruppe="addBenutzerKompetenzGruppe"
				:remove-benutzer-kompetenz-gruppe="removeBenutzerKompetenzGruppe" :benutzer-kompetenzen="benutzerKompetenzen" />
		</div>
		<div>
			<s-benutzergruppe-add-benutzer :list-benutzer-alle="listBenutzerAlle"
				:list-benutzergruppen-benutzer="listBenutzergruppenBenutzer"
				:aktualisiere-liste-benutzer-gruppen-benutzer="aktualisiereListeBenutzerGruppenBenutzer"
				:add-benutzer-to-benutzergruppe="addBenutzerToBenutzergruppe"
				:remove-benutzer-from-benutzergruppe="removeBenutzerFromBenutzergruppe"
				:goto-benutzer="gotoBenutzer" />
		</div>
	</div>
</template>

<script setup lang="ts">

	import { computed } from "vue";
	import type { BenutzergruppeProps } from "./SBenutzergruppeProps";

	const props = defineProps<BenutzergruppeProps>();

	const visible = computed(() => props.auswahl() !== undefined);

</script>
