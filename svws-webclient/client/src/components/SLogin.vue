<template>
	<svws-ui-app-layout :fullwidth-content="!authentication_success" :skeleton="authentication_success" :class="{'app--layout--login': !authentication_success}">
		<template #main>
			<div class="login-wrapper">
				<div class="login-container pt-5">
					<div class="login-form modal modal--sm my-auto">
						<div class="modal--content-wrapper pb-3">
							<div class="modal--content px-5">
								<div class="mb-6 mt-2">
									<h1 class="font-bold text-headline-xl leading-none w-full py-2">
										SVWS NRW
									</h1>
									<h2 class="text-headline-sm leading-tight opacity-50">Schulverwaltung für<br>Nordrhein-Westfalen</h2>
								</div>
								<svws-ui-input-wrapper center>
									<svws-ui-text-input v-model.trim="inputHostname" type="text" url placeholder="Serveraddresse" @keyup.enter="connect" @focus="inputFocus = true" :debounce-ms="0" />
									<svws-ui-button type="secondary" @click="connect" :disabled="!(inputDBSchemata.size() === 0 || connecting || inputFocus )" :class="{'opacity-25 hover:opacity-100': inputDBSchemata.size() > 0 && !inputFocus}">
										<span v-if="inputDBSchemata.size() === 0 || connecting || inputFocus">Verbinden</span>
										<span v-else>Verbunden</span>
										<svws-ui-spinner :spinning="connecting" />
										<span class="icon i-ri-check-line" v-if="!connecting && inputDBSchemata.size() > 0 && !inputFocus" />
									</svws-ui-button>
								</svws-ui-input-wrapper>
								<Transition>
									<svws-ui-input-wrapper v-if="inputDBSchemata.size() > 0 && !connecting" class="mt-10" center>
										<svws-ui-select v-model="schema" title="Datenbank-Schema" :items="inputDBSchemata" :item-text="i => i.name ?? 'SCHEMANAME FEHLT'" class="w-full" @update:model-value="schema => schema && setSchema(schema)" />
										<svws-ui-text-input v-model.trim="username" type="text" placeholder="Benutzername" @keyup.enter="doLogin" ref="refUsername" />
										<svws-ui-text-input v-model.trim="password" type="password" placeholder="Passwort" @keyup.enter="doLogin" />
										<svws-ui-spacing />
										<div class="flex gap-2">
											<svws-ui-modal-hilfe> <s-login-hilfe /> </svws-ui-modal-hilfe>
											<svws-ui-button @click="doLogin" type="primary" :disabled="authenticating">
												Anmelden
												<svws-ui-spinner v-if="authenticating" spinning />
												<span class="icon i-ri-login-circle-line" v-else />
											</svws-ui-button>
										</div>
									</svws-ui-input-wrapper>
								</Transition>
								<div class="mt-16 text-sm font-medium">
									<div class="flex gap-2 items-center opacity-50">
										<img src="/images/Wappenzeichen_NRW_bw.svg" alt="Logo NRW" class="h-11">
										<div class="text-left flex flex-col">
											<span class="pb-0.5">Powered by SVWS-NRW</span>
											<div class="flex gap-2 place-items-center">
												<div><span class="font-bold">v{{ version }}</span> <a :href="`https://github.com/SVWS-NRW/SVWS-Server/commit/${githash}`" v-if="version.includes('SNAPSHOT')">{{ githash.substring(0, 8) }}</a></div>
												<div @click="copyToClipboard" class="cursor-pointer place-items-center flex">
													<span class="icon-sm i-ri-file-copy-line inline-block" v-if="copied === null" />
													<span class="icon-sm i-ri-error-warning-fill inline-block" v-else-if="copied === false" />
													<span class="icon-sm i-ri-check-line icon-primary inline-block" v-else />
												</div>
											</div>
											<nav class="flex items-center gap-2">
												<a class="login-footer-link" href="#">Impressum</a>
												<datenschutz-modal v-slot="{ openModal }">
													<a class="login-footer-link" href="#" @click="openModal()">Datenschutz</a>
												</datenschutz-modal>
											</nav>
										</div>
									</div>
									<div class="mt-3 -mb-3 opacity-50">
										<p class="text-sm text-left">
											Hinweis: Um eine gute Lesbarkeit zu erzeugen, wird bei SVWS-NRW möglichst auf
											geschlechtsneutrale Begriffe wie Lehrkräfte, Klassenleitung, Erziehungsberechtigte usw.
											zurückgegriffen. An Stellen, wo das nicht möglich ist, wird versucht alle
											Geschlechter gleichermaßen zu berücksichtigen.
										</p>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</template>
	</svws-ui-app-layout>
	<svws-ui-notifications v-if="error">
		<svws-ui-notification type="error">
			<template #header> {{ error.name }} </template>
			{{ error.message }}
		</svws-ui-notification>
	</svws-ui-notifications>
</template>

<script setup lang="ts">

	import { computed, nextTick, ref, shallowRef } from "vue";
	import { type ComponentExposed } from "vue-component-type-helpers";
	import type { DBSchemaListeEintrag, List } from "@core";
	import { ArrayList, DeveloperNotificationException, JsonCoreTypeReader } from "@core";
	import { SvwsUiTextInput } from "@ui";
	import { version } from '../../version';
	import { githash } from '../../githash';
	import type { LoginProps } from "./SLoginProps";

	const props = defineProps<LoginProps>();

	const refUsername = ref<ComponentExposed<typeof SvwsUiTextInput>>();
	const firstauth = ref(true);
	const schema = shallowRef<DBSchemaListeEintrag | undefined>();
	const username = ref("Admin");
	const password = ref("");
	const error = ref<{name: string; message: string;}|null>(null);
	const copied = ref<boolean|null>(null);

	async function copyToClipboard() {
		try {
			await navigator.clipboard.writeText(`${version} ${githash}`);
		} catch(e) {
			copied.value = false;
		}
		copied.value = true;
	}

	const connecting = ref(false);
	const authenticating = ref(false);
	const inputFocus = ref(false);

	const connection_failed = ref(false);
	const authentication_success = ref(false);

	const inputDBSchemata = shallowRef<List<DBSchemaListeEintrag>>(new ArrayList<DBSchemaListeEintrag>());

	const inputHostname = computed<string>({
		get: () => props.hostname,
		set: (value) => props.setHostname(value)
	});

	// Versuche zu beim Laden der Komponente automatisch mit Default-Einstellungen eine Verbindung zu dem Server aufzubauen
	void connect();

	async function initCoreTypes() {
		const reader = new JsonCoreTypeReader(`https://${props.hostname}`);
		await reader.loadAll();
		reader.readAll();
		props.setMapCoreTypeNameJsonData(reader.mapCoreTypeNameJsonData);
	}

	async function connect() {
		connecting.value = true;
		inputFocus.value = false;
		error.value = null;
		try {
			inputDBSchemata.value = await props.connectTo(props.hostname);
			if (inputDBSchemata.value.size() <= 0)
				throw new DeveloperNotificationException("Es sind keine Schemata vorhanden.");
			await initCoreTypes();
		} catch (e) {
			connection_failed.value = true;
			connecting.value = false;
			const message = e instanceof DeveloperNotificationException ? e.message : "Verbindung zum Server fehlgeschlagen. Bitte die Serveradresse prüfen und erneut versuchen.";
			error.value = {name: "Serverfehler", message};
			return;
		}
		let hasDefault = false;
		for (const s of inputDBSchemata.value) {
			if (s.isDefault) {
				schema.value = s;
				hasDefault = true;
			}
			if (s.name === props.schemaPrevious) {
				schema.value = s;
				hasDefault = true;
				break;
			}
		}
		if (!hasDefault) {
			schema.value = inputDBSchemata.value.get(0);
			const lastSchema = localStorage.getItem("SVWS-Client Last Used Schema");
			if ((lastSchema !== null) && (lastSchema !== ''))
				for (const s of inputDBSchemata.value)
					if (s.name === lastSchema) {
						schema.value = s;
						break;
					}
		}
		connection_failed.value = false;
		connecting.value = false;
		await nextTick(() => {
			refUsername.value?.doFocus();
		});
	}

	async function doLogin() {
		inputFocus.value = false;
		error.value = null;
		if ((schema.value === undefined) || (schema.value.name === null))
			return error.value = {name: "Eingabefehler", message: "Es muss ein gültiges Schema ausgewählt sein."};
		authenticating.value = true;
		await props.login(schema.value.name, username.value, password.value);
		authenticating.value = false;
		firstauth.value = false;
		if (!props.authenticated)
			error.value = {name: "Eingabefehler", message: "Passwort oder Benutzername falsch."};
		else
			localStorage.setItem("SVWS-Client Last Used Schema", schema.value.name);
	}

</script>

<style lang="postcss" scoped>
	.login-wrapper {
		@apply flex h-full flex-col justify-between;
	}

	.app--layout--login {
		@apply p-0 bg-none bg-transparent;
	}

	.app--layout--login :global(.app--content-container) {
		@apply bg-white/5;
	}

	.login-container {
		@apply bg-cover bg-top h-full flex flex-col justify-center items-center px-4;
		/*background-image: url('/images/noise.svg'), url('/images/placeholder-background.jpg');
		background-size: 100px, cover;
		background-blend-mode: overlay, normal;*/
		background-image: url('/images/placeholder-background.jpg');
		/*background: radial-gradient(circle at 50% 50%, rgba(255, 255, 255, 0.8), transparent 90%),
		linear-gradient(to top, #2285d5 0%, transparent 70%),
		linear-gradient(to bottom, transparent, rgba(255, 255, 255, 0.4) 70%),
		#e3eefb;
		animation: bg 30s infinite;

		&:before {
			content: '';
			@apply absolute inset-0 pointer-events-none;
			background: radial-gradient(circle at 20% 20%, rgba(255, 255, 255, 0.5), transparent 60%);
		}

		&:after {
			content: '';
			@apply absolute inset-0 opacity-10 pointer-events-none;
			background-image:  linear-gradient(rgba(255, 255, 255, 1) 2px, transparent 2px), linear-gradient(90deg, rgba(255, 255, 255, 1) 2px, transparent 2px), linear-gradient(rgba(255, 255, 255, 1) 1px, transparent 1px), linear-gradient(90deg, rgba(255, 255, 255, 1) 1px, rgba(255, 255, 255, 0) 1px);
			background-size: 50px 50px, 50px 50px, 10px 10px, 10px 10px;
			background-position: -2px -2px, -2px -2px, -1px -1px, -1px -1px;
		}*/
	}

	@keyframes bg {
		0%, 100% { background-color: #2285d5; }
		25% { background-color: #8a5cf6; }
		50% { background-color: #84cc16; }
		75% { background-color: #fff693; }
	}

	.modal {
		@apply shadow-2xl shadow-black/50 rounded-3xl;
	}

	.login-footer-link {
		@apply inline-block;
	}

	.login-footer-link:hover,
	.login-footer-link:focus,
	.login-footer-link:hover .hover-underline,
	.login-footer-link:focus .hover-underline {
		@apply underline;
	}

	.v-enter-active,
	.v-leave-active {
		transition: opacity 0.5s ease;
	}

	.v-enter-from,
	.v-leave-to {
		opacity: 0;
	}
</style>
