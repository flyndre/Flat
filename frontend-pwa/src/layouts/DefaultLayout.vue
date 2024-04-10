<script setup lang="ts">
import { isOnMobile } from '@/util/mobileDetection';
</script>

<template>
    <div class="h-screen flex flex-col">
        <div class="fixed top-0 left-0 right-0 bottom-0 -z-50">
            <slot name="background" />
        </div>
        <header v-if="!isOnMobile || $slots.title" class="p-2">
            <template v-if="isOnMobile">
                <header class="flex flex-row justify-center items-center gap-2">
                    <h1 class="text-base font-semibold">
                        <slot name="title"></slot>
                    </h1>
                </header>
            </template>
            <template v-else>
                <nav class="flex flex-row justify-between items-center gap-2">
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
        </header>
        <main class="px-2 flex flex-col flex-grow basis-0 overflow-hidden">
            <slot />
        </main>
        <nav
            v-if="
                isOnMobile &&
                ($slots?.['action-right'] || $slots?.['action-left'])
            "
            class="flex flex-row-reverse justify-between items-center gap-2 p-2.5"
        >
            <slot name="action-right">
                <div></div>
            </slot>
            <slot name="action-left">
                <div></div>
            </slot>
        </nav>
    </div>
</template>
