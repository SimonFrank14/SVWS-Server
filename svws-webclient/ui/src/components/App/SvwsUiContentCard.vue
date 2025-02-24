<script setup lang='ts'>
	const props = withDefaults(defineProps<{
		title?: string;
		overflowScroll?: boolean;
		largeTitle?: boolean;
		hasBackground?: boolean;
	}>(), {
		title: '',
		overflowScroll: false,
		largeTitle: false,
		hasBackground: false,
	});
</script>

<template>
	<div class="content-card" :class="{'h-full': overflowScroll, 'svws-has-background': hasBackground}">
		<div v-if="title || $slots.actions || $slots.title" class="content-card--header" :class="{
			'content-card--header--has-actions': $slots.actions
		}">
			<slot name="title">
				<h3 v-if="title" class="content-card--headline" :class="{'content-card--headline--large': largeTitle}" :title="title">
					{{ title }}
				</h3>
			</slot>
			<div v-if="$slots.actions" class="content-card--actions">
				<slot name="actions" />
			</div>
		</div>
		<div class="content-card--content" :class="{'content-card--content--with-title': title || $slots.title, 'content-card--content--overflow-scroll': overflowScroll}">
			<slot />
		</div>
	</div>
</template>

<style lang="postcss">
	.content-card {
		@apply h-fit;

		&.svws-has-background {
			@apply bg-light dark:bg-white/5 px-6 py-3;

      &:first-child {
        @apply pt-6;
      }

      &:last-child {
        @apply pb-6;
      }

			.svws-ui-table {
				@apply rounded-lg;

				.svws-ui-tr {
					.svws-ui-td:last-child {
						@apply !pr-0;
					}
				}

				.svws-ui-tbody:last-child .svws-ui-tr:last-child .svws-ui-td,
				.svws-ui-tfoot {
					@apply border-b-0;
				}
			}

			.svws-ui-thead {
				@apply rounded-t-lg;
			}
		}

		&--content {
			&--with-title {
				@apply mt-4;
			}

			&--overflow-scroll {
				@apply h-full flex flex-col;
			}
		}

		&--headline {
			@apply text-headline-md;
			@apply text-black dark:text-white;
			@apply flex-shrink-0 max-w-full leading-none;

			&--large {
				@apply text-headline;
			}
		}

		&--header {
			@apply inline-flex items-center justify-between mb-1 py-1 w-auto;

			&--has-actions {
				@apply flex gap-x-8 gap-y-1 w-full items-start flex-wrap mb-0;

				.content-card--headline {
					@apply hyphens-auto sm:flex-grow;
				}
			}
		}

		&--actions {
			@apply flex items-center gap-x-2 max-xl:w-full max-xl:mb-3 -mt-1;
		}

    &.col-span-full {
      .content-card--actions {
        @apply max-xl:w-auto max-xl:mb-0 max-md:w-full max-md:mb-3;
      }
    }

    &.h-full {
      .content-card--content {
        @apply min-h-full;
      }
    }
	}

	.content-card--wrapper--light-bg .content-card--header {
		@apply bg-white dark:bg-black;
	}

	.page--wrapper[class*="gost_kursplanung_halbjahr_ergebnis"] .svws-ui-tab-content {
		@apply overflow-hidden;
	}
</style>
