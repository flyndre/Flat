<script setup lang="ts">
import MdiIcon from "@/components/MdiIcon.vue";
import { clientId } from "@/data/clientMetadata";
import { collectionService } from "@/data/collections";
import DefaultLayout from "@/layouts/DefaultLayout.vue";
import { Collection } from "@/types/collection";
import validateCollection from "@/validation/validateCollection";
import { mdiArrowLeft, mdiCheck, mdiPlay } from "@mdi/js";
import Button from "primevue/button";
import Card from "primevue/card";
import InputText from "primevue/inputtext";
import { computed, onMounted, ref } from "vue";
import { RouteLocationRaw, useRouter } from "vue-router";

const props = withDefaults(
    defineProps<{
        edit?: boolean;
        id?: number;
    }>(),
    {
        edit: false,
        id: undefined,
    }
);

const router = useRouter();
const collection = ref<Collection>({
    name: "",
    adminClientId: clientId.value,
});
const loading = ref(false);
const title = props.edit ? "Edit" : "Create";
const submittable = computed(() => validateCollection(collection.value));

onMounted(async () => {
    if (props.edit) {
        if (Number.isNaN(props.id)) {
            // todo: show toast
            await router.replace({ name: "presets" });
            return;
        }
        try {
            const storedCollection = await collectionService.get(props.id);
            if (storedCollection === undefined) {
                // todo: show toast
                await router.replace({ name: "presets" });
            }
            collection.value = storedCollection;
        } catch (error) {
            // todo: show toast
            await router.replace({ name: "presets" });
        }
    }
});

async function _saveCollection(target: RouteLocationRaw) {
    loading.value = true;
    try {
        if (props.edit) {
            await collectionService.put({ ...collection.value });
        } else {
            await collectionService.add({ ...collection.value });
        }
        router.push(target);
    } catch (error) {
        // todo: show toast
    } finally {
        loading.value = false;
    }
}

const save = () => _saveCollection({ name: "presets" });
const start = () => _saveCollection({ name: "presets" });
</script>

<template>
    <DefaultLayout>
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
                <Button
                    label="Save"
                    severity="primary"
                    text
                    @click="save"
                    :disabled="!submittable"
                >
                    <template #icon>
                        <MdiIcon class="mr-2.5" :icon="mdiCheck" />
                    </template>
                </Button>
                <Button
                    label="Start"
                    severity="primary"
                    @click="start"
                    :disabled="!submittable"
                >
                    <template #icon>
                        <MdiIcon class="mr-2.5" :icon="mdiPlay" />
                    </template>
                </Button>
            </div>
        </template>
        <template #default>
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
        </template>
    </DefaultLayout>
</template>