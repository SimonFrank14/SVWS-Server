<template>
	<footer class="status svws-ui-bg-white svws-ui-text-black svws-ui-border-dark-20">
		<s-loading-status />
		<span v-if="error !== undefined" class="text-red-400 font-bold">{{ error }}</span>
	</footer>
</template>

<script setup lang="ts">

	defineProps<{
		error?: string;
	}>();


</script>

<style lang="postcss">
	footer.status {
		@apply px-4 py-1;
		@apply flex flex-col;
		@apply 2xl:flex-row 2xl:items-center;
		@apply absolute bottom-0;
		@apply w-full;
	}
	.toggle-theme-button {
		@apply flex flex-row items-center;
		@apply space-x-2;
	}
	.toggle-theme-button:focus {
		@apply outline-none ring-2 ring-blue-500 ring-opacity-50;
	}

	.status-nav {
		@apply flex items-center;
		@apply space-x-2;
	}

	.status-progress {
		@apply flex-shrink-0;
		@apply w-64;
	}

	.status-content {
		@apply flex flex-row items-center;
		@apply 2xl:flex-grow 2xl:justify-end;
		@apply text-xs;
		@apply 2xl:text-base;
		@apply space-x-4;
	}
</style>
