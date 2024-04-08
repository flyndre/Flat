<script setup lang="ts">
import AppBar from "@/components/AppBar.vue";
import Button from "primevue/button";
import MdiIcon from "@/components/MdiIcon.vue";
import { mdiArrowLeft, mdiCheck, mdiPlay } from "@mdi/js";
import { ref } from "vue";
import { Collection } from "@/types/collection";
import { clientId } from "@/data/clientMetadata";
import Card from "primevue/card";
import InputText from "primevue/inputtext";
import { collectionService } from "@/data/collections";
import { RouteLocationRaw, useRouter } from "vue-router";
import MainWrapper from "@/components/MainWrapper.vue";

const router = useRouter();
const collection = ref<Collection>({
    name: "",
    adminClientId: clientId.value,
});
const loading = ref(false);
const title = collection.value.id ? "Edit" : "Create";

async function _saveCollection(target: RouteLocationRaw) {
    console.log(collection.value);
    loading.value = true;
    try {
        if (collection.value.id) {
            await collectionService.put({ ...collection.value });
        } else {
            await collectionService.add({ ...collection.value });
        }
        router.push(target);
    } catch (error) {
        // Show error toast
    } finally {
        loading.value = false;
    }
}

const save = () => _saveCollection({ name: "presets" });
const start = () => _saveCollection({ name: "presets" });
</script>

<template>
    <div>
        <AppBar>
            <template #action-left>
                <router-link :to="{ name: 'presets' }">
                    <Button label="Back" severity="secondary" text>
                        <template #icon>
                            <MdiIcon class="mr-2.5" :icon="mdiArrowLeft" />
                        </template>
                    </Button>
                </router-link>
            </template>
            <template #title> {{ title }} </template>
            <template #action-right>
                <div class="flex flex-row gap-2">
                    <Button label="Save" severity="primary" text @click="save">
                        <template #icon>
                            <MdiIcon class="mr-2.5" :icon="mdiCheck" />
                        </template>
                    </Button>
                    <Button label="Start" severity="primary" @click="start">
                        <template #icon>
                            <MdiIcon class="mr-2.5" :icon="mdiPlay" />
                        </template>
                    </Button>
                </div>
            </template>
        </AppBar>
        <MainWrapper>
            <Card>
                <template #content>
                    <div class="flex flex-col gap-2.5">
                        <InputText
                            placeholder="Name"
                            v-model="collection.name"
                        />
                        <div
                            class="w-full h-48 bg-gray-300 flex flex-col items-center justify-center text-gray-500 rounded-md"
                        >
                            Map Placeholder
                        </div>
                    </div>
                </template>
            </Card>
        </MainWrapper>
    </div>
</template>
