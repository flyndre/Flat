<script setup lang="ts">
import { isOnMobile } from "@/util/mobileDetection";
</script>

<template>
    <template v-if="isOnMobile">
        <header
            v-if="$slots.title"
            class="flex flex-row justify-center items-center gap-2 py-2.5"
        >
            <h1 class="text-base font-semibold">
                <slot name="title"></slot>
            </h1>
        </header>
        <nav
            class="fixed bottom-0 left-0 right-0 flex flex-row justify-between items-center gap-2 p-2.5"
        >
            <slot name="action-left">
                <div></div>
            </slot>
            <slot name="action-right">
                <div></div>
            </slot>
        </nav>
    </template>
    <template v-else>
        <nav class="flex flex-row justify-between items-center gap-2 py-2.5">
            <div class="flex-grow basis-0 flex flex-row gap-2">
                <slot name="action-left">
                    <div></div>
                </slot>
            </div>
            <h1 class="text-base font-semibold leading-[0]">
                <slot name="title"></slot>
            </h1>
            <div class="flex-grow basis-0 flex flex-row-reverse gap-2">
                <slot name="action-right">
                    <div></div>
                </slot>
            </div>
        </nav>
    </template>
</template>
