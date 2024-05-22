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
import { useI18n } from 'vue-i18n';

const visible = defineModel<boolean>('visible', {
    default: false,
});

const props = defineProps<{
    collections: Collection[];
}>();

const { add } = useToast();
const { t } = useI18n();

const exportData = computed(() => collectionListToBackup(props.collections));
const { copy, copied, isSupported } = useClipboard({ source: exportData });

function copyData() {
    copy();
    watchOnce(copied, (success) =>
        add({
            summary: success
                ? t('components.export_dialog.copy_success')
                : t('components.export_dialog.copy_failed'),
            severity: success ? 'success' : 'error',
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
                    :label="$t('universal.back')"
                    severity="secondary"
                    @click="visible = false"
                    text
                >
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiArrowLeft" />
                    </template>
                </Button>
            </template>
            <template #title>
                {{ $t('components.export_dialog.title') }}
            </template>
            <template #action-right>
                <Button
                    :label="$t('components.export_dialog.action_download')"
                    severity="primary"
                    @click="downloadData"
                >
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiFileDownload" />
                    </template>
                </Button>
                <Button
                    v-if="isSupported"
                    :label="isOnMobile ? '' : $t('universal.copy')"
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
