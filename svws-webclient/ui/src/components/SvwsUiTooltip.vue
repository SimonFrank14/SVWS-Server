<template>
	<span @mouseenter="hoverEnterTooltip"
		@mouseleave="hoverLeaveTooltip"
		@focus="showTooltip"
		@blur="hideTooltip"
		@click="toggleTooltip"
		class="inline-flex flex-wrap items-center gap-0.5 tooltip-trigger"
		:class="{'tooltip-trigger--underline': indicator, 'tooltip-trigger--triggered': isOpen, 'tooltip-trigger--danger': color === 'danger' || (indicator && indicator === 'danger'), 'cursor-help': !hover, 'cursor-default': hover }"
		ref="reference" v-bind="$attrs">
		<slot />
		<template v-if="(indicator && indicator !== 'underline') || $slots.icon">
			<slot name="icon">
				<span class="icon i-ri-information-fill icon--indicator" v-if="indicator === 'info'" />
				<span class="icon i-ri-alert-fill icon--indicator" v-else-if="indicator === 'danger'" />
				<span class="icon i-ri-question-line icon--indicator -my-1 text-headline-md" v-else />
			</slot>
		</template>
	</span>
	<Teleport to="body">
		<Transition enter-active-class="duration-200 ease-out"
			enter-from-class="transform opacity-0"
			enter-to-class="opacity-100"
			leave-active-class="duration-100 ease-in"
			leave-from-class="opacity-100"
			leave-to-class="transform opacity-0">
			<div v-if="isOpen"
				:style="floatingStyles"
				class="tooltip transition-opacity"
				:class="[`tooltip--${color}`, {'tooltip--autosize': autosize}]"
				ref="floating">
				<span v-if="showArrow"
					:style="{
						left: middlewareData.arrow?.x != null ? middlewareData.arrow.x + 'px' : '',
						top: middlewareData.arrow?.y != null ? middlewareData.arrow.y + 'px' : '',
						...floatingArrowBalance,
					}"
					class="absolute rotate-45 bg-inherit aspect-square w-2"
					ref="floatingArrow" />
				<slot name="content" />
			</div>
		</Transition>
	</Teleport>
</template>

<script setup lang="ts">
	import { useFloating, autoUpdate, arrow, flip, offset, shift } from "@floating-ui/vue";
	import { ref, computed } from "vue";
	import { onClickOutside } from '@vueuse/core'

	const props = withDefaults(defineProps<{
		position?: "top" | "top-start" | "top-end" | "bottom" | "bottom-start" | "bottom-end" | "left" | "left-start" | "left-end" | "right" | "right-start" | "right-end";
		hover?: boolean;
		showArrow?: boolean;
		color?: "primary" | "light" | "dark" | "danger";
		indicator?: "help" | "info" | "danger" | "underline" | false;
		keepOpen?: boolean;
		initOpen?: boolean;
		autosize?: boolean;
	}>(), {
		position: "bottom",
		showArrow: true,
		color: "light",
		hover: true,
		indicator: "underline",
		keepOpen: false,
		initOpen: false,
		autosize: false,
	});

	const emit = defineEmits<{
		"close": [value: void];
	}>();

	const isOpen = ref(false);
	const reference = ref(null);
	const floating = ref(null);
	const floatingArrow = ref(null);

	if (props.hover === false || props.initOpen === true)
		onClickOutside(floating, hideTooltip, { ignore: [reference] });

	if ((props.keepOpen === true) || (props.initOpen === true))
		isOpen.value = true;

	const { placement, middlewareData, floatingStyles } = useFloating(
		reference,
		floating,
		{
			placement: props.position,
			middleware: [flip(), shift(), offset(props.showArrow ? 6 : 2), arrow({element: floatingArrow})],
			whileElementsMounted: autoUpdate,
		}
	);

	const floatingArrowBalance = computed(() => {
		if (props.showArrow === false)
			return {};
		const flipped = {
			top: "bottom",
			right: "left",
			bottom: "top",
			left: "right",
		};
		const side = placement.value.split('-')[0] as keyof typeof flipped;
		return {[flipped[side]]: '-4px'};
	});

	function showTooltip() {
		isOpen.value = true;
	}

	function hoverEnterTooltip() {
		if (props.hover && !props.keepOpen)
			showTooltip();
	}

	function hideTooltip() {
		if (props.keepOpen)
			return;
		isOpen.value = false;
		emit("close");
	}

	function hoverLeaveTooltip() {
		if (props.hover && !props.keepOpen)
			isOpen.value = false;
	}

	function toggleTooltip() {
		if (!props.keepOpen)
			isOpen.value = !isOpen.value;
	}

</script>

<style lang="postcss">
.tooltip-trigger {
	.icon--indicator {
		@apply text-black dark:text-white;
		width: 1em;
		height: 1em;
		margin-left: 0.1em;
	}

	.cursor-pointer & {
		cursor: pointer;
	}

	.cursor-auto & {
		cursor: auto;
	}

	&--triggered {
		.icon--indicator span.icon {
			-webkit-filter: invert(44%) sepia(52%) saturate(1260%) hue-rotate(173deg) brightness(91%) contrast(86%);
			filter: invert(44%) sepia(52%) saturate(1260%) hue-rotate(173deg) brightness(91%) contrast(86%);

			.page--statistik & {
				@apply text-violet-500;
			}

			.text-error & {
				color: inherit;
			}
		}
	}

	&--danger {
		span.icon {
			-webkit-filter: invert(22%) sepia(96%) saturate(2323%) hue-rotate(331deg) brightness(88%) contrast(103%);
			filter: invert(22%) sepia(96%) saturate(2323%) hue-rotate(331deg) brightness(88%) contrast(103%);
		}
	}

	&--underline {
		@apply underline decoration-black/20 dark:decoration-white/20 decoration-dashed;

		&.tooltip-trigger--triggered {
			@apply no-underline;
		}
	}
}

.tooltip {
	@apply rounded-md z-50;
	@apply px-2 py-0.5 w-max max-w-[24rem];
	@apply bg-white text-black border border-light dark:bg-black dark:text-white dark:border-white/5;
	box-shadow: -8px -8px 25px -3px rgb(0 0 0 / 0.1), 8px 8px 25px -3px rgb(0 0 0 / 0.1), -4px 4px 6px -4px rgb(0 0 0 / 0.1);

	&--autosize {
		@apply max-w-none;
	}

	&--primary {
		@apply bg-svws text-white dark:bg-svws border-none;
		@apply shadow-sm shadow-black/25;

		.page--statistik & {
			@apply bg-violet-500 dark:bg-violet-500;
		}
	}

	&--danger {
		@apply bg-error dark:bg-error text-white border border-error/5 shadow-error/10 shadow-md;
	}
}
</style>
