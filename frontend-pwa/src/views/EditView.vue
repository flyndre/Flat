<script setup lang="ts">
import DivisionsList from '@/components/collections/DivisionsList.vue';
import MdiInputIcon from '@/components/icons/MdiInputIcon.vue';
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import MapWithControls from '@/components/map/MapWithControls.vue';
import { clientId } from '@/data/clientMetadata';
import { collectionDraft, collectionService } from '@/data/collections';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import { Collection } from '@/types/Collection';
import { Division } from '@/types/Division';
import { dbSafe } from '@/util/dbUtils';
import validateCollection from '@/validation/validateCollection';
import { mdiArrowLeft, mdiCheck, mdiMapMarkerPath, mdiPlay } from '@mdi/js';
import Button from 'primevue/button';
import Card from 'primevue/card';
import IconField from 'primevue/iconfield';
import InputText from 'primevue/inputtext';
import { v4 as uuidv4 } from 'uuid';
import { computed, onMounted, ref } from 'vue';
import { RouteLocationRaw, useRouter } from 'vue-router';

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

const displayedDivisions = computed<Division[]>(() => [
    ...(!collection.value.area
        ? []
        : <Division[]>[
              {
                  id: '0',
                  area: {
                      type: 'MultiPolygon',
                      coordinates: [collection.value.area?.coordinates ?? [[]]],
                  },
              },
          ]),
    ...(collection.value.divisions ?? []),
]);

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

function editDivisions() {
    router.push({
        name: props.edit ? 'edit-map' : 'create-map',
        params: { id: props.id },
    });
}

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
                    <MdiTextButtonIcon :icon="mdiArrowLeft" />
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
                        <MdiTextButtonIcon :icon="mdiCheck" />
                    </template>
                </Button>
                <Button
                    label="Start"
                    severity="primary"
                    @click="start"
                    :disabled="!submittable"
                >
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiPlay" />
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
                        :divisions="displayedDivisions"
                    />
                </template>
                <template #content>
                    <div class="flex flex-col gap-2.5">
                        <IconField iconPosition="left">
                            <MdiInputIcon :icon="mdiMapMarkerPath" />
                            <InputText
                                class="w-full"
                                placeholder="Collection Name"
                                v-model="collection.name"
                            />
                        </IconField>
                        <DivisionsList
                            v-model="collection.divisions"
                            :edit-divisions-handler="editDivisions"
                        />
                    </div>
                </template>
            </Card>
        </template>
    </DefaultLayout>
</template>
