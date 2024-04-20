<script setup lang="ts">
import ExportDialog from '@/components/collections/ExportDialog.vue';
import ImportDialog from '@/components/collections/ImportDialog.vue';
import MdiIcon from '@/components/icons/MdiIcon.vue';
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import { collections, collectionService } from '@/data/collections';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import { Collection } from '@/types/Collection';
import { dbSafe } from '@/util/dbUtils';
import { isOnMobile } from '@/util/mobileDetection';
import {
    mdiArrowLeft,
    mdiCheck,
    mdiCheckboxMultipleBlank,
    mdiChevronRight,
    mdiClose,
    mdiDeleteSweep,
    mdiPlus,
    mdiTrayArrowDown,
    mdiTrayArrowUp,
} from '@mdi/js';
import { computedAsync } from '@vueuse/core';
import Button from 'primevue/button';
import Card from 'primevue/card';
import Column from 'primevue/column';
import DataTable from 'primevue/datatable';
import Dialog from 'primevue/dialog';
import { MenuItem } from 'primevue/menuitem';
import SplitButton from 'primevue/splitbutton';
import { v4 as uuidv4 } from 'uuid';
import { computed, ref } from 'vue';

const displayedCollections = computedAsync(() =>
    collections.value.sort((a, b) => a.name.localeCompare(b.name))
);
const selectedCollections = ref<Collection[]>([]);
const selectionEmpty = computed(() => selectedCollections.value?.length === 0);
function deleteSelected() {
    collectionService.bulkDelete(selectedCollections.value.map((c) => c.id));
    selectedCollections.value = [];
}
function deleteSingle(id: string) {
    collectionService.delete(id);
}
function duplicateSelected() {
    collectionService.bulkAdd([
        ...selectedCollections.value.map((c) => ({
            ...dbSafe(c),
            name: `${c.name} (copy)`,
            id: uuidv4(),
        })),
    ]);
    selectedCollections.value = [];
}

const selectedActions: MenuItem[] = [
    {
        label: 'Duplicate',
        command: duplicateSelected,
        disabled: () => selectionEmpty.value,
        icon: mdiCheckboxMultipleBlank,
    },
    {
        label: 'Export',
        command: () => (exportDialogVisible.value = true),
        disabled: () => selectionEmpty.value,
        icon: mdiTrayArrowUp,
    },
];

function deleteDialogConfirm() {
    deleteSelected();
    deleteDialogVisible.value = false;
}

const exportDialogVisible = ref(false);
const importDialogVisible = ref(false);
const deleteDialogVisible = ref(false);
</script>

<template>
    <DefaultLayout>
        <template #action-left>
            <router-link :to="{ name: 'home' }">
                <Button label="Back" severity="secondary" text>
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiArrowLeft" />
                    </template>
                </Button>
            </router-link>
        </template>
        <template #title> My Collections </template>
        <template #action-right>
            <router-link :to="{ name: 'create' }">
                <Button label="Create new" severity="primary">
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiPlus" />
                    </template>
                </Button>
            </router-link>
            <Button
                label="Import"
                severity="secondary"
                text
                @click="importDialogVisible = true"
            >
                <template #icon>
                    <MdiTextButtonIcon :icon="mdiTrayArrowDown" />
                </template>
            </Button>
        </template>
        <template #default>
            <ExportDialog
                v-model:visible="exportDialogVisible"
                :collections="selectedCollections"
            />
            <ImportDialog v-model:visible="importDialogVisible" />
            <Dialog
                v-model:visible="deleteDialogVisible"
                :closable="false"
                :draggable="false"
                modal
                :position="isOnMobile ? 'bottom' : 'top'"
                class="overflow-hidden"
                :header="`Delete ${selectedCollections?.length === collections?.length ? 'all' : selectedCollections?.length} Collection${selectedCollections?.length === 1 ? '' : 's'}?`"
            >
                Are you sure you want to delete
                {{
                    selectedCollections?.length === collections?.length
                        ? 'all'
                        : selectedCollections?.length
                }}
                Collection{{ selectedCollections?.length === 1 ? '' : 's' }}?
                <template #footer>
                    <div
                        class="w-full flex flex-row justify-stretch gap-2 [&>*]:grow"
                    >
                        <Button
                            label="Cancel"
                            severity="secondary"
                            @click="deleteDialogVisible = false"
                        >
                            <template #icon>
                                <MdiTextButtonIcon :icon="mdiClose" />
                            </template>
                        </Button>
                        <Button
                            label="Delete"
                            severity="danger"
                            @click="deleteDialogConfirm"
                        >
                            <template #icon>
                                <MdiTextButtonIcon :icon="mdiCheck" />
                            </template>
                        </Button>
                    </div>
                </template>
            </Dialog>
            <Card>
                <template #content>
                    <div v-if="collections?.length === 0" class="opacity-30">
                        No Collections yet
                    </div>
                    <div v-else class="flex flex-col">
                        <DataTable
                            v-model:selection="selectedCollections"
                            :value="displayedCollections"
                            :dataKey="(c: Collection) => c.id"
                            :pt="{
                                bodyRow: {
                                    class: '[&>*]:p-2 first:[&>*]:pl-0 last:[&>*]:pr-0 [&>*]:last:border-b-0 [&>*]:last:pb-0',
                                },
                                headerRow: {
                                    class: '[&>*]:p-2 [&>*]:pt-0 first:[&>*]:pl-0 last:[&>*]:pr-0',
                                },
                            }"
                        >
                            <Column selectionMode="multiple" header-class="w-6">
                            </Column>
                            <Column
                                header-class="flex flex-row justify-end [&>*]:contents [&>*]:whitespace-nowrap"
                            >
                                <template #header>
                                    <div class="flex-grow text-left px-4 py-2">
                                        {{
                                            selectionEmpty
                                                ? ''
                                                : `${selectedCollections.length} Selected`
                                        }}
                                    </div>
                                    <SplitButton
                                        label="Delete"
                                        severity="secondary"
                                        :model="selectedActions"
                                        :disabled="selectionEmpty"
                                        @click="deleteDialogVisible = true"
                                    >
                                        <template #icon>
                                            <MdiTextButtonIcon
                                                :icon="mdiDeleteSweep"
                                            />
                                        </template>
                                        <template #menuitemicon="slotProps">
                                            <MdiTextButtonIcon
                                                :icon="slotProps.item.icon"
                                            />
                                        </template>
                                    </SplitButton>
                                </template>
                                <template #body="slotProps">
                                    <router-link
                                        :to="{
                                            name: 'edit',
                                            params: { id: slotProps.data.id },
                                        }"
                                    >
                                        <Button
                                            class="w-full"
                                            severity="contrast"
                                            text
                                        >
                                            <template #default>
                                                <div
                                                    class="w-full flex flex-row justify-between items-center gap-2"
                                                >
                                                    <span
                                                        class="text-left whitespace-nowrap"
                                                    >
                                                        {{
                                                            slotProps.data.name
                                                        }}
                                                    </span>
                                                    <MdiIcon
                                                        :icon="mdiChevronRight"
                                                    />
                                                </div>
                                            </template>
                                        </Button>
                                    </router-link>
                                </template>
                            </Column>
                        </DataTable>
                    </div>
                </template>
            </Card>
        </template>
    </DefaultLayout>
</template>
