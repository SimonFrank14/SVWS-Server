<template>
	<TransitionRoot appear :show="show().value">
		<Dialog class="modal--wrapper" @close="autoCloseModal" @keyup.esc="autoCloseModal">
			<div class="modal--pageWrapper"
				:class="{ 'modal--pageWrapper--help': size === 'help', }">
				<TransitionChild v-if="size !== 'help'" as="div"
					enter="ease-out duration-200" enter-from="opacity-0" enter-to="opacity-100"
					leave="ease-in duration-100" leave-from="opacity-100" leave-to="opacity-0">
					<div class="modal--overlay" @click="autoCloseModal" />
				</TransitionChild>
				<TransitionChild as="div"
					enter="ease-out duration-200" enter-from="opacity-0 scale-95" enter-to="opacity-100 scale-100"
					leave="ease-in duration-100" leave-from="opacity-100 scale-100" leave-to="opacity-0 scale-95"
					class="modal"
					:class="{
						'modal--sm': size === 'small',
						'modal--md': size === 'medium',
						'modal--lg': size === 'big',
						'modal--help': size === 'help',
						'modal--danger': type === 'danger',
					}">
					<div class="modal--titlebar">
						<DialogTitle class="modal--title inline-flex items-center gap-1">
							<span class="icon i-ri-alert-fill icon-error inline-block" v-if="type === 'danger'" />
							<slot name="modalTitle" />
						</DialogTitle>
						<svws-ui-tooltip v-if="$slots.hilfe" autosize>
							<svws-ui-button type="secondary" @click.stop>
								<span class="icon i-ri-question-line" />
								<span>Hilfe</span>
							</svws-ui-button>
							<template #content><slot name="hilfe" /></template>
						</svws-ui-tooltip>
						<svws-ui-button v-if="closeInTitle" type="icon" @click="closeModal">
							<span class="icon i-ri-close-line" />
						</svws-ui-button>
					</div>
					<div class="modal--content-wrapper" :class="{ 'modal--content-noscroll': noScroll }">
						<DialogDescription v-if="$slots.modalDescription" class="modal--description">
							<slot name="modalDescription" />
						</DialogDescription>

						<div v-if="$slots.modalContent" class="modal--content" :class="{ 'modal--content-noscroll': noScroll }">
							<slot name="modalContent" />
						</div>

						<div class="modal--actions">
							<slot name="modalActions" />
						</div>

						<div class="modal--logs" v-if="$slots.modalLogs">
							<slot name="modalLogs" />
						</div>
					</div>
				</TransitionChild>
			</div>
		</Dialog>
	</TransitionRoot>
</template>

<script setup lang='ts'>

	import { Dialog, DialogTitle, DialogDescription, TransitionRoot, TransitionChild } from "@headlessui/vue";
	import type { Ref } from "vue";
	import type { Size } from "../../types";

	const props = withDefaults(defineProps<{
		show: () => Ref<boolean>;
		size?: Extract<Size, 'small' | 'medium' | 'big'> | 'help';
		type?: 'default' | 'danger';
		autoClose?: boolean;
		closeInTitle?: boolean;
		noScroll?: boolean;
	}>(), {
		size: 'small',
		type: 'default',
		autoClose: true,
		closeInTitle: true,
		noScroll: false,
	});

	function autoCloseModal() {
		if (props.autoClose)
			closeModal();
	}

	function closeModal() {
		props.show().value = false;
	}

</script>

<style lang="postcss">

	.modal--pageWrapper {
		@apply flex items-center justify-center;
		@apply h-screen;
		@apply p-6;
		@apply pointer-events-auto;
	}

	.modal--pageWrapper--help {
		@apply justify-end items-start;
		@apply pointer-events-none;
	}

	.modal--titlebar {
		@apply flex flex-row items-center justify-between;
		@apply w-full;
	}

	.modal {
		@apply align-bottom sm:align-middle my-8 transform transition-all overflow-hidden;
		@apply bg-white dark:bg-black max-h-full max-w-modal-sm border border-black/20 dark:border-white/5;
		@apply flex flex-col items-center;
		@apply w-full mx-auto;
		@apply relative z-50;
		@apply rounded-xl;
		@apply shadow-xl shadow-black/10 dark:shadow-white/5;

		&--sm {
			@apply w-full max-w-modal-sm;
		}

		&--md {
			@apply w-full max-w-modal-md;
			@apply rounded-xl;
		}

		&--lg {
			@apply w-full max-w-modal-lg;
			@apply rounded-xl;
		}

		&--help {
			@apply m-0 h-full;
			@apply shadow-xl;
			@apply border border-black/25 dark:border-white/25;
			@apply pointer-events-auto rounded-lg;

			.modal--description,
			.modal--content {
				@apply text-left text-base;
			}
		}

		&--danger {
			@apply border-2 border-error dark:border-error;
		}

		.modal--titlebar {
			@apply p-2 border-b-2 border-light dark:border-white/10;

			.button {
				@apply rounded-md;

				&:focus {
					@apply outline-none ring-0 border-0;
				}
			}
		}

		&--title {
			@apply flex-grow px-2;
			@apply text-headline-sm;
		}

		&--content-wrapper:not(&--content-noscroll) {
			@apply h-full overflow-y-auto w-full;
			-webkit-overflow-scrolling: touch;
		}

		&--content-wrapper {
			@apply h-full overflow-hidden w-full flex flex-col;
		}

		&--description {
			@apply px-4 py-5 text-headline-sm text-center;
		}

		&--content:not(&--content-noscroll) {
			@apply w-full text-base px-4 py-5 text-center;
		}

		&--content {
			@apply h-full w-full text-base px-4 py-5 text-center overflow-hidden flex flex-col;
		}

		&--wrapper {
			@apply fixed inset-0 z-50 pointer-events-none;
			@apply overflow-y-auto;
			-webkit-overflow-scrolling: touch;
		}

		&--actions {
			@apply flex items-center justify-center p-4 pb-6 gap-2;
		}

		&--logs {
			@apply flex items-center justify-center p-4 pb-6 gap-2;
		}

	}

	.modal--description + .modal--content,
	.modal--description + .modal--actions,
	.modal--content + .modal--actions{
		@apply pt-0;
	}

	.modal--overlay {
		@apply bg-light/75 dark:bg-[#000]/75 backdrop-filter backdrop-grayscale;
		@apply absolute top-0 left-0;
		@apply h-full w-full;
		@apply z-50;
	}

</style>
