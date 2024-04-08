<script setup lang="ts">
import MdiIcon from "@/components/MdiIcon.vue";
import { clientId } from "@/data/clientMetadata";
import { collections, collectionService } from "@/data/collections";
import DefaultLayout from "@/layouts/DefaultLayout.vue";
import { mdiArrowLeft, mdiDelete, mdiDeleteSweep, mdiPlus } from "@mdi/js";
import Button from "primevue/button";
import Card from "primevue/card";
import { ref } from "vue";

const newName = ref("");
const createNew = () => {
    collectionService.add({
        name: newName.value,
        adminClientId: clientId.value,
    });
    newName.value = "";
};
</script>

<template>
    <DefaultLayout>
        <template #action-left>
            <router-link :to="{ name: 'home' }">
                <Button label="Back" severity="secondary" text>
                    <template #icon>
                        <MdiIcon class="mr-2.5" :icon="mdiArrowLeft" />
                    </template>
                </Button>
            </router-link>
        </template>
        <template #title> My Collections </template>
        <template #action-right>
            <router-link :to="{ name: 'edit' }">
                <Button label="Create new" severity="primary">
                    <template #icon>
                        <MdiIcon class="mr-2.5" :icon="mdiPlus" />
                    </template>
                </Button>
            </router-link>
        </template>
        <template #default>
            <Card>
                <template #content>
                    <div v-if="collections?.length === 0" class="opacity-30">
                        No Collections yet
                    </div>
                    <div v-else class="flex flex-col mb-4">
                        <div
                            class="flex flex-row justify-between items-center py-2 border-solid border-0 border-b border-b-slate-500 border-opacity-20"
                        >
                            <span class="font-bold"> Name </span>
                            <Button
                                label="Delete All"
                                @click="collectionService.clear()"
                                severity="secondary"
                                text
                            >
                                <template #icon>
                                    <MdiIcon
                                        class="mr-2.5"
                                        :icon="mdiDeleteSweep"
                                    />
                                </template>
                            </Button>
                        </div>
                        <div
                            v-for="collection of collections"
                            class="flex flex-row gap-2 py-2 items-center border-solid border-0 border-b border-b-slate-500 border-opacity-20"
                        >
                            <span class="flex-grow">
                                {{ collection.name }}
                            </span>
                            <Button
                                @click="collectionService.delete(collection.id)"
                                severity="secondary"
                                text
                            >
                                <template #icon>
                                    <MdiIcon :icon="mdiDelete" />
                                </template>
                            </Button>
                        </div>
                    </div>
                </template>
            </Card>
        </template>
    </DefaultLayout>
</template>
