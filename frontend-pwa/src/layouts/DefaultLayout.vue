<script setup lang="ts">
import { useSettings } from '@/plugins/SettingsPlugin';
import { isOnMobile } from '@/util/mobileDetection';

const { settings } = useSettings();
</script>

<template>
    <div class="h-screen flex flex-col">
        <div
            class="fixed top-0 left-0 right-0 bottom-0 -z-50 flex flex-col justify-center items-center overflow-visible"
        >
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
            class="sticky left-0 right-0 bottom-0 p-2.5 flex justify-between items-center gap-2"
            :class="
                settings.handedness === 'right'
                    ? 'flex-row'
                    : 'flex-row-reverse'
            "
        >
            <div
                class="flex-grow flex flex-row gap-2"
                :class="
                    settings.handedness === 'right'
                        ? 'justify-start'
                        : 'justify-end'
                "
            >
                <slot name="action-left">
                    <div></div>
                </slot>
            </div>
            <div
                class="flex-grow flex gap-2 justify-start"
                :class="
                    settings.handedness === 'right'
                        ? 'flex-row-reverse'
                        : 'flex-row'
                "
            >
                <slot name="action-right">
                    <div></div>
                </slot>
            </div>
        </nav>
    </div>
</template>
