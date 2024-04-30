<script setup lang="ts">
import DivisionsList from '@/components/collections/DivisionsList.vue';
import MdiInputIcon from '@/components/icons/MdiInputIcon.vue';
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import MapWithControls from '@/components/map/MapWithControls.vue';
import { clientId } from '@/data/clientMetadata';
import { collectionDB, collectionDraft } from '@/data/collections';
import { TOAST_LIFE } from '@/data/constants';
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
import { useToast } from 'primevue/usetoast';
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

const { add } = useToast();

onMounted(async () => {
    if (props.edit) {
        try {
            const storedCollection = await collectionDB.get(props.id);
            if (storedCollection === undefined) {
                add({
                    life: TOAST_LIFE,
                    severity: 'error',
                    summary: 'The requested collection id does not exist.',
                });
                await router.replace({ name: 'presets' });
            }
            collection.value = storedCollection;
        } catch (error) {
            add({
                life: TOAST_LIFE,
                severity: 'error',
                summary: 'Failed to get the requested collection.',
            });
            await router.replace({ name: 'presets' });
        }
    }
});

async function _saveCollection(target: RouteLocationRaw) {
    if (!submittable.value) {
        add({
            life: TOAST_LIFE,
            severity: 'error',
            summary: 'The collection is missing required information.',
        });
        return;
    }
    loading.value = true;
    try {
        if (props.edit) {
            await collectionDB.put(dbSafe(collection.value));
        } else {
            await collectionDB.add(dbSafe(collection.value));
            collectionDraft.set(null);
        }
        await router.push(target);
    } catch (error) {
        add({
            life: TOAST_LIFE,
            severity: 'error',
            summary: 'Failed to save the colelction.',
        });
    } finally {
        loading.value = false;
    }
}

const save = () => _saveCollection({ name: 'presets' });
function start() {
    _saveCollection({ name: 'presets' });
    // TODO: start collection and redirect to /track
}

function editDivisions() {
    collectionDraft.set(collection.value);
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
                    header: { class: 'relative h-[30vh]' },
                }"
            >
                <template #header>
                    <MapWithControls
                        controls="none"
                        center="area"
                        :locked="true"
                        :labels="false"
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
