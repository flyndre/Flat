<script setup lang="ts">
import { openCollection } from '@/api/rest';
import DivisionsList from '@/components/collections/DivisionsList.vue';
import MdiInputIcon from '@/components/icons/MdiInputIcon.vue';
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import MapWithControls from '@/components/map/MapWithControls.vue';
import StatsList from '@/components/stats/StatsList.vue';
import { clientId } from '@/data/clientMetadata';
import {
    collectionDB,
    collectionDraft,
    lastActiveCollection,
} from '@/data/collections';
import { TOAST_LIFE } from '@/data/constants';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import { Collection } from '@/types/Collection';
import { Division } from '@/types/Division';
import { CollectionStats } from '@/types/stats/CollectionStats';
import { dbSafe } from '@/util/dbUtils';
import { getGeoJsonArea } from '@/util/statsUtils';
import validateCollection from '@/validation/validateCollection';
import { mdiArrowLeft, mdiCheck, mdiMapMarkerPath, mdiPlay } from '@mdi/js';
import Button from 'primevue/button';
import Card from 'primevue/card';
import IconField from 'primevue/iconfield';
import InputText from 'primevue/inputtext';
import { useToast } from 'primevue/usetoast';
import { v4 as uuidv4 } from 'uuid';
import { computed, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { RouteLocationRaw, useRouter } from 'vue-router';
import { statsOf, collectionStatsDB } from '@/data/collectionStats';

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

const { t } = useI18n();
const router = useRouter();
const collection = ref<Collection>({
    ...defaultCollection,
    ...collectionDraft.get(),
});

const loading = ref(false);
const title = t(props.edit ? 'edit.title_edit' : 'edit.title_create');
const submittable = computed(() => validateCollection(collection.value));

const displayedDivisions = computed<Division[]>(() => [
    ...(!collection.value.area
        ? []
        : <Division[]>[
              {
                  id: '0',
                  area: {
                      type: 'Polygon',
                      coordinates: collection.value.area?.coordinates[0] ?? [
                          [],
                      ],
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
                    summary: t('edit.error_collection_not_found'),
                });
                await router.replace({ name: 'presets' });
            }
            collection.value = storedCollection;
        } catch (error) {
            add({
                life: TOAST_LIFE,
                severity: 'error',
                summary: t('edit.error_collection_retrieval'),
            });
            await router.replace({ name: 'presets' });
        }
    }
});

async function _saveCollection(afterSavedCallback: () => any) {
    if (!submittable.value) {
        add({
            life: TOAST_LIFE,
            severity: 'error',
            summary: t('edit.error_collection_incomplete'),
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
        afterSavedCallback();
    } catch (error) {
        add({
            life: TOAST_LIFE,
            severity: 'error',
            summary: t('edit.error_failed_to_save'),
        });
    } finally {
        loading.value = false;
    }
}

async function save() {
    await _saveCollection(() => router.push({ name: 'presets' }));
}

async function start() {
    lastActiveCollection.set(undefined);
    await _saveCollection(async () => {
        try {
            const response = await openCollection(collection.value);
            if (response.status != 200) throw response;
            await router.push(`/track/${collection.value.id}`);
        } catch (e) {
            add({
                life: TOAST_LIFE,
                severity: 'error',
                summary: t('edit.error_failed_to_start'),
            });
        }
    });
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

const stats = statsOf(props.id);
</script>

<template>
    <DefaultLayout>
        <template #action-left>
            <Button
                :label="$t('universal.back')"
                severity="secondary"
                text
                @click="back"
            >
                <template #icon>
                    <MdiTextButtonIcon :icon="mdiArrowLeft" />
                </template>
            </Button>
        </template>
        <template #title> {{ title }} </template>
        <template #action-right>
            <div class="flex flex-row gap-2">
                <Button
                    :label="$t('universal.save')"
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
                    :label="$t('universal.start')"
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
                                :placeholder="$t('edit.collection_name')"
                                v-model="collection.name"
                            />
                        </IconField>
                        <DivisionsList
                            v-model="collection.divisions"
                            :edit-divisions-handler="editDivisions"
                        />
                        <StatsList
                            v-if="edit && stats?.length > 0"
                            :stats="stats"
                        />
                    </div>
                </template>
            </Card>
        </template>
    </DefaultLayout>
</template>
