<script setup lang="ts">
import ExportDialog from '@/components/collections/ExportDialog.vue';
import ImportDialog from '@/components/collections/ImportDialog.vue';
import MdiIcon from '@/components/icons/MdiIcon.vue';
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import { collections, collectionService } from '@/data/collections';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import { Collection } from '@/types/Collection';
import {
    mdiArrowLeft,
    mdiChevronRight,
    mdiDeleteSweep,
    mdiPlus,
    mdiTrayArrowDown,
    mdiTrayArrowUp,
} from '@mdi/js';
import Button from 'primevue/button';
import Card from 'primevue/card';
import Column from 'primevue/column';
import DataTable from 'primevue/datatable';
import { MenuItem } from 'primevue/menuitem';
import SplitButton from 'primevue/splitbutton';
import { ref } from 'vue';

const selectedCollections = ref<Collection[]>([]);
function deleteSelected() {
    collectionService.bulkDelete(selectedCollections.value.map((c) => c.id));
    selectedCollections.value = [];
}
function deleteSingle(id: string) {
    collectionService.delete(id);
}

const selectedActions: MenuItem[] = [
    {
        label: 'Export',
        command: () => (exportDialogVisible.value = true),
        disabled: () => selectedCollections.value.length === 0,
        icon: mdiTrayArrowUp,
    },
];

const exportDialogVisible = ref(false);
const importDialogVisible = ref(false);
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
            <Card>
                <template #content>
                    <div v-if="collections?.length === 0" class="opacity-30">
                        No Collections yet
                    </div>
                    <div v-else class="flex flex-col">
                        <DataTable
                            v-model:selection="selectedCollections"
                            :value="collections"
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
                                            selectedCollections.length === 0
                                                ? ''
                                                : `${selectedCollections.length} Selected`
                                        }}
                                    </div>
                                    <SplitButton
                                        label="Delete Selected"
                                        severity="secondary"
                                        :model="selectedActions"
                                        :disabled="
                                            selectedCollections.length === 0
                                        "
                                        text
                                        @click="deleteSelected"
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
                                                    class="w-full flex flex-row justify-between items-center"
                                                >
                                                    <span>
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
