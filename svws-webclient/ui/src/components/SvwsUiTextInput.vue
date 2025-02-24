<template>
	<label class="text-input-component"
		:class="{
			'text-input--filled': (`${data}`.length > 0 && data !== null) || type === 'date',
			'text-input--invalid': (isValid === false),
			'text-input--disabled': disabled,
			'text-input--readonly': readonly,
			'text-input--select': isSelectInput,
			'text-input--statistics': statistics,
			'text-input--search': type === 'search',
			'text-input--date': type === 'date',
			'text-input-component--headless': headless,
			'col-span-full': span === 'full',
			'col-span-2': span === '2',
		}">
		<span v-if="url" data-before="https://" class="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3 opacity-60 before:content-[attr(data-before)]" />
		<span class="icon i-ri-search-line text-input--search-icon" v-if="type === 'search'" />
		<div v-if="readonly && !isSelectInput" :class="{ 'text-input--control': !headless, 'text-input--headless': headless, 'text-input--rounded': rounded, 'text-input--prefix': url, }">
			{{ data }}
		</div>
		<input v-else ref="input"
			v-focus
			:class="{ 'text-input--control': !headless, 'text-input--headless': headless, 'text-input--rounded': rounded, 'text-input--prefix': url, }"
			v-bind="{ ...$attrs }"
			:type="type"
			:value="data"
			:disabled="disabled"
			:required="required"
			:readonly="readonly"
			:aria-labelledby="labelId"
			:placeholder="headless || type === 'search' ? placeholder : ''"
			@input="onInput"
			@keyup.enter="onKeyEnter"
			@blur="onBlur">
		<span v-if="placeholder && !headless && (type !== 'search')" :id="labelId" class="text-input--placeholder"
			:class="{ 'text-input--placeholder--required': required, 'text-input--placeholder--prefix': url }">
			<span>{{ placeholder }}</span>
			<span class="icon-xs i-ri-alert-line ml-0.5 icon-error" v-if="(isValid === false)" />
			<span v-if="(maxLen !== undefined) || (minLen !== undefined)" class="inline-flex ml-1 gap-1" :class="{'text-error': !maxLenValid || !minLenValid, 'opacity-50': maxLenValid || minLenValid}">
				{{ (maxLen !== undefined) && (minLen === undefined) ? ` (max. ${maxLen} Zeichen)` : '' }}
				{{ (minLen !== undefined) && (maxLen === undefined) ? ` (mind. ${minLen} Zeichen)` : '' }}
				{{ (minLen !== undefined) && (maxLen !== undefined) ? ` (zwischen ${minLen} und ${maxLen} Zeichen)` : '' }}
			</span>
			<span v-if="statistics" class="cursor-pointer">
				<svws-ui-tooltip position="right">
					<span class="inline-flex items-center">
						<span class="icon i-ri-bar-chart-2-line icon-statistics pointer-events-auto ml-0.5" />
						<span class="icon i-ri-alert-fill" v-if="data === '' || data === null || data === undefined" />
					</span>
					<template #content>
						{{ statisticsText }}
					</template>
				</svws-ui-tooltip>
			</span>
		</span>
		<span v-if="removable && (type === 'date') && (!readonly)" @keydown.enter="updateData('')" @click.stop="updateData('')" class="svws-icon--remove icon i-ri-close-line" tabindex="0" />
		<span v-if="type === 'date'" class="svws-icon icon i-ri-calendar-line" />
		<span v-if="type === 'email'" class="svws-icon icon i-ri-at-line" />
		<span v-if="type === 'tel'" class="svws-icon icon i-ri-phone-line" />
	</label>
</template>


<script setup lang="ts">

	import { ref, computed, watch, type ComputedRef, type Ref, onBeforeMount } from "vue";
	import { genId } from "../utils";

	defineOptions({
		inheritAttrs: false,
	});

	const input = ref<null | HTMLInputElement>(null);

	const props = withDefaults(defineProps<{
		type?: "text" | "date" | "email" | "search" | "tel" | "password";
		modelValue?: string | null;
		placeholder?: string;
		statistics?: boolean;
		statisticsText?: string;
		valid?: (value: string | null) => boolean;
		disabled?: boolean;
		required?: boolean;
		readonly?: boolean;
		headless?: boolean;
		isSelectInput? : boolean;
		focus?: boolean;
		rounded?: boolean;
		url?: boolean;
		maxLen?: number;
		minLen?: number;
		span?: 'full' | '2';
		removable?: boolean;
	}>(), {
		type: "text",
		modelValue: null,
		placeholder: "",
		statistics: false,
		statisticsText: "Relevant für die Statistik",
		valid: () => true,
		disabled: false,
		required: false,
		readonly: false,
		headless: false,
		isSelectInput: false,
		focus: false,
		rounded: false,
		url: false,
		maxLen: undefined,
		minLen: undefined,
		span: undefined,
		removable: false,
	});

	const emit = defineEmits<{
		"update:modelValue": [value: string | null];
		"change": [value: string | null];
		"blur": [value: string | null];
	}>();

	const vFocus = {
		mounted: (el: HTMLInputElement) => {
			if (props.focus)
				el.focus();
		}
	};

	const data = ref<string | null>(null);
	onBeforeMount(() => data.value = props.modelValue);

	watch(() => props.modelValue, (value: string | null) => updateData(value), { immediate: false });

	const validatorEmail = (value: string | null) : boolean => ((value === null) || (value === '')) ? true : (
		/^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))[^@]?$/.test(value) ||
		/^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/.test(value)
	);

	const isValid = computed((): boolean => {
		let tmpIsValid = true;
		if (props.required && ((data.value === null) || (data.value === '')))
			tmpIsValid = false;
		if (tmpIsValid && (!minLenValid.value || !maxLenValid.value))
			tmpIsValid = false;
		if (tmpIsValid && props.type === "email")
			tmpIsValid = validatorEmail(data.value ?? '');
		if (tmpIsValid)
			tmpIsValid = props.valid(data.value);
		return tmpIsValid;
	})

	function updateData(value: string | null) {
		if (data.value !== value) {
			data.value = value;
			emit("update:modelValue", data.value);
		}
	}

	const minLenValid = computed((): boolean => {
		if ((props.minLen === undefined) || ((data.value === null) && (props.minLen <= 0)))
			return true;
		return (data.value !== null) && (data.value.toLocaleString().length >= props.minLen);
	})

	const maxLenValid = computed((): boolean => {
		if ((props.maxLen === undefined) || (data.value === null))
			return true;
		return data.value.toLocaleString().length <= props.maxLen;
	})

	function onInput(event: Event) {
		const value = (event.target as HTMLInputElement).value;
		if (value !== data.value)
			updateData(value);
	}

	function onBlur(event: Event) {
		if (props.modelValue !== data.value)
			emit("change", data.value);
		emit("blur", data.value);
	}

	function onKeyEnter(event: Event) {
		if (props.modelValue !== data.value)
			emit("change", data.value);
	}

	function reset() {
		data.value = props.modelValue;
	}

	function doFocus() {
		input.value?.focus();
	}

	const labelId = genId();

	const content = computed<string | null>(() => data.value);

	defineExpose<{
		content: ComputedRef<string | null>,
		input: Ref<HTMLInputElement | null>,
		reset: () => void;
		doFocus: () => void;
	}>({ content, input, reset, doFocus });

</script>


<style lang="postcss">

	.text-input-component {
		@apply flex;
		@apply relative;
		@apply w-full;
		@apply overflow-hidden whitespace-nowrap text-base;

		input::placeholder {
			@apply text-black dark:text-white opacity-25;

			.placeholder--visible & {
				@apply text-black dark:text-white;
			}
		}

		&:focus {
			@apply outline-none;
		}

		input {
			@apply cursor-text overflow-ellipsis;

			&[type="email"],
			&[type="tel"] {
				@apply pr-[1.6rem];
			}

			&:focus {
				@apply outline-none;
			}
		}
	}

	.text-input-component .icon.svws-icon {
		@apply pointer-events-none absolute top-1 right-1 bottom-1 w-5 rounded inline-flex items-center justify-end pr-1 text-base opacity-25 mt-1;

		&--remove {
			@apply pointer-events-auto cursor-pointer absolute top-1 right-1 bottom-1 w-5 rounded inline-flex items-center justify-end pr-1 text-base mr-6 mt-1;
			&:hover {
				-webkit-filter: invert(22%) sepia(96%) saturate(2323%) hue-rotate(331deg) brightness(88%) contrast(103%);
				filter: invert(22%) sepia(96%) saturate(2323%) hue-rotate(331deg) brightness(88%) contrast(103%);
			}
		}
	}

	.text-input-component:not(.text-input--readonly) {
		&:hover,
		&:focus-within {
			.svws-icon.icon {
				@apply opacity-50;
			}
		}
	}

	.text-input--date {
		.svws-ui-table & {
			input {
				@apply -my-0.5;
			}
		}

		.text-input--control {
			@apply pr-0;
		}

		.svws-icon {
			@apply w-7;
		}

		&.text-input-component--headless,
		.svws-ui-table .svws-ui-tbody .svws-ui-td & {
			@apply my-auto -ml-1;

			.svws-icon.icon {
				@apply w-6 h-6 -top-1 right-0 relative;
			}
		}

		&:focus-within {
			.svws-icon.icon {
				@apply opacity-75;
			}
		}
	}

	.text-input--statistics .svws-icon.icon {
		@apply text-violet-500 opacity-50;
	}

	.text-input--invalid .svws-icon {
		@apply text-error;
	}

	.text-input--control {
		@apply bg-white dark:bg-black;
		@apply rounded-md border border-black/5 dark:border-white/5;
		@apply h-9 w-full;
		@apply text-base;
		@apply whitespace-nowrap;
		padding: 0.5em 0.7em;

		&:hover {
			@apply border-black/25 dark:border-white/25;
		}
	}

	.text-input--prefix {
		padding-left: 4.3em;
	}

	.text-input--rounded {
		@apply rounded-full;
	}

	.multiselect-input-component .text-input--control,
	.multiselect-input-component .text-input--headless {
		@apply overflow-hidden text-ellipsis;
		padding-right: 3.2rem;
	}

	.text-input-component:focus-within .text-input--control,
	.text-input--filled .text-input--control {
		@apply border-black dark:border-white;
		@apply outline-none;
	}

	.text-input--filled:not(:focus-within):not(:hover) .text-input--control {
		@apply border-black/25 dark:border-white/25;
	}

	.text-input--statistics.text-input--filled:not(:focus-within):not(:hover) .text-input--control,
	.text-input--statistics:not(:focus-within):not(:hover) .text-input--control {
		@apply border-violet-500/25 dark:border-violet-800/25;
	}

	.text-input--invalid.text-input--filled:not(:focus-within):not(:hover) .text-input--control {
		@apply border-error/25 dark:border-error/25;
	}

	.text-input--statistics.text-input--filled:not(:focus-within):hover .text-input--control,
	.text-input--statistics:not(:focus-within):hover .text-input--control {
		@apply border-violet-500/50 dark:border-violet-800/50;
	}

	.text-input--invalid.text-input--filled:not(:focus-within):hover .text-input--control {
		@apply border-error/50 dark:border-error/50;
	}

	.text-input--invalid.text-input--filled:focus-within:hover .text-input--control {
		@apply border-error/50 dark:border-error/50;
	}

	.text-input--filled:not(:focus-within):hover .text-input--control {
		@apply border-black/50 dark:border-white/50;
	}

	.text-input--statistics.text-input-component:focus-within .text-input--control,
	.text-input--statistics.text-input--filled .text-input--control {
		@apply border-violet-500;
	}

	.text-input--readonly:not(.text-input--select):hover .text-input--control {
		@apply border-black/5 dark:border-white/5;
	}

	.text-input--readonly.text-input--filled:not(.text-input--select):hover .text-input--control {
		@apply border-black/25 dark:border-white/25;
	}

	.text-input--control--multiselect-tags {
		@apply border-b-0 rounded-b-none pt-1 pb-0;
	}

	.text-input--statistics {
		.tooltip-trigger--triggered span.icon {
			@apply text-violet-800;
		}
	}

	.text-input--search {
		@apply relative;

		&.text-input--filled {
			@apply text-svws;
		}

		&-icon {
			@apply absolute left-2 opacity-25;
			top: 50%;
			transform: translateY(-50%) scale(90%);

			.text-input-component:not(.text-input--filled):not(:focus-within):not(.text-input--disabled):hover & {
				@apply opacity-50;
			}

			.text-input-component:focus-within &,
			.text-input--filled & {
				@apply opacity-100;
				transform: translateY(-50%) scale(100%);
			}
		}

		input {
			@apply pl-8;
		}
	}

	.text-input--control[type="date"]::-webkit-inner-spin-button,
	.text-input--control[type="date"]::-webkit-calendar-picker-indicator {
		@apply opacity-0;
	}

	.text-input--readonly .text-input--control {
		@apply pointer-events-auto cursor-default select-none;
	}

	.text-input--placeholder {
		@apply absolute;
		@apply pointer-events-none;
		@apply opacity-60;
		@apply transform;
		@apply flex items-center font-medium;

		top: 0.5em;
		left: 0.7em;
		line-height: 1.33;
	}

	.text-input-component:not(.text-input--filled) .text-input--placeholder {
		@apply font-normal;

		.wrapper--tag-list & {
			@apply font-medium;
		}

		.wrapper--tag-list:not(.wrapper--filled) & {
			@apply font-medium;
		}
	}

	.text-input-component:not(.text-input--filled):not(:focus-within):not(.text-input--disabled):not(.text-input--readonly):hover .text-input--placeholder {
		@apply opacity-100;
	}

	.text-input--placeholder--prefix {
		left: 4.5em;
		top: 0.5em;
	}

	.multiselect-input-component .text-input--placeholder {
		top: 0.5em;
	}

	.text-input-component:focus-within .text-input--placeholder,
	.text-input--filled .text-input--placeholder {
		@apply -translate-y-1/2;
		@apply bg-white dark:bg-black opacity-100;
		@apply rounded;
		@apply px-1;

		top: 0;
		left: 0.7em;
		font-size: 0.78rem;

		&:after {
			content: "";
		}
	}

	.text-input--statistics .text-input--placeholder {
		@apply text-violet-500 font-bold;
	}

	.text-input--invalid:not(:focus-within) .text-input--placeholder,
	.text-input--invalid:not(:focus-within) .text-input--control {
		@apply text-error;
	}

	.text-input--disabled {
		@apply cursor-default;

		.text-input--placeholder {
			@apply text-black/25 dark:text-white/25;
		}
	}

	.text-input--control:disabled {
		@apply bg-black/10 dark:bg-white/10 border-black/25 dark:border-white/25 text-black;
		@apply opacity-20;
		@apply pointer-events-none;
	}

	.text-input-component:focus-within,
	.text-input--filled {
		@apply overflow-visible;
	}

	.text-input--placeholder--required:after {
		@apply text-error inline-block font-normal relative;
		content: "*";
		font-size: 1.2em;
		margin-bottom: -0.2em;
		top: -0.1em;
		left: 0.1em;
	}

	.text-input--headless,
	.svws-ui-table .text-input--control {
		@apply w-full whitespace-nowrap border-0 outline-none;

		&:not([class*="bg-"]) {
			background-color: unset;
		}

		&::placeholder {
			@apply font-normal;
			color: inherit;
		}

		&:hover:not(:focus) {
			@apply underline decoration-dotted underline-offset-2;
		}
	}

	.text-input--inline {
		@apply cursor-text underline decoration-dotted underline-offset-2;
	}

	.text-input-component .icon.svws-icon--remove:focus-visible {
		-webkit-filter: invert(22%) sepia(96%) saturate(2323%) hue-rotate(331deg) brightness(88%) contrast(103%);
		filter: invert(22%) sepia(96%) saturate(2323%) hue-rotate(331deg) brightness(88%) contrast(103%);
	}

</style>
