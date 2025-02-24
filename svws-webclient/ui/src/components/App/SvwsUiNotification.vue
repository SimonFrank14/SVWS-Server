<template>
	<div v-if="isOpen" class="notification inline-flex transform overflow-hidden"
		:class="{
			'notification--info': type === 'info',
			'notification--success': type === 'success',
			'notification--error': type === 'error' || type === 'bug',
			'notification--warning': type === 'warning',
			'notification--details-open': stackOpen,
		}">
		<div class="notification--content-wrapper flex justify-between items-start">
			<div class="notification--content" :class="{'notification--content--has-header': $slots.header}">
				<div class="notification--header">
					<span v-if="icon || type" class="notification--icon">
						<span class="icon i-ri-lock-2-line" v-if="icon === 'login'" />
						<span class="icon i-ri-alert-fill" v-else-if="icon === 'error' || type === 'error'" />
						<span class="icon i-ri-bug-fill" v-else-if="icon === 'bug' || type === 'bug'" />
						<span class="icon i-ri-check-line" v-else-if="icon === 'success' || type === 'success'" />
						<span class="icon i-ri-information-line" v-else-if="icon === 'info' || type === 'info'" />
						<span class="icon i-ri-error-warning-line" v-else-if="icon === 'warning' || type === 'warning'" />
					</span>
					<slot name="header" />
				</div>
				<div class="notification--text">
					<slot />
				</div>
				<div class="mt-3 -mb-1 flex flex-wrap gap-1" v-if="$slots.stack || type === 'bug'">
					<svws-ui-button v-if="type === 'bug'" type="secondary">
						Fehler melden
						<span class="icon i-ri-send-plane-fill" />
					</svws-ui-button>
					<svws-ui-button type="transparent" @click="toggleStackOpen" v-if="$slots.stack">
						<span>Details</span>
						<span class="icon i-ri-arrow-up-s-line -ml-1" v-if="stackOpen" />
						<span class="icon i-ri-arrow-down-s-line -ml-1" v-else />
					</svws-ui-button>
					<svws-ui-button type="transparent" @click="copyToClipboard" v-if="toCopy !== undefined">
						<span>Meldung Kopieren</span>
						<span class="icon i-ri-file-copy-line" v-if="copied === null" />
						<span class="icon i-ri-error-warning-fill" v-else-if="copied === false" />
						<span class="icon i-ri-check-line icon-success" v-else />
					</svws-ui-button>
				</div>
				<div v-if="copied === false" class="p-2 bg-white border rounded-md text-error mt-2">{{ "Es gab einen Fehler beim Kopieren in die Zwischenablage. Ist die Zwischenablage gesperrt?" }}</div>
				<div class="notification--stack" v-if="$slots.stack && stackOpen">
					<slot name="stack" />
				</div>
			</div>
			<div class="absolute top-0 right-0 p-1">
				<svws-ui-button type="icon" @click="close" tabindex="-1" class="notification--close-button">
					<span class="icon i-ri-close-line" />
				</svws-ui-button>
			</div>
		</div>
	</div>
</template>

<script setup lang='ts'>

	import { ref } from "vue";

	const props = withDefaults(defineProps<{
		type?: 'info' | 'error' | 'success' | 'warning' | 'bug';
		icon?: 'error' | 'login' | 'success' | 'warning' | 'info' | 'bug';
		id?: number;
		toCopy?: string;
	}>(), {
		type: 'info',
		icon: undefined,
		id: 0,
		toCopy: undefined,
	});

	const emit = defineEmits<{
		click: [id: number];
	}>()

	const isOpen = ref(true);
	const stackOpen = ref(false);
	const copied = ref<boolean|null>(null);

	function setIsOpen(value: boolean) {
		isOpen.value = value;
	}

	function toggleStackOpen() {
		stackOpen.value = !stackOpen.value;
	}

	async function copyToClipboard() {
		if (props.toCopy === undefined)
			return;
		try {
			await navigator.clipboard.writeText(props.toCopy);
		} catch(e) {
			copied.value = false;
		}
		copied.value = true;
	}

	function close() {
		isOpen.value = false;
		if (props.id > 0)
			emit('click', props.id);
	}

	defineExpose({
		setIsOpen,
		toggleStackOpen,
		isOpen,
		stackOpen,
	});

</script>

<style lang="postcss">

	.notification {
		@apply flex flex-col flex-shrink-0;
		@apply w-full;
		@apply relative z-40;
		@apply rounded-lg overflow-hidden;
		@apply shadow-lg shadow-black/10;
		@apply text-base pointer-events-auto;
		@apply bg-primary text-white font-bold;
		transition: transform 0.2s ease-out;

		.button:not(.button--secondary), .button--icon {
			@apply rounded-md;

			&:hover {
				@apply ring-0 bg-white/25;
			}
		}

		&--error {
			@apply bg-error text-white;

			.notification--icon {
				@apply animate-pulse;
			}
			span.icon {
				-webkit-filter: invert(95%) sepia(100%) saturate(14%) hue-rotate(213deg) brightness(104%) contrast(104%);
				filter: invert(95%) sepia(100%) saturate(14%) hue-rotate(213deg) brightness(104%) contrast(104%);
				@apply inline-block -my-1;
			}
		}

		&--success {
			@apply bg-success text-white;

			.button, .button--icon {
				&:hover,
				&:focus {
					@apply bg-black/10;
				}
			}
		}
	}

	.notification--content-wrapper {
		@apply h-full overflow-y-auto w-full;
		-webkit-overflow-scrolling: touch;
	}

	.notification--wrapper {
		@apply fixed inset-0 z-50;
		@apply overflow-y-auto;
	}

	.notification--content {
		@apply flex-grow flex flex-wrap;
		@apply px-4 py-2 overflow-hidden;

		.notification--icon {
			@apply inline-block mr-1 text-base leading-none -mb-1;
		}

		.notification--text {
			@apply text-base font-bold;
		};

		&--has-header {
			@apply py-3;

			.notification--icon {
				@apply text-headline-sm;
			}

			.notification--header {
				@apply w-auto text-headline-sm font-bold mb-1;
			}

			.notification--text {
				@apply w-full font-medium break-words;
			}
		}

		.notification--stack {
			@apply whitespace-pre-wrap bg-black mt-4 -mb-2 -mx-3 p-3 font-mono overflow-auto min-w-full rounded-md;
		}
	}

	.notification--close-button {
		@apply w-7 h-7;
	}

</style>
