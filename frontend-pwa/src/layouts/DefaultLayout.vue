<script setup lang="ts">
import BottomNav from '@/components/navigation/BottomNav.vue';
import TopNav from '@/components/navigation/TopNav.vue';
import { useSettings } from '@/plugins/SettingsPlugin';
import { isOnMobile } from '@/util/mobileDetection';
import ScrollPanel from 'primevue/scrollpanel';

const { settings } = useSettings();
</script>

<template>
    <ScrollPanel class="min-h-[100dvh] h-[100dvh] max-h-[100dvh]">
        <div
            class="max-w-[800px] mx-auto min-h-[100dvh] flex flex-col justify-stretch items-stretch"
        >
            <div
                class="fixed top-0 left-0 right-0 bottom-0 -z-50 flex flex-col justify-center items-center overflow-visible"
            >
                <slot name="background" />
            </div>
            <TopNav v-if="!isOnMobile || $slots.title">
                <template v-if="!isOnMobile" #action-left>
                    <slot name="action-left" />
                </template>
                <template #title>
                    <slot name="title" />
                </template>
                <template v-if="!isOnMobile" #action-right>
                    <slot name="action-right" />
                </template>
            </TopNav>
            <main class="flex flex-col grow px-2 pb-2 h-full">
                <slot />
            </main>
            <BottomNav
                v-if="
                    isOnMobile &&
                    ($slots?.['action-right'] || $slots?.['action-left'])
                "
                :layout="settings.handedness"
            >
                <template #action-left>
                    <slot name="action-left" />
                </template>

                <template #action-right>
                    <slot name="action-right" />
                </template>
            </BottomNav>
        </div>
    </ScrollPanel>
</template>
