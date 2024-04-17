<script setup lang="ts">
import InputIcon from '@/components/icons/InputIcon.vue';
import TextButtonIcon from '@/components/icons/TextButtonIcon.vue';
import MapWithControls from '@/components/map/MapWithControls.vue';
import { clientId } from '@/data/clientMetadata';
import { collectionDraft, collectionService } from '@/data/collections';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import { Collection } from '@/types/Collection';
import validateCollection from '@/validation/validateCollection';
import {
    mdiArrowLeft,
    mdiCheck,
    mdiMapMarkerPath,
    mdiPencil,
    mdiPlay,
    mdiViewDashboardEdit,
} from '@mdi/js';
import Button from 'primevue/button';
import Card from 'primevue/card';
import IconField from 'primevue/iconfield';
import InputText from 'primevue/inputtext';
import { v4 as uuidv4 } from 'uuid';
import { computed, onMounted, ref } from 'vue';
import { RouteLocationRaw, useRouter } from 'vue-router';
import { dbSafe } from '@/util/dbUtils';

const props = withDefaults(
    defineProps<{
        edit?: boolean;
        id?: string;
    }>(),
    {
        edit: false,
        id: undefined,
    }
);

const defaultCollection: Collection = {
    id: uuidv4(),
    adminClientId: clientId.value,
    name: '',
};

const router = useRouter();
const collection = ref<Collection>({
    ...defaultCollection,
    ...collectionDraft.get(),
});

const loading = ref(false);
const title = props.edit ? 'Edit' : 'Create';
const submittable = computed(() => validateCollection(collection.value));

onMounted(async () => {
    if (props.edit) {
        try {
            const storedCollection = await collectionService.get(props.id);
            if (storedCollection === undefined) {
                // todo: show toast
                await router.replace({ name: 'presets' });
            }
            collection.value = storedCollection;
        } catch (error) {
            // todo: show toast
            await router.replace({ name: 'presets' });
        }
    }
});

async function _saveCollection(target: RouteLocationRaw) {
    if (!submittable.value) {
        // todo: show toast
        return;
    }
    loading.value = true;
    try {
        if (props.edit) {
            await collectionService.put(dbSafe(collection.value));
        } else {
            await collectionService.add(dbSafe(collection.value));
            collectionDraft.set(null);
        }
        await router.push(target);
    } catch (error) {
        // todo: show toast
    } finally {
        loading.value = false;
    }
}

const save = () => _saveCollection({ name: 'presets' });
const start = () => _saveCollection({ name: 'presets' });

function back() {
    collectionDraft.set(null);
    router.push({ name: 'presets' });
}
</script>

<template>
    <DefaultLayout>
        <template #action-left>
            <Button label="Back" severity="secondary" text @click="back">
                <template #icon>
                    <TextButtonIcon :icon="mdiArrowLeft" />
                </template>
            </Button>
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
                        <TextButtonIcon :icon="mdiCheck" />
                    </template>
                </Button>
                <Button
                    label="Start"
                    severity="primary"
                    @click="start"
                    :disabled="!submittable"
                >
                    <template #icon>
                        <TextButtonIcon :icon="mdiPlay" />
                    </template>
                </Button>
            </div>
        </template>
        <template #default>
            <Card
                :pt="{
                    root: { class: 'overflow-hidden' },
                    header: { class: 'relative h-[30vh]' },
                }"
            >
                <template #header>
                    <MapWithControls
                        class="pointer-events-none [&>*]:rounded-none"
                        :controls="false"
                        :areas="collection.divisions"
                    />
                    <router-link
                        :to="{
                            name: edit ? 'edit-map' : 'create-map',
                            params: { id: props.id },
                        }"
                    >
                        <Button
                            class="absolute bottom-6 right-6"
                            label="Edit Areas"
                            severity="secondary"
                            raised
                        >
                            <template #icon>
                                <TextButtonIcon :icon="mdiPencil" />
                            </template>
                        </Button>
                    </router-link>
                </template>
                <template #content>
                    <div class="flex flex-col gap-2.5">
                        <IconField iconPosition="left">
                            <InputIcon :icon="mdiMapMarkerPath" />
                            <InputText
                                class="w-full"
                                placeholder="Collection Name"
                                v-model="collection.name"
                            />
                        </IconField>
                    </div>
                </template>
            </Card>
        </template>
    </DefaultLayout>
</template>
