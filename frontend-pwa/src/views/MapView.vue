<script setup lang="ts">
import MdiTextButtonIcon from '@/components/icons/MdiTextButtonIcon.vue';
import MapHelp from '@/components/map/MapHelp.vue';
import MapWithControls from '@/components/map/MapWithControls.vue';
import { clientId } from '@/data/clientMetadata';
import { collectionDB, collectionDraft } from '@/data/collections';
import { TOAST_LIFE } from '@/data/constants';
import DefaultLayout from '@/layouts/DefaultLayout.vue';
import { Collection } from '@/types/Collection';
import { areaFromShapeList, divisionToShape } from '@/util/converters';
import { dbSafe } from '@/util/dbUtils';
import { mapCenterWithDefaults } from '@/util/googleMapsUtils';
import { mdiArrowLeft, mdiCheck, mdiHelp } from '@mdi/js';
import { useGeolocation } from '@vueuse/core';
import Button from 'primevue/button';
import { useToast } from 'primevue/usetoast';
import { v4 as uuidv4 } from 'uuid';
import { computed, onMounted, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRouter } from 'vue-router';

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
};

const router = useRouter();
const { t } = useI18n();
const collection = ref<Collection>({
    ...defaultCollection,
    ...collectionDraft.get(),
});
const loading = ref(false);

onMounted(async () => {
    if (props.edit) {
        try {
            const storedCollection = await collectionDB.get(props.id);
            if (storedCollection === undefined) {
                add({
                    life: TOAST_LIFE,
                    severity: 'error',
                    summary: t('map.error_collection_not_found'),
                });
                await router.replace({ name: 'presets' });
            }
            collection.value = storedCollection;
        } catch (error) {
            add({
                life: TOAST_LIFE,
                severity: 'error',
                summary: t('map.error_collection_retrieval'),
            });
            await router.replace({ name: 'presets' });
        }
    }
});

const submittable = computed(() => collection.value.divisions?.length > 0);

const { add } = useToast();

async function save() {
    loading.value = true;
    collection.value.area = areaFromShapeList(
        collection.value.divisions.map((d) => divisionToShape(d))
    );
    try {
        if (props.edit) {
            await collectionDB.put(dbSafe(collection.value));
        } else {
            collectionDraft.set(collection.value);
        }
        await router.push({
            name: props.edit ? 'edit' : 'create',
            params: { id: props.id },
        });
    } catch (error) {
        add({
            life: TOAST_LIFE,
            severity: 'error',
            summary: t('map.error_failed_to_save'),
        });
    } finally {
        loading.value = false;
    }
}

const helpVisible = ref(false);
const clientPos = mapCenterWithDefaults(useGeolocation().coords, {
    lat: null,
    lng: null,
});
</script>

<template>
    <DefaultLayout>
        <template #action-left>
            <router-link
                :to="{
                    name: edit ? 'edit' : 'create',
                    params: { id },
                }"
            >
                <Button :label="$t('universal.back')" severity="secondary" text>
                    <template #icon>
                        <MdiTextButtonIcon :icon="mdiArrowLeft" />
                    </template>
                </Button>
            </router-link>
        </template>
        <template #title> {{ $t('map.title') }} </template>
        <template #action-right>
            <Button
                :label="$t('universal.save')"
                severity="primary"
                @click="save"
                :disabled="!submittable"
            >
                <template #icon>
                    <MdiTextButtonIcon :icon="mdiCheck" />
                </template>
            </Button>
            <Button
                :label="$t('universal.help')"
                severity="secondary"
                text
                @click="helpVisible = true"
            >
                <template #icon>
                    <MdiTextButtonIcon :icon="mdiHelp" />
                </template>
            </Button>
        </template>
        <template #default>
            <MapHelp v-model:visible="helpVisible" />
            <MapWithControls
                v-model:divisions="collection.divisions"
                controls="drawing"
                :center="edit ? 'area' : 'position'"
                :client-pos
            />
        </template>
    </DefaultLayout>
</template>
