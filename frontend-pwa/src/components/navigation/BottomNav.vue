<script setup lang="ts">
defineProps<{
    layout: 'left' | 'right';
}>();
</script>

<template>
    <nav
        class="sticky left-0 right-0 bottom-0 z-10 p-2.5 flex justify-between items-center gap-2 backdrop-blur"
        :class="layout === 'right' ? 'flex-row' : 'flex-row-reverse'"
    >
        <div
            class="flex-grow flex flex-row gap-2"
            :class="layout === 'right' ? 'justify-start' : 'justify-end'"
        >
            <slot name="action-left">
                <div></div>
            </slot>
        </div>
        <div
            class="flex-grow flex gap-2 justify-start"
            :class="layout === 'right' ? 'flex-row-reverse' : 'flex-row'"
        >
            <slot name="action-right">
                <div></div>
            </slot>
        </div>
    </nav>
</template>
