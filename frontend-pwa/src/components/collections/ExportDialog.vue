<script setup lang="ts">
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import { Collection } from '@/types/Collection';
import { downloadText } from '@/util/download';
import { collectionListToBackup } from '@/util/importExport';
import { isOnMobile } from '@/util/mobileDetection';
import { mdiArrowLeft, mdiContentCopy, mdiFileDownload } from '@mdi/js';
import { useClipboard, watchOnce } from '@vueuse/core';
import Button from 'primevue/button';
import Sidebar from 'primevue/sidebar';
import { useToast } from 'primevue/usetoast';
import { computed } from 'vue';

const visible = defineModel<boolean>('visible', {
    default: false,
});

const props = defineProps<{
    collections: Collection[];
}>();

const { add } = useToast();

const exportData = computed(() => collectionListToBackup(props.collections));
const { copy, copied, isSupported } = useClipboard({ source: exportData });

function copyData() {
    copy();
    watchOnce(copied, (v) =>
        add({
            summary: v
                ? 'Copied data to clipboard!'
                : 'Failed to copy data to clipboard.',
            severity: v ? 'success' : 'error',
        })
    );
}

function downloadData() {
    downloadText(
        `Flat Export ${new Date().toLocaleString()}.txt`,
        exportData.value
    );
}
</script>

<template>
    <Sidebar
        class="w-full max-w-[787px] h-fit rounded-t-xl -bottom-px p-0 overflow-hidden"
        v-model:visible="visible"
        modal
        position="bottom"
        :block-scroll="true"
        :show-close-icon="false"
        :pt="{
            header: {
                class: 'hidden',
            },
            content: {
                class: 'h-full flex flex-col justify-stretch items-stretch p-0',
            },
        }"
    >
        <DefaultLayout height="60vh">
            <template #action-left>
                <Button
                    label="Back"
                    severity="secondary"
                    @click="visible = false"
                    text
                >
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiArrowLeft" />
                    </template>
                </Button>
            </template>
            <template #title>Export</template>
            <template #action-right>
                <Button
                    label="Download"
                    severity="primary"
                    @click="downloadData"
                >
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiFileDownload" />
                    </template>
                </Button>
                <Button
                    v-if="isSupported"
                    :label="isOnMobile ? '' : 'Copy'"
                    severity="primary"
                    text
                    @click="copyData"
                >
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiContentCopy" />
                    </template>
                </Button>
            </template>
            <template #default>
                <div class="text-xs w-full break-words">
                    {{ exportData }}
                </div>
            </template>
        </DefaultLayout>
    </Sidebar>
</template>
