<template>
	<button class="button" :class="{
		'button--primary': type === 'primary',
		'button--secondary': type === 'secondary',
		'button--danger': (type === 'error') || (type === 'danger'),
		'button--transparent': type === 'transparent',
		'button--icon': type === 'icon',
		'button--trash': type === 'trash',
		'button--small': size === 'small',
		'button--big': size === 'big',
	}" :disabled="disabled" ref="addButton">
		<slot v-if="type !== 'trash'" />
		<span v-if="type === 'trash'" class="button--trash-icon">
			<span class="inline-block icon i-ri-delete-bin-line icon--line" />
			<span class="inline-block icon i-ri-delete-bin-fill icon--fill" />
		</span>
		<span v-if="$slots.badge" class="button--badge">
			<slot name="badge" />
		</span>
	</button>
</template>

<script lang="ts" setup>

	import type { ButtonType } from '../types';
	import { onMounted, ref } from "vue";

	const addButton = ref<HTMLButtonElement|null>(null);

	const props = withDefaults(defineProps<{
		type?: ButtonType;
		disabled?: boolean;
		size?: 'small' | 'normal' | 'big';
		hasFocus?: boolean;
	}>(),{
		type: 'primary',
		disabled: false,
		size: 'normal',
		hasFocus: false
	});

	onMounted(() => {
		if(props.hasFocus && (addButton.value !== null))
			addButton.value.focus();
	})

</script>

<style lang="postcss">
.button {
	@apply rounded-md border;
	@apply select-none relative;
	@apply text-button font-bold;
	@apply flex items-center;
	gap: 0.25em;
	padding: 0.45em 0.75em;

  .svws-ui-table-filter & {
    min-height: 2.25rem;
  }

	svg {
		margin-top: -0.1em;
		margin-bottom: -0.1em;
	}

	&:hover {
		@apply brightness-110;
	}

	&:focus {
		@apply outline-none;
	}

	&:focus-visible {
		@apply ring;
	}

	&:active {
		@apply ring-0 brightness-100;
	}

	.svws-ui-tfoot & {
		@apply h-7 min-h-[unset];
	}

	span.icon {
		@apply -my-1;
	}
}

.button--primary {
	@apply bg-svws text-white border-svws;

	.icon, .icon-sm, .icon-lg {
		-webkit-filter: invert(95%) sepia(100%) saturate(14%) hue-rotate(213deg) brightness(104%) contrast(104%);
		filter: invert(95%) sepia(100%) saturate(14%) hue-rotate(213deg) brightness(104%) contrast(104%);
	}

	.page--statistik & {
		@apply bg-violet-500 border-violet-500;
	}

	&:focus-visible {
		@apply ring-svws/50;

		.page--statistik & {
			@apply ring-violet-500/50;
		}
	}
}

.button--secondary {
	@apply bg-transparent dark:bg-transparent text-black dark:text-white border-black/90 dark:border-white/90;

	.notification--error &,
	.svws-ui-stundenplan--unterricht--warning & {
		@apply text-white dark:text-black border-white/25 dark:border-black/25;
	}

	&:hover {
		@apply border-svws text-svws;
		.icon, .icon-sm, .icon-lg {
			-webkit-filter: invert(44%) sepia(52%) saturate(1260%) hue-rotate(173deg) brightness(91%) contrast(86%);
			filter: invert(44%) sepia(52%) saturate(1260%) hue-rotate(173deg) brightness(91%) contrast(86%);
		}

		.page--statistik & {
			@apply border-violet-500 text-violet-500;
		}

		.notification--error &,
		.svws-ui-stundenplan--unterricht--warning & {
			@apply border-white dark:border-black text-white dark:text-black brightness-100;
		}
	}

	&:focus-visible {
		@apply ring-svws/25 border-svws;

		.page--statistik & {
			@apply ring-violet-500/25 border-violet-500;
		}

		.notification--error &,
		.svws-ui-stundenplan--unterricht--warning & {
			@apply ring-white/25 dark:ring-black/25 border-white dark:border-black;
		}
	}

	&:active {
		@apply bg-svws/5 brightness-100;

		.notification--error &,
    .svws-ui-stundenplan--unterricht--warning &{
			@apply bg-white/10 dark:bg-black/10;
		}
	}
}

.button--transparent {
	@apply bg-transparent border-transparent dark:bg-transparent dark:border-transparent;

	&:hover {
		@apply bg-black/10 dark:bg-white/5 brightness-95;
	}

	&:focus-visible {
		@apply bg-black/10 dark:bg-white/5 ring-black/25 dark:ring-white/25;
	}
}

.button--danger {
	@apply bg-transparent dark:bg-transparent text-error border-error;

	.icon, .icon-sm, .icon-lg {
		-webkit-filter: invert(22%) sepia(96%) saturate(2323%) hue-rotate(331deg) brightness(88%) contrast(103%);
		filter: invert(22%) sepia(96%) saturate(2323%) hue-rotate(331deg) brightness(88%) contrast(103%);
	}

	&:hover,
	&:focus {
		@apply bg-error text-white;
	.icon, .icon-sm, .icon-lg {
		-webkit-filter: invert(95%) sepia(100%) saturate(14%) hue-rotate(213deg) brightness(104%) contrast(104%);
		filter: invert(95%) sepia(100%) saturate(14%) hue-rotate(213deg) brightness(104%) contrast(104%);
	}
	}

	&:focus-visible {
		@apply ring-error/50;
	}
}

.button--trash {
	@apply bg-transparent rounded relative;
	@apply py-0 px-2;
	.icon, .icon-sm, .icon-lg {
		-webkit-filter: invert(22%) sepia(96%) saturate(2323%) hue-rotate(331deg) brightness(88%) contrast(103%);
		filter: invert(22%) sepia(96%) saturate(2323%) hue-rotate(331deg) brightness(88%) contrast(103%);
	}
	border: 0 !important;
	padding: 0.2em !important;
	width: 1.6em;
	height: 1.6em;

	.icon--fill {
		@apply hidden;
	}

	&:hover {
		@apply bg-error;
		.icon, .icon-sm, .icon-lg {
			-webkit-filter: invert(95%) sepia(100%) saturate(14%) hue-rotate(213deg) brightness(104%) contrast(104%);
			filter: invert(95%) sepia(100%) saturate(14%) hue-rotate(213deg) brightness(104%) contrast(104%);
		}

		.icon--line {
			@apply hidden;
		}

		.icon--fill {
			@apply inline-block;
		}
	}

	&:focus {
		@apply bg-error;
		/* @apply icon-white; */
		.icon, .icon-sm, .icon-lg {
	-webkit-filter: invert(95%) sepia(100%) saturate(14%) hue-rotate(213deg) brightness(104%) contrast(104%);
	filter: invert(95%) sepia(100%) saturate(14%) hue-rotate(213deg) brightness(104%) contrast(104%);
		}
	}

	&:focus-visible {
		@apply ring-error/25;
	}
}

.button--trash,
.button--icon {
	&.button--small,
  	.svws-ui-tbody & {
		@apply text-sm font-medium h-6 w-6;
		padding: 0.3em !important;

		svg {
			width: 1.2em;
			height: 1.2em;
		}
	}

	.svws-ui-tfoot &.button {
		@apply text-button h-7 w-7;
		padding: 0.25em !important;

		svg {
			width: 1.3em;
			height: 1.3em;
		}
	}
}

.button--icon {
	@apply p-1.5 justify-center border-0 items-center;
	@apply w-9 h-9;

	svg {
		width: 1.3rem;
		height: 1.3rem;
	}

	&:hover, &:focus {
		@apply bg-black/10 dark:bg-white/5;
	}

	&:focus-visible {
		@apply ring-black/25 dark:ring-white/25;
	}
}

.button:disabled {
	&,
	&:hover,
	&:focus,
	&:focus-visible {
		@apply bg-black/25 border-black/50 text-black dark:bg-white/25 dark:border-white/50 dark:text-white;
		@apply opacity-25;
		@apply cursor-not-allowed pointer-events-none;
		span.icon {
			-webkit-filter: invert(32%) sepia(97%) saturate(0%) hue-rotate(163deg) brightness(103%) contrast(104%);
			filter: invert(32%) sepia(97%) saturate(0%) hue-rotate(163deg) brightness(103%) contrast(104%);
		}
	}
}

.hover--danger:hover {
	@apply text-error bg-error/10;
}

.button--small,
.content-card--header .button,
.svws-ui-tbody .button,
.router-tab-bar--subnav .button,
.router-tab-bar--subnav .radio--label {
	@apply text-sm font-bold;
}

.button--small,
.svws-ui-tbody .button,
.content-card--header .button,
.router-tab-bar--subnav .button {
	padding: 0.3rem 0.65rem;
}

.svws-ui-tbody .button {
	@apply -my-2 h-full self-center rounded;
	padding: 0.1rem 0.5rem;

	&.button--transparent {
		@apply px-1.5;
	}
}

.svws-ui-tbody .button--icon.button--small {
	@apply w-5 h-5 -m-0.5;
	@apply p-0 !important;
}

.button--big {
	padding-top: 0.64em;
	padding-bottom: 0.64em;
}

.button--badge {
	@apply absolute top-0 left-[100%];
	@apply font-bold text-white;
	@apply bg-primary rounded-full shadow;
	@apply flex items-center justify-center;
	@apply pointer-events-none;
	@apply -mt-3 -ml-3;
	@apply px-1.5;
	@apply h-5;
	font-size: 0.8rem;

	.svws-ui-tfoot .button &,
	.content-card--header .button &,
	.router-tab-bar--subnav .button & {
		@apply -mt-0 h-4 -ml-1.5;
		font-size: 0.75rem;
	}

	.router-tab-bar--subnav .button & {
		@apply -ml-3;
	}
}
</style>
