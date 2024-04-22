<script setup lang="ts">
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import { collectionDB } from '@/data/collections';
import { TOAST_LIFE } from '@/data/constants';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import { dbSafe } from '@/util/dbUtils';
import { backupToCollectionList } from '@/util/importExport';
import {
    mdiArrowLeft,
    mdiCheck,
    mdiCheckboxMultipleMarked,
    mdiCloseBoxMultiple,
    mdiFileUpload,
} from '@mdi/js';
import { computedAsync } from '@vueuse/core';
import Button from 'primevue/button';
import FileUpload, { FileUploadUploadEvent } from 'primevue/fileupload';
import SelectButton from 'primevue/selectbutton';
import Sidebar from 'primevue/sidebar';
import Textarea from 'primevue/textarea';
import { useToast } from 'primevue/usetoast';
import { v4 as uuidv4 } from 'uuid';
import { computed, ref } from 'vue';
import MdiIcon from '../icons/MdiIcon.vue';

const visible = defineModel<boolean>('visible', {
    default: false,
});

const importDataString = ref('');
const loading = ref(false);
const importedCollections = computedAsync(
    async () => backupToCollectionList(importDataString.value),
    [],
    loading
);
const importable = computed(() => importedCollections.value !== undefined);

const { add } = useToast();

const overwriteExisting = ref(false);
const overwriteExistingOptions = [
    {
        label: 'Overwrite existing',
        value: true,
        icon: mdiCloseBoxMultiple,
    },
    {
        label: 'Keep Duplicates',
        value: false,
        icon: mdiCheckboxMultipleMarked,
    },
];

const uploader = async (event: FileUploadUploadEvent) => {
    loading.value = true;
    const file = Array.isArray(event.files) ? event.files[0] : event.files;
    importDataString.value = await file.text();
    loading.value = false;
};

async function importData() {
    loading.value = true;
    try {
        if (overwriteExisting.value) {
            await collectionDB.bulkPut(
                importedCollections.value.map((c) => dbSafe(c))
            );
        } else {
            await collectionDB.bulkPut(
                importedCollections.value.map((c) => ({
                    ...dbSafe(c),
                    id: uuidv4(),
                }))
            );
        }
        add({
            life: TOAST_LIFE,
            severity: 'success',
            summary: 'Successfully imported collections.',
        });
        visible.value = false;
    } catch (error) {
        add({
            life: TOAST_LIFE,
            severity: 'error',
            summary: 'Failed to import collections.',
        });
    } finally {
        loading.value = false;
    }
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
            <template #title>Import</template>
            <template #action-right>
                <Button
                    label="Import"
                    severity="primary"
                    :disabled="!importable"
                    :loading
                    @click="importData"
                >
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiCheck" />
                    </template>
                </Button>
            </template>
            <template #default>
                <div
                    class="h-full grow flex flex-col justify-stretch items-stretch gap-2"
                >
                    <Textarea
                        v-model="importDataString"
                        class="resize-none w-full text-xs grow"
                        placeholder="Paste data here..."
                    />
                    <div
                        class="w-full flex flex-row justify-stretch items-stretch gap-2 flex-wrap"
                    >
                        <SelectButton
                            v-model="overwriteExisting"
                            :options="overwriteExistingOptions"
                            option-value="value"
                            option-label="label"
                            :allow-empty="false"
                            :pt="{
                                root: {
                                    class: 'grow flex flex-row justify-stretch [&>*]:grow',
                                },
                            }"
                        >
                            <template #option="slotProps">
                                <div
                                    class="flex flex-row justify-center items-center flex-nowrap w-full gap-3 min-h-6"
                                >
                                    <MdiIcon :icon="slotProps.option.icon" />
                                    <span
                                        class="text-ellipsis overflow-hidden z-10"
                                    >
                                        {{ slotProps.option.label }}
                                    </span>
                                </div>
                            </template>
                        </SelectButton>
                        <FileUpload
                            mode="basic"
                            chooseLabel="Upload Backup File"
                            accept="text/plain"
                            :multiple="false"
                            :auto="true"
                            :disabled="loading"
                            custom-upload
                            @uploader="uploader"
                            :pt="{
                                root: { class: 'flex-grow' },
                                chooseButton: {
                                    class: 'w-full p-button-secondary',
                                },
                            }"
                        >
                            <template #uploadicon>
                                <MdiTextButtonIcon :icon="mdiFileUpload" />
                            </template>
                        </FileUpload>
                    </div>
                </div>
            </template>
        </DefaultLayout>
    </Sidebar>
</template>
