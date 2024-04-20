<script setup lang="ts">
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import { Collection } from '@/types/Collection';
import { downloadText } from '@/util/download';
import { collectionListToBackup } from '@/util/import';
import { isOnMobile } from '@/util/mobileDetection';
import {
    mdiArrowLeft,
    mdiClose,
    mdiContentCopy,
    mdiFileDownload,
} from '@mdi/js';
import { useClipboard, watchOnce } from '@vueuse/core';
import Button from 'primevue/button';
import ScrollPanel from 'primevue/scrollpanel';
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
        `Flat Export ${new Date().toLocaleString()}`,
        exportData.value
    );
}
</script>

<template>
    <Sidebar
        class="w-full max-w-[787px] h-fit rounded-t-xl -bottom-px"
        v-model:visible="visible"
        modal
        position="bottom"
        header="help"
        :block-scroll="true"
        :show-close-icon="false"
        :pt="{
            header: {
                class: 'flex flex-row justify-stretch gap-2',
            },
            content: {
                class: 'h-full flex flex-col justify-stretch items-stretch',
            },
        }"
    >
        <template #header>
            <div
                v-if="!isOnMobile"
                class="grow shrink-0 basis-0 flex flex-row justify-start gap-2"
            >
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
            </div>
            <span class="text-center grow basis-0 font-bold">Export</span>
            <div
                v-if="!isOnMobile"
                class="grow basis-0 flex flex-row justify-end gap-2"
            >
                <Button
                    v-if="isSupported"
                    label="Copy"
                    severity="primary"
                    text
                    @click="copyData"
                >
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiContentCopy" />
                    </template>
                </Button>
                <Button
                    label="Download"
                    severity="primary"
                    @click="downloadData"
                >
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiFileDownload" />
                    </template>
                </Button>
            </div>
        </template>
        <template #default>
            <ScrollPanel class="h-[65vh]">
                <div class="text-xs w-full break-words">
                    {{ exportData }}
                </div>
            </ScrollPanel>
            <div
                v-if="isOnMobile"
                class="w-full sticky bottom-0 left-0 right-0 z-10 shrink-0 mt-5 flex flex-row gap-2"
            >
                <Button
                    class="mr-auto"
                    label="Close"
                    severity="secondary"
                    @click="visible = false"
                    text
                >
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiClose" />
                    </template>
                </Button>
                <Button
                    v-if="isSupported"
                    label="Copy"
                    severity="primary"
                    text
                    @click="copyData"
                >
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiContentCopy" />
                    </template>
                </Button>
                <Button
                    label="Download"
                    severity="primary"
                    @click="downloadData"
                >
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiFileDownload" />
                    </template>
                </Button>
            </div>
        </template>
    </Sidebar>
</template>
