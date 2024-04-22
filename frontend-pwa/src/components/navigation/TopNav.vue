<script setup lang="ts">
import { computed, useSlots } from 'vue';
const slots = useSlots();
const onlyHeader = computed(
    () => !slots['action-left'] && !slots['action-right']
);
</script>

<template>
    <header class="sticky left-0 top-0 right-0 p-2 z-10 backdrop-blur">
        <nav
            class="flex flex-row justify-between items-center gap-2 h-10 [&>*]:whitespace-nowrap"
        >
            <div
                v-if="!onlyHeader"
                class="flex-grow flex-shrink-0 basis-0 flex flex-row gap-2"
            >
                <slot name="action-left">
                    <div></div>
                </slot>
            </div>
            <h1
                class="flex-grow flex-shrink text-center text-base font-semibold leading-[0]"
            >
                <slot name="title"></slot>
            </h1>
            <div
                v-if="!onlyHeader"
                class="flex-grow flex-shrink-0 basis-0 flex flex-row-reverse gap-2"
            >
                <slot name="action-right">
                    <div></div>
                </slot>
            </div>
        </nav>
    </header>
</template>
